package com.tempodb.client;

/**
 *  A builder object for a Client instance.
 *  <p>
 *  Typical use:
 *  <p>
 *  <pre>
 *  {@code
 *  Client client = new ClientBuilder()
 *      .key("your-api-key")
 *      .secret("your-api-secret")
 *      .host("api.tempo-db.com")
 *      .port(80)
 *      .secure(true)
 *      .build();
 *  }
 *  </pre>
 */
public class ClientBuilder {

    private String key;
    private String secret;
    private String host = "api.tempo-db.com";
    private int port = 443;
    private boolean secure = true;

    public ClientBuilder() { }

    /**
     *  Returns the built Client instance
     *
     *  @return A new Client instance
     */
    public Client build() {
        return new Client(key, secret, host, port, secure);
    }

    /**
     *  Sets the api key for the client instance.
     *
     *  @param key The api key for the database being accessed
     */
    public ClientBuilder key(String key) {
        this.key = key;
        return this;
    }

    /**
     *  Sets the api secret for the client instance.
     *
     *  @param secret The api secret for the database being accessed
     */
    public ClientBuilder secret(String secret) {
        this.secret = secret;
        return this;
    }

    /**
     *  Sets the api host for the client instance.
     *
     *  @param host The hostname of the server being accessed
     */
    public ClientBuilder host(String host) {
        this.host = host;
        return this;
    }

    /**
     *  Sets the api port for the client instance.
     *
     *  @param port The port of the server being accessed
     */
    public ClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     *  Sets the protocol being used.
     *
     *  @param secure true = https, false = http
     */
    public ClientBuilder secure(boolean secure) {
        this.secure = secure;
        return this;
    }
}
