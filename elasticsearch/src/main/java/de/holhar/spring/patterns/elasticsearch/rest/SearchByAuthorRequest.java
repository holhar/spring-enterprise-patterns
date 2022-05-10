package de.holhar.spring.patterns.elasticsearch.rest;

public class SearchByAuthorRequest {

  private String author;

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }
}
