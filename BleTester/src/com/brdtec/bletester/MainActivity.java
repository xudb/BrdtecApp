package com.brdtec.bletester;

import com.brdtec.bletester.MainActivity;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button mLoginButton = (Button) findViewById(R.id.login_button);
		mLoginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				Intent intent = new Intent();
				 intent.setClass(MainActivity.this, com.brdtec.bletester.DeviceStateActivity.class);
				 startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
