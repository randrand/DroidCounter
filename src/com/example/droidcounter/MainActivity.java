/*
 * Android practice project: simple time counter
 * Components: UI(buttons), event handler, system timer
 * Author: JJ
 */
package com.example.droidcounter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/* 
 * Activity dealing with three buttons: start, pause/resume, reset
 */
public class MainActivity extends Activity implements OnClickListener 
{
	TextView timeText;
	Button startbtn, pause_resumebtn, resetbtn;
	boolean timerStarted; // started from very beginning
	boolean timerRunning; // running or pause?

	// Event types used for message handler
	final int M_START_TIMER = 0;
	final int M_PAUSE_TIMER = 1;
	final int M_RESET_TIMER = 2;
	final int M_UPDATE_TIMER = 3; // transition between states; refresh timer display

	
	MyTimer timer = new MyTimer();
	final int REFRESH_RATE = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initLocalVars();
	}

	private void initLocalVars() 
	{
		timeText = (TextView) findViewById(R.id.time_view);
		startbtn = (Button)findViewById(R.id.start_btn);
		pause_resumebtn= (Button)findViewById(R.id.pause_btn);
		resetbtn= (Button)findViewById(R.id.reset_btn);
		startbtn.setOnClickListener(this);
		pause_resumebtn.setOnClickListener(this);
		resetbtn.setOnClickListener(this);
		timerRunning = false;
		timerStarted = false;
	}

	// Route different button click events to handler
	public void onClick(View v) 
	{
		if(v == startbtn) {
			mHandler.sendEmptyMessage(M_START_TIMER);
		}
		else if(v == pause_resumebtn) {
			mHandler.sendEmptyMessage(M_PAUSE_TIMER);
		}
		else if(v == resetbtn) {
			mHandler.sendEmptyMessage(M_RESET_TIMER);
		}
	}

	// Handle button click events
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case M_START_TIMER:
				if(!timerRunning && !timerStarted){
					timer.start();
					mHandler.sendEmptyMessage(M_UPDATE_TIMER);
					timerRunning = true;
					timerStarted = true;
				}
				break;

			case M_UPDATE_TIMER:
				timer.updateTimer();
				timeText.setText(formatTime(timer.getElapsedTime()));
				// Refresh timer periodically
				mHandler.sendEmptyMessageDelayed(M_UPDATE_TIMER, REFRESH_RATE);
				break;
			case M_PAUSE_TIMER:
				mHandler.removeMessages(M_UPDATE_TIMER);
				if(timerStarted){
					if(!timerRunning){
						timer.start();
						mHandler.sendEmptyMessage(M_UPDATE_TIMER);
					}
					else{
						timer.updateTimer();
						timeText.setText(formatTime(timer.getElapsedTime()));
					}
					timerRunning = !timerRunning; // toggle between pause/resume
				}
				break;

			case M_RESET_TIMER:
				mHandler.removeMessages(M_UPDATE_TIMER);
				timer.updateTimer();
				timer.reset();
				timeText.setText(formatTime(timer.getElapsedTime()));
				timerStarted = false;
				timerRunning = false;
				break;

			default:
				break;
			}
		}

		// Format timer -- seconds:milliseconds
		private String formatTime(long millis)
		{
			if(millis/1000 > 0)
				return String.format("%d:%03d", millis/1000, millis%1000);
			else
				return String.format("0:%03d", millis%1000);
		}
	};

	/* 
	 * Time counting and state maintenance
	 */
	class MyTimer
	{
		long timeElapsed = 0;
		long startTime;

		public void start() 
		{
			startTime = SystemClock.uptimeMillis();
			startTime -= timeElapsed;
		}

		public void reset() 
		{
			timeElapsed = 0;
		}

		// Recalculate timerElapsed; called on refresh
		public void updateTimer() 
		{
			long nowTime = SystemClock.uptimeMillis();
			timeElapsed = nowTime - startTime;
		}

		public long getElapsedTime() 
		{
			return timeElapsed;
		}
	};

}