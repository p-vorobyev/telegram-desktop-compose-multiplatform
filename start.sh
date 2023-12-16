#!/bin/bash

PID=$(pgrep -f backend-0.0.1-SNAPSHOT.jar)
if [ $PID != "" ]; then
    kill $PID
fi

LOG_FILE=$(ls backend.log)
if [ $LOG_FILE != "" ]; then
    rm $LOG_FILE
fi

nohup java -jar backend-0.0.1-SNAPSHOT.jar > backend.log &
sleep 3
nohup java -jar TelegramCM-macos-arm64-1.0.0.jar 2>/dev/null &
