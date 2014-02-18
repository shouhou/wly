package com.fourw.wly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fourw.global.Global;
import com.fourw.util.Client;
import com.fourw.util.Helper;
import com.fourw.util.Msg;
import com.fourw.util.Position;
import com.fourw.wly.CallActivity.TextViewListener;

public class SignActivity extends Activity {

	private Button btn_sign;

	private TextView tv_sign;
	private TextView tv_call;
	private TextView tv_result;

	private TextView tv_show;
	private TextView tv_welcome;
	private TextView tv_ip;
	private TextView tv_prompt;

	private TelephonyManager telephoneManager;
	private LocationClient locationClient;
	private SensorManager sensorManager;
	private Sensor sensor;
	private Vibrator vibrator;
	private WifiManager wifiManager;
	private WifiInfo wifiInfo;

	private String phoneNum = "";
	private Position position = new Position();
	private Boolean isShake = false;
	private String ipAddr = "";

	private Handler handler;

	private Client client;
	private static String TAG = "SignTAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign);

		btn_sign = (Button) findViewById(R.id.btn_call);
		btn_sign.setEnabled(false);

		tv_sign = (TextView) findViewById(R.id.tv_sign);
		tv_call = (TextView) findViewById(R.id.tv_call);
		tv_result = (TextView) findViewById(R.id.tv_result);

		tv_show = (TextView) findViewById(R.id.tv_show);
		tv_welcome = (TextView) findViewById(R.id.tv_welcome);
		tv_ip = (TextView) findViewById(R.id.tv_ip);
		tv_prompt = (TextView) findViewById(R.id.tv_prompt);

		btn_sign.setOnClickListener(new ButtonListener());
		pageSkip();
		showMsg();

		Init();
		phoneNum = getPhoneNum();
		ipAddr = getIpAddr();
		tv_welcome.setText("用户: " + phoneNum + ", 您好");
		tv_ip.setText("IP:" + ipAddr);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Msg.HINT:
					Toast.makeText(SignActivity.this, "您收到了一条签到提醒！",
							Toast.LENGTH_SHORT).show();
					btn_sign.setEnabled(true);	
					break;
				case Msg.DISCONNECTED:
					Toast.makeText(SignActivity.this, "连接服务器失败，程序即将推出！", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "退出程序");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Helper.exitProcedure();
					break;
				}
			}
		};
		client = new Client(handler);// 将handler传递给子线程

		Log.d(TAG, "sign new a client");
		// 开启与服务器连接
		new Thread(client).start();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "sign on pause");
		client.sendMessageViaString(Msg.STRMSG);
		client.sendMessageViaString("exit");
		client.closeAll();
		super.onPause();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
        	Helper.exitProcedure();
        }
        return super.onKeyDown(keyCode, event);
    } 

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == btn_sign) {
				// 获取系统相关参数
				Msg msg = new Msg();
				msg.setCode(Msg.SIGN);
				msg.setIpAddr(ipAddr);
				msg.setPhoneNum(phoneNum);
				msg.setIsShake(isShake);
				msg.setPositon(position);
				client.sendMessageViaString(Msg.OBJMSG);
				client.sendMessageViaObject(msg);
				btn_sign.setEnabled(false);
			}
		}
	}

	class TextViewListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == tv_sign) {
			}
			if (v == tv_call) {
				Intent intent = new Intent(SignActivity.this,
						CallActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				client.sendMessageViaString(Msg.EXIT);
				finish();
			}
			if (v == tv_result) {
				Intent intent = new Intent(SignActivity.this,
						ResultActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				client.sendMessageViaString(Msg.EXIT);
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void Init() {
		locationClient = new LocationClient(this);
		locationClient.setAK("CnsPnnwb966YuwqePcSZ5h3y");

		telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		startOrientListener();
		startShakeListener();

	}

	private void pageSkip() {
		if (Global.isTeacher) {
			tv_call.setClickable(true);
			tv_call.setFocusable(true);
			tv_result.setClickable(true);
			tv_result.setFocusable(true);
			tv_call.setOnClickListener(new TextViewListener());
			tv_result.setOnClickListener(new TextViewListener());
		} else {
			tv_sign.setClickable(true);
			tv_sign.setFocusable(true);
			tv_sign.setOnClickListener(new TextViewListener());
		}
	}

	private void showMsg() {
		StringBuffer sb = new StringBuffer(256);
		sb.append("\n经度: " + position.getLongitude());
		sb.append("  纬度: " + position.getLatitude());
		if(isShake)
			sb.append("\n亲,你摇一摇了哦！");
		else
			sb.append("\n请摇一摇你的手机哦！");
		tv_show.setText(sb.toString());
	}

	private String getPhoneNum() {
		String num;
		num = telephoneManager.getLine1Number();
		if (num == null || num.equals("")) {
			return "15850520425";
		}
		return num;
	}

	private String getIpAddr() {
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = Helper.intToIp(ipAddress);
		return ip;
	}

	private void startOrientListener() {
		locationClient.registerLocationListener(new LocationListener());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd0911");
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);

		option.setAddrType("all");
		option.setScanSpan(3000);

		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPoiNumber(10);
		option.disableCache(true);
		locationClient.setLocOption(option);
		locationClient.start();
	}

	private void startShakeListener() {
		sensorManager.registerListener(new ShakeListener(), sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	class ShakeListener implements SensorEventListener {
		long now, last = 0;
		float lastX, lastY, lastZ = 0;
		float intervalX, intervalY, intervalZ = 0;
		long interval = 50;// 每隔50ms检测一次

		long threshold = 300;// 速度门限值
		long overNum = 5;// 在50ms*5的时间内
		int count = 0;// 计数值

		@Override
		public void onSensorChanged(SensorEvent event) {
			float values[] = event.values;
			float x = values[0];
			float y = values[1];
			float z = values[2];

			long now = System.currentTimeMillis();
			if ((now - last) > interval) {
				intervalX = x - lastX;
				intervalY = y - lastY;
				intervalZ = z - lastZ;
				lastX = x;
				lastY = y;
				lastZ = z;
				last = now;
				double speed = Math.sqrt(intervalX * intervalX + intervalY
						* intervalY + intervalZ * intervalZ)
						/ interval * 10000;// 计算速度
				// Log.i(TAG, String.valueOf(speed));
				if (speed > threshold) {
					count++;
					if (count > overNum) {// 一定时间内速度较快，判定摇动
						isShake = true;
						vibrator.vibrate(500);
						count = 0;

						showMsg();// 摇一摇获取信息
					}
				} else {
					count = 0;
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	}

	class LocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			position.setLatitude(location.getLatitude());
			position.setLongitude(location.getLongitude());
			// 考虑时间问题
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}
