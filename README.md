# Magical Wheel

A simple game of socket programming lab for course CS494 - Internetworking Protocols.

## Modules

- server: Game server that receives connections and hosts the game
- client: Client program for each player
## Run Locally

**Prerequisite**: [JDK 15](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html)

Build modules

```bash
./gradlew clean build
```

Launch the server

```bash
python ./server/server.py
```

Launch the client

```bash
java -jar ./client/build/libs/client.jar
```

## Demo

[Youtube link](https://youtu.be/nNBSOzz82Gg)
