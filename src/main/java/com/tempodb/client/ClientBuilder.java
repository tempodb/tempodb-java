package com.tempodb.client;


public class ClientBuilder {

    private String key;
    private String secret;
    private String host = "api.tempo-db.com";
    private int port = 443;
    private boolean secure = true;

    public ClientBuilder() { }

    public Client build() {
        return new Client(key, secret, host, port, secure);
    }

    public ClientBuilder key(String key) {
        this.key = key;
        return this;
    }

    public ClientBuilder secret(String secret) {
        this.secret = secret;
        return this;
    }

    public ClientBuilder host(String host) {
        this.host = host;
        return this;
    }

    public ClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ClientBuilder secure(boolean secure) {
        this.secure = secure;
        return this;
    }
}
