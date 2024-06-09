# README

Rails API sample

## build and start (develop)

```sh
docker-compose up -d
```

You can access the API server to `http://localhost:3000`

## Endpoints

### 1. Get All Tasks

- **URL:** `/api/v2/todos`
- **Method:** GET
- **Description:** Todo list
- **Response:**
  - **Status Code:** 200 OK
  - **Body:**
    ```json
    [
    	{
        "id": 2,
        "content": "Task 2",
        "completed": false,
        "deadline": "2024-01-01T00:00:00.000+09:00",
        "created_at": "2024-01-01T00:00:00.000+09:00",
        "updated_at": "2024-01-01T00:00:00.000+09:00"
	    },
      {
        "id": 1,
        "content": "Task 1",
        "completed": false,
        "deadline": "2024-01-01T00:00:00.000+09:00",
        "created_at": "2024-01-01T00:00:00.000+09:00",
        "updated_at": "2024-01-01T00:00:00.000+09:00"
      }
    ]
    ```

### 2. Create a Task

- **URL:** `/api/v2/todos`
- **Method:** POST
- **Description:** Create a new task.
- **Request Body:**
  ```json
  {
  "todo": {
      "content": "new Task",
      "completed": false,
      "deadline": "2024-01-1T00:00:00Z"
    }
  }
  ```

### 3. Update a Task

- **URL:** `/api/v2/todos/:id`
- **Method:** PUT
- **Description:** Update task.
- **Request Body:**
  ```json
  {
  "todo": {
      "content": "Task 1",
      "completed": true,
      "deadline": "2024-01-1T00:00:00Z"
    }
  }
  ```

### 4. Kotlin Multi Platform

- **URL:**
  - `GET http://localhost:123456/todos/list`
  - `GET http://localhost:123456/todos/add?content={:content}&completed={:completed}&deadline={:deadline}`
  - `GET http://localhost:123456/todos/modify?id={:id}&content={:content}&completed={:completed}&deadline={:deadline}`
  - `GET http://localhost:123456/todos/delete?id={:id}`

- **TODO**
  - Set up an environment kotlin-multiplatform
  - Android,iOS,Desktop Client ...
    - change Server IP adress to Global IP or Private Local IP.
    - `kotlin/composeApp/src/commonMain/kotlin/Api.kt`
      - NOT use loopback(0.0.0.0, localhost, 127.0.0.1)
  - JVM,Native Server ...
    - connect internal docker server, use hostname as "db" and port 5432 to connect Database.
    - connect from outer host pc, use any IP address and port 5432 to connect Database.
    - `kotlin/server/src/jvmMain/kotlin/jp/lowput/todo_api_sample/staging/Application.jvm.kt`
    - `kotlin/server/src/nativeMain/kotlin/jp/lowput/todo_api_sample/staging/Application.native.kt`

- **RUN:**
  - Android: AndroidStudio -> Run
  - iOS: AndroidStudio -> Run
  - Desktop:
    - Run
      - `./gradlew :composeApp:run`
  - Server(JVM):
    - Build
      - `./gradlew :server:assemble`
    - Run/Debug
      - AndroidStudio -> Run (Configuration: MainClass: jp.lowput.todo_api_sample.staging.Application_jvmKt)
    - Run
      - AndroidStudio -> Make Project
        - `java -jar kotlin/server/build/libs/server-all.jar`
  - Server(Native):
    - Build
      - `./gradlew :server:assemble`
    - Run
      - `bash -c ./server/build/bin/native/releaseExecutable/server.kexe`
  - Docker: `docker-compose up -d --build`
    - Defaults: running JVM Server