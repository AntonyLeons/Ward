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

        let response = app
            .oneshot(Request::builder().uri("/").body(Body::empty()).unwrap())
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);
        // Should return setup template
        let body = axum::body::to_bytes(response.into_body(), usize::MAX).await.unwrap();
        let body_str = String::from_utf8(body.to_vec()).unwrap();
        assert!(body_str.contains("SERVER DASHBOARD")); // from setup.html
    }

    #[tokio::test]
    async fn test_api_info() {
        let app = test_app();

        let response = app
            .oneshot(Request::builder().uri("/api/info").body(Body::empty()).unwrap())
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_api_setup() {
        let app = test_app();

        let setup_json = r#"{
            "serverName": "TestServer",
            "theme": "dark",
            "port": "4000",
            "enableFog": "true",
            "backgroundColor": "default"
        }"#;

        let response = app
            .oneshot(
                Request::builder()
                    .method("POST")
                    .uri("/api/setup")
                    .header("content-type", "application/json")
                    .body(Body::from(setup_json))
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);

        // Clean up
        let _ = std::fs::remove_file("test_integration.ini");
    }
}
