package com.sugaishun.atndsearch;
/**
 * 非同期処理でWebAPIを叩いてデータを取ってくるクラス。
 * 複数のActivityから使えるようにするために、
 * resultJSONをコールバックで渡すようにしている。
 */

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class FetchDataTask extends AsyncTask<Void, String, Void> {
	private static final String TAG = FetchDataTask.class.getSimpleName();
	private HttpGet requestUrl;
	private ProgressDialog myDialog;
	private AlertDialog.Builder adb;
	private Handler handler;
	private String resultJSON;
	private CallBackTask callBackTask;
	
	public FetchDataTask(Context context, HttpGet requestUrl) {
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
			resultJSON = httpClient.execute(requestUrl, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) 
						throws ClientProtocolException, IOException {
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
		return null;
	}

	@Override
	protected void onPostExecute(Void unused) {
		super.onPostExecute(unused);
		// doInBackgroundが正常に終了しなかった場合
		if (resultJSON == null)
			return;
		// 検索結果が0件の場合
		try {
			JSONObject rootObject = new JSONObject(resultJSON);
			int resultsReturned = rootObject.getInt("results_returned");
			
			if (resultsReturned == 0) {
				showAlert("検索結果は0件でした");
				return;
			}	
		} catch (JSONException e) {
			showAlert("JSONエラー");
		}
		// 正常時処理 呼び出し元にJSONを返す
		callBackTask.CallBack(resultJSON);
		closeDialog();
	}

	@Override
	protected void onCancelled() {
		closeDialog();
		super.onCancelled();
	}
	
	public void setOnCallBack(CallBackTask _cbj) {
		this.callBackTask = _cbj;
	}
	public static class CallBackTask {
		public void CallBack(String result) {
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
