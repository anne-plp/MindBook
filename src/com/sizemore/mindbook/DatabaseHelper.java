package com.sizemore.mindbook;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String dbName = "Agenda";
	private static final int Database_Version = 1;
	
	//Database creation statement
	private static final String Database_Create = "create table calendar (_id integer primary key autoincrement, "
			+ "category text not null, summary text not null, description text not null);";
	
	public DatabaseHelper(Context context){
		super(context, dbName, null, Database_Version);
	}
	
	//Method is called during creation
	public void onCreate(SQLiteDatabase database){
		database.execSQL(Database_Create);
	}
	
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS todo");
		onCreate(database);
	}
	
}
