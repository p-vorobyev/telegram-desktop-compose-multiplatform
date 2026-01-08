#!/bin/sh

echo "🧹 Cleaning and building backend..."
./gradlew clean :backend:bootJar

echo "📦 Preparing desktop distribution..."
./gradlew :desktop:copyTelegramBackend :desktop:createDistributable

echo "✅ Build completed successfully."
