package org.jrestful.tests.components.article;

import org.jrestful.data.documents.support.GenericSequencedDocument;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "articles")
public class Article extends GenericSequencedDocument {

  private static final long serialVersionUID = 1L;

  private String title;

  public Article() {
    // no-op
  }

  public Article(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
