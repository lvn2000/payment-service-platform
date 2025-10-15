# Multi-stage build for Scala application
FROM openjdk:11-jdk-slim

# Install sbt and curl
RUN apt-get update && \
    apt-get install -y curl && \
    curl -L -o sbt-1.11.7.deb https://repo.scala-sbt.org/scalasbt/debian/sbt-1.11.7.deb && \
    dpkg -i sbt-1.11.7.deb || apt-get install -f -y && \
    rm sbt-1.11.7.deb && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy build files first for better caching
COPY build.sbt ./
COPY project/ project/

# Download dependencies (this layer will be cached if build.sbt doesn't change)
RUN sbt update

# Copy source code
COPY . .

# Make the start script executable
RUN chmod +x start-app.sh

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/docs/ || exit 1

# Run the application as root (for now, to avoid permission issues)
CMD ["./start-app.sh"]
