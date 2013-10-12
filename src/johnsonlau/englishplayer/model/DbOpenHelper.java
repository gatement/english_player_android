package johnsonlau.englishplayer.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "DbOpenHelper";

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;

	// table settings
	public static final String TABLE_SETTINGS = "settings";
	public static final String TABLE_SETTINGS_ROWID = "_id";
	public static final String TABLE_SETTINGS_SOUND_URL = "sound_url";
	private static final String TABLE_SETTINGS_CREATE = "CREATE TABLE "
			+ TABLE_SETTINGS 
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT"
			+ ",sound_url TEXT NOT NULL);";
	private static final String TABLE_SETTINGS_INITIALIZE = "INSERT INTO settings "
			+ "(sound_url)"
			+ " VALUES('http://eng.johnsonlau.net/fetchSoundUrl.php');";

	DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// @Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_SETTINGS_CREATE);
		db.execSQL(TABLE_SETTINGS_INITIALIZE);
	}

	// @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			upgradeToVersion2();
		}
		if (oldVersion == 2 && newVersion == 3) {
			upgradeToVersion3();
		}

		Log.i(TAG, "Upgraded database " + DATABASE_NAME + " from version "
				+ oldVersion + " to " + newVersion);
	}

	private void upgradeToVersion2() {
		// do upgrading job
	}

	private void upgradeToVersion3() {
		// do upgrading job
	}
}
