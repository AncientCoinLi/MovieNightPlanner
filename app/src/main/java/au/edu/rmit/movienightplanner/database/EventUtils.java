package au.edu.rmit.movienightplanner.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import au.edu.rmit.movienightplanner.model.Event;
import au.edu.rmit.movienightplanner.model.Movie;

public class EventUtils {

    public static void addMovie(SQLiteDatabase db, Movie movie) {
        ContentValues values = new ContentValues();
        values.put("id", movie.getId());
        values.put("title", movie.getTitle());
        values.put("year", movie.getYear());
        values.put("poster", movie.getPoster());
        long rowId = db.insert(Database.TABLE_MOVIE, null, values);
    }

    public static void addEvent(SQLiteDatabase db, Event event) {
        ContentValues values = new ContentValues();
        values.put("id", event.getId());
        values.put("title", event.getTitle());
        values.put("start", event.getStartDateString());
        values.put("end", event.getEndDateString());
        values.put("venue", event.getVenue());
        values.put("location", event.getLocationString());
        StringBuffer attendees = new StringBuffer();
        for(String attendee : event.getAttendees()){
            attendees.append(attendee+";");
        }
        String attendee = attendees.toString();
        if(attendee.equals("")) attendee = null;
        values.put("attendees", attendee);
        values.put("movieid", event.getMovieId());
        long rowId = db.insert(Database.TABLE_EVENT, null, values);
    }

}
