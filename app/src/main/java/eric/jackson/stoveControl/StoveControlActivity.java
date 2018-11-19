package eric.jackson.stoveControl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ToggleButton;

import androidx.preference.PreferenceManager;

public class StoveControlActivity extends Activity {
	/** Called when the activity is first created. */
    ToggleButton togglebutton;
    //String urlBase ="http://crazycat.ddns.net:8080/";
    String urlBase ="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        urlBase = sharedPref.getString(settingsActivity.KEY_PREF_DNS,
                "crazycat.ddns.net");
        urlBase="http://"+urlBase+":8080/";
        setContentView(R.layout.main);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, settingsActivity.class);
                startActivity(intent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        togglebutton = findViewById(R.id.toggleButton1);
        Log.i("[MESSAGE]", "OnStart");
        new getStoveState().execute();
    }

	public void myClickHandler(View view) {
		switch (view.getId()) {
		case R.id.toggleButton1:
			// Perform action on clicks
			if (togglebutton.isChecked()) {
                new postData().execute(true);
			} else {
                new postData().execute(false);
			}
		}
	}

	private StringBuilder inputStreamToString(InputStream is) {
		String line;
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

	private void displayResponse(String htmlString) {
		WebView web = findViewById(R.id.webView1);
		web.loadData(htmlString, "text/html", null);
	}

    private class getStoveState extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();

        // This is run in a background thread
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            Boolean stoveOn = false;
            String responseText;

            Request.Builder builder = new Request.Builder();
            builder.url(urlBase+"/stove/status");
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                responseText = response.body().string();
            } catch (Exception e) {
                responseText = e.toString();
            }
            return responseText;
        }

        // This runs in UI when background thread finishes
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            displayResponse(result);
            togglebutton.setChecked(result.contains("on"));
        }
    }

    private class postData extends AsyncTask<Boolean, Void, String> {

        OkHttpClient client = new OkHttpClient();

        // This is run in a background thread
        protected String doInBackground(Boolean... params) {
            // Create a new HttpClient and Post Header
            String responseText;
            Boolean setState = params[0];
            Request.Builder builder = new Request.Builder();
            if (setState) {
                builder.url(urlBase+"/stove/1");
            } else {
                builder.url(urlBase+"/stove/0");
            }
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                responseText = response.body().string();
            } catch (Exception e) {
                responseText = e.toString();
            }
            return responseText;
       }

        // This runs in UI when background thread finishes
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            displayResponse(result);
            togglebutton.setChecked(result.contains("on"));
        }
    }
}