# Heavy Mock HTTP Server

![](https://img.shields.io/badge/Status-Under%20Development-red)
![](https://img.shields.io/badge/Version-0.0.0-blue)
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

### 🛠️ Configuration

The server uses a configuration file to define the routes and the responses that it should return. The configuration
file is a YAML file that contains the following fields:

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

**Notes**: See the [examples](./examples) directory for more information

### 🐳 Docker Compose

*The following example uses the configuration file `basic-example.yml` from the [examples](./examples) directory,
if you want to use another configuration file, you need to change the `HEAVY_MOCK_CONFIG` environment variable
and make sure that the file is in the `/configs` directory bound to the container*

```yaml
version: '3.8'

services:
  heavy-mock-http-server:
    image: heavynimbus/heavy-mock-http-server:0.0.0
    ports:
      - "8080:80"
    environment:
      - HEAVY_MOCK_CONFIG=/configs/single-endpoint.yml # The configuration file that the server should use
    volumes:
      - type: bind
        source: ./examples
        target: /configs
        read_only: true
```
