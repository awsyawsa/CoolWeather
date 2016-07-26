package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.R.integer;

public class HttpUtil {
	public interface HttpCallbackListener {

		void onFinish(String response);

		void onError(Exception e);

	}

	public static void sendHttpRequest(final String address,
			final HttpCallbackListener httpCallbackListener) {
		new Thread(new Runnable() {

			private BufferedReader in;

			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setRequestProperty( "Accept-Encoding", "utf-8" );
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = "";
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (httpCallbackListener != null) {
						httpCallbackListener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (httpCallbackListener != null) {
						httpCallbackListener.onError(e);
					}
				} finally {
					in = null;
					if (connection != null) {
						connection.disconnect();

					}
				}

			}
		}).start();
	}

}
