#!/bin/bash

PID=$(pgrep -f client.jar)
if [ $PID != "" ]; then
    kill $PID
fi

LOG_FILE=$(ls client.log)
if [ $LOG_FILE != "" ]; then
    rm $LOG_FILE
fi

nohup java -jar client.jar > client.log &
sleep 5
java -jar telegram.jar
