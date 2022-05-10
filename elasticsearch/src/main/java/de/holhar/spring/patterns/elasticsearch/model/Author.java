package de.holhar.spring.patterns.elasticsearch.model;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Author {

  @Field(type = FieldType.Text)
  private String name;

  public Author() {
  }

  public Author(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
