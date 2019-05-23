package au.edu.rmit.movienightplanner.model;

import java.util.Date;
import java.util.List;

import static au.edu.rmit.movienightplanner.model.MovieNightPlannerDateFormat.dateFormat;

public class EventImpl extends AbstractEvent {


    public EventImpl(String id, String title, String startDate, String endDate, String venue, String[] location) {
        super(id, title, startDate, endDate, venue, location);
    }

    public EventImpl(String id, String title, Date startDate, Date endDate, String venue,
            String[] location){
        super(id, title, startDate, endDate, venue, location);
    }

    public EventImpl(String id, String title, String startDate, String endDate, String venue,
            String locationX, String locationY, List<String> attendees) {
        super(id, title, startDate, endDate, venue, locationX, locationY, attendees);
    }

    public EventImpl(String id, String title, String start, String end, String venue,
            String location, String attendees, String movieId) {
        super(id, title, start, end, venue, location, attendees, movieId);
    }

    @Override
    public String getId() {
        return super.id;
    }


    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return super.title;
    }

    @Override
    public String getStartDateString() {
        return dateFormat.format(startDate);
    }

    @Override
    public String getEndDateString() {
        return dateFormat.format(endDate);
    }

    @Override
    public Date getStartDate() {
        return super.startDate;
    }

    @Override
    public String getVenue() {
        return super.venue;
    }

    @Override
    public String[] getLocation() {
        return super.location;
    }

    @Override
    public String getLocationString() {
        return super.location[0].trim() + "," + super.location[1].trim();
    }

    @Override
    public int getNumOfAttendees() {
        return attendees.size();
    }

    @Override
    public List<String> getAttendees() {
        return attendees;
    }

    @Override
    public void addAttendee(String item) {
        attendees.add(item);
    }

    @Override
    public void removeAttendee(String item) {
        attendees.remove(item);
    }

    @Override
    public String getMovieName() {
        if(movie != null) return movie.getTitle();
        else return "null";
    }

    @Override
    public void setMovie(Movie movie) {
        super.movie = movie;
    }

    @Override
    public void setMovie(String title) {
        for (String id : DAO.getMovies().keySet()) {
            if (DAO.getMovies().get(id).getTitle().equals(title)) {
                super.movie = DAO.getMovies().get(id);
            }
        }

    }

    @Override
    public String getMovieId() {
        if(this.movie == null) return null;
        else return this.movie.getId();
    }
}
