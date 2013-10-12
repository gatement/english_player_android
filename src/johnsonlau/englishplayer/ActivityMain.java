package johnsonlau.englishplayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import johnsonlau.englishplayer.model.DbAdapter;
import johnsonlau.englishplayer.model.DbOpenHelper;
import johnsonlau.util.HttpRequest;
import johnsonlau.util.Message1;
import johnsonlau.util.PlayingState;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityMain extends Activity {
	private static final int MENU_ID_EXIT = Menu.FIRST;
	private static final int MENU_ID_ABOUT = Menu.FIRST + 1;
	private static final int MENU_ID_SETTINGS = Menu.FIRST + 2;

	private Handler mMainHandler;
	private MediaPlayer mPlayer = null;
	private boolean mIsPause = false;

	Timer mTimer = new Timer();
	TimerTask mTimerTask;
	private WifiManager mWifiManager;

	private TextView mMsgTextView;
	private Button mPlayButton;
	private Button mPauseButton;
	private Button mStopButton;
	private Button mNextButton;
	private Button mSleepButton;
	private CheckBox mLoopingCheckBox;
	private EditText mSleepTimeEditText;

	// @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		initMembers();
		bindEvents();

		// turn on wifi
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTimer.cancel();
	}
	
	// @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ID_EXIT, 0, R.string.main_menu_exit);
		menu.add(0, MENU_ID_SETTINGS, 1, R.string.main_menu_settings);
		menu.add(0, MENU_ID_ABOUT, 2, R.string.main_menu_about);

		return true;
	}

	// @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {

		case MENU_ID_EXIT:
			exit();
			return true;

		case MENU_ID_SETTINGS:
			goToSettingsActivity();
			return true;

		case MENU_ID_ABOUT:
			goToAboutActivity();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void initMembers() {
		mMsgTextView = (TextView) findViewById(R.id.main_msg);
		mPlayButton = (Button) findViewById(R.id.main_play);
		mPauseButton = (Button) findViewById(R.id.main_pause);
		mStopButton = (Button) findViewById(R.id.main_stop);
		mNextButton = (Button) findViewById(R.id.main_next);
		mSleepButton = (Button) findViewById(R.id.main_btn_sleep);
		mSleepTimeEditText = (EditText) findViewById(R.id.main_sleep_time);
		mLoopingCheckBox = (CheckBox) findViewById(R.id.main_cb_looping_mode);

		setPlayButtonsState(PlayingState.initialized);

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		mMainHandler = new Handler() {
			public void handleMessage(Message msg) {
				Message1 message = (Message1) msg.obj;

				if (message.getCmd() == "GotSoundUrl") {
					play(message.getTextValue());
				} else if (message.getCmd() == "Message") {
					mMsgTextView.setText(message.getTextValue());
				}
			}
		};
	}

	private void bindEvents() {
		mPlayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mIsPause && mPlayer != null) {
					mPlayer.start();

					mMsgTextView.setText("Playing");
					setPlayButtonsState(PlayingState.playing);
				} else {
					new GetSoundUrl().start();
					setPlayButtonsState(PlayingState.gettingSoundUrl);
				}
			}
		});
		mPauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mPlayer != null) {
					mPlayer.pause();
				}

				mMsgTextView.setText("Paused");
				setPlayButtonsState(PlayingState.paused);
			}
		});
		mStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mPlayer != null) {
					mPlayer.stop();
				}

				mMsgTextView.setText("Stopped");
				setPlayButtonsState(PlayingState.stoped);
			}
		});
		mNextButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mPlayer != null) {
					mPlayer.stop();
				}
				new GetSoundUrl().start();

				setPlayButtonsState(PlayingState.gettingSoundUrl);
			}
		});
		mSleepButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Button sleepButton = (Button) view;
				if (sleepButton.getText().equals(
						getResources().getString(R.string.main_btn_sleep_set))) {

					String sleepTimeText = mSleepTimeEditText.getText()
							.toString();
					if (!sleepTimeText.isEmpty()) {

						mTimerTask = new TimerTask() {
							public void run() {
								// turn off wifi
								if (mWifiManager.isWifiEnabled()) {
									mWifiManager.setWifiEnabled(false);
								}

								exit();
							}
						};

						long sleepTime = Integer.parseInt(sleepTimeText);
						sleepTime = sleepTime * 60 * 1000;
						mTimer.schedule(mTimerTask, sleepTime);

						sleepButton.setText(R.string.main_btn_sleep_clear);
					}
				} else {
					sleepButton.setText(R.string.main_btn_sleep_set);
					mTimerTask.cancel();
				}
			}
		});
		mLoopingCheckBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (mPlayer != null) {
							mPlayer.setLooping(isChecked);
						}
					}
				});
	}

	private void setPlayButtonsState(PlayingState state) {
		switch (state) {
		case initialized:
			mPlayButton.setEnabled(true);
			mPauseButton.setEnabled(false);
			mStopButton.setEnabled(false);
			mNextButton.setEnabled(false);
			mLoopingCheckBox.setEnabled(false);
			mIsPause = false;
			break;
		case gettingSoundUrl:
			mPlayButton.setEnabled(false);
			mPauseButton.setEnabled(false);
			mStopButton.setEnabled(false);
			mNextButton.setEnabled(false);
			mLoopingCheckBox.setEnabled(false);
			mIsPause = false;
			break;
		case playing:
			mPlayButton.setEnabled(false);
			mPauseButton.setEnabled(true);
			mStopButton.setEnabled(true);
			mNextButton.setEnabled(true);
			mLoopingCheckBox.setEnabled(true);
			mIsPause = false;
			break;
		case paused:
			mPlayButton.setEnabled(true);
			mPauseButton.setEnabled(false);
			mStopButton.setEnabled(true);
			mNextButton.setEnabled(true);
			mLoopingCheckBox.setEnabled(true);
			mIsPause = true;
			break;
		case stoped:
			mPlayButton.setEnabled(true);
			mPauseButton.setEnabled(false);
			mStopButton.setEnabled(false);
			mNextButton.setEnabled(false);
			mLoopingCheckBox.setEnabled(true);
			mIsPause = false;
			break;
		}
	}

	private void play(String soundUrl) {
		if (mPlayer == null) {
			mPlayer = MediaPlayer.create(this, Uri.parse(soundUrl));
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					if (!mp.isLooping()) {
						new GetSoundUrl().start();
					}
				}
			});
		} else {
			mPlayer.reset();
			try {
				mPlayer.setDataSource(soundUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mPlayer.start();

		mMsgTextView.setText("Playing");
		setPlayButtonsState(PlayingState.playing);
	}

	private void exit() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
		}

		setResult(RESULT_OK);
		finish();
	}

	private void sendMessage(Message1 msg) {
		Message toMain = mMainHandler.obtainMessage();
		toMain.obj = msg;
		mMainHandler.sendMessage(toMain);
	}

	private String getSoundUrl() {
		String result = "";

		try {
			String response = HttpRequest.doGet(getServiceUrl());

			if (!response.isEmpty() && response != "failed") {
				JSONObject obj = new JSONObject(response);
				if (obj.has("soundUrl")) {
					result = obj.getString("soundUrl");
				}
			}
		} catch (Exception e) {
			result = "";
		}

		return result;
	}

	private class GetSoundUrl extends Thread {
		public void run() {
			sendMessage(new Message1("Message", "Getting sound Url..."));

			// turn on wifi
			if (!mWifiManager.isWifiEnabled()) {
				mWifiManager.setWifiEnabled(true);
			}

			String soundUrl = getSoundUrl();

			while (soundUrl == "") {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}

				soundUrl = getSoundUrl();
			}

			sendMessage(new Message1("GotSoundUrl", soundUrl));
		}
	}

	private String getServiceUrl() {
		String result = "";

		DbAdapter dbAdapter;
		dbAdapter = new DbAdapter(this).open();

		try {
			Cursor settingsCursor = dbAdapter.fetchSettings();
			settingsCursor.moveToFirst();
			result = settingsCursor
					.getString(settingsCursor
							.getColumnIndexOrThrow(DbOpenHelper.TABLE_SETTINGS_SOUND_URL));
			settingsCursor.close();
		} catch (SQLException ex) {
			mMsgTextView.setText("Load settings error!");
		}

		dbAdapter.close();

		return result;
	}

	private void goToSettingsActivity() {
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}

	private void goToAboutActivity() {
		Intent intent = new Intent(this, ActivityAbout.class);
		startActivity(intent);
	}
}