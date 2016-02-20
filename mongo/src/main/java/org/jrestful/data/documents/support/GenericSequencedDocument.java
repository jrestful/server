package org.jrestful.data.documents.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic abstract class for a sequenced document.
 */
public abstract class GenericSequencedDocument extends GenericDocument {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  private Long sequence;

  @JsonProperty
  public Long getSequence() {
    return sequence;
  }

  @JsonIgnore
  public void setSequence(Long sequence) {
    this.sequence = sequence;
  }

}
