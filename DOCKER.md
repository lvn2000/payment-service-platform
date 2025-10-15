# Docker Deployment Guide for PSP System

This guide explains how to containerize and deploy the PSP (Payment Service Provider) System using Docker.

## Prerequisites

- Docker Engine 20.10+ 
- Docker Compose 2.0+
- At least 2GB of available RAM
- Port 8080 available on your system

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start the application
./docker-scripts.sh compose

# Check if the application is running
./docker-scripts.sh health

# View logs
./docker-scripts.sh logs-compose

# Stop the application
./docker-scripts.sh stop-compose
```

### Option 2: Using Docker directly

```bash
# Build and run the container
./docker-scripts.sh run

# Check if the application is running
./docker-scripts.sh health

# View logs
./docker-scripts.sh logs

# Stop the container
./docker-scripts.sh stop
```

## Manual Docker Commands

### Build the Image

```bash
docker build -t psp-system:latest .
```

### Run the Container

```bash
docker run -d \
  --name psp-system \
  -p 8080:8080 \
  --restart unless-stopped \
  psp-system:latest
```

### Using Docker Compose

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## Application Endpoints

Once the container is running, the application will be available at:

- **Main Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/docs/
- **Health Check**: http://localhost:8080/docs/ (returns 200 if healthy)

## API Usage

### Process a Payment

```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4242424242424242",
    "expiry": "2025-12",
    "cvv": "123",
    "amount": 100.00,
    "currency": "USD",
    "merchantId": "merchant-1"
  }'
```

### Example Response

```json
{
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "Approved",
  "message": "Processed"
}
```

## Container Details

### Image Information

- **Base Image**: OpenJDK 11 JRE Slim
- **Application JAR**: psp-app-assembly-0.1.0-SNAPSHOT.jar
- **Port**: 8080
- **User**: Non-root user `psp`
- **Health Check**: Built-in HTTP health check

### Resource Requirements

- **Memory**: Minimum 256MB, Recommended 512MB
- **CPU**: 1 core minimum
- **Disk**: ~200MB for the image

### Environment Variables

You can customize the JVM settings using the `JAVA_OPTS` environment variable:

```bash
docker run -d \
  --name psp-system \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  psp-system:latest
```

## Monitoring and Logs

### View Container Logs

```bash
# Using the script
./docker-scripts.sh logs

# Or directly with Docker
docker logs -f psp-system
```

### Health Monitoring

The container includes a built-in health check that verifies the application is responding:

```bash
# Check container health
docker inspect --format='{{.State.Health.Status}}' psp-system

# Manual health check
curl -f http://localhost:8080/docs/
```

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   ```bash
   # Use a different port
   docker run -d --name psp-system -p 8081:8080 psp-system:latest
   ```

2. **Container fails to start**
   ```bash
   # Check logs for errors
   docker logs psp-system
   ```

3. **Application not responding**
   ```bash
   # Check if container is running
   docker ps
   
   # Check health status
   docker inspect psp-system | grep -A 10 Health
   ```

### Debug Mode

To run the container in debug mode with a shell:

```bash
docker run -it --rm psp-system:latest /bin/bash
```

## Development

### Building for Development

```bash
# Build without cache
docker build --no-cache -t psp-system:latest .

# Build with specific tag
docker build -t psp-system:v1.0.0 .
```

### Multi-stage Build

The Dockerfile uses a multi-stage build:
1. **Builder stage**: Downloads dependencies and compiles the application
2. **Runtime stage**: Creates a minimal image with only the JAR file

This approach results in a smaller final image and better security.

## Security Considerations

- The application runs as a non-root user (`psp`)
- Only necessary ports are exposed (8080)
- Minimal base image (OpenJDK JRE Slim)
- No unnecessary packages installed in the runtime image

## Cleanup

To remove all Docker resources related to the PSP system:

```bash
./docker-scripts.sh cleanup
```

This will:
- Stop and remove containers
- Remove the Docker image
- Clean up volumes and networks

## Production Deployment

For production deployment, consider:

1. **Using a specific image tag** instead of `latest`
2. **Setting up proper logging** with log aggregation
3. **Configuring resource limits** in docker-compose.yml
4. **Using secrets management** for sensitive configuration
5. **Setting up monitoring** and alerting
6. **Using a reverse proxy** (nginx, traefik) for SSL termination

Example production docker-compose.yml:

```yaml
version: '3.8'

services:
  psp-app:
    image: psp-system:v1.0.0
    container_name: psp-system-prod
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC
    restart: always
    deploy:
      resources:
        limits:
          memory: 1g
          cpus: '1.0'
        reservations:
          memory: 512m
          cpus: '0.5'
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
    networks:
      - psp-network

networks:
  psp-network:
    driver: bridge
```
