package co.mobilemakers.drinking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Gonzalo on 16/02/2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final static String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private final static String DATABASE_NAME = "challenges.db";
    private final static int DATABASE_VERSION = 1;
    private static final String DATABASE_PATH = "/data/data/co.mobilemakers.drinking/databases/";


    private Dao<Challenge, Integer> mContactDao = null;


    public Dao<Challenge, Integer> getContactDao() throws SQLException {
        if (mContactDao == null){
            mContactDao = getDao(Challenge.class);
        }
        return mContactDao;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(LOG_TAG, "Creating database.");
            TableUtils.createTable(connectionSource, Challenge.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error creating database.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }



    public DatabaseHelper(Context context) {
        super(context, DATABASE_PATH+DATABASE_NAME, null, DATABASE_VERSION);

        boolean dbexist = checkdatabase();
        if (!dbexist) {

            // If database did not exist, try copying existing database from assets folder.
            try {
                File dir = new File(DATABASE_PATH);
                dir.mkdirs();
                InputStream myinput = context.getAssets().open(DATABASE_NAME);
                String outfilename = DATABASE_PATH + DATABASE_NAME;
                Log.i(DatabaseHelper.class.getName(), "DB Path : " + outfilename);
                OutputStream myoutput = new FileOutputStream(outfilename);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myinput.read(buffer)) > 0) {
                    myoutput.write(buffer, 0, length);
                }
                myoutput.flush();
                myoutput.close();
                myinput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * Check whether or not database exist
    */
    private boolean checkdatabase() {
        boolean checkdb = false;

        String myPath = DATABASE_PATH + DATABASE_NAME;
        File dbfile = new File(myPath);
        checkdb = dbfile.exists();

        Log.i(DatabaseHelper.class.getName(), "DB Exist : " + checkdb);

        return checkdb;
    }
}