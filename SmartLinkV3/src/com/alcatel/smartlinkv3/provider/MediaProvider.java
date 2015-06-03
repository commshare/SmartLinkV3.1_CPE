/**
 * 
 */
package com.alcatel.smartlinkv3.provider;

import java.util.HashMap;

import static com.alcatel.smartlinkv3.provider.MediaProvider.MediaTable.*;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author 
 *
 */
public class MediaProvider extends ContentProvider {
    /**
     * The authority we use to get to our sample provider.
     */
    public static final String AUTHORITY = "com.alcatel.smartlinkv3.provider.MediaProvider";

	private FileManagerDbHelper mDbhelper;

    // A projection map used to select columns from the database
    private final HashMap<String, String> mFilesProjectionMap;
    // Uri matcher to decode incoming URIs.
    private final UriMatcher mUriMatcher;
    
    // The incoming URI matches the file table URI pattern
    public static final int FILE = 1;
    // The incoming URI matches the file table row ID URI pattern
    public static final int FILE_ID = 2;
    public static final int FILE_LOC = 3;
    public static final int FILE_PATH = 4;
    public static final int FILE_SIZE = 5;
    public static final int FILE_DATE = 6;
    public static final int FILE_MIME = 7;
    public static final int FILE_SENDER = 8;
    public static final int FILE_THUMBNAIL = 9;
    public static final int FILE_NAME = 10;
    public static final int FILE_NEW = 11;
    public static final int SENDER_LIST = 21;

    private static final String TAG = "MediaProvider";

    /**
     * Global provider initialization.
     */
    public MediaProvider() {
        // Create and initialize URI matcher.
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME, FILE);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", FILE_ID);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_LOC + "/#", FILE_LOC);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_PATH +"/#", FILE_PATH);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_SIZE + "/#", FILE_SIZE);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_DATE + "/#", FILE_DATE);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_MIME + "/#", FILE_MIME);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_SENDER + "/#", FILE_SENDER);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_THUMBNAIL + "/#", FILE_THUMBNAIL);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_NAME + "/#", FILE_NAME);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + COLUMN_NAME_NEW + "/#", FILE_NEW);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + SENDER_LIST_PATH, SENDER_LIST);

        // Create and initialize projection map for all columns.  This is
        // simply an identity mapping.
        mFilesProjectionMap = new HashMap<String, String>();
        mFilesProjectionMap.put(MediaTable._ID, MediaTable._ID);
        mFilesProjectionMap.put(COLUMN_NAME_LOC, COLUMN_NAME_LOC);
        mFilesProjectionMap.put(COLUMN_NAME_PATH, COLUMN_NAME_PATH);
        mFilesProjectionMap.put(COLUMN_NAME_SIZE, COLUMN_NAME_SIZE);
        mFilesProjectionMap.put(COLUMN_NAME_DATE, COLUMN_NAME_DATE);
        mFilesProjectionMap.put(COLUMN_NAME_MIME, COLUMN_NAME_MIME);
        mFilesProjectionMap.put(COLUMN_NAME_SENDER, COLUMN_NAME_SENDER);
        mFilesProjectionMap.put(COLUMN_NAME_THUMBNAIL, COLUMN_NAME_THUMBNAIL);
        mFilesProjectionMap.put(COLUMN_NAME_NAME, COLUMN_NAME_NAME);
        mFilesProjectionMap.put(COLUMN_NAME_NEW, COLUMN_NAME_NEW);
    }

    /**
     * Perform provider creation.
     */
	@Override
	public boolean onCreate() {
		mDbhelper = new FileManagerDbHelper(getContext());
        // Assumes that any failures will be reported by a thrown exception.
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MediaTable.TABLE_NAME);

        SQLiteDatabase db = mDbhelper.getReadableDatabase();
        CharSequence inWhere = null;
        
        Log.w(TAG, "uri is " + uri + ", match " + mUriMatcher.match(uri));

        switch (mUriMatcher.match(uri)) {
            case FILE:
                // If the incoming URI is for main table.
                break;

            case FILE_ID:
                // The incoming URI is for a single row.
            	inWhere = MediaTable._ID;
                break;
                
            case FILE_LOC:
                // The incoming URI is for a single row.
            	inWhere = MediaTable.COLUMN_NAME_LOC;
                break;
                
            case FILE_PATH:
                // The incoming URI is for a single row.
            	inWhere = MediaTable.COLUMN_NAME_PATH;
                break;
                
            case FILE_SIZE:
            	inWhere = MediaTable.COLUMN_NAME_SIZE;
                break;
                
            case FILE_DATE:
            	inWhere = MediaTable.COLUMN_NAME_DATE;
                break;
                
            case FILE_MIME:
            	inWhere = MediaTable.COLUMN_NAME_MIME;
                break;
                
            case FILE_SENDER:
            	inWhere = MediaTable.COLUMN_NAME_SENDER;
                break;
                
            case FILE_THUMBNAIL:
            	inWhere = MediaTable.COLUMN_NAME_THUMBNAIL;
                break;
            
            case FILE_NAME:
            	inWhere = MediaTable.COLUMN_NAME_NAME;
            	break;
            	
            case FILE_NEW:
            	inWhere = MediaTable.COLUMN_NAME_NEW;
            	break;
            
            case SENDER_LIST:
                Cursor cursor = db.query(true, MediaTable.TABLE_NAME, 
                		new String[] { MediaTable.COLUMN_NAME_SENDER}, 
                		null, null, null, null, 
                		MediaTable.COLUMN_NAME_SENDER + " ASC " , null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + ", match " + mUriMatcher.match(uri));
        }
        

    	qb.setProjectionMap(mFilesProjectionMap);
		if (inWhere != null) {
			qb.appendWhere(inWhere + "=?");
			selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs,
					new String[] { uri.getLastPathSegment() });
    	}

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = MediaTable.DEFAULT_SORT_ORDER;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null /* no group */, null /* no filter */, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case FILE:
			return MediaTable.CONTENT_TYPE;
		case FILE_ID:
		case FILE_PATH:
		case FILE_LOC:
		case FILE_DATE:
		case FILE_SIZE:
		case FILE_SENDER:
		case FILE_THUMBNAIL:
		case FILE_MIME:
		case FILE_NAME:
		case FILE_NEW:
			return MediaTable.CONTENT_ITEM_TYPE;
		case SENDER_LIST:
			return MediaTable.CONTENT_SENDER_TYPE;								
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}


	@Override
	public Uri insert(Uri uri, ContentValues values /*initialValues*/) {
        if (mUriMatcher.match(uri) != FILE) {
            // Can only insert into to file URI.
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (values == null)
            throw new IllegalArgumentException(" values can not null.");
        /*
        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        if (values.containsKey(FileTable.COLUMN_NAME_DATA) == false) {
            values.put(MainTable.COLUMN_NAME_DATA, "");
        }
        */

        SQLiteDatabase db = mDbhelper.getWritableDatabase();

        long rowId = db.insertWithOnConflict(MediaTable.TABLE_NAME, null, values, 
        		SQLiteDatabase.CONFLICT_IGNORE);

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(MediaTable.CONTENT_ID_URI_BASE, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        Log.w(TAG, "Failed to insert row into " + uri);
        return null;
        //throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbhelper.getWritableDatabase();
        String finalWhere;

        int count;

        switch (mUriMatcher.match(uri)) {
            case FILE:
                // If URI is main table, delete uses incoming where clause and args.
                count = db.delete(MediaTable.TABLE_NAME, selection, selectionArgs);
                break;

                // If the incoming URI matches a single note ID, does the delete based on the
                // incoming data, but modifies the where clause to restrict it to the
                // particular note ID.
            case FILE_ID:
                // If URI is for a particular row ID, delete is based on incoming
                // data but modified to restrict to the given ID.
                finalWhere = DatabaseUtils.concatenateWhere(
                		MediaTable._ID + " = " + ContentUris.parseId(uri), selection);
                count = db.delete(MediaTable.TABLE_NAME, finalWhere, selectionArgs);
                break;
            case FILE_LOC:  
            	// If URI is for a particular row ID, delete is based on incoming
                // data but modified to restrict to the given ID.
                finalWhere = DatabaseUtils.concatenateWhere(
                		MediaTable.COLUMN_NAME_LOC + " = " + ContentUris.parseId(uri), selection);
                count = db.delete(MediaTable.TABLE_NAME, finalWhere, selectionArgs);
                break;
                
            case FILE_PATH:
            	//finalWhere = DatabaseUtils.concatenateWhere(
                //		FileTable._ID + " = " + ContentUris.parseId(uri), selection);
            	finalWhere = selection;
                count = db.delete(MediaTable.TABLE_NAME, finalWhere, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

        SQLiteDatabase db = mDbhelper.getWritableDatabase();
        int count;
        String finalWhere;

        switch (mUriMatcher.match(uri)) {
            case FILE:
                // If URI is main table, update uses incoming where clause and args.
                count = db.update(MediaTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            case FILE_ID:
                // If URI is for a particular row ID, update is based on incoming
                // data but modified to restrict to the given ID.
                finalWhere = DatabaseUtils.concatenateWhere(
                		MediaTable._ID + " = " + ContentUris.parseId(uri), selection);
                count = db.update(MediaTable.TABLE_NAME, values, finalWhere, selectionArgs);
                break;
                
            case FILE_PATH:
            	//finalWhere = DatabaseUtils.concatenateWhere(
                //		FileTable._ID + " = " + ContentUris.parseId(uri), selection);
            	finalWhere = selection;
                count = db.update(MediaTable.TABLE_NAME, values, finalWhere, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " " + mUriMatcher.match(uri));
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
	}

  public static int getDatabaseVersion(Context context) {
    try {
      return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    } catch (NameNotFoundException e) {
      throw new RuntimeException("couldn't get version code for " + context);
    }
  }

    /**
     * Definition of the contract for the main table of our provider.
     */
    public static final class MediaTable implements BaseColumns {
        // This class cannot be instantiated
        private MediaTable() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "smartlinkmedia";

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse("content://" + AUTHORITY + "/files");

        /**
         * The content URI base for a single row of data. Callers must
         * append a numeric row id to this Uri to retrieve a row
         */
        public static final Uri CONTENT_ID_URI_BASE
                = Uri.parse("content://" + AUTHORITY + "/files/");

        /**
         * The MIME type of {@link #CONTENT_URI}.
         */
        public static final String CONTENT_TYPE
                = "vnd.android.cursor.dir/vnd.alcatel.smartlinkv3.media";
        
        public static final String CONTENT_SENDER_TYPE
        		= "vnd.android.cursor.dir/vnd.alcatel.smartlinkv3.media.senderlist";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single row.
         */
        public static final String CONTENT_ITEM_TYPE
                = "vnd.android.cursor.item/vnd.alcatel.smartlinkv3.media";
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "path COLLATE LOCALIZED ASC";
        //"data COLLATE LOCALIZED ASC";

        /**
         * Column name for the single column holding our path.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PATH = "path";

        public static final String COLUMN_NAME_NAME = "name"; 
        /**
         * Column name for the single column holding file storage location.
         * <P>Type: INTEGER ,
         * 			1 internal FLASH, 
         * 			2 external SD card storage,
         * 			3 USB storage</P>
         */
        public static final String COLUMN_NAME_LOC = "location"; 
        /**
         * Column name for the single column holding file MIME.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_MIME = "mime";
        /**
         * Column name for the single column holding file size.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_SENDER = "sender"; 
        /**
         * Column name for the single column holding file size.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_DATE = "date"; //send date
        /**
         * Column name for the single column holding file size.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_SIZE = "size";
        /**
         * Column name for the single column holding thumb nail of video or picture.
         * <P>Type: BLOB</P>
         */
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        
        /**
         * Column name for the single column holding WHETHER USER CHECK THIS FILE.
         * <P>Type: BLOB</P>
         */
        public static final String COLUMN_NAME_NEW = "isNew";
        
        public static final String SENDER_LIST_PATH = "senderlist";
        
        //public static final int FILE_LOC_FLASH = 1;
        //public static final int FILE_LOC_SD = 2;
        //public static final int FILE_LOC_USB = 3;
    }	

	public class FileManagerDbHelper extends SQLiteOpenHelper {
		// database
		private static final String DB_NAME = "FileManager.db";
		//ADD new attribute , update from 1 to 2.
		private static final int DB_VERSION = 2;

		// table name
		public static final String FilesInFlash_Table = "FileInFlash";

		// table column
		public static final int BLOCK_TABLE_ID = 0;
		public static final int BLOCK_TABLE_NUMBER = 1;

		private Context mContext;
    private SQLiteDatabase mDatabase;

		public FileManagerDbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			mContext = context;
		}

		@SuppressWarnings("unused")
		@Override
		public void onCreate(SQLiteDatabase db) {
			if (true) {
				db.execSQL("CREATE TABLE " + MediaTable.TABLE_NAME + " ( "
						+ MediaTable._ID + " INTEGER PRIMARY KEY,"
						+ MediaTable.COLUMN_NAME_PATH
						+ " TEXT NOT NULL UNIQUE,"						
						+ MediaTable.COLUMN_NAME_NAME + " TEXT,"
						+ MediaTable.COLUMN_NAME_MIME + " TEXT,"
						+ MediaTable.COLUMN_NAME_SENDER + " TEXT,"
						+ MediaTable.COLUMN_NAME_LOC + " INTEGER,"
						+ MediaTable.COLUMN_NAME_DATE + " INTEGER," // LONG, OR DATE
						+ MediaTable.COLUMN_NAME_SIZE + " INTEGER,"
						+ MediaTable.COLUMN_NAME_NEW + " INTEGER,"
						+ MediaTable.COLUMN_NAME_THUMBNAIL + " BLOB" + " );");

			} else {
				createTable(db);
			}
      mDatabase = db;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			// Kills the table and existing data
			db.execSQL("DROP TABLE IF EXISTS " + MediaTable.TABLE_NAME);

			// Recreates the database with a new version
			onCreate(db);
		}
		
		private void createTable(SQLiteDatabase db) {
			String sql = "CREATE TABLE "
					+ FilesInFlash_Table
					+ "(id INTEGER PRIMARY KEY NOT NULL, path TEXT NOT NULL UNIQUE)";
			db.execSQL(sql);
		}

	}
}