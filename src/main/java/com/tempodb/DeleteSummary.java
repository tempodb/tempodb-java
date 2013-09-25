package com.tempodb;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.tempodb.json.Json;
import static com.tempodb.util.Preconditions.*;


/**
 *  Information about a delete series operation.
 *  @since 1.0.0
 */
public class DeleteSummary implements Serializable {

  private int deleted;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public DeleteSummary() {
    this(0);
  }

  /**
   *  Base constructor.
   *  @param deleted The number of Series deleted.
   *  @see Series
   *  @since 1.0.0
   */
  public DeleteSummary(int deleted) {
    this.deleted = deleted;
  }

  public DeleteSummary(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    DeleteSummary summary = Json.loads(body, DeleteSummary.class);

    this.deleted = checkNotNull(summary.deleted);
  }

  /**
   *  Returns the number of Series deleted.
   *  @return Number of Series deleted.
   *  @see Series
   *  @since 1.0.0
   */
  public int getDeleted() { return deleted; }

  /**
   *  Sets the number of Series deleted.
   *  @param deleted The number of Series deleted.
   *  @see Series
   *  @since 1.0.0
   */
  public void setDeleted(int deleted) { this.deleted = deleted; }

  @Override
  public String toString() {
    return String.format("DeleteSummary(deleted=%d)", deleted);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(179, 181)
      .append(deleted)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof DeleteSummary)) return false;

    DeleteSummary rhs = (DeleteSummary)obj;
    return new EqualsBuilder()
      .append(deleted, rhs.deleted)
      .isEquals();
  }
}
