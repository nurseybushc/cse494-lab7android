package edu.asu.bscs.a1203737023.lab7android;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
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
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        DialogInterface.OnClickListener, TextView.OnEditorActionListener {

    private Button addButt, removeButt;
    private EditText titleET, yearET, ratedET, releasedET, runtimeET, genreET, actorsET, plotET;
    private Spinner movieTitlesSpinner;
    private String selectedMovie;
    private String[] movies;
    private ArrayAdapter<String> moviesAdapter;
    MovieDB mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButt = (Button)findViewById(R.id.addButt);
        removeButt = (Button)findViewById(R.id.removeButt);
        titleET = (EditText)findViewById(R.id.titleET);
        yearET = (EditText)findViewById(R.id.yearET);
        ratedET = (EditText)findViewById(R.id.ratedET);
        releasedET = (EditText)findViewById(R.id.releasedET);
        runtimeET = (EditText)findViewById(R.id.runtimeET);
        genreET = (EditText)findViewById(R.id.genreET);
        actorsET = (EditText)findViewById(R.id.actorsET);
        plotET = (EditText)findViewById(R.id.plotET);
        movieTitlesSpinner = (Spinner)findViewById(R.id.movieTitlesSpinner);

        this.selectedMovie = this.setupMovieSpinner();
        loadFields();

        //mydb = new MovieDB(this);
        //ArrayList<String> array_list = mydb.getAllMovies();
        //ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, array_list);
        //movieTitlesSpinner.setAdapter(arrayAdapter);

    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        setupMovieSpinner();
    }

    private String setupMovieSpinner(){
        String ret = "unknown";
        try{
            MovieDB db = new MovieDB((Context)this);
            SQLiteDatabase crsDB = db.openDB();
            Cursor cur = crsDB.rawQuery("select title from movie;", new String[]{});
            ArrayList<String> al = new ArrayList<String>();
            while(cur.moveToNext()){
                try{
                    al.add(cur.getString(0));
                }catch(Exception ex){
                    android.util.Log.w(this.getClass().getSimpleName(),"exception stepping thru cursor"+ex.getMessage());
                }
            }
            movies = (String[]) al.toArray(new String[al.size()]);
            ret = (movies.length>0 ? movies[0] : "unknown");
            moviesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, movies);
            movieTitlesSpinner.setAdapter(moviesAdapter);
            movieTitlesSpinner.setOnItemSelectedListener(this);
            cur.close();
            crsDB.close();
            db.close();
        }catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"unable to setup student spinner");
        }
        return ret;
    }

    private void loadFields() {
        try {
            MovieDB db = new MovieDB((Context) this);
            SQLiteDatabase crsDB = db.openDB();
            Cursor cur = crsDB.rawQuery("select title,year,rated,released,runtime,genre,actors,plot from movie where title=? ;",
                    new String[]{selectedMovie});
            String title = "unknown";
            String year = "unknown";
            String rated = "unknown";
            String released = "unknown";
            String runtime = "unknown";
            String genre = "unknown";
            String actors = "unknown";
            String plot = "unknown";


            while (cur.moveToNext()) {
                title = cur.getString(0);
                year = cur.getString(1);
                rated = cur.getString(2);
                released = cur.getString(3);
                runtime = cur.getString(4);
                genre = cur.getString(5);
                actors = cur.getString(6);
                plot = cur.getString(7);
            }

            titleET.setText(title);
            yearET.setText(year);
            ratedET.setText(rated);
            releasedET.setText(released);
            runtimeET.setText(runtime);
            genreET.setText(genre);
            actorsET.setText(actors);
            plotET.setText(plot);


            cur.close();
            /*
            cur = crsDB.rawQuery("select course.coursename FROM student,course,studenttakes WHERE student.studentid=studenttakes.studentid and course.courseid=studenttakes.courseid and student.name=?",
                    new String[]{selectedStudent});
            ArrayList<String> al = new ArrayList<>();
            while(cur.moveToNext()){
                al.add(cur.getString(0));
            }
            String[] courses = (String[]) al.toArray(new String[al.size()]);
            if (courses.length == 0){
                courses = new String[]{"unknown"};
            }
            courseAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, courses);
            coursesSpinner.setAdapter(courseAdapter);
            coursesSpinner.setOnItemSelectedListener(this);
            cur.close();*/
            crsDB.close();
            db.close();
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(), "Exception getting movie info: " +
                    ex.getMessage());
        }
    }

    public void removeClicked(View v){
        android.util.Log.d(this.getClass().getSimpleName(), "remove Clicked");
        MovieDB db = new MovieDB((Context)this);

        SQLiteDatabase crsDB = db.openDB();
        String title = this.titleET.getText().toString();

        String selection = "title = ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { title };
// Issue SQL statement.
        crsDB.delete("movie", selection, selectionArgs);
        crsDB.close();
        db.close();
        setupMovieSpinner();
        //delete from studenttakes where courseid=(select courseid from course where coursename='Ser421 Web/Mobile');
        //delete from course where coursename='Ser421 Web/Mobile';
    }

    public void updateClicked(View v){
        android.util.Log.d(this.getClass().getSimpleName(), "update Clicked");
        MovieDB db = new MovieDB((Context)this);

        SQLiteDatabase crsDB = db.openDB();
        String title = this.titleET.getText().toString();
        ContentValues values = new ContentValues();

        String year = this.yearET.getText().toString();
        String rated= this.ratedET.getText().toString();
        String released = this.releasedET.getText().toString();
        String runtime = this.runtimeET.getText().toString();
        String genre = this.genreET.getText().toString();
        String actors = this.actorsET.getText().toString();
        String plot = this.plotET.getText().toString();


        values.put("year", year);
        values.put("rated", rated);
        values.put("released", released);
        values.put("runtime", runtime);
        values.put("genre", genre);
        values.put("actors", actors);
        values.put("plot", plot);

        String selection = "title = ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { title };
// Issue SQL statement.
        int count = crsDB.update("movie", values, selection, selectionArgs);
        crsDB.close();
        db.close();
        //setupMovieSpinner();
        //delete from studenttakes where courseid=(select courseid from course where coursename='Ser421 Web/Mobile');
        //delete from course where coursename='Ser421 Web/Mobile';
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
        // note that inputType and keyboard actions imeOptions must be defined to manage the keyboard
        // these can be defined in the xml as an attribute of the EditText.
        // returning false from this method
        android.util.Log.d(this.getClass().getSimpleName(), "onEditorAction: keycode " +
                ((event == null) ? "null" : event.toString()) + " actionId " + actionId);
        if(actionId== EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE){
            android.util.Log.d(this.getClass().getSimpleName(),"entry is: "+v.getText().toString());
        }
        return false; // without returning false, the keyboard will not disappear or move to next field
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        android.util.Log.d(this.getClass().getSimpleName(), "onClick with which= " +which);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        /*if(parent.getId() == R.id.movieTitlesSpinner) {
            this.selectedMovie = studentSpinner.getSelectedItem().toString();
            android.util.Log.d(this.getClass().getSimpleName(), "studentSpinner item selected " + selectedStudent);
            this.loadFields();
        }else{
            android.util.Log.d(this.getClass().getSimpleName(), "CourseSpinner item selected " + coursesSpinner.getSelectedItem());
        }*/
        selectedMovie = movieTitlesSpinner.getSelectedItem().toString();
        android.util.Log.d(this.getClass().getSimpleName(),"spinner item selected "+selectedMovie);
        this.loadFields();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        android.util.Log.d(this.getClass().getSimpleName(), "onNothingSelected");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.util.Log.d(this.getClass().getSimpleName(), "called onOptionsItemSelected()");
        if (item.getItemId() == R.id.action_add){
            Log.d("Action bar button", "add clicked");
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);

            //launch add activity here
        } else if(item.getItemId() == R.id.action_search) {
            Log.d("Action bar button", "search clicked");
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

