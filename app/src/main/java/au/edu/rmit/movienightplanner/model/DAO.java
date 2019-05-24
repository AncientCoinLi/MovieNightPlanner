package au.edu.rmit.movienightplanner.model;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import au.edu.rmit.movienightplanner.MainActivity;
import au.edu.rmit.movienightplanner.R;
import au.edu.rmit.movienightplanner.database.Database;

/**
 * read file data
 */
public class DAO {

    private static HashMap<String, Movie> movies;
    private static HashMap<String, Event> events;
    private static List<Event> futureEvents;

    public static final int NotificationInAdvance = 60;
    private static final int movieResource = R.raw.movies;
    private static final int eventResource = R.raw.events;
    private static final int configurationResource = R.raw.configuration;

    private static boolean ascending;

    private static Event editingEvent;
    private static Database db;
    private static List<Event> dismissEvents;
    private static int CheckPeriod;
    private static int RemindAgainDuration;
    private static int NotificationThreshold;

    static {
        movies = new HashMap<>();
        events = new HashMap<>();
        dismissEvents = new ArrayList<>();
        ascending = true;
    }


    public static void initData(Activity activity) {
        db = new Database(activity);
        initMovie(activity.getResources());
        initEvent(activity.getResources());
        if(db.checkTableExists()){
            // print test
            System.out.println("Database exists, read data from database");
            List<HashMap> data = db.readData();
            movies = data.get(0);
            events = data.get(1);
        }else{
            //print test
            System.out.println("database does not exist, read date from *.txt");
        }

        readSettings(activity.getResources());
        updateFutureEvents();
    }

    private static void readSettings(Resources resources) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        File setting = new File(path + "/data/data/setting");
        if(setting.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(setting);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + System.lineSeparator());
                }
                JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                CheckPeriod = jsonObject.getInt("notification period");
                RemindAgainDuration = jsonObject.getInt("remind again duration");
                NotificationThreshold = jsonObject.getInt("notification threshold");
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            InputStream inputStream = resources.openRawResource(configurationResource);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + System.lineSeparator());
                }
                JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                CheckPeriod = jsonObject.getInt("notification period");
                RemindAgainDuration = jsonObject.getInt("remind again duration");
                NotificationThreshold = jsonObject.getInt("notification threshold");
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }

    /*
    read data from movie.txt and event.txt
    initialize movies and events
     */
    private static void initMovie(Resources resources) {
        InputStream inputStream = resources.openRawResource(movieResource);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String[] content;
            String id, title, poster;
            int year;

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("//")) {
                    line = line.replace("\"", "");
                    content = line.split(",");
                    // Id, Title, Year, Poster (filename)
                    id = content[0];
                    title = content[1];
                    year = Integer.parseInt(content[2]);
                    poster = content[3];
                    movies.put(id, new MovieImpl(id, title, year, poster));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initEvent(Resources resources) {
        InputStream inputStream = resources.openRawResource(eventResource);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String[] content;
            String id, title, startDate, endDate, venue;
            String[] location;

            // Id, Title, Start Date, End Date, Venue, Location (latitude/longitude)
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("//")) {
                    line = line.replace("\"", "");
                    content = line.split(",");
                    id = content[0];
                    title = content[1];
                    startDate = content[2];
                    endDate = content[3];
                    venue = content[4];
                    location = new String[2];
                    location[0] = content[5];
                    location[1] = content[6];
                    events.put(id, new EventImpl(id, title, startDate, endDate, venue, location));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, Movie> getMovies() {
        return movies;
    }

    public static HashMap<String, Event> getEvents() {
        return events;
    }

    public static void removeEvent(String id) {
        if (events.containsKey(id)) {
            events.remove(id);
            futureEvents.remove(events.get(id));
        }
    }

    public static void removeMovie(String id) {
        if (movies.containsKey(id)) {
            movies.remove(id);
        }
    }

    public static void addEvent(String title, Date startDate, Date endDate,
            String venue, String[] location) {
        int count = events.size();
        if (events.containsKey("e" + count)) {
            count++;
        }
        addEvent(count, title, startDate, endDate, venue, location);
    }

    public static void addEvent(String title, String startDate, String endDate, String venue,
            String locationX, String locationY) {
        String[] location = new String[2];
        location[0] = locationX;
        location[1] = locationY;
        try {
            addEvent(title, MovieNightPlannerDateFormat.dateFormat.parse(startDate),
                     MovieNightPlannerDateFormat.dateFormat.parse(endDate), venue,
                     location);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void addEvent(int id, String title, Date startDate, Date endDate,
            String venue, String[] location){
        if (events.containsKey("e" + id)) {
            addEvent(id+1, title, startDate, endDate, venue, location);
        } else{
            events.put("e" + id, new EventImpl(id+"", title, startDate, endDate, venue,
                                                 location));
        }

    }

    public static void setCurrentEvent(Event event) {
        editingEvent = event;
    }

    public static Event getCurrentEvent() {
        return editingEvent;
    }

    public static void modifyEvent(String title, String startDate, String endDate, String venue,
            String locationX, String locationY) {
        if(title.equals("")) title = editingEvent.getTitle();
        if(startDate.equals("")) startDate = editingEvent.getStartDateString();
        if(endDate.equals("")) endDate = editingEvent.getEndDateString();
        if(venue.equals("")) venue = editingEvent.getVenue();
        if(locationX.equals("")) locationX = editingEvent.getLocation()[0];
        if(locationY.equals("")) locationY = editingEvent.getLocation()[1];

        String id = editingEvent.getId();
        events.remove(id);
        events.put(id, new EventImpl(id, title, startDate, endDate, venue, locationX, locationY,
                                     editingEvent.getAttendees()));
    }

    public static List<Event> getSortedEvent() {
        List<Event> list = new ArrayList<>();
        if(events.size() == 0) return list;
        for (String id : events.keySet()) {
            list.add(events.get(id));
        }
        Collections.sort(list, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                try {
                    Date e1 = MovieNightPlannerDateFormat.dateFormat.parse(o1.getStartDateString());
                    Date e2 = MovieNightPlannerDateFormat.dateFormat.parse(o2.getStartDateString());
                    if (e1.before(e2)) {
                        return -1;
                    } else return 1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return list;
    }



    public static void changeOrder() {
        ascending = !ascending;
    }

    public static boolean isAscending() {
        return ascending;
    }

    public static void saveDataToDatabase() {
        db.writeData(new ArrayList<Movie>(movies.values()), new ArrayList<Event>(events.values()));
    }

    public static int getCheckPeriod() {
        return CheckPeriod;
    }

    public static int getNotificationThreshold() {
        return NotificationThreshold;
    }

    public static int getRemindAgainDuration() {
        return RemindAgainDuration;
    }

    public static void refreshNotificationTime(HashMap<Event, Date> notificationTime) {
        notificationTime.clear();
        for (Event event : events.values()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(event.getStartDate());
            if(calendar.after(System.currentTimeMillis())) continue;
            calendar.add(Calendar.MINUTE, -NotificationThreshold-NotificationInAdvance);
            notificationTime.put(event, calendar.getTime());
        }
    }

    private static void updateFutureEvents() {
        futureEvents = new ArrayList<>();
        List<Event> sortedEvents = DAO.getSortedEvent();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        for (int i = 0; i < sortedEvents.size(); i++) {
            if (sortedEvents.get(i).getStartDate().before(currentCalendar.getTime())) {
                System.out.println("BEFORE");
                continue;
            } else if (dismissEvents.contains(sortedEvents.get(i))) {
                continue;
            } else {
                futureEvents.add(sortedEvents.get(i));
            }
            //futureEvents.add(sortedEvents.get(i));
        }
    }

    public static List<Event> getFutureEvents() {
        updateFutureEvents();
        return futureEvents;
    }

    public static void removeFromFutureEvents(Event event) {
        futureEvents.remove(event);
    }

    public static void addDismissEvent(Event event) {
        dismissEvents.add(event);
    }

    public static boolean isDismissEvent(Event event) {
        return dismissEvents.contains(event);
    }

    public static int getNotificationThresholdMillis() {
        return NotificationThreshold;
    }

    public static void saveSettings(int remind, int period, int threshold) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        DAO.NotificationThreshold = threshold;
        DAO.CheckPeriod = period;
        DAO.RemindAgainDuration = remind;
        File setting = new File(path + "/data/data/setting");
        File d = new File(path + "/data/data/");
        d.mkdirs();
        if(setting.exists()) {
            setting.delete();
        }
        try {

            setting.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(setting);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            JSONObject jsonObject = new JSONObject();
            String string = String.format(
                    "{\"notification period\": \"%d\"," +
                            "    \"notification threshold\": \"%d\"," +
                            "    \"remind again duration\": \"%d\"" +
                            "}",
                    period, threshold, remind);
            bufferedWriter.append(string);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
