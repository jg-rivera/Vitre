package local.vitre.desktop.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.dropbox.core.util.StringUtil;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.patch.file.PatchFile.PatchType;
import local.vitre.desktop.ui.ExpandableAlert;

public class NetworkManager {

	// Network fields
	public static final String HOST_URL = Vitre.assets.getEntryValue("HOST_URL");
	public static final String API_SYNC_URL = HOST_URL + Vitre.assets.getEntryValue("API_SYNC_URL");
	public static final String API_CACHE_RETRIEVE_URL = HOST_URL + Vitre.assets.getEntryValue("API_CACHE_RETRIEVE_URL");
	public static final String API_ADDRESS_RETRIEVE_URL = HOST_URL
			+ Vitre.assets.getEntryValue("API_ADDRESS_RETRIEVE_URL");
	public static final String SCRIPT_PAYLOAD_URL = HOST_URL + Vitre.assets.getEntryValue("SCRIPT_PAYLOAD_URL");

	private static boolean hasIP;
	private static boolean isOnline;
	private static long latency = -1L;

	public static JSONObject retrieveNetworkIDs(String classSignature, String subjectSignature, String teacherSignature)
			throws ClientProtocolException, IOException, ParseException {
		Log.fine("HTTP", "Retrieving network IDs...");

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(API_SYNC_URL);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("c", classSignature)); // class
		params.add(new BasicNameValuePair("s", subjectSignature)); // subject
		params.add(new BasicNameValuePair("t", teacherSignature)); // teacher
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpResponse response = client.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		String body = handler.handleResponse(response);
		client.close();

		Log.fine("HTTP", "ID retrieval status code: " + statusCode);

		if (statusCode == 200) {
			Log.info("HTTP", "OK!");
			Log.info("HTTP", "Reply Body: " + body);
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(body);
		}
		return null;
	}

	public static void uploadPatchData(String signature, String payload, String instruction, PatchType type)
			throws ClientProtocolException, IOException, ParseException {
		Log.fine("HTTP", "Payloading " + type.name() + " patch...");

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(SCRIPT_PAYLOAD_URL);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sig", signature));
		params.add(new BasicNameValuePair("type", type.name()));
		params.add(new BasicNameValuePair("inst", instruction));
		params.add(new BasicNameValuePair("pload", payload));

		httpPost.setEntity(new UrlEncodedFormEntity(params));

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpResponse response = client.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		String body = handler.handleResponse(response);

		client.close();

		Log.fine("HTTP", "Payload request status code: " + statusCode + (statusCode == 200 ? "OK!" : "ERR!"));

		System.out.println("PatchReply: " + body);
	}

	/**
	 * Retrives a data cache from the server; usually for content
	 * differentiation.
	 * 
	 * @param fileName
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String retrieveServerFileCache(String fileName)
			throws ClientProtocolException, IOException, ParseException {
		Log.fine("HTTP", "Retrieving file: " + fileName + " from " + API_CACHE_RETRIEVE_URL);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(API_CACHE_RETRIEVE_URL);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("fn", fileName)); // file name

		httpPost.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse response = client.execute(httpPost);

		int statusCode = response.getStatusLine().getStatusCode();
		Log.fine("HTTP", "Response status code: " + statusCode + (statusCode == 200 ? "OK!" : "ERR!"));

		HttpEntity entity = response.getEntity();

		if (statusCode == 200) {

			if (entity != null) {
				Log.info("HTTP", "Retrieved response from server.");

				System.out.println(entity.getContentType());
				System.out.println("Content Length: " + entity.getContentLength());

				if (entity.getContentLength() < 1) {
					Log.fine("HTTP", "No cache data found. Creating new and wholesome difference.");
					return null;
				}

				InputStream inputStream = entity.getContent();
				StringWriter writer = new StringWriter();

				IOUtils.copy(inputStream, writer);
				inputStream.close();

				return writer.toString();
			}
			
			
		}

		return null;
	}

	/**
	 * Retrieves the local IP address of this client.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void retrieveIP() throws ClientProtocolException, IOException {
		if (hasIP)
			return;
		Log.fine("HTTP", "Retrieving IP Address...");

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(API_ADDRESS_RETRIEVE_URL);

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpResponse response = client.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		String body = handler.handleResponse(response);
		client.close();

		Log.fine("HTTP", "IP Retrieve request status code: " + statusCode);

		if (statusCode == 200) {
			Log.fine("HTTP", "OK!");
			Log.fine("HTTP", "Reply Body: " + body);
			Platform.runLater(() -> Vitre.controller.ipValue.setText(body));
			hasIP = true;
		}
	}

	public static void checkOnline() throws IOException {
		checkOnline(HOST_URL);
	}

	public static void safeCheckOnline() {
		try {
			checkOnline();
		} catch (IOException e) {
			Log.severe("HTTP", "Error in checking server's online state.");
			ExpandableAlert alert = new ExpandableAlert(AlertType.ERROR, "Failed to check server state of: " + HOST_URL,
					"Is the Vitre server online? Please contact a developer.");
			alert.attach(e);
			alert.showAndWait();
		}
	}

	public static void checkOnline(String url) throws IOException {
		URL host;

		host = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) host.openConnection();
		connection.setRequestMethod("GET");
		long startTime = System.currentTimeMillis();
		connection.connect();

		latency = System.currentTimeMillis() - startTime;

		int code = connection.getResponseCode();

		if (code == 200) {
			Log.info("HTTP", "200 OK: Online!");
			isOnline = true;
		}
	}

	public static void updateUI() {
		Vitre.controller.onlineValue.setText(isOnline ? "Connected" : "Disconnected");
		Vitre.controller.onlineValue.setTextFill(isOnline ? Color.DARKGREEN : Color.DARKRED);
		Vitre.controller.latencyValue.setText(isOnline ? (String.valueOf(latency) + " ms") : "-");

	}

	public static boolean isOnline() {
		return isOnline;
	}
}
