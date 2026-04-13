pub mod models;
pub mod system;
pub mod config;

use axum::{
    routing::{get, post},
    Router, Json, extract::State, response::Html,
};
use axum::http::{header, StatusCode, Uri, HeaderValue};
use axum::response::IntoResponse;
use std::sync::Arc;
use tokio::sync::Mutex;
use tower_http::services::ServeDir;
use tower_http::set_header::SetResponseHeaderLayer;

use crate::system::SystemMonitor;
use crate::config::ConfigManager;
use crate::models::{SetupDto, ResponseDto, UsageDto, InfoDto, UptimeDto};

struct AppState {
    sys_monitor: Arc<Mutex<SystemMonitor>>,
    config_manager: Arc<ConfigManager>,
    active_port: String,
    port_overridden: bool,
}


use clap::Parser;



#[derive(Parser, Debug)]
#[command(author, version, about = "Ward dashboard rewrite in Rust", long_about = None)]
struct Args {
    /// Port to run the server on (1024-65535)
    #[arg(short, long, value_parser = port_in_range)]
    port: Option<u16>,
}

fn port_in_range(s: &str) -> Result<u16, String> {
    let port: usize = s
        .parse()
        .map_err(|_| format!("`{s}` isn't a valid port number"))?;
    if (1024..=65535).contains(&port) {
        Ok(port as u16)
    } else {
        Err(format!("Port not in range 1024-65535 (provided {port})"))
    }
}

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();

    let args = Args::parse();

    let sys_monitor = Arc::new(Mutex::new(SystemMonitor::new()));
    let config_manager = Arc::new(ConfigManager::new("setup.ini"));

    let port_from_cli = args.port;
    let port_from_env = std::env::var("WARD_PORT").ok().and_then(|p| p.parse::<u16>().ok());

    let port_overridden = port_from_cli.is_some() || port_from_env.is_some();

    let port = port_from_cli.or(port_from_env).unwrap_or_else(|| {
        config_manager
            .read_config()
            .and_then(|c| c.port.parse().ok())
            .unwrap_or(4000)
    });

    let app_state = Arc::new(AppState {
        sys_monitor,
        config_manager: config_manager.clone(),
        active_port: port.to_string(),
        port_overridden,
    });

    let app = Router::new()
        .route("/", get(index_handler))
        .route("/api/info", get(info_handler))
        .route("/api/usage", get(usage_handler))
        .route("/api/uptime", get(uptime_handler))
        .route("/api/setup", post(setup_handler))
        .nest_service("/css", ServeDir::new("assets/css"))
        .nest_service("/js", ServeDir::new("assets/js"))
        .nest_service("/img", ServeDir::new("assets/img"))
        .nest_service("/fonts", ServeDir::new("assets/fonts"))
        .layer(SetResponseHeaderLayer::overriding(
            header::X_FRAME_OPTIONS,
            HeaderValue::from_static("DENY"),
        ))
        .layer(SetResponseHeaderLayer::overriding(
            header::X_CONTENT_TYPE_OPTIONS,
            HeaderValue::from_static("nosniff"),
        ))
        .layer(SetResponseHeaderLayer::overriding(
            header::X_XSS_PROTECTION,
            HeaderValue::from_static("1; mode=block"),
        ))
        .with_state(app_state);

    let addr = format!("0.0.0.0:{port}");
    tracing::info!("Listening on {}", addr);
    let listener = tokio::net::TcpListener::bind(&addr).await.unwrap();
    axum::serve(listener, app).await.unwrap();
}

use askama::Template;

#[derive(Template)]
#[template(path = "index.html")]
struct IndexTemplate {
    theme: String,
    enable_fog: String,
    background_color: String,
    server_name: String,
    version: String,
    info: InfoDto,
    uptime: UptimeDto,
}

#[derive(Template)]
#[template(path = "setup.html")]
struct SetupTemplate {
    #[allow(dead_code)]
    theme: String,
    #[allow(dead_code)]
    enable_fog: String,
    #[allow(dead_code)]
    background_color: String,
    #[allow(dead_code)]
    server_name: String,
    #[allow(dead_code)]
    port: String,
    #[allow(dead_code)]
    port_overridden: bool,
}

async fn index_handler(State(state): State<Arc<AppState>>) -> Html<String> {
    if !state.config_manager.is_configured() {
        let tmpl = SetupTemplate {
            theme: "light".to_string(),
            enable_fog: "true".to_string(),
            background_color: "default".to_string(),
            server_name: "Ward".to_string(),
            port: state.active_port.clone(),
            port_overridden: state.port_overridden,
        };
        return Html(tmpl.render().unwrap());
    }

    let config = state.config_manager.read_config().unwrap();
    let monitor = state.sys_monitor.lock().await;
    
    let tmpl = IndexTemplate {
        theme: config.theme,
        enable_fog: config.enable_fog,
        background_color: config.background_color,
        server_name: config.server_name,
        version: "2.6.0 (Rust)".to_string(),
        info: monitor.get_info(),
        uptime: monitor.get_uptime(),
    };

    Html(tmpl.render().unwrap())
}

    #[allow(dead_code)]
async fn setup_page_handler(State(state): State<Arc<AppState>>) -> Html<String> {
    let config = state.config_manager.read_config();
    let tmpl = SetupTemplate {
        theme: config.as_ref().map(|c| c.theme.clone()).unwrap_or_else(|| "light".to_string()),
        enable_fog: config.as_ref().map(|c| c.enable_fog.clone()).unwrap_or_else(|| "true".to_string()),
        background_color: config.as_ref().map(|c| c.background_color.clone()).unwrap_or_else(|| "default".to_string()),
        server_name: config.as_ref().map(|c| c.server_name.clone()).unwrap_or_else(|| "Ward".to_string()),
        port: state.active_port.clone(),
        port_overridden: state.port_overridden,
    };
    Html(tmpl.render().unwrap())
}

async fn info_handler(State(state): State<Arc<AppState>>) -> Json<InfoDto> {
    let monitor = state.sys_monitor.lock().await;
    Json(monitor.get_info())
}

async fn usage_handler(State(state): State<Arc<AppState>>) -> Json<UsageDto> {
    let monitor = state.sys_monitor.lock().await;
    Json(monitor.get_usage())
}

async fn uptime_handler(State(state): State<Arc<AppState>>) -> Json<UptimeDto> {
    let monitor = state.sys_monitor.lock().await;
    Json(monitor.get_uptime())
}

async fn setup_handler(
    State(state): State<Arc<AppState>>,
    Json(payload): Json<SetupDto>,
) -> Json<ResponseDto> {
    if state.config_manager.is_configured() {
        return Json(ResponseDto { message: "Application already configured".to_string() });
    }

    match state.config_manager.write_config(&payload) {
        Ok(_) => Json(ResponseDto { message: "Settings saved correctly".to_string() }),
        Err(e) => Json(ResponseDto { message: format!("Failed to save settings: {e}") }),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use axum::{
        body::Body,
        http::{Request, StatusCode},
    };
    use tower::ServiceExt;

    fn test_app() -> Router {
        let sys_monitor = Arc::new(Mutex::new(SystemMonitor::new()));
        let config_manager = Arc::new(ConfigManager::new("test_integration.ini"));
        let _ = std::fs::remove_file("test_integration.ini"); // ensure clean

        let app_state = Arc::new(AppState {
            sys_monitor,
            config_manager,
            active_port: "4000".to_string(),
            port_overridden: false,
        });

        Router::new()
            .route("/", get(index_handler))
            .route("/api/info", get(info_handler))
            .route("/api/usage", get(usage_handler))
            .route("/api/uptime", get(uptime_handler))
            .route("/api/setup", post(setup_handler))
            .with_state(app_state)
    }

    #[tokio::test]
    async fn test_index_unconfigured() {
        let app = test_app();

        let request = Request::builder().uri("/").body(Body::empty()).unwrap();
        let response = app.oneshot(request).await.unwrap();

        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_api_info() {
        let app = test_app();

        let request = Request::builder().uri("/api/info").body(Body::empty()).unwrap();
        let response = app.oneshot(request).await.unwrap();

        assert_eq!(response.status(), StatusCode::OK);
    }
}
