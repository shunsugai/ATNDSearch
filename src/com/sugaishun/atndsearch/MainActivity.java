package com.sugaishun.atndsearch;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int MYREQUEST = 1;
	private static final String MY_AD_UNIT_ID = "a14fdd0d7d55ff6";
	private AdView adView;
	private GetDataTask getData;
	private String keyword, prefecture, result;
	private int period;
	private Handler handler;
	private JSONArray eventArray;

	private static final String[] prefectures = { "全国", 
			"北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県", 
			"茨城県", "栃木県", "群馬県", "埼玉県", "千葉県","東京都", "神奈川県", 
			"新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県",
			"三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県",
			"鳥取県", "島根県", "岡山県", "広島県", "山口県", 
			"徳島県", "香川県", "愛媛県", "高知県",
			"福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県" };

	private static final String[] dates = { "すべての期間", "本日", "明日", "今週", "来週", "今月", "来月" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setSearchButton();
		setSpinnerPref();
		setSpinnerDate();
		setAd();
	}

	private void setAd() {
		adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
		// Lookup your LinearLayout assuming it’s been given
		// the attribute android:id="@+id/mainLayout"
		LinearLayout layout = (LinearLayout) findViewById(R.id.footer);
		layout.addView(adView);
		// for Test
		AdRequest adrequest = new AdRequest();
		adrequest.addTestDevice(AdRequest.TEST_EMULATOR);
		// Initiate a generic request to load it with an ad
		adView.loadAd(adrequest);
		// adView.loadAd(new AdRequest());
	}

	private void setSearchButton() {
		Button button = (Button) this.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.editText1);
				keyword = text.getText().toString();
				Log.d(TAG, keyword);
				getData = new GetDataTask();
				getData.execute();
			}
		});
	}

	private void setSpinnerPref() {
		ArrayAdapter<String> aaPref = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, prefectures);
		Spinner spinPref = (Spinner) findViewById(R.id.spinner1);

		spinPref.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				prefecture = prefectures[position];
				if (prefecture == "全国")
					prefecture = null;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		aaPref.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinPref.setAdapter(aaPref);
	}

	private void setSpinnerDate() {
		ArrayAdapter<String> aaDate = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dates);
		Spinner spinDate = (Spinner) findViewById(R.id.spinner2);

		spinDate.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				period = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				period = 0;
			}
		});

		aaDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinDate.setAdapter(aaDate);
	}

	// HTMLRequest AsyncTask
	class GetDataTask extends AsyncTask<Void, String, Void> {
		private ProgressDialog myDialog;
		private AlertDialog.Builder adb;

		public GetDataTask() {
			myDialog = new ProgressDialog(MainActivity.this);
			adb = new AlertDialog.Builder(MainActivity.this);
			handler = new Handler();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				RequestURIBuilder rub = new RequestURIBuilder(keyword, prefecture, period);
				HttpGet url = rub.getRequestURI();
				Log.d(TAG, "URL:" + url.getURI().toString());

				DefaultHttpClient httpClient = new DefaultHttpClient();
				try {
					result = httpClient.execute(url,
							new ResponseHandler<String>() {
								@Override
								public String handleResponse(HttpResponse response)
										throws ClientProtocolException,
										IOException {
									switch (response.getStatusLine()
											.getStatusCode()) {
									case HttpStatus.SC_OK:
										return EntityUtils.toString(
												response.getEntity(), "UTF-8");
									case HttpStatus.SC_NOT_FOUND:
										throw new RuntimeException("No data");
									default:
										throw new RuntimeException(
												"Connection Error");
									}
								}
							});
				} catch (Exception e) {
					Log.d(TAG,
							"Exception raised: " + e.getStackTrace()
									+ e.getLocalizedMessage());
					handler.post(new Runnable() {

						@Override
						public void run() {
							closeDialog();
							setAlert("Connection Errorです");
							showAlert();
						}
					});
					return null;
				} finally {
					httpClient.getConnectionManager().shutdown();
					Log.d(TAG, "Connection closed");
				}

				if (isCancelled()) {
					Log.d(TAG, "doInBackground()内のHttpClientで接続した後でキャンセル");
					return null;
				}

				JSONObject rootObject = new JSONObject(result);
				eventArray = rootObject.getJSONArray("events");
				Log.d(TAG, "Set data to eventArray");
			} catch (Exception e) {
				Log.d(TAG, "Exception raised: " + e.getStackTrace());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			super.onPostExecute(unused);
			if (result != null && eventArray.length() == 0) {
				closeDialog();
				setAlert("検索結果は0件でした");
				showAlert();
			} else if (result != null) {
				Intent intent = new Intent(MainActivity.this,
						EventListActivity.class);
				intent.putExtra("jsonArray", eventArray.toString());
				startActivityForResult(intent, MYREQUEST);
				closeDialog();
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(TAG, "onCanceled");
			getData = null;
			closeDialog();
			super.onCancelled();
		}

		protected void showDialog() {
			myDialog.setIndeterminate(true);
			myDialog.setMessage("読み込んでいます…");
			myDialog.setCancelable(true);
			myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					getData.cancel(true);
				}
			});
			myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getData.cancel(true);
						}
					});
			myDialog.show();
		}

		protected void setAlert(String message) {
			adb.setTitle("ATND Search");
			adb.setMessage(message);
			adb.setPositiveButton("もどる", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}

		protected void showAlert() {
			AlertDialog ad = adb.create();
			ad.show();
		}

		protected void closeDialog() {
			if (myDialog != null && myDialog.isShowing())
				myDialog.dismiss();
		}
	}
}