package au.edu.rmit.movienightplanner.model;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractEvent implements Event {

    protected String id;
    protected String title;
    protected Date startDate;
    protected Date endDate;
    protected String venue;
    protected String[] location;
    protected Movie movie;
    protected List<String> attendees;

    public AbstractEvent(String id, String title, String startDate, String endDate, String venue,
     String[] location) {
        // "2/01/2019 1:00:00 AM"
        this.id = id;
        this.title = title;
        this.venue = venue;
        this.location = location;
        this.attendees = new ArrayList<>();
        movie = null;
        try {
            this.startDate = MovieNightPlannerDateFormat.dateFormat.parse(startDate);
            this.endDate = MovieNightPlannerDateFormat.dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public AbstractEvent(String id, String title, String startDate, String endDate, String venue,
            String locationX, String locationY, List<String> attendees) {
        this.id = id;
        this.title = title;
        this.venue = venue;
        this.location = new String[2];
        this.location[0] = locationX;
        this.location[1] = locationY;
        this.attendees = attendees;
        movie = null;
        try {
            this.startDate = MovieNightPlannerDateFormat.dateFormat.parse(startDate);
            this.endDate = MovieNightPlannerDateFormat.dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public AbstractEvent(String id, String title, Date startDate, Date endDate, String venue,
            String[] location) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.venue = venue;
        this.location = location;
        this.attendees = new ArrayList<>();
        movie = null;
    }

    public AbstractEvent(String id, String title, String start, String end, String venue,
            String location, String attendees, String movieId) {
        this.id = id;
        this.title = title;
        try {
            this.startDate = MovieNightPlannerDateFormat.dateFormat.parse(start);
            this.endDate = MovieNightPlannerDateFormat.dateFormat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.venue = venue;
        this.location = location.split(",");
        this.attendees = new ArrayList<>();
        if(attendees != null) {
            String[] attendeeArray = attendees.split(";");
            for (String attendee : attendeeArray) {
                this.attendees.add(attendee);
            }
        }
        if(movieId != null)
            this.movie = DAO.getMovies().get(movieId);
    }


    @Override
    public String toString() {
        // startDate.toString() and endDate.toString() do not work well
        return id + "," + title + "," + venue + "," + location[0] + "," + location[1];
    }


}
