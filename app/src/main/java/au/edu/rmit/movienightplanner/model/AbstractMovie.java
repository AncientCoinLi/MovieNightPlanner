package au.edu.rmit.movienightplanner.model;

public abstract class AbstractMovie implements Movie {

    protected String id;
    protected String title;
    protected int year;
    protected String poster;

    public AbstractMovie(String id, String title, int year, String poster) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.poster = poster;
    }

    //parse fileData and use content in it to create object
    public AbstractMovie(String fileData) {

    }
}
