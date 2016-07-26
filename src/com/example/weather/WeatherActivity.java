package com.example.weather;

import util.HttpUtil;
import util.HttpUtil.HttpCallbackListener;
import util.Utility;

import com.example.wheather.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	private TextView cityNameTextView;
	private TextView publishTimeTextView;
	private TextView weatherDescTextView;
	private TextView temp1TextView;
	private TextView temp2TextView;
	private TextView currentDaTextView;
	private Button switchCity;
	private Button refreshWeather;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameTextView = (TextView) findViewById(R.id.city_name);
		publishTimeTextView = (TextView) findViewById(R.id.publish_text);

		weatherDescTextView = (TextView) findViewById(R.id.weather_desc);
		temp1TextView = (TextView) findViewById(R.id.temp1);
		temp2TextView = (TextView) findViewById(R.id.temp2);
		currentDaTextView = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			publishTimeTextView.setText("同步中");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameTextView.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		System.out.println("cityname"+prefs.getString("city_name", "hh"));
		String cityname=prefs.getString("city_name", "");
		cityNameTextView.setText(cityname);
		publishTimeTextView.setText(prefs.getString("publish_time", ""));
		weatherDescTextView.setText(prefs.getString("weather_desp", ""));
		temp1TextView.setText(prefs.getString("temp1", ""));
		temp2TextView.setText(prefs.getString("temp2", ""));
		currentDaTextView.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameTextView.setVisibility(View.VISIBLE);
	}

	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {

						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCodeString = array[1];
							queryWeatherInfo(weatherCodeString);
						}
					}
				} else if ("weatherCode".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showWeather();

						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishTimeTextView.setText("同步失败");
					}
				});

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishTimeTextView.setText("同步中。。。");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weathercode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weathercode)) {
				queryWeatherInfo(weathercode);
			}
			break;
		default:
			break;
		}

	}

	private void queryWeatherInfo(String weathercode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weathercode + ".html";
		queryFromServer(address, "weatherCode");

	}
}
