# Magical Wheel

A simple game of socket programming lab for course CS494 - Internetworking Protocols.

## Modules

- server: Game server that receives connections and hosts the game
- client: Client program for each player
## Run Locally

**Prerequisite**: [JDK 15](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html) 
(recommend JDK 11+)

Build modules

```bash
./gradlew clean build
```

## Server Configuration

Look at the `config.py` file.

Launch the server

```bash
python ./server/server.py
```

Launch the client

```bash
java -jar ./client/build/libs/client.jar
```

## Demo

Youtube link: To be updated
