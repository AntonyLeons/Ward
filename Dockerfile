# Build stage
FROM rust:1.88-slim-bookworm AS builder

# Create a new empty shell project
WORKDIR /usr/src/ward-rs

# Copy dependencies and build them first (caching)
COPY Cargo.toml Cargo.lock ./
RUN mkdir src && echo "fn main() {}" > src/main.rs
RUN cargo build --release
RUN rm -rf src

# Copy the actual source code and templates
COPY src ./src
COPY templates ./templates
COPY assets ./assets

# Build the final binary
RUN cargo build --release

# Production stage
FROM debian:bookworm-slim

WORKDIR /app

# Install necessary runtime dependencies (e.g., for sysinfo or SSL if needed)
RUN apt-get update && apt-get install -y libssl3 && rm -rf /var/lib/apt/lists/*

# Run as a non-root user for better security
RUN useradd -m -s /bin/bash ward_user && chown -R ward_user /app
USER ward_user

# Copy the compiled binary
COPY --from=builder /usr/src/ward/target/release/ward ./ward

# Create empty setup.ini or ensure it can be created
RUN touch setup.ini && chmod 666 setup.ini

# Expose port
EXPOSE 4000

# Run the binary
ENTRYPOINT ["./ward"]
