package de.holhar.spring.patterns.elasticsearch.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "blog", type = "article")
public class Article {

  @Id
  private String id;

  private String title;

  @Field(type = FieldType.Nested, includeInParent = true)
  private List<Author> authors;

  public Article() {
  }

  public Article(String title) {
    this.title = title;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Author> getAuthors() {
    return authors;
  }

  public void setAuthors(List<Author> authors) {
    this.authors = authors;
  }

  @Override
  public String toString() {
    return "Article{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", authors=" + authors +
        '}';
  }
}
