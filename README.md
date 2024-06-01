# README

Rails API sample

## build and start (develop)

```sh
docker-compose up -d
```

You can access the API server to `http://localhost:3000`

## Endpoints

### 1. Get All Tasks

- **URL:** `/api/todos`
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

- **URL:** `/api/todos`
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

- **URL:** `/api/todos/:id`
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
