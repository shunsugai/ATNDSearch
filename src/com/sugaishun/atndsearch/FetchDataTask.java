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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class FetchDataTask extends AsyncTask<Void, String, Void> {
	private static final String TAG = FetchDataTask.class.getSimpleName();
	private Context context;
	private HttpGet requestUrl;
	private ProgressDialog myDialog;
	private AlertDialog.Builder adb;
	private Handler handler;
	private String result;
	private JSONArray eventArray;
	private CallBackTask callBackTask;
	
	public FetchDataTask(Context context, HttpGet requestUrl) {
		this.context = context;
		this.requestUrl = requestUrl;
		myDialog = new ProgressDialog(context);
		adb = new AlertDialog.Builder(context);
		handler = new Handler();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showDialog();
	}

	@Override
	protected Void doInBackground(Void... params) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			result = httpClient.execute(requestUrl, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					switch (response.getStatusLine().getStatusCode()) {
					case HttpStatus.SC_OK:
						return EntityUtils.toString(response.getEntity(), "UTF-8");
					case HttpStatus.SC_NOT_FOUND:
						throw new RuntimeException("No data");
					default:
						throw new RuntimeException("Connection Error");
					}
				}
			});
		} catch (Exception e) {
			showAlert("ネットワークのエラーです");
			return null;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		try {
			JSONObject rootObject = new JSONObject(result);
			eventArray = rootObject.getJSONArray("events");
		} catch (Exception e) {
			showAlert("よくわからんエラー");
			return null;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void unused) {
		super.onPostExecute(unused);
		
		if (result == null)
			return;
		
		if (eventArray.length() == 0) {
			showAlert("検索結果は0件でした");
			return;
		}
		
//		Intent intent = new Intent(context, EventListActivity.class);
//		intent.putExtra("jsonArray", eventArray.toString());
//		context.startActivity(intent);
		callBackTask.CallBack();
		closeDialog();
	}

	@Override
	protected void onCancelled() {
		closeDialog();
		super.onCancelled();
	}
	
	// CallBackの設定。試してる途中
	public void setOnCallBack(CallBackTask _cbj) {
		callBackTask = _cbj;
	}
	public static class CallBackTask {
		public void CallBack() {
		}
	}
	
	protected void showDialog() {
		myDialog.setIndeterminate(true);
		myDialog.setMessage("読み込んでいます…");
		myDialog.setCancelable(true);
		myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				FetchDataTask.this.cancel(true);
			}
		});
		myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FetchDataTask.this.cancel(true);
					}
				});
		myDialog.show();
	}
	
	protected void showAlert(final String message) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				closeDialog();
				setAlert(message);
				AlertDialog ad = adb.create();
				ad.show();
			}
		});
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
		
	protected void closeDialog() {
		if (myDialog != null && myDialog.isShowing())
			myDialog.dismiss();
	}
}
