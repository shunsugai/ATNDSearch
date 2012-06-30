package com.sugaishun.atndsearch;
/*
 * todo: mainactivityから渡されるデータを追加する
 * todo: atndapiにリクエストするときの「何件目」を保持する
 * todo: footerviewの背景
 * todo: footerview押下時のプログレスダイアログ
 * todo: これ以上読み込む記事ないときにfooter隠す
 * todo: 例外発生時の挙動
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EventListActivity extends Activity implements OnItemClickListener, OnScrollListener {
	private static final String TAG = EventListActivity.class.getSimpleName();
	/* AdMobのID */
	private static final String MY_AD_UNIT_ID = "a14fdd0d7d55ff6";
	private static final int MYREQUEST = 2;
	
	// メンバ変数多い。
	private EventAdapter eventAdapter;
	private List<Event> events = new ArrayList<Event>();
	private AdView adView;
	private View mFooter;
	private ListView mListView;
	private String resultJSON;
	private TextView footerText;
	private ProgressBar footerProgressBar;
	private String keyword, prefecture;
	private int period;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlist);
		
		// setEvents
		Intent intent = getIntent();
		String strJSONArray = intent.getStringExtra("jsonArray");
		addListData(getJsonArray(strJSONArray));
		
		// MainからrequestURLをつくるためのデータを受け取る。
		// 何度もやりとりするのがダサい。もっとスマートな方法があるはず
		keyword = intent.getStringExtra("KEYWORD");
		prefecture = intent.getStringExtra("PREFECTURE");
		period = intent.getIntExtra("PERIOD", 0);
		
		// setListView
		eventAdapter = new EventAdapter(EventListActivity.this, events);
		ListView listView = getListView();
		listView.addFooterView(getFooter());
		listView.setAdapter(eventAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        
        // setFooter
        footerText = (TextView) findViewById(R.id.foote_text);
        footerProgressBar = (ProgressBar) findViewById(R.id.progressbar_small);
        setFooterWaiting();
        
        getFooter().setBackgroundDrawable(
        		this.getResources().getDrawable(R.drawable.atnd_list_background));
        getFooter().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				readMore();
			}
        });
        
        setAd();
	}

	// Listの各アイテムをクリックしたら詳細Activityへ飛ぶ
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
		Event event = events.get(position);
		
		intent.putExtra("TITLE", event.getTitle());
		intent.putExtra("DATE", event.getDate());
		intent.putExtra("ADDRESS", event.getAddress());
		intent.putExtra("DESCRIPTION", event.getDescription());
		
		startActivityForResult(intent, MYREQUEST);
	}
	
	// 「もっと読む」機能
	private AsyncTask<Void, String, Void> myTask;
	/* readMore()の状態 */
	private boolean isLoading = false;
	private int counter = 1;
	
	private void readMore() {
		if (myTask != null && myTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		}
		
		myTask = new AsyncTask<Void, String, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				footerProgressBar.setVisibility(View.VISIBLE);
				footerText.setText("読み込み中…");
			}

			@Override
			protected Void doInBackground(Void... params) {
				RequestURIBuilder rub = new RequestURIBuilder(keyword, prefecture, period);
				rub.setStartPosition(counter * 20 + 1);
				final HttpGet requestURL = rub.getRequestURI();
				Log.d(TAG, requestURL.getURI().toString());
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				try {
					resultJSON = httpClient.execute(requestURL, new ResponseHandler<String>() {
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
					Log.d("TEST", "Network Error");
					resultJSON = null;
				} finally {
					httpClient.getConnectionManager().shutdown();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.d("TEST", "onPostExecute()");
				super.onPostExecute(result);
				// doInBackgroundでエラーの場合.
				if (resultJSON == null) {
					Toast.makeText(EventListActivity.this, "読み込めませんでした", Toast.LENGTH_SHORT).show();
					setFooterWaiting();
					return;
				}
				// 正常時 リストにEventオブジェクトを追加して更新
				try {
					JSONArray eventArray = new JSONObject(resultJSON).getJSONArray("events");
					addListData(eventArray);
					eventAdapter.notifyDataSetChanged();
					getListView().invalidateViews();
				} catch (Exception e) { 
					e.getStackTrace(); 
				} finally {
					myTask = null;
					isLoading = false;
					counter++;
					setFooterWaiting();
				}
			}
		}.execute();
	}
	
	private JSONArray getJsonArray(String stringExtra) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(stringExtra);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}
	
	private View getFooter() {
		if (mFooter == null) {
			mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		}
		return mFooter;
	}

	private void invisibleFooter() {
		getListView().removeFooterView(getFooter());
	}

	public ListView getListView() {
		if (mListView == null) {
			mListView = (ListView) findViewById(R.id.list);
		}
		return mListView;
	}
	
	private void addListData(JSONArray array) {			
		if(array == null) return;
		
		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = null;
			try {
				jsonObject = array.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			events.add(toEvent(jsonObject));
		}
	}

	private Event toEvent(JSONObject jsonObject) {
		Event event = new Event();
		try {
			event.setTitle(jsonObject.getString("title"));		
			event.setDate(jsonObject.getString("started_at"));
			event.setAddress(jsonObject.getString("address"));
			event.setCatchcopy(jsonObject.getString("catch"));
			event.setDescription(jsonObject.getString("description"));
		} catch (JSONException e) { 
			e.printStackTrace(); 
		}					
		return event;
	}
	
	private void setFooterWaiting() {
		footerText.setText("次を読み込む");
        footerProgressBar.setVisibility(View.GONE);
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (isLoading) return;
		if (totalItemCount == firstVisibleItem + visibleItemCount) {
			isLoading = true;
			readMore();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
	private void setAd() {
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);

		// Lookup your LinearLayout assuming it’s been given
		// the attribute android:id="@+id/mainLayout"
		LinearLayout layout = (LinearLayout) findViewById(R.id.footer);

		// Add the adView to it
		layout.addView(adView);

		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());
	}
}
