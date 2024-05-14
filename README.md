# Heavy Mock HTTP Server

![](https://img.shields.io/badge/Status-Under%20Development-red)
![](https://img.shields.io/badge/Version-0.0.0-blue)
![](https://img.shields.io/badge/License-MIT-blue)

![](https://img.shields.io/badge/Docker-1D63ED?logo=Docker)
![](https://img.shields.io/badge/SpringBoot-v3.2.5-6DB33F?logo=Spring)
![](https://img.shields.io/badge/Spring-v6.1.6-6DB33F?logo=Spring)
![](https://img.shields.io/badge/Java-21-007396?logo=OpenJDK)

## 📝 Description

This project is a mock HTTP server that can be used to simulate different HTTP responses. Let me list you some use
cases:

* You are developing a client application that consumes a server that is not ready yet
* You are developing a client application that consumes an external service that is too expensive to call during
  development and testing
* You are developing a client application that consumes an external service that is not reliable

## 🚀 Getting Started

### 🐳 Docker

```yaml
version: '3.8'

services:
  heavy-mock-http-server:
    image: heavynimbus/mock-http-server:0.0.0
    ports:
      - "8080:80"
    environment:
      - HEAVY_MOCK_CONFIG=/configs/basic-example.yml
    volumes:
      - ./examples:/configs
```

**Notes**:

* Volumes are used to mount the configuration files to the container, so you can change the configuration without
  rebuilding the image
* The `HEAVY_MOCK_CONFIG` environment variable is used to specify the configuration file that the server should use
* The configuration file is a YAML file that contains the routes and the responses that the server should return,
  see the [configuration](#-configuration) section for more information or the [examples](./examples) directory

## 🛠️ Configuration


*Definition*:
```yaml
endpoints:
  - name: <string>
    request:
      methods:
        - GET | POST | PUT | DELETE | PATCH | OPTIONS | HEAD
      paths:
        - <regex>
      headers:
        <string>: <string>
        
    response:
      delay: <integer> # In milliseconds
      status: <HTTP_STATUS> # OK | CREATED | BAD_REQUEST | ...
      headers:
        <string1>:
          - <string>
      
      body: # Only one field of body can be used
        # You can use the | character to write a multiline string
        string: <string>
        file: <string>

    callbacks:
      - delay: <integer> # In milliseconds
        async: true | false
        redirect: NEVER | ALWAYS | NORMAL # Default is NORMAL
        version: HTTP_1_1 | HTTP_2 # Default is HTTP_1_1
        connectTimeout: <duration> # Default is 10s (PT10S)
        proxy:
          host: <string>
          port: <integer>
        
        url: <string>
        method: GET | POST | PUT | DELETE | PATCH | OPTIONS | HEAD # Default is GET
        headers:
          <string>: <string>
        body:
          string: <string>
          file: <string>
        
```


*Simple Example*:
```yaml
# Your HEAVY_MOCK_CONFIG file

# Declaration of endpoints
endpoints:
  # First endpoint
  - name: Get Hello Heavy
    # If the request matches the following criteria, the response will be returned
    request:
      methods:
        - GET
      paths:
        - /hello/heavy

    # The response to be returned
    response:
      status: OK
      headers:
        "Content-Type":
          - application/json
      body: |
        {
        "message": "Hello, Heavy!"
        }

  - name: Get Hello for any other path
    # If the request matches the following criteria, the response will be returned
    request:
      methods:
        - GET
      paths:
        - /hello/.* # This is a regular expression

    # The response to be returned
    response:
      status: OK
      headers:
        "Content-Type":
          - application/json
      body: |
        {
          "message": "Hello, World!"
        }

  - name: 404 Not Found
    # If the request matches the following criteria, the response will be returned
    request:
      methods:
        - GET
      paths:
        - /not-found

    # The response to be returned
    response:
      status: NOT_FOUND
      headers:
        "Content-Type":
          - application/json
      body: |
        {
          "message": "Not Found"
        }
```
