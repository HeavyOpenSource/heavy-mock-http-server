# Heavy Mock HTTP Server

![](https://img.shields.io/badge/Status-Under%20Development-red)
![](https://img.shields.io/badge/Version-2.0.0-blue)
[![](https://img.shields.io/badge/License-GPL--3.0-blue)](./LICENSE.md)

![](https://img.shields.io/badge/Docker-1D63ED?logo=Docker)
![](https://img.shields.io/badge/SpringBoot-v3.3.0-6DB33F?logo=Spring)
![](https://img.shields.io/badge/Spring-v6.1.6-6DB33F?logo=Spring)
![](https://img.shields.io/badge/Java-v21.0.3-007396?logo=Oracle)

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
        <string>:
          - <string>

      body: # Only one field of body can be used
        # You can use the | character to write a multiline string
        string: <string>
        file: <string>
```