#!/bin/sh

./gradlew clean
./gradlew :backend:bootJar
./gradlew :desktop:copyTelegramBackend
./gradlew :desktop:createDistributable
