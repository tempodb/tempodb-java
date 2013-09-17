package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Represents a summary of the deletion operation
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DeleteSummary {
  private final int deleted;

  @JsonCreator
  public DeleteSummary(@JsonProperty("deleted") int deleted) {
    this.deleted = deleted;
  }

  public int getDeleted() { return deleted; }
}
