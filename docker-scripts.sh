#!/bin/bash

# Docker scripts for PSP System

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Build the Docker image
build_image() {
    print_status "Building PSP System Docker image..."
    docker build -t psp-system:latest .
    print_success "Docker image built successfully!"
}

# Run the container
run_container() {
    print_status "Running PSP System container..."
    docker run -d \
        --name psp-system \
        -p 8080:8080 \
        --restart unless-stopped \
        psp-system:latest
    print_success "Container started successfully!"
    print_status "Application is available at: http://localhost:8080"
    print_status "API documentation at: http://localhost:8080/docs/"
}

# Run with docker-compose
run_compose() {
    print_status "Starting PSP System with docker-compose..."
    docker-compose up -d
    print_success "Services started successfully!"
    print_status "Application is available at: http://localhost:8080"
    print_status "API documentation at: http://localhost:8080/docs/"
}

# Stop the container
stop_container() {
    print_status "Stopping PSP System container..."
    docker stop psp-system || true
    docker rm psp-system || true
    print_success "Container stopped and removed!"
}

# Stop docker-compose
stop_compose() {
    print_status "Stopping PSP System services..."
    docker-compose down
    print_success "Services stopped!"
}

# Show logs
show_logs() {
    print_status "Showing container logs..."
    docker logs -f psp-system
}

# Show compose logs
show_compose_logs() {
    print_status "Showing compose logs..."
    docker-compose logs -f
}

# Clean up
cleanup() {
    print_status "Cleaning up Docker resources..."
    docker-compose down --volumes --remove-orphans || true
    docker rmi psp-system:latest || true
    print_success "Cleanup completed!"
}

# Health check
health_check() {
    print_status "Checking application health..."
    if curl -f http://localhost:8080/docs/ > /dev/null 2>&1; then
        print_success "Application is healthy!"
    else
        print_error "Application is not responding!"
        exit 1
    fi
}

# Show help
show_help() {
    echo "PSP System Docker Management Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build       Build the Docker image"
    echo "  run         Run the container"
    echo "  compose     Run with docker-compose"
    echo "  stop        Stop the container"
    echo "  stop-compose Stop docker-compose services"
    echo "  logs        Show container logs"
    echo "  logs-compose Show compose logs"
    echo "  health      Check application health"
    echo "  cleanup     Clean up Docker resources"
    echo "  help        Show this help message"
    echo ""
}

# Main script logic
case "${1:-help}" in
    build)
        build_image
        ;;
    run)
        build_image
        run_container
        ;;
    compose)
        build_image
        run_compose
        ;;
    stop)
        stop_container
        ;;
    stop-compose)
        stop_compose
        ;;
    logs)
        show_logs
        ;;
    logs-compose)
        show_compose_logs
        ;;
    health)
        health_check
        ;;
    cleanup)
        cleanup
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
