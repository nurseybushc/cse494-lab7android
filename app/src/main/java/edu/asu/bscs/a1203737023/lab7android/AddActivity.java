package edu.asu.bscs.a1203737023.lab7android;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

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
public class AddActivity extends Activity {
    private EditText titleET, yearET, ratedET, releasedET, runtimeET, genreET, actorsET, plotET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleET = (EditText)findViewById(R.id.titleET);
        yearET = (EditText)findViewById(R.id.yearET);
        ratedET = (EditText)findViewById(R.id.ratedET);
        releasedET = (EditText)findViewById(R.id.releasedET);
        runtimeET = (EditText)findViewById(R.id.runtimeET);
        genreET = (EditText)findViewById(R.id.genreET);
        actorsET = (EditText)findViewById(R.id.actorsET);
        plotET = (EditText)findViewById(R.id.plotET);
    }

    public void addClicked(View v){
        android.util.Log.d(this.getClass().getSimpleName(), "add Clicked. Adding: " + this.titleET.getText().toString());
        try{
            MovieDB db = new MovieDB((Context)this);

            SQLiteDatabase crsDB = db.openDB();
            //SQLiteDatabase crsDB = db.getWritableDatabase();

            String title = this.titleET.getText().toString();
            String year = this.yearET.getText().toString();
            String rated= this.ratedET.getText().toString();
            String released = this.releasedET.getText().toString();
            String runtime = this.runtimeET.getText().toString();
            String genre = this.genreET.getText().toString();
            String actors = this.actorsET.getText().toString();
            String plot = this.plotET.getText().toString();


            //String insert = "insert into student values('"+this.titleET.getText().toString()+"','"+
            //        this.yearET.getText().toString()+"','"+this.ratedET.getText().toString()+"',"+
            //        this.releasedET.getText().toString()+",null);";

            //Resources res = getResources();
            //String insertSQL = String.format(res.getString(R.string.insert_sql), title,year,rated,released,runtime,genre,actors,plot);
            //Log.d("adding", insertSQL);

            //crsDB.execSQL(insertSQL);

            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("year", year);
            values.put("rated", rated);
            values.put("released", released);
            values.put("runtime", runtime);
            values.put("genre", genre);
            values.put("actors", actors);
            values.put("plot", plot);

            long newRowId;
            newRowId = crsDB.insert("movie",null,values);


            crsDB.close();
            db.close();
            finish();
            //String addedName = this.titleET.getText().toString();
            //setupMovieSpinner();
            //this.selectedMovie = addedName;
            //this.movieTitlesSpinner.setSelection(Arrays.asList(movies).indexOf(this.selectedMovie));
            //this.loadFields();
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception adding movie information: "+
                    ex.getMessage());
        }
    }

    public void cancelAdd(View v){
        finish();
    }
}
