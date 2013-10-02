package com.tempodb;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.tempodb.util.Preconditions.*;


/**
 *  A Database representation.
 *
 *  @since 1.0.0
 */
public class Database implements Serializable {
  private String id;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public Database() {
    this("");
  }

  public Database(String id) {
    this.id = checkNotNull(id);
  }

  /**
   *  Returns the id for the Database.
   *  @return Database id..
   *  @since 1.0.0
   */
  public String getId() { return id; }

  /**
   *  Sets the id for the Database.
   *  @param id Database id..
   *  @since 1.0.0
   */
  public void setId(String id) { this.id = checkNotNull(id); }

  @Override
  public String toString() {
    return String.format("Database(id=%s)", id);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(241, 251)
      .append(id)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Database)) return false;

    Database rhs = (Database)obj;
    return new EqualsBuilder()
      .append(id, rhs.id)
      .isEquals();
  }
}
