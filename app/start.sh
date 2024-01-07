#!/bin/bash

PID=$(pgrep -f backend.jar)
if [ $PID != "" ]; then
    kill $PID
fi

LOG_FILE=$(ls backend.log)
if [ $LOG_FILE != "" ]; then
    rm $LOG_FILE
fi

nohup java -Xms64m -Xmx64m -Djava.library.path=./macos_silicon -jar jars/backend.jar > backend.log &
sleep 3
nohup java -jar jars/TelegramComposeMultiplatform.jar 2>/dev/null &
