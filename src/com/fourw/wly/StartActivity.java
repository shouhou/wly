package com.fourw.wly;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

import com.fourw.global.Global;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		AlertDialog alert = new AlertDialog.Builder(StartActivity.this)
				.create();
		alert.setTitle("系统提示："); // 设置对话框的标题
		alert.setMessage("请选择你的身份!"); // 设置要显示的内容
		// 添加取消按钮
		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "学生",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Global.isTeacher = false;
						Intent intent = new Intent(StartActivity.this,
								SignActivity.class);
						startActivity(intent);
						finish();
					}
				});
		// 添加确定按钮
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "教师",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Global.isTeacher = true;
						Intent intent = new Intent(StartActivity.this,
								CallActivity.class);
						startActivity(intent);
						finish();
					}
				});
		alert.show(); // 创建对话框并显示
	}
}
