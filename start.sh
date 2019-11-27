#!/bin/bash
deploy_folder=/home/linhnv5/project.english.teaching.video
nohup /usr/bin/java -jar $deploy_folder/target/video.teaching-0.0.1-SNAPSHOT.jar  > /tmp/english.teaching.video.log 2>&1 &
