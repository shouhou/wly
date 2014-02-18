package com.fourw.wly;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourw.global.Global;
import com.fourw.util.Client;
import com.fourw.util.Helper;
import com.fourw.util.Msg;
import com.fourw.util.Student;

public class CallActivity extends Activity {
	public static TextView tv_sign;
	private TextView tv_call;
	private TextView tv_result;
	private ListView lv_people;

	private Button btn_call;
	private Button btn_add;
	private Button btn_stopCall;
	private Button btn_submit;

	private List<String> peoples = new ArrayList<String>();
	private ArrayAdapter<String> adapter;

	private Handler handler;

	private Client client;
	private static String TAG = "CallTAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		tv_call = (TextView) findViewById(R.id.tv_call);
		tv_result = (TextView) findViewById(R.id.tv_result);
		lv_people = (ListView) findViewById(R.id.lv_people);

		btn_add = (Button) findViewById(R.id.btn_add);
		btn_call = (Button) findViewById(R.id.btn_call);
		btn_stopCall = (Button) findViewById(R.id.btn_stopCall);
		btn_submit = (Button) findViewById(R.id.btn_submit);

		btn_add.setOnClickListener(new ButtonListener());
		btn_call.setOnClickListener(new ButtonListener());
		btn_stopCall.setOnClickListener(new ButtonListener());
		btn_submit.setOnClickListener(new ButtonListener());

		pageSkip();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Msg.HINT:
					Toast.makeText(CallActivity.this, "开始通知所有学生准备点名！",
							Toast.LENGTH_SHORT).show();
					break;
				case Msg.DISCONNECTED:
					Toast.makeText(CallActivity.this, "连接服务器失败，程序即将推出！",
							Toast.LENGTH_SHORT).show();
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

		// 开启与服务器连接
		Log.d(TAG, "call new a client");
		new Thread(client).start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "call on pause");
		client.sendMessageViaString(Msg.STRMSG);
		client.sendMessageViaString("exit");
		client.closeAll();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Helper.exitProcedure();
		}
		return super.onKeyDown(keyCode, event);
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

	class TextViewListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == tv_sign) {
				Intent intent = new Intent(CallActivity.this,
						SignActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				Log.d(TAG, "send exit");
				client.sendMessageViaString(Msg.EXIT);
				Log.d(TAG, "call will finish");
				finish();
			}
			if (v == tv_call) {

			}
			if (v == tv_result) {
				Intent intent = new Intent(CallActivity.this,
						ResultActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				client.sendMessageViaString(Msg.EXIT);
				finish();
			}
		}
	}

	class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btn_add) {
				LayoutInflater inflater = getLayoutInflater();
				final View view = inflater.inflate(R.layout.dialog,
						(ViewGroup) findViewById(R.id.dialog));

				AlertDialog.Builder builder = new AlertDialog.Builder(
						CallActivity.this);
				builder.setTitle("请输入信息:").setView(view);
				builder.setPositiveButton("确定",
						new android.content.DialogInterface.OnClickListener() {// 注意和view里面的冲突
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								EditText tb_name = (EditText) view
										.findViewById(R.id.tb_name);
								EditText tb_phoneNum = (EditText) view
										.findViewById(R.id.tb_phoneNum);
								String s1 = tb_name.getText().toString();
								String s2 = tb_phoneNum.getText().toString();
								peoples.add(s1 + "," + s2);
								adapter = new ArrayAdapter<String>(
										CallActivity.this,
										android.R.layout.simple_list_item_1,
										peoples);
								lv_people.setAdapter(adapter);
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
			if (v == btn_call) {
				Msg msg = new Msg();
				msg.setCode(Msg.CALL);
				client.sendMessageViaString(Msg.OBJMSG);
				client.sendMessageViaObject(msg);
				btn_call.setVisibility(View.INVISIBLE);
				btn_stopCall.setVisibility(View.VISIBLE);
			}
			if (v == btn_stopCall) {
				Msg msg = new Msg();
				msg.setCode(Msg.STOP_CALL);
				client.sendMessageViaString(Msg.OBJMSG);
				client.sendMessageViaObject(msg);
				btn_call.setVisibility(View.VISIBLE);
				btn_stopCall.setVisibility(View.INVISIBLE);
			}
			if (v == btn_submit) {
				StringBuffer sb = new StringBuffer();
				for (String p : peoples) {
					sb.append(p);
					sb.append("|");
				}
				Msg msg = new Msg();
				msg.setCode(Msg.SUBMIT);
				msg.setInfo(sb.toString());
				client.sendMessageViaString(Msg.OBJMSG);
				client.sendMessageViaObject(msg);
			}
		}
	}
}
