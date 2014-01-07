package com.sizemore.mindbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DataBaseAdapter {

	//Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_DESCRIPTION = "description";
	private static final String DATABASE_TABLE = "Calendar";
	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	
	public DataBaseAdapter(Context context){
		this.context = context;
	}
	
	public DataBaseAdapter open() throws SQLException{
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	 // Create a new Calendar If the Calendar is successfully created return the new
		//	 * rowId for that note, otherwise return a -1 to indicate failure.
			// */

			public long createCalendar(String date, String category, String discription) {
				ContentValues initialValues = createContentValues(date, category, discription);

				return database.insert(DATABASE_TABLE, null, initialValues);
			}

			
		/**
			 * Update the Calendar
			 */

			public boolean updateAgenda(String date,String catergory,
					String description) {
				ContentValues updateValues = createContentValues(date, catergory,
						description);

				return database.update(DATABASE_TABLE, updateValues, KEY_DATE + "="
						+ date, null) > 0;
			}

			
		/**
			 * Deletes Agenda
			 */

			public boolean deleteAgenda(long date) {
				return database.delete(DATABASE_TABLE, KEY_DATE + "=" + date, null) > 0;
			}

			
		/**
			 * Return a Cursor over the list of all todo in the database
			 * 
			 * @return Cursor over all notes
			 */

			public Cursor fetchAllAgenda() {
				return database.query(DATABASE_TABLE, new String[] { KEY_DATE, KEY_CATEGORY, KEY_DESCRIPTION }, null, null, null,
						null, null);
			}

			
		/**
			 * Return a Cursor positioned at the defined Agenda
			 */

			public Cursor fetchAgenda(long date) throws SQLException {
				Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
						KEY_DATE, KEY_CATEGORY, KEY_DESCRIPTION },
						KEY_DATE + "=" + date, null, null, null, null, null);
				if (mCursor != null) {
					mCursor.moveToFirst();
				}
				return mCursor;
			}

			private ContentValues createContentValues(String date, String category,
					String description) {
				ContentValues values = new ContentValues();
				values.put(KEY_DATE, date);
				values.put(KEY_CATEGORY, category);
				values.put(KEY_DESCRIPTION, description);
				return values;
			}
		}


