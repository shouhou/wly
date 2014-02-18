package com.fourw.wly;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourw.global.Global;
import com.fourw.util.Client;
import com.fourw.util.Helper;
import com.fourw.util.Msg;
import com.fourw.util.Student;
import com.fourw.wly.CallActivity.TextViewListener;

public class ResultActivity extends Activity {
	private TextView tv_sign;
	private TextView tv_call;
	private TextView tv_result;
	
	private TextView tv_num;
	
	private ListView lv_show;

	private Handler handler;
	private Client client;
	private static String TAG = "SignTAG";

	private Button btn_lookResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		tv_call = (TextView) findViewById(R.id.tv_call);
		tv_result = (TextView) findViewById(R.id.tv_result);
		lv_show=(ListView)findViewById(R.id.lv_show);
			
		tv_num = (TextView) findViewById(R.id.tv_num);
		
		btn_lookResult = (Button) findViewById(R.id.btn_lookResult);

		btn_lookResult.setOnClickListener(new ButtonListener());

		pageSkip();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Msg.SHOW1:
					int allNum=msg.arg1;
					int absentNum=msg.arg2;
					StringBuffer sb=new StringBuffer();					
					sb.append("总人数： "+allNum+" 已到人数： "+(allNum-absentNum)+" 未到人数："+absentNum);
					tv_num.setText(sb.toString());
					break;
				case Msg.SHOW2:
					List<Student> stus=(List<Student>) msg.obj;
					List<String> shows=new ArrayList<String>();
					for(Student stu:stus){
						String s=stu.getName()+","+stu.getPhone();
						shows.add(s);
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							ResultActivity.this,
							android.R.layout.simple_list_item_1,
							shows);
					lv_show.setAdapter(adapter);
					break;
				case Msg.DISCONNECTED:
					//无效
					//Toast.makeText(ResultActivity.this, "连接服务器失败，程序即将推出！", Toast.LENGTH_SHORT).show();
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
		new Thread(client).start();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "result on pause");
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

	class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btn_lookResult) {
				Log.d(TAG, "Result");
				Msg msg = new Msg();
				msg.setCode(Msg.RESULT);
				client.sendMessageViaString(Msg.OBJMSG);
				client.sendMessageViaObject(msg);
				btn_lookResult.setEnabled(false);
			}
		}
	}

	class TextViewListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == tv_sign) {
				Intent intent = new Intent(ResultActivity.this,
						SignActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				client.sendMessageViaString(Msg.EXIT);
				finish();
			}
			if (v == tv_call) {
				Intent intent = new Intent(ResultActivity.this,
						CallActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				client.sendMessageViaString(Msg.EXIT);
				finish();
			}
			if (v == tv_result) {

			}
		}
	}

}
