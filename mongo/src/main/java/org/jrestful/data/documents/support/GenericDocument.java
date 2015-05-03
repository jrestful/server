package org.jrestful.data.documents.support;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class GenericDocument implements Persistable<String> {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Override
  public boolean equals(Object obj) {
    if (id == null) {
      return false;
    } else if (this == obj) {
      return true;
    } else if (!getClass().isInstance(obj)) {
      return false;
    } else {
      return id.equals(((GenericDocument) obj).id);
    }
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] { id });
  }

  @Override
  public final String getId() {
    return id;
  }

  public final void setId(String id) {
    this.id = id;
  }

  @JsonIgnore
  @Override
  public final boolean isNew() {
    return id == null;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "@" + getId();
  }

}
