package com.scorpioneal.countdownview;

import com.scorpioneal.countdownview.CountDownView.OnCountDownStatuChangeListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private Context mContext = MainActivity.this;
	private CountDownView mCountDownView, mCountDownView1, mCountDownView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mCountDownView = (CountDownView) findViewById(R.id.countdownview);
		mCountDownView1 = (CountDownView) findViewById(R.id.countdownview1);
		mCountDownView2 = (CountDownView) findViewById(R.id.countdownview2);

		mCountDownView
				.setOnCountDownStatuChangeListener(new OnCountDownStatuChangeListener() {

					@Override
					public void onCountingDownDone() {
						Log.d(TAG, "counting down finished");
					}

					@Override
					public void onCountingDownCancel() {
						Log.d(TAG, "cancel counting down");

					}

					@Override
					public void onCountingDown(long count) {
						Log.d(TAG, "counting down " + count);
					}
				});
		mCountDownView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCountDownView.isCountingDown()) {
					mCountDownView.cancelCountingDown();
					Log.d(TAG, "click it and cancel counting down");
				} else if (mCountDownView.isTimeUp()) {
					Toast.makeText(mContext, "Time is up and do other thing",
							Toast.LENGTH_SHORT).show();
				} else {
					mCountDownView.startCountingDown();
					Log.d(TAG, "click it and start counting down");
				}
			}
		});
		mCountDownView1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCountDownView1.isCountingDown()) {
					mCountDownView1.cancelCountingDown();
				} else {
					mCountDownView1.startCountingDown();
				}
			}
		});
		mCountDownView2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCountDownView2.isCountingDown()) {
					mCountDownView2.cancelCountingDown();
				} else {
					mCountDownView2.startCountingDown();
				}
			}
		});
	}

}
