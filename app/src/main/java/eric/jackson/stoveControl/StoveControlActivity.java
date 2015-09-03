package eric.jackson.stoveControl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StoveControlActivity extends Activity {
	/** Called when the activity is first created. */
    ToggleButton togglebutton;
    String urlBase ="http://crazycats.ddns.net:8080/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

    @Override
    public void onStart() {
        super.onStart();
        togglebutton = (ToggleButton) findViewById(R.id.toggleButton1);
        togglebutton.setChecked(getStoveState());
        Log.i("[MESSAGE]", "OnStart");
    }


	// This method is called at button click because we assigned the name to the
	// "On Click property" of the button
	public void myClickHandler(View view) {
		switch (view.getId()) {
		case R.id.toggleButton1:
			// Perform action on clicks
			if (togglebutton.isChecked()) {
				postData(true);
			} else {
				postData(false);
			}
		}
	}

	private void postData(boolean stoveState) {
		// Create a new HttpClient and Post Header
		InputStream content = null;
		try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 6000);
            HttpConnectionParams.setSoTimeout(httpParameters, 6000);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);

			if (stoveState == true) {
				HttpResponse response = httpclient
						.execute(new HttpGet(
                                urlBase+"?stove=on&pword=jebg"));
				content = response.getEntity().getContent();
				Toast.makeText(StoveControlActivity.this, "Stove On",
						Toast.LENGTH_SHORT).show();
			} else {
				HttpResponse response = httpclient
						.execute(new HttpGet(
                                urlBase+"?stove=off&pword=jebg"));
				content = response.getEntity().getContent();
				Toast.makeText(StoveControlActivity.this, "Stove Off",
						Toast.LENGTH_SHORT).show();
			}
			String responseText = inputStreamToString(content).toString();
			displayResponse(responseText);
		} catch (Exception e) {
            displayResponse("Network Timeout");
            Toast.makeText(StoveControlActivity.this, "Network Timeout",
                    Toast.LENGTH_SHORT).show();
            togglebutton.setChecked(false);
		}
	}

	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return full string
		return total;
	}

	private boolean getStoveState() {
		InputStream content = null;
		boolean stoveOn = false;

		try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 6000);
            HttpConnectionParams.setSoTimeout(httpParameters, 6000);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpclient.execute(new HttpGet(urlBase));
			content = response.getEntity().getContent();
			String responseText = inputStreamToString(content).toString();
			displayResponse(responseText);
			stoveOn = responseText.contains("<state>on</state>");

			Document doc = XMLfunctions.XMLfromString(responseText);
			NodeList nodes = doc.getElementsByTagName("status");
			Element e = (Element) nodes.item(0);
			String state = XMLfunctions.getValue(e, "state");
			String pot = XMLfunctions.getValue(e, "pot");
			String servo = XMLfunctions.getValue(e, "servo");

		} catch (Exception e) {
            displayResponse("Network Timeout");
            Toast.makeText(StoveControlActivity.this, "Network Timeout",
                    Toast.LENGTH_SHORT).show();
            stoveOn=false;
        }
		return stoveOn;
	}

	private void displayResponse(String htmlString) {
		WebView web = (WebView) findViewById(R.id.webView1);
		web.loadData(htmlString, "text/html", null);
	}

}