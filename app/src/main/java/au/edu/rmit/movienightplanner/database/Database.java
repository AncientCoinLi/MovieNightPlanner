package au.edu.rmit.movienightplanner.database;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.edu.rmit.movienightplanner.model.Event;
import au.edu.rmit.movienightplanner.model.EventImpl;
import au.edu.rmit.movienightplanner.model.Movie;
import au.edu.rmit.movienightplanner.model.MovieImpl;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;


public class Database {

    private static final String DATABASE_NAME = "movie_night_planner.db";

    // TABLE (COLUMN) NAMES
    static final String TABLE_EVENT = "tbl_events";
    static final String TABLE_MOVIE = "tbl_movies";

    // SQL CREATE AND DROP TABLE STATEMENTS
    // Modified by Caspar to have _ID field which is required by CursorLoader
    private static final String CREATE_EVENT_TABLE = String.format(
            "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY, title TEXT, start TEXT, end" +
                    " TEXT, venue TEXT, location TEXT, attendees TEXT, movieid TEXT);",
            TABLE_EVENT);
    private static final String CREATE_MOVIE_TABLE = String.format(
            "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY, title TEXT, year INTEGER, " +
                    "poster TEXT);", TABLE_MOVIE);
    private static final String CHECK_TABLE_COUNT = "SELECT COUNT(*) FROM sqlite_master " +
            "WHERE TYPE = 'table' and name='tbl_movies'";

    private static final String DROP_EVENT_TABLE = "DROP TABLE IF EXISTS tbl_events;";
    private static final String DROP_MOVIE_TABLE = "DROP TABLE IF EXISTS tbl_movies;";
    private static SQLiteDatabase mDatabase;

    public Database(Activity activity) {
        mDatabase = activity.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        //mDatabase.execSQL(CREATE_MOVIE_TABLE);
        //mDatabase.execSQL(CREATE_EVENT_TABLE);
    }

    public boolean checkTableExists() {
        Cursor cursor = mDatabase.rawQuery(CHECK_TABLE_COUNT, null);
        if(cursor.moveToNext()) {
            int count = cursor.getInt(0);
            return count > 0;
        }
        return false;
    }

    public List<HashMap> readData() {
        HashMap<String, Movie> movies;
        HashMap<String, Event> events = new HashMap<>();
        movies = readMovies();
        events = readEvents();
        List<HashMap> data = new ArrayList<>();
        data.add(movies);
        data.add(events);
        return data;
    }

    private HashMap<String, Event> readEvents() {
        HashMap<String, Event> events = new HashMap<>();
        Cursor cursor = mDatabase.query(TABLE_EVENT, null,null, null, null,null, null);
        Event event;
        if (cursor.moveToFirst()) {
            do{
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String start = cursor.getString(2);
                String end = cursor.getString(3);
                String venue = cursor.getString(4);
                String location = cursor.getString(5);
                String attendees = cursor.getString(6);
                String movieId = cursor.getString(7);
                event = new EventImpl(id+"", title, start, end, venue, location, attendees, movieId);
                events.put(id+"", event);
            }while (cursor.moveToNext());
        }
        return events;
    }

    private HashMap<String, Movie> readMovies() {
        HashMap<String, Movie> movies = null;
        Cursor cursor = mDatabase.query(TABLE_MOVIE, null,null, null, null,null, null);
        Movie movie;
        if (cursor.moveToFirst()) {
            movies = new HashMap<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.move(i);
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                int year = cursor.getInt(2);
                String poster = cursor.getString(3);
                movie = new MovieImpl(id, title, year, poster);
                movies.put(id, movie);
            }
        }
        return movies;
    }

    public void writeData(List<Movie> movies, List<Event> events){
        // delete tables and rewrite new data into database
        // drop
        mDatabase.execSQL(DROP_EVENT_TABLE);
        mDatabase.execSQL(DROP_MOVIE_TABLE);
        // create
        mDatabase.execSQL(CREATE_MOVIE_TABLE);
        mDatabase.execSQL(CREATE_EVENT_TABLE);
        for(Movie movie: movies){
            EventUtils.addMovie(mDatabase, movie);
        }
        for (Event event : events) {
            EventUtils.addEvent(mDatabase, event);
        }
        //mDatabase.close();
    }


}
