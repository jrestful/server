package org.jrestful.data.documents;

import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Auto-incremented sequence.
 */
@Document(collection = "sequences")
public final class Sequence extends GenericDocument {

  private static final long serialVersionUID = 1L;

  private Long value;

  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
    this.value = value;
  }

}
