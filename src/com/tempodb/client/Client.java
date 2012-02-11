package com.tempodb.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONArray;

public class Client {
	
	private String baseAPIURL = "api.tempo-db.com";
	private String APIVersion = "v1";
	private HttpHost targetHost = new HttpHost(baseAPIURL, 443, "https");
	private DefaultHttpClient httpclient = new DefaultHttpClient();
	private AuthCache authCache = new BasicAuthCache();
	private BasicHttpContext localcontext = new BasicHttpContext();
	private SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public Client(String apiKey, String apiSecret) {
		httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials(apiKey, apiSecret));
		
		// Generate BASIC scheme object and add it to the local
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        
        // Add AuthCache to the execution context
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
	}
	
    public static void main(String[] args) throws Exception {
    	Client client = new Client("myagley", "opensesame");

    	Date start = client.iso8601.parse("2011-01-03T13:42:00+0000");
    	Date end = client.iso8601.parse("2011-01-04T13:42:00+0000");
    	//JSONArray read_id = client.read_id("your-series-id", start, end);
    	JSONArray read_key = client.read_key("your-series-key", start, end);
    	System.out.println(read_key);
    	
    	/*
    	JSONArray series = client.getSeries();

    	for (int i = 0; i < series.length(); i++) {
    		try {
    			JSONObject s = series.getJSONObject(i);
    			System.out.println( "id: " + s.getString("id") );
    			System.out.println( "key: " + s.getString("key") );
    			System.out.println( "tags: " + s.getJSONArray("tags") );
    			System.out.println( "attributes: " + s.getJSONObject("attributes") );
    			System.out.println();
    		}
    		catch (JSONException e){}
		}
		*/
    }
    
    public JSONArray getSeries() throws Exception {
    	HttpGet httpget = new HttpGet("/"+this.APIVersion+"/series/");
    	String resultString = request(httpget);
    	
    	return new JSONArray(resultString);
    }

    public JSONArray read_id(String seriesID, Date start, Date end) throws Exception {
    	return read_id(seriesID, start, end, null, null);
    }
    
    public JSONArray read_id(String seriesID, Date start, Date end, String interval) throws Exception {
    	return read_id(seriesID, start, end, interval, null);
    }
    
    public JSONArray read_id(String seriesID, Date start, Date end, String interval, String function) throws Exception {
    	return read("id", seriesID, start, end, interval, function);
    }
    
    
    public JSONArray read_key(String seriesKey, Date start, Date end) throws Exception {
    	return read_key(seriesKey, start, end, null, null);
    }
    
    public JSONArray read_key(String seriesKey, Date start, Date end, String interval) throws Exception {
    	return read_key(seriesKey, start, end, interval, null);
    }
    
    public JSONArray read_key(String seriesKey, Date start, Date end, String interval, String function) throws Exception {
    	return read("key", seriesKey, start, end, interval, function);
    }
    
    
    public JSONArray read(String seriesType, String seriesValue, Date start, Date end, String interval, String function) throws Exception {
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("start", iso8601.format(start)));
        params.add(new BasicNameValuePair("end", iso8601.format(end)));
        params.add(new BasicNameValuePair("interval", interval));
        params.add(new BasicNameValuePair("function", function));

        String qsParams = URLEncodedUtils.format(params, "UTF-8");

        HttpGet httpget = new HttpGet("/"+this.APIVersion+"/series/"+ seriesType +"/" + seriesValue + "/data/?"+qsParams);
        
        String resultString = request(httpget);
    	return new JSONArray(resultString);
    }

    public String request(HttpUriRequest uriRequest) throws Exception {
    	uriRequest.addHeader("Content-Type", "application/json");

    	String resultString = "";

    	try {
            //System.out.println("executing request: " + httpget.getRequestLine());
            //System.out.println("to target: " + targetHost);

            HttpResponse response = this.httpclient.execute(this.targetHost, uriRequest, this.localcontext);
            HttpEntity entity = response.getEntity();

            /*
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
            }
            */

            InputStream instream = entity.getContent();
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            	instream = new GZIPInputStream(instream);
            }
            
            // convert content stream to a String
            resultString= convertStreamToString(instream);
            instream.close();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

    	return resultString;
    }

    private static String convertStreamToString(InputStream is) {
	/*
	 * To convert the InputStream to String we use the BufferedReader.readLine()
	 * method. We iterate until the BufferedReader return null which means
	 * there's no more data to read. Each line will appended to a StringBuilder
	 * and returned as String.
	 * 
	 * (c) public domain: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
	 */
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
}