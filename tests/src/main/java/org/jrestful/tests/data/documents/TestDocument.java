package org.jrestful.tests.data.documents;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "testSequencedDocuments")
public class TestDocument extends GenericSequencedDocument {

  private static final long serialVersionUID = 1L;

  private String label;

  public TestDocument() {
    // no-op
  }

  public TestDocument(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

}
