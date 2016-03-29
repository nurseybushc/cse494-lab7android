package edu.asu.bscs.a1203737023.lab7android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Chance Nursey-Bush on 3/29/16.
 * @author   Chance Nursey-Bush    mailto:cnurseyb@asu.edu.
 * @version Mar 29, 2016
 * Copyright 2016 Chance Nursey-Bush
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MovieDB extends SQLiteOpenHelper {

    private static final boolean debugon = true;
    private static final int DATABASE_VERSION = 3;
    private static String dbName = "moviedb.db";
    private String dbPath;
    private SQLiteDatabase crsDB;
    private final Context context;


    public MovieDB(Context context){
        super(context,dbName, null, DATABASE_VERSION);
        this.context = context;
        dbPath = context.getFilesDir().getPath()+"/";
        android.util.Log.d(this.getClass().getSimpleName(), "dbpath: " + dbPath);
    }

    public void createDB() throws IOException {
        this.getReadableDatabase();
        try {
            copyDB();
        } catch (IOException e) {
            android.util.Log.w(this.getClass().getSimpleName(),
                    "createDB Error copying database " + e.getMessage());
        }
    }

    private boolean checkDB(){    //does the database exist and is it initialized?
        SQLiteDatabase checkDB = null;
        boolean ret = false;
        try{
            //String path = dbPath + dbName + ".db";
            String path = dbPath + dbName;
            debug("MovieDB --> checkDB: path to db is", path);
            File aFile = new File(path);
            if(aFile.exists()){
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
                if (checkDB!=null) {
                    debug("MovieDB --> checkDB","opened db at: "+checkDB.getPath());
                    //Cursor tabChk = checkDB.rawQuery("SELECT name FROM sqlite_master where type='table' and name='c';", null);
                    Cursor tabChk = checkDB.rawQuery("SELECT name FROM sqlite_master where type='table' and name='movie';", null);

                    boolean crsTabExists = false;
                    if(tabChk == null){
                        debug("MovieDB --> checkDB","check for course table result set is null");
                    }else{
                        tabChk.moveToNext();
                        debug("MovieDB --> checkDB", "check for movie table result set is: " +
                                ((tabChk.isAfterLast() ? "empty" : (String) tabChk.getString(0))));
                        crsTabExists = !tabChk.isAfterLast();
                        tabChk.close();
                    }
                    if(crsTabExists){
                        Cursor c = checkDB.rawQuery("SELECT * FROM movie", null);
                        c.moveToFirst();
                        while(! c.isAfterLast()) {
                            String crsTitle = c.getString(0);
                            String crsYear = c.getString(1);
                            debug("MovieDB --> checkDB","Movie table has title: "+
                                    crsTitle + "\tYear: "+crsYear);
                            c.moveToNext();
                        }
                        ret = true;
                        c.close();
                    }
                }
            }
        }catch(SQLiteException e){
            android.util.Log.w("MovieDB->checkDB",e.getMessage());
        }
        if(checkDB != null){
            checkDB.close();
        }
        return ret;
    }

    public void copyDB() throws IOException{
        try {
            if(!checkDB()){
                // only copy the database if it doesn't already exist in my database directory
                debug("MovieDB --> copyDB", "checkDB returned false, starting copy");
                InputStream ip =  context.getResources().openRawResource(R.raw.moviedb);
                // make sure the database path exists. if not, create it.
                File aFile = new File(dbPath);
                if(!aFile.exists()){
                    aFile.mkdirs();
                }
                //String op =  dbPath  +  dbName +".db";
                String op = dbPath + dbName;
                OutputStream output = new FileOutputStream(op);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = ip.read(buffer))>0){
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                ip.close();
            }
        } catch (IOException e) {
            android.util.Log.w("MovieDB --> copyDB", "IOException: "+e.getMessage());
        }
    }

    public SQLiteDatabase openDB() throws SQLException {
        String myPath = dbPath + dbName + ".db";
        if(checkDB()) {
            crsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            debug("MovieDB --> openDB", "opened db at path: " + crsDB.getPath());
        }else{
            try {
                this.copyDB();
                crsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            }catch(Exception ex) {
                android.util.Log.w(this.getClass().getSimpleName(),"unable to copy and open db: "+ex.getMessage());
            }
        }
        return crsDB;
    }

    @Override
    public synchronized void close() {
        if(crsDB != null)
            crsDB.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void debug(String hdr, String msg){
        if(debugon){
            android.util.Log.d(hdr,msg);
        }
    }

    //check if this works
    public boolean insertMovie(String title, String year, String rated, String released,String runtime, String genre, String actors, String plot)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("year", year);
        contentValues.put("rated", rated);
        contentValues.put("released", released);
        contentValues.put("runtime", runtime);
        contentValues.put("genre", genre);
        contentValues.put("actors", actors);
        contentValues.put("plot", plot);
        db.insert("movie", null, contentValues);
        return true;
    }

    public ArrayList<String> getAllMovies()
    {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from movie", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex("title")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> getMovie(String title)
    {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] args={title};
        Cursor res =  db.rawQuery( "select * from movie where title=?", args );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex("title")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}
