#!/bin/bash

./gradlew -p ../backend bootJar && ./gradlew -p ../desktop packageUberJarForCurrentOS

rm -rf jars && mkdir jars

cp ../backend/build/libs/backend*.jar ./jars/backend.jar

cp ../desktop/build/compose/jars/TelegramComposeMultiplatform*.jar ./jars/TelegramComposeMultiplatform.jar