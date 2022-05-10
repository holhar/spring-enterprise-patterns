# Spring Data Elasticsearch Tutorial

## Source

https://www.baeldung.com/spring-data-elasticsearch-tutorial

## Example requests:

Create index initially:

    curl -X POST "http://localhost:8080/index/create"

Search article by word in title:

    curl -X GET "http://localhost:8080/article/search/data"

Search article by given author:

    curl -X POST "http://localhost:8080/articles/search" --data '{"author": "John Smith"}' -H"Content-Type: application/json"

Update article title:

    curl -X PUT "http://localhost:8080/articles/article" --data '{"oldTitle": "Spring Data Elastic", "newTitle": "Spring Boot Data Elasticsearch"}' -H"Content-Type: application/json"

Delete article, first found by word in title:

    curl -X DELETE "http://localhost:8080/articles/article/data"
