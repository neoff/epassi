# Getting Started

It is test technical task for the company [Epassi](https://www.epassi.fi/)
### Run
 * DockerHub image: [varg/epassi](https://hub.docker.com/r/varg/epassi)
 * Application work on port `8080` by default
```shell
docker run -p 8080:8080 -v /target/test-classes:/app/target varg/epassi
```
 * By default application has a `book.txt` file in `/app/target` directory
 * To run with a custom file(s) you need to mount the volume (or file) to the container in the `/app/target` directory
```shell
docker run -p 8080:8080 -v /path/to/uploaded/file.txt:/app/target/file.txt varg/epassi
```
or
```shell
docker run -p 8080:8080 -v /path/to/files/folder:/app/target varg/epassi
```
### API
 * The Swagger UI page will then be available at 
[http://server:port/swagger-ui.html](http://localhost:8080/swagger-ui.html) 
and the OpenAPI description will be available at the following url for json format: [http://server:port/v3/api-docs](http://localhost:8080/v3/api-docs)
   * server: The server name or IP
   * port: The server port
   * context-path: The context path of the application

#### Request examples:
##### Search in existing file
```shell
curl --location 'http://127.0.0.1:8080/api/freqency?file=.%2Ftarget%2Ftest-classes%2Fbook.txt&word=Romeo' \
--header 'Accept: application/json' \
--header 'Content-Type: text/plain'
```
##### Upload file and search
```shell
curl -X POST -F "file=@src/test/resources/small.txt" http://localhost:8080/api/freqency\?word\=doom
```
### Builds

#### Prerequisites
* Java 17 +
* Maven 3.9.5

#### Build and run
```shell
./mvnw clean package
java -jar target/epassi-0.0.1-SNAPSHOT.jar
```

##### Build with docker
```shell
docker build -t test-app .
docker run -p 8080:8080 test-app
```

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.2/maven-plugin/reference/html/)
* [OpenAPI Generator](https://openapi-generator.tech/)
* [OpenAPI Description](https://learn.openapis.org)
* [Docker documentation](https://docs.docker.com)

