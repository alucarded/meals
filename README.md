## Meals
REST API for application used to track meals of a user and their calories

## Motivation
This is a showcase project.

## Tech/framework used
* Java 8
* Spring Boot
* MongoDB

<b>Built with</b>
- Maven

## Features
* [custom query filter parser with field type resolution from provided class](https://github.com/alucarded/meals/tree/master/src/main/java/com/devpeer/calories/core/query)
* [https://github.com/alucarded/meals/blob/master/src/main/java/com/devpeer/calories/meal/MealsRestController.java#L76](filtering and pagination for endpoints returning multiple elements)

## Installation
```sh
mvn install
```

## Documentation

Documentation is auto-generated upon installation using Spring REST Docs and Asciidoctor in **target/generated-docs/index.html**


## Tests
```sh
mvn compile test
```
