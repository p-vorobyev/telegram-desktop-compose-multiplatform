# Telegram Desktop with Compose Multiplatform

## Contents
- [Requirements](#requirements)
- [Current versions](#versions)
- [Supported platforms](#platforms)
- [Build](#build)
- [Run application](#run)



<a name="requirements"></a>
## Requirements
|   | Version |
|-------------|----|
| jdk         | 21 |



<a name="versions"></a>
## Current versions
|                       | Version |
|-----------------------|---------|
| Compose Multiplatform | 1.6.2   |
| TDLib                 | 1.8.34  |



<a name="platforms"></a>
## Supported platforms
MacOS (x64 and M)

Linux (x64 and arm64)

Windows (x64)



<a name="build"></a>
## Build

1) Download source code:
```shell
git clone https://github.com/p-vorobyev/telegram-desktop-compose-multiplatform.git
```
2) Specify environment variables(`GIT_HUB_LOGIN`, `GIT_HUB_TOKEN`) with your GitHub credentials or write them directly to
`backend/build.gradle.kts` file. We need them to download some dependencies from GitHub packages.
```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/p-vorobyev/*")
        credentials {
            username = System.getenv("GIT_HUB_LOGIN")
            password = System.getenv("GIT_HUB_TOKEN")
        }
    }
}
```

3) Go to directory `backend/src/main/resources` and fill in the `application.properties` with your Telegram credentials:
```shell
spring.telegram.client.api-id=
spring.telegram.client.api-hash=
spring.telegram.client.phone=<international_format_phonenumber>
spring.telegram.client.database-encryption-key=
spring.telegram.client.database-directory=<directory_for_telegram_data>
```

4) Run the build script in the project root directory:
```shell
./build.sh
```
When the script completes, you will see the destination path of the binaries in the console output.
```shell
> Task :desktop:createDistributable
The distribution is written to /Users/vorobyev/Documents/projects/telegram-desktop-client/desktop/build/compose/binaries/main/app
```



<a name="run"></a>
## Run application

At the first start you need to authorize the app with code from Telegram(will be sent to official app).
![](https://github.com/p-vorobyev/telegram-desktop-compose-multiplatform/blob/master/img/auth.png)
If you have activated 2-Step verification you will also need to confirm it.
![](https://github.com/p-vorobyev/telegram-desktop-compose-multiplatform/blob/master/img/2step.png)
After this, the chats will load for a while and become available. Example with filter some chats:
![](https://github.com/p-vorobyev/telegram-desktop-compose-multiplatform/blob/master/img/chatListFilter.png)
Currently only some basic message types (text and photo) are available without additional features 
(reactions, delivery status, reply info, etc.).
![](https://github.com/p-vorobyev/telegram-desktop-compose-multiplatform/blob/master/img/messages.png)
Some features will be implemented in the future as possible.
