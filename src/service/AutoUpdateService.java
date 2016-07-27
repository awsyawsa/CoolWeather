package service;

import util.HttpUtil;
import util.Utility;
import util.HttpUtil.HttpCallbackListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	public IBinder onBind(Intent intent) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {

			public void run() {
				updateWeather();

			}

		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anhour = 8 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anhour;
		Intent intent2 = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
				pIntent);

		return super.onStartCommand(intent, flags, startId);
	}

	private void updateWeather() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = preferences.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);

			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();

			}
		});
	}
}
