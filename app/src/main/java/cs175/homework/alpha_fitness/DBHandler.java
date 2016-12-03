package cs175.homework.alpha_fitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by joshua on 12/2/16.
 */

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Workouts";
    // Contacts table name
    private static final String TABLE_SESSIONS = "sessions";
    // Shops Table Columns names
    private static final String KEY_ID = "_workoutId";
    private static final String KEY_TIME = "time";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_CALORIES = "calories";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORKOUT_TABLE = "CREATE TABLE ” + TABLE_SESSIONS + “(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TIME + " INTEGER)";
        db.execSQL(CREATE_WORKOUT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
// Creating tables again
        onCreate(db);
    }

    public void addSession(WorkoutSession sess){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, sess.getId());
        values.put(KEY_TIME, sess.getTime());

        //insert
        db.insert(TABLE_SESSIONS, null, values);
        db.close();
    }

    public WorkoutSession getSession(int wId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SESSIONS, new String[]{KEY_ID, KEY_TIME}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
            WorkoutSession sess = new WorkoutSession(cursor.getInt(0), cursor.getInt(1) , 0, 0 );
            return sess;
    }

    public List<WorkoutSession> getAllSessions(){
        List<WorkoutSession> sessionsList = new ArrayList<WorkoutSession>();

        //select all
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                WorkoutSession session = new WorkoutSession();
                session.setId(cursor.getInt(0));
                session.setTime(cursor.getInt(1));

                sessionsList.add(session);
            }while (cursor.moveToNext());
        }

        return sessionsList;
    }
}
