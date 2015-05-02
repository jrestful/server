package org.jrestful.data.documents.support.sequence;

import org.jrestful.data.documents.support.GenericDocument;

public abstract class GenericSequencedDocument extends GenericDocument {

  private static final long serialVersionUID = 1L;

  private Long sequence;

  public final Long getSequence() {
    return sequence;
  }

  public final void setSequence(Long sequence) {
    this.sequence = sequence;
  }

}
