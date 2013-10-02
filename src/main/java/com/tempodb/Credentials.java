package com.tempodb;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.tempodb.util.Preconditions.*;


/**
 * Api Credentials
 *
 *  @since 1.0.0
 */
public class Credentials implements Serializable {
  private String key;
  private String secret;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public Credentials() {
    this("", "");
  }

  public Credentials(String key, String secret) {
    this.key = checkNotNull(key);
    this.secret = checkNotNull(secret);
  }

  /**
   *  Returns the key for these Credentials.
   *  @return Credentials key.
   *  @since 1.0.0
   */
  public String getKey() { return key; }

  /**
   *  Sets the key for these Credentials.
   *  @param key Credentials key.
   *  @since 1.0.0
   */
  public void setKey(String key) { this.key = checkNotNull(key); }

  /**
   *  Returns the secret for these Credentials.
   *  @return Credentials secret.
   *  @since 1.0.0
   */
  public String getSecret() { return secret; }

  /**
   *  Sets the secret for these Credentials.
   *  @param secret Credentials secret.
   *  @since 1.0.0
   */
  public void setSecret(String secret) { this.secret = checkNotNull(secret); }

  @Override
  public String toString() {
    return String.format("Credentials(key=%s, secret=%s)", key, secret);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(191, 193)
      .append(key)
      .append(secret)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Credentials)) return false;

    Credentials rhs = (Credentials)obj;
    return new EqualsBuilder()
      .append(key, rhs.key)
      .append(secret, rhs.secret)
      .isEquals();
  }
}
