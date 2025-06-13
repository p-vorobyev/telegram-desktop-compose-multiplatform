#!/bin/sh

cd desktop/resources || exit
curl -LJO 'https://github.com/p-vorobyev/spring-boot-starter-telegram/releases/download/1.17.0/libs.zip'
unzip -q libs.zip
cp libs/linux_arm64/*.so linux-arm64
cp libs/linux_x64/*.so linux-x64
cp libs/macos_silicon/*.dylib macos-arm64
cp libs/macos_x64/*.dylib macos-x64
cp libs/windows_x64/*.dll windows-x64
rm libs.zip && rm -rf libs/
cd ../..

./gradlew clean
./gradlew :backend:bootJar
./gradlew :desktop:copyTelegramBackend
./gradlew :desktop:createDistributable
