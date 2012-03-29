package com.tempodb.client2;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tempodb.models.DataSet;
import com.tempodb.models.Series;


public class Client {

    private String key;
    private String secret;
    private String host;
    private int port;
    private boolean secure;

    private DefaultHttpClient client = null;
    private HttpHost _targetHost = null;
    private BasicHttpContext _context = null;
    private ObjectMapper _mapper = null;
    private final DateTimeFormatter iso8601 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /** How often the monitoring thread checks for connections to close. */
    private static final int DEFAULT_TIMEOUT_MILLIS = 30000; // 30 seconds

    private static final String API_VERSION = "v1";

    public Client(String key, String secret, String host, int port, boolean secure) {
        this.key = key;
        this.secret = secret;
        this.host = host;
        this.port = port;
        this.secure = secure;
    }

    public List<Series> getSeries() throws Exception {
        String json = request("/series/");
        ObjectMapper mapper = getMapper();

        ArrayList<Series> result = mapper.readValue(json, new TypeReference<ArrayList<Series>>() {});
        return result;
    }

    public DataSet readId(String seriesId, DateTime start, DateTime end) throws Exception {
        return readId(seriesId, start, end, null, null);
    }

    public DataSet readId(String seriesId, DateTime start, DateTime end, String interval) throws Exception {
        return readId(seriesId, start, end, interval, null);
    }

    public DataSet readId(String seriesId, DateTime start, DateTime end, String interval, String function) throws Exception {
        return read("id", seriesId, start, end, interval, function);
    }


    public DataSet readKey(String seriesKey, DateTime start, DateTime end) throws Exception {
        return readKey(seriesKey, start, end, null, null);
    }

    public DataSet readKey(String seriesKey, DateTime start, DateTime end, String interval) throws Exception {
        return readKey(seriesKey, start, end, interval, null);
    }

    public DataSet readKey(String seriesKey, DateTime start, DateTime end, String interval, String function) throws Exception {
        return read("key", seriesKey, start, end, interval, function);
    }

    public DataSet read(String seriesType, String seriesValue, DateTime start, DateTime end, String interval, String function) throws Exception {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start", start.toString(iso8601)));
        params.add(new BasicNameValuePair("end", end.toString(iso8601)));

        if (interval != null)
            params.add(new BasicNameValuePair("interval", interval));

        if (function != null)
            params.add(new BasicNameValuePair("function", function));

        String qsParams = URLEncodedUtils.format(params, "UTF-8");

        String url = String.format("/series/%s/%s/data/?%s", seriesType, seriesValue, qsParams);
        String json = request(url);

        ObjectMapper mapper = getMapper();
        DataSet dataset = mapper.readValue(json, DataSet.class);
        return dataset;
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
        String uri = protocol + host + portString + "/" + API_VERSION + url;

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
        HttpClient client = getHttpClient();

        HttpHost targetHost = getTargetHost();
        BasicHttpContext context = getContext();

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = client.execute(targetHost, uri, responseHandler, context);
        return responseBody;
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

    private synchronized ObjectMapper getMapper() {
        if (_mapper == null) {
            _mapper = new ObjectMapper();
            _mapper.registerModule(new JodaModule());
        }
        return _mapper;
    }
}
