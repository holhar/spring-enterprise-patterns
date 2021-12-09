# Connect to Redis

## Spin up and execute into container
    $ docker compose up -d
    $ docker exec -it docker-redis-1 sh

## Redis login
    $ redis-cli -a <password>

## Get keys
    KEYS *

## Get value
    HGETALL <key-name>

## Delete value
    DEL <key-name>