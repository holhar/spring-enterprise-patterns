package de.holhar.spring.patterns.elasticsearch.rest;

public class UpdateDocumentRequest {

  private String oldTitle;
  private String newTitle;

  public String getOldTitle() {
    return oldTitle;
  }

  public void setOldTitle(String oldTitle) {
    this.oldTitle = oldTitle;
  }

  public String getNewTitle() {
    return newTitle;
  }

  public void setNewTitle(String newTitle) {
    this.newTitle = newTitle;
  }
}
