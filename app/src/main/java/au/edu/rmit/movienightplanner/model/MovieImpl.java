package au.edu.rmit.movienightplanner.model;

public class MovieImpl extends AbstractMovie {

    public MovieImpl(String id, String title, int year, String poster) {
        super(id, title, year, poster);
    }

    @Override
    public String getId() {
        return super.id;
    }

    @Override
    public String getTitle() {
        return super.title;
    }

    @Override
    public int getYear() {
        return super.year;
    }

    @Override
    public String getPoster() {
        return super.poster;
    }

    @Override
    public String toString() {
        return getId()+","+getTitle()+","+getYear()+","+getPoster();
    }
}
