package au.edu.rmit.movienightplanner.model;

import java.util.Date;
import java.util.List;

public interface Event {

    String toString();

    // Id, Title, Start Date, End Date, Venue, Location (latitude/longitude)
    String getId();

    void setId(String id);

    String getTitle();

    String getStartDateString();

    String getEndDateString();

    Date getStartDate();

    String getVenue();

    String[] getLocation();

    String getLocationString();

    int getNumOfAttendees();

    List<String> getAttendees();

    void addAttendee(String item);

    void removeAttendee(String item);

    String getMovieName();

    void setMovie(Movie movie);

    void setMovie(String title);

    String getMovieId();
}
