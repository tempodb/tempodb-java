package com.tempodb;

import java.net.InetSocketAddress;

import static com.tempodb.util.Preconditions.*;

/**
 *  A helper class to create Client objects.
 *
 *  <p>The database and credentials must be specified. All other parameters are optional.
 *  <p>The following default values are used:
 *  <ul>
 *    <li>host - new InetSocketAddress("api.tempo-db.com", 443)</li>
 *    <li>scheme - "https"</li>
 *  </ul>
 *  <p>This class uses the fluent style. A client can be created by chaining calls:
 *  <pre>
 *    import java.net.InetSocketAddress;
 *
 *    Database database = new Database("id");
 *    Credentials credentials = new Credentials("api-key", "api-secret");
 *    InetSocketAddress host = new InetSocketAddress("api.tempo-db.com", 443);
 *
 *    Client client = new ClientBuilder()
 *                      .database(database)
 *                      .credentials(credentials)
 *                      .host(host)
 *                      .scheme("https")
 *                      .build();
 *  </pre>
 *
 *  @since 1.0.0
 */
public class ClientBuilder {

  private Database database;
  private Credentials credentials;
  private InetSocketAddress host;
  private String scheme;

  private static final InetSocketAddress DEFAULT_HOST = new InetSocketAddress("api.tempo-db.com", 443);
  private static final String DEFAULT_SCHEME = "https";

  /**
   *  Base constructor.
   *  @since 1.0.0
   */
  public ClientBuilder() {
    this.database = null;
    this.credentials = null;
    this.host = DEFAULT_HOST;
    this.scheme = DEFAULT_SCHEME;
  }

  /**
   *  Sets the database to connect to. This is required.
   *  @param database Database to connect to
   *  @since 1.0.0
   */
  public ClientBuilder database(Database database) {
    this.database = checkNotNull(database);
    return this;
  }

  /**
   *  Sets the credentials to use for the specified database.
   *  This is required.
   *  @param credentials Database credentials
   *  @since 1.0.0
   */
  public ClientBuilder credentials(Credentials credentials) {
    this.credentials = checkNotNull(credentials);
    return this;
  }

  /**
   *  Sets the host to connect to. Defaults to <tt>api.tempo-db.com:443</tt>
   *  @param host Host to connect to.
   *  @since 1.0.0
   */
  public ClientBuilder host(InetSocketAddress host) {
    this.host = checkNotNull(host);
    return this;
  }

  /**
   *  Sets the scheme. Valid values are "http" and "https". Defaults to
   *  "https".
   *  @param scheme The scheme.
   *  @since 1.0.0
   */
  public ClientBuilder scheme(String scheme) {
    this.scheme = checkNotNull(scheme);
    return this;
  }

  /**
   *  Creates the client object using the specified parameters.
   *  @return The build client
   *  @since 1.0.0
   */
  public Client build() {
    validate();
    Client client = new Client(database, credentials, host, scheme);
    return client;
  }

  private void validate() {
    checkNotNull(database, "Database must not be null.");
    checkNotNull(credentials, "Credentials must not be null.");
  }
}
