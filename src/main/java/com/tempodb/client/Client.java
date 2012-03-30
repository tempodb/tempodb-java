package com.tempodb.client;

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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tempodb.models.BulkDataSet;
import com.tempodb.models.DataPoint;
import com.tempodb.models.DataSet;
import com.tempodb.models.Filter;
import com.tempodb.models.Series;


public class Client {

    private String key;
    private String secret;
    private String host;
    private int port;
    private boolean secure;

    private DefaultHttpClient _client = null;
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
        return getSeries(new Filter());
    }

    public List<Series> getSeries(Filter filter) throws Exception {
        String filterString = URLEncodedUtils.format(filter.getParams(), "UTF-8");
        String json = request(String.format("/series/?%s", filterString));
        ObjectMapper mapper = getMapper();

        ArrayList<Series> result = mapper.readValue(json, new TypeReference<ArrayList<Series>>() {});
        return result;
    }

    public Series updateSeries(Series series) throws Exception {
        String url = String.format("/series/id/%s/", series.getId());

        ObjectMapper mapper = getMapper();
        String json = mapper.writeValueAsString(series);

        String response = request(url, HttpMethod.PUT, json);
        return mapper.readValue(response, Series.class);
    }

    public List<DataSet> read(DateTime start, DateTime end, Filter filter) throws Exception {
        return read(start, end, filter, null, null);
    }

    public List<DataSet> read(DateTime start, DateTime end, Filter filter, String interval) throws Exception {
        return read(start, end, filter, interval, null);
    }

    public List<DataSet> read(DateTime start, DateTime end, Filter filter, String interval, String function) throws Exception {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start", start.toString(iso8601)));
        params.add(new BasicNameValuePair("end", end.toString(iso8601)));

        if (filter != null)
            params.addAll(filter.getParams());

        if (interval != null)
            params.add(new BasicNameValuePair("interval", interval));

        if (function != null)
            params.add(new BasicNameValuePair("function", function));

        String qsParams = URLEncodedUtils.format(params, "UTF-8");
        String url = String.format("/data/?%s", qsParams);
        String json = request(url);

        ObjectMapper mapper = getMapper();
        List<DataSet> datasets = mapper.readValue(json, new TypeReference<ArrayList<DataSet>>() {});
        return datasets;
    }

    public DataSet readId(String seriesId, DateTime start, DateTime end) throws Exception {
        return readId(seriesId, start, end, null, null);
    }

    public DataSet readId(String seriesId, DateTime start, DateTime end, String interval) throws Exception {
        return readId(seriesId, start, end, interval, null);
    }

    public DataSet readId(String seriesId, DateTime start, DateTime end, String interval, String function) throws Exception {
        return readOne("id", seriesId, start, end, interval, function);
    }


    public DataSet readKey(String seriesKey, DateTime start, DateTime end) throws Exception {
        return readKey(seriesKey, start, end, null, null);
    }

    public DataSet readKey(String seriesKey, DateTime start, DateTime end, String interval) throws Exception {
        return readKey(seriesKey, start, end, interval, null);
    }

    public DataSet readKey(String seriesKey, DateTime start, DateTime end, String interval, String function) throws Exception {
        return readOne("key", seriesKey, start, end, interval, function);
    }

    public List<DataPoint> writeId(String seriesId, List<DataPoint> data) throws Exception {
        return write("id", seriesId, data);
    }

    public List<DataPoint> writeKey(String seriesKey, List<DataPoint> data) throws Exception {
        return write("key", seriesKey, data);
    }

    public List<DataPoint> write(String seriesType, String seriesValue, List<DataPoint> data) throws Exception {
        String url = String.format("/series/%s/%s/data/", seriesType, seriesValue);

        ObjectMapper mapper = getMapper();
        String json = mapper.writeValueAsString(data);

        request(url, HttpMethod.POST, json);
        return data;
    }

    public void bulkWrite(BulkDataSet dataset) throws Exception {
        String url = "/data/";

        ObjectMapper mapper = getMapper();
        String json = mapper.writeValueAsString(dataset);

        request(url, HttpMethod.POST, json);
    }

    private DataSet readOne(String seriesType, String seriesValue, DateTime start, DateTime end, String interval, String function) throws Exception {
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
        if (_client == null) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
            HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            HttpProtocolParams.setUserAgent(httpParams, "TempoDB Java Client");

            _client = new DefaultHttpClient(new ThreadSafeClientConnManager(), httpParams);

            _client.getCredentialsProvider().setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(key, secret));
        }
        return _client;
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
            _mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
        return _mapper;
    }
}
