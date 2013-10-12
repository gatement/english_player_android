package johnsonlau.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class HttpRequest {
	public static String doPost(String url, List<NameValuePair> params)
			throws IOException {

		String result = "";

		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
		HttpConnectionParams.setSoTimeout(httpParams, 300000);

		AbstractHttpEntity formEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		formEntity.setContentEncoding("UTF-8");
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(formEntity);

		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = httpClient.execute(httpPost);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			InputStream inputStream = response.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, Charset.forName("UTF-8"));
			BufferedReader buffer = new BufferedReader(inputStreamReader);

			String inputLine = null;
			while ((inputLine = buffer.readLine()) != null) {
				result += inputLine + "\n";
			}
		}

		return result;
	}

	public static String doGet(String url) throws IOException {

		String result = "";

		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
		HttpConnectionParams.setSoTimeout(httpParams, 300000);
		
		HttpGet httpGet = new HttpGet(url);

		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = httpClient.execute(httpGet);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			InputStream inputStream = response.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, Charset.forName("UTF-8"));
			BufferedReader buffer = new BufferedReader(inputStreamReader);

			String inputLine = null;
			while ((inputLine = buffer.readLine()) != null) {
				result += inputLine + "\n";
			}
		}

		return result;
	}
}
