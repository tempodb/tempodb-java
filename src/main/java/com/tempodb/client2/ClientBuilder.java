package com.tempodb.client2;


public class ClientBuilder {

    private String _key;
    private String _secret;
    private String _host = "api.tempo-db.com";
    private int _port = 443;
    private boolean _secure = true;

    public ClientBuilder() { }

    public Client build() {
        return new Client(_key, _secret, _host, _port, _secure);
    }

    public ClientBuilder key(String _key) {
        this._key = _key;
        return this;
    }

    public ClientBuilder secret(String _secret) {
        this._secret = _secret;
        return this;
    }

    public ClientBuilder host(String _host) {
        this._host = _host;
        return this;
    }

    public ClientBuilder port(int _port) {
        this._port = _port;
        return this;
    }

    public ClientBuilder secure(boolean _secure) {
        this._secure = _secure;
        return this;
    }
}
