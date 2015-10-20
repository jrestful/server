package org.jrestful.data.documents.support;


/**
 * Generic abstract class for a sequenced document.
 */
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
