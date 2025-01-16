# Crud Test (Kotlin)

This project is a very simple to-do list HTTP server and HTTP client.

The goal will be to test

- your ability to understand code you have not seen before
- add to the code using the existing code as examples
- explain your thought process as you are doing so

**Project structure**

```
crud-test/
|- client/
|  |- src/
|     |- main/
|        |- kotlin/
|           |- ca.dialai.crud.test/
|              |- Main.kt
|              |- TodoClient.kt
|              |- TodoRecord.kt
|- server/
|  |- src/
|     |- main/
|        |- kotlin/
|           |- ca.dialai.crud.test/
|              |- Main.kt
```

__**The only files you will have to touch are:**__

`server/src/main/kotlin/ca/dialai/crud/test/Main.kt`: Which contains the entire server

`client/src/main/kotlin/ca/dialai/crud/test/Main.kt`: Which contains the code that uses the client to test the server

`client/src/main/kotlin/ca/dialai/crud/test/TodoClient.kt`: Which contains the implementation of the client to interact
with the server.

__**Everything else you can safely ignore**__

## Running the Project

**Run the server**

```bash
./gradlew server:run
```

**Run the client**

```bash
./gradlew client:run
```