#!/usr/bin/env bash

echo ccloud config file: $DBZ_CONFIG_FILE
echo ccloud CLI credentials file: $CCLOUD_CREDENTIALS

. $CCLOUD_CREDENTIALS

echo Stopping containers
cd src/main/docker
docker-compose down -v
cd -

docker run -ti --rm \
    -e XX_CCLOUD_EMAIL=$XX_CCLOUD_EMAIL -e XX_CCLOUD_PASSWORD=$XX_CCLOUD_PASSWORD \
    -v ~/.ccloud:/root/.ccloud -v $(pwd):/work \
    --entrypoint "bash" \
    ccloud /work/dostart.sh

echo Removing state store
rm -rf /tmp/kafka-streams

cd -

sleep 5

echo Starting containers
docker-compose up -d
cd -

osascript -e 'display notification "Initialization finished" with title "Init"'



