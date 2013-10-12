package johnsonlau.englishplayer.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DbAdapter {

	private final Context mContext;
	private DbOpenHelper mDbOpenHelper;
	private SQLiteDatabase mDb;

	public DbAdapter(Context content) {
		this.mContext = content;
	}

	public DbAdapter open() throws SQLException {
		mDbOpenHelper = new DbOpenHelper(mContext);
		mDb = mDbOpenHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbOpenHelper.close();
	}

	// == settings ===============================

	public boolean updateSettings(String sound_url) {
		ContentValues args = new ContentValues();
		args.put(DbOpenHelper.TABLE_SETTINGS_SOUND_URL, sound_url);

		return mDb.update(DbOpenHelper.TABLE_SETTINGS, args, null, null) > 0;
	}

	public Cursor fetchSettings() throws SQLException {
		Cursor cursor = mDb.query(true, DbOpenHelper.TABLE_SETTINGS,
				new String[] { DbOpenHelper.TABLE_SETTINGS_SOUND_URL }, null,
				null, null, null, null, null);

		return cursor;
	}
}
