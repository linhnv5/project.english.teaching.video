#!/bin/bash
proccess_id=$(ps aux | grep frontend | grep 'video.teaching-0.0.1-SNAPSHOT' | awk '{print $2}')
if test -n "$proccess_id"; then
    echo "Attempting to kill $proccess_id..."
    kill -9 $proccess_id
        echo "Process $proccess_id terminated"
else
    echo "Application is not running"
fi;
