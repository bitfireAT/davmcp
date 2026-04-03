# Docker

1. Build the docker image:
    ```shell
    docker build -t davx5-mcp -f docker/Dockerfile .
    ```
2. Start the container:
    ```shell
    docker run -p 3000 -v davx5-mcp-data:/app/data davx5-mcp
    ```

# Docker Compose

1. Build the docker image:
    ```shell
    docker compose -f docker/compose.yml build
    ```
2. Start the container:
    ```shell
    docker compose -f docker/compose.yml up
    ```
