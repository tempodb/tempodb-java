package com.tempodb.client2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;


public class Client {

    private String key;
    private String secret;
    private String host;
    private int port;
    private boolean secure;

    private DefaultHttpClient client = null;
    private HttpHost _targetHost = null;
    private BasicHttpContext _context = null;
    private static final int DEFAULT_TIMEOUT_MILLIS = 30000; // 30 seconds

    public Client(String key, String secret, String host, int port, boolean secure) {
        this.key = key;
        this.secret = secret;
        this.host = host;
        this.port = port;
        this.secure = secure;
    }

    public String request(String url) throws Exception {
        return request(url, HttpMethod.GET, "");
    }

    public String request(String url, HttpMethod method) throws Exception {
        return request(url, method, "");
    }

    public String request(String url, HttpMethod method, String body) throws Exception {
        String protocol = secure ? "https://" : "http://";
        String portString = (port == 80) ? "" : ":" + port;
        String uri = protocol + host + portString + url;

        String rv = "";
        switch (method) {
            case POST:
                HttpPost post = new HttpPost(uri);
                post.setEntity(new StringEntity(body));
                rv = execute(post);
                break;

            case PUT:
                HttpPut put = new HttpPut(uri);
                put.setEntity(new StringEntity(body));
                rv = execute(put);
                break;

            case GET:
            default:  // Drop down by design
                HttpGet get = new HttpGet(uri);
                rv = execute(get);
                break;
        }

        return rv;
    }

    private String execute(HttpUriRequest uri) throws Exception {
        String responseBody = "";
        HttpClient client = getHttpClient();

        HttpHost targetHost = getTargetHost();
        BasicHttpContext context = getContext();

        HttpResponse response = client.execute(targetHost, uri, context);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                instream = new GZIPInputStream(instream);
            }

            // convert content stream to a String
            responseBody = convertStreamToString(instream);
            instream.close();
        }
        return responseBody;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private synchronized HttpClient getHttpClient() {
        if (client == null) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
            HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            HttpProtocolParams.setUserAgent(httpParams, "TempoDB Java Client");

            client = new DefaultHttpClient(new ThreadSafeClientConnManager(), httpParams);

            client.getCredentialsProvider().setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(key, secret));
        }
        return client;
    }

    private synchronized HttpHost getTargetHost() {
        if (_targetHost == null) {
            _targetHost = new HttpHost(host, port, secure ? "https" : "http");
        }
        return _targetHost;
    }

    private synchronized BasicHttpContext getContext() {
        if (_context == null) {
            HttpHost targetHost = getTargetHost();

            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            _context = new BasicHttpContext();
            _context.setAttribute(ClientContext.AUTH_CACHE, authCache);
        }
        return _context;
    }
}
