package com.scorpioneal.countdownview;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class CountDownView extends RelativeLayout {

	private static final String TAG = CountDownView.class.getSimpleName();
	private Context mContext;

	private String mPlanString = "请指定计划名称";// 计划名称
	private int backgroundColor = Color.GREEN;
	private int textColor = Color.YELLOW;
	private long mCountDownTime = 20;
	private int mViewDrawWidth = -1;// view的画图高度

	private int mRawViewWith = -1;// view的原始宽度
	private int mRawViewHeight = -1;

	private Timer mTimer;

	private int i = 0;

	private Paint mPaint;

	private int countingStatus = STATUS_START;

	public static final int STATUS_START = 0x100;
	public static final int STATUS_COUNTINGDOWN = 0x101;
	public static final int STATUS_TIMEUP = 0x110;
	public static final int STATUS_CANCEL = 0x111;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == STATUS_COUNTINGDOWN) {
				countingStatus = STATUS_COUNTINGDOWN;
			} else if (msg.what == STATUS_TIMEUP) {
				countingStatus = STATUS_TIMEUP;
			}
			postInvalidate();
		};
	};

	public CountDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mPaint = new Paint();

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.countdownview);
		mPlanString = a.getText(R.styleable.countdownview_text).toString();
		mCountDownTime = a.getInt(R.styleable.countdownview_time, 10);
		backgroundColor = a.getColor(R.styleable.countdownview_background,
				Color.GREEN);
		textColor = a.getColor(R.styleable.countdownview_textColor,
				Color.YELLOW);
		a.recycle();

		setBackgroundColor(Color.TRANSPARENT);
		// startCountingDown();
	}

	public CountDownView(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	public void cancelCountingDown() {
		mTimer.cancel();
		i = 0;
		mViewDrawWidth = mRawViewWith;
		countingStatus = STATUS_CANCEL;
		if (null != iCountDownStatuChangeListener) {
			iCountDownStatuChangeListener.onCountingDownCancel();
		}

		invalidate();
	}

	public void startCountingDown() {
		countingStatus = STATUS_START;
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mViewDrawWidth >= 0) {
					if (i++ >= mCountDownTime) {
						if (null != iCountDownStatuChangeListener) {
							iCountDownStatuChangeListener.onCountingDownDone();
						}
						mTimer.cancel();
						mHandler.sendEmptyMessage(STATUS_TIMEUP);
						Log.d(TAG, mCountDownTime + " 时间到了， 停止");
					} else {
						if (null != iCountDownStatuChangeListener) {
							iCountDownStatuChangeListener
									.onCountingDown(mCountDownTime - i + 1);
						}
						mViewDrawWidth = (int) (mViewDrawWidth - (mRawViewWith / mCountDownTime));
						Log.d(TAG, "mViewWith now is " + mViewDrawWidth
								+ " i is " + i);
						mHandler.sendEmptyMessage(STATUS_COUNTINGDOWN);
					}
				} else {
					Log.d(TAG, "还没有获取绘制结果， 暂不处理");
				}

			}
		}, 0, 1000);
	}

	public boolean isCountingDown() {
		if (countingStatus == STATUS_COUNTINGDOWN) {
			return true;
		}
		return false;
	}
	
	public boolean isTimeUp(){
		if(countingStatus == STATUS_TIMEUP){
			return true;
		}
		return false;
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (mRawViewWith < 0) {
			mRawViewWith = specSize;
		}
		mViewDrawWidth = specSize;

		Log.d(TAG, "width size in onMeasure " + mViewDrawWidth);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) getPaddingLeft() + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (mRawViewHeight < 0) {
			mRawViewHeight = specSize;
		}
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (countingStatus == STATUS_START) {
			mPaint.setTextSize(80);
			mPaint.setStrokeWidth(25);
			mPaint.setColor(backgroundColor);
			canvas.drawRect(0, 0, mViewDrawWidth, mRawViewHeight, mPaint);
			mPaint.setColor(textColor);
			canvas.drawText(getmPlanString(),
					getWidth() / 2 - mPaint.measureText(getmPlanString()) / 2,
					getHeight() / 2, mPaint);
		} else if (countingStatus == STATUS_COUNTINGDOWN) {
			mPaint.setTextSize(80);
			mPaint.setStrokeWidth(25);
			mPaint.setColor(backgroundColor);
			canvas.drawRect(0, 0, mViewDrawWidth, mRawViewHeight, mPaint);
			mPaint.setColor(textColor);
			canvas.drawText(getmPlanString(),
					getWidth() / 2 - mPaint.measureText(getmPlanString()) / 2,
					getHeight() / 2, mPaint);
			Log.d(TAG, "draw counting down");
		} else if (countingStatus == STATUS_TIMEUP) {
			Log.d(TAG, "draw text" + mRawViewWith / 2 + " " + mRawViewHeight
					/ 2);
			mPaint.setColor(textColor);
			mPaint.setTextSize(80);
			mPaint.setStrokeWidth(25);
			canvas.drawText("Time is up",
					getWidth() / 2 - mPaint.measureText("time is up") / 2,
					getHeight() / 2, mPaint);
		} else if (countingStatus == STATUS_CANCEL) {
			mPaint.setTextSize(80);
			mPaint.setStrokeWidth(25);
			mPaint.setColor(backgroundColor);
			canvas.drawRect(0, 0, mViewDrawWidth, mRawViewHeight, mPaint);
			mPaint.setColor(textColor);
			canvas.drawText("U r a  Loser ~ LOL",
					getWidth() / 2 - mPaint.measureText("U r a  Loser ~ LOL")
							/ 2, getHeight() / 2, mPaint);
		}

	}

	public String getmPlanString() {
		return mPlanString;
	}

	/**
	 * 设置计划名称
	 * 
	 * @param mPlanString
	 */
	public void setPlanString(String mPlanString) {
		this.mPlanString = mPlanString;
	}

	public long getCountDownTime() {
		return mCountDownTime;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float touchX = event.getX();
			float touchY = event.getY();
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 设置倒计时时间
	 * 
	 * @param timeInSeconds
	 */
	public void setCountDownTime(long timeInSeconds) {
		this.mCountDownTime = timeInSeconds;
	}

	public interface OnCountDownStatuChangeListener {
		void onCountingDown(long count);

		void onCountingDownDone();

		void onCountingDownCancel();
	}

	private OnCountDownStatuChangeListener iCountDownStatuChangeListener;

	public void setOnCountDownStatuChangeListener(
			OnCountDownStatuChangeListener iCountDownStatuChangeListener) {
		this.iCountDownStatuChangeListener = iCountDownStatuChangeListener;
	}

	public int getCountingStatus() {
		return countingStatus;
	}

}
