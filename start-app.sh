#!/bin/bash

# Start script for PSP System
set -e

echo "Starting PSP System..."

# Compile the application
echo "Compiling application..."
sbt -batch "app/compile"

# Run the application
echo "Starting application..."
sbt -batch "app/run"
