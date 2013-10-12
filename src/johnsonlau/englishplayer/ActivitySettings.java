package johnsonlau.englishplayer;

import johnsonlau.englishplayer.model.DbAdapter;
import johnsonlau.englishplayer.model.DbOpenHelper;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivitySettings extends Activity {

	private Button mConfirmButton;
	private EditText mSoundUrlEditText;
	private TextView mMsgTextView;

	private DbAdapter mDbAdapter;

	// @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		initMembers();
		populateData();
		bindEvents();
	}

	// @Override
	protected void onDestroy() {
		super.onDestroy();

		mDbAdapter.close();
	}

	private void initMembers() {
		mDbAdapter = new DbAdapter(this).open();

		mConfirmButton = (Button) findViewById(R.id.settings_confirm);
		mSoundUrlEditText = (EditText) findViewById(R.id.settings_sound_url);
		mMsgTextView = (TextView) findViewById(R.id.settings_msg);
	}

	private void populateData() {
		try {
			Cursor settingsCursor = mDbAdapter.fetchSettings();
			settingsCursor.moveToFirst();
			startManagingCursor(settingsCursor);

			mSoundUrlEditText
					.setText(settingsCursor.getString(settingsCursor
							.getColumnIndexOrThrow(DbOpenHelper.TABLE_SETTINGS_SOUND_URL)));
		} catch (SQLException ex) {
			mMsgTextView.setText("Load settings error!");
		}
	}

	private void bindEvents() {
		bindConfirmButtonClick();
	}

	private void bindConfirmButtonClick() {
		this.mConfirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// get setting values
				String soundUrl = mSoundUrlEditText.getText().toString().trim();

				if (soundUrl.isEmpty()) {
					mMsgTextView.setText("Please fill out all requred fields.");
					return;
				}

				// save settings and return
				mDbAdapter.updateSettings(soundUrl);
				goToMainActivity();
			}
		});
	}

	private void goToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}
}