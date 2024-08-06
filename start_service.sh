#!/bin/bash

# Paths to your Kafka and Elasticsearch directories
KAFKA_DIR="/Users/ujjwalkukreti/Desktop/notificationsApp/kafka"
ELASTICSEARCH_DIR="/Users/ujjwalkukreti/Desktop/notificationsApp/elasticsearch"

# Start Zookeeper in a new terminal
osascript <<EOF
tell application "Terminal"
    do script "cd $KAFKA_DIR; bin/zookeeper-server-start.sh config/zookeeper.properties"
end tell
EOF

# Wait for a few seconds to ensure Zookeeper starts properly
sleep 10

# Start Kafka in a new terminal
osascript <<EOF
tell application "Terminal"
    do script "cd $KAFKA_DIR; bin/kafka-server-start.sh config/server.properties"
end tell
EOF

sleep 10
osascript <<EOF
tell application "Terminal"
    do script "cd $ELASTICSEARCH_DIR; ./bin/elasticsearch"
end tell
EOF