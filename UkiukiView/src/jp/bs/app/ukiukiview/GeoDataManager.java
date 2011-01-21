package jp.bs.app.ukiukiview;

import jp.co.brilliantservice.utility.SdLog;
import jp.co.brilliantservice.app.openar.data.ARObject;
//import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeoDataManager {
	public static final String KEY_ROWID = "_id";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final String[] COLUMN_NAME = {
			KEY_ROWID
			,"uid"
			,"lat"
			,"lon"
			,"name"
			,"summary"
			,"owner_name"
			,"icon_uri"
			,"type"
			,"language"
			,"feature_name"
			,"content"
	};

	private static final String DATABASE_NAME = "cache";
	private static final String DATABASE_TABLE = "geodata";
	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_CREATE =
		"create table "+ DATABASE_TABLE + " (_id integer primary key autoincrement"
			+ ", uid text not null"
			+ ", lat1e6 int not null"
			+ ", lon1e6 int not null"
			+ ", language text not null"
			+ ", title text not null"
			+ ", userid text not null"
			+ ", parent_id text not null"
			+ ", moderate int not null"
			+ ", mime_type text not null"
			+ ", uri text not null"
			+ ", posted_date_time DATETIME not null"
			+ ", stored_date_time DATETIME not null"
			+ ");";

	private final Context context;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		    db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			SdLog.put("Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS messages");
			onCreate(db);
		}
	}

	public GeoDataManager(Context context) {
		this.context = context;
	}

	public GeoDataManager open() throws SQLException {
		mDbHelper = new DatabaseHelper(context);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDb.close();
		mDb = null;
		mDbHelper.close();
		mDbHelper = null;
	}

	public long createGeoData(ARObject item) {
		/*
		ContentValues initialValues = new ContentValues();
		initialValues.put("uid", item.getUid());
		initialValues.put("lat", Double.toString(item.getLocation().getLatitude()));
		initialValues.put("lon", Double.toString(item.getLocation().getLongitude()));
		initialValues.put("name", item.getName());
		initialValues.put("summary", item.getSummary());
		initialValues.put("owner_name", item.getOwner().getName());
		initialValues.put("icon_uri", item.getIconUri().toString());
		initialValues.put("type", item.getType());
		initialValues.put("language", item.getLanguage());
		initialValues.put("feature_name", item.getAddress().getFeatureName());
		initialValues.put("content", item.getContent());
		return mDb.insert(DATABASE_TABLE, null, initialValues);
		*/
		return 0;
	}

	public boolean deleteItem(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor fetchAllItems() {
		return mDb.query(DATABASE_TABLE
				, COLUMN_NAME
				, null, null, null, null, KEY_ROWID + " DESC");
	}

	public Cursor fetchItems(int status) {
		return mDb.query(DATABASE_TABLE
			, COLUMN_NAME
			, "status=" + status, null, null, null, KEY_ROWID + " DESC");
	}
}
