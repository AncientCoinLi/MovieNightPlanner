package au.edu.rmit.movienightplanner.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import au.edu.rmit.movienightplanner.MainActivity;
import au.edu.rmit.movienightplanner.R;
import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Distance;
import au.edu.rmit.movienightplanner.model.Event;

public class NotificationJobService extends JobService {


    public static final String REMIND_LATER = "REMIND_LATER";
    public static final String DISMISS = "DISMISS";
    public static final String CANCEL = "CANCEL";

    public static final String CHANNEL_ID = "channel id";
    private static final String CHANNEL_NAME = "channel name";
    private static final int ONE_HOUR_DISTANCE = 3600;
    private static final int THRESHOLD_IN_MILLIS = DAO.getNotificationThresholdMillis();
    private final int RemindLater = 0;
    private final int Dismiss = 1;
    private final int Cancel = 2;
    private List<Event> futureEvents;
    private static NotificationJobService notificationJobService;
    private Distance distance;
    //private NotificationActionReceiver notificationActionReceiver;

    @Override
    public boolean onStartJob(JobParameters params) {
//        initBroadcastReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                doService();
            }
        }).start();
        return false;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void onCreate() {
        initialise();
    }


    private NotificationCompat.Builder getNotification(Event event) {
        String title = event.getTitle() + " is coming soon";
        String content = "Starts on " + event.getStartDateString();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(NotificationJobService.this,
                                                                            CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.notification_bg)
                .setTicker("You Have a Notification")
                .setAutoCancel(true);

        Intent dismissIntent = new Intent();
        dismissIntent.putExtra("action", 1);
        dismissIntent.putExtra("event", event.getId());
        dismissIntent.setAction(DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                                                                        Integer.parseInt(event.getId()),
                                                                        dismissIntent,
                                                                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.notification_bg, "Dismiss", dismissPendingIntent);

        Intent cancelIntent = new Intent();
        cancelIntent.putExtra("action", 2);
        cancelIntent.putExtra("event", event.getId());
        cancelIntent.setAction(CANCEL);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                                                                       Integer.parseInt(event.getId()), cancelIntent,
                                                                       PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.drawable.notification_bg, "Cancel", cancelPendingIntent);

        Intent remindLaterIntent = new Intent();
        remindLaterIntent.putExtra("action", 0);
        remindLaterIntent.putExtra("event", event.getId());
        remindLaterIntent.setAction(REMIND_LATER);
        PendingIntent remindLaterPendingIntent = PendingIntent.getBroadcast(this,
                                                                            Integer.parseInt(event.getId()),
                                                                            remindLaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.notification_bg, "Remind Later", remindLaterPendingIntent);

        return builder;
    }


    public void createChannel() {
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(R.color.colorPrimary);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }
    
    private void doService() {
        System.out.println("FUTURE EVENT SIZE = "+futureEvents.size());
        distance = new Distance(this.futureEvents);
        createChannel();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        Calendar startCalendar = Calendar.getInstance();

        try {
            HashMap<Event, Integer> eventDistance = distance.execute(getApplicationContext()).get();
            for (int i = 0; i < futureEvents.size(); i++) {
                startCalendar.setTime(futureEvents.get(i).getStartDate());
                long timeLeft = startCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
                System.out.println(timeLeft);
                System.out.println("Start Time = "+startCalendar.getTimeInMillis() +"  " +
                                           "Current Time = "+ currentCalendar.getTimeInMillis());

                if (timeLeft <= THRESHOLD_IN_MILLIS){

                    if (eventDistance.get(futureEvents.get(i)) >= ONE_HOUR_DISTANCE) {
                        NotificationCompat.Builder builder = getNotification(futureEvents.get(i));
                        Intent intent = new Intent(NotificationJobService.this, MainActivity.class);
                        intent.putExtra("event", futureEvents.get(i).getId());
                        intent.putExtra("type", 4);
                        intent.setAction(futureEvents.get(i).getTitle());
                        PendingIntent resultPendingIntent =
                                PendingIntent.getActivity(this, Integer.parseInt(futureEvents.get(i).getId()), intent,
                                                          PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setAutoCancel(true);
                        builder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(Integer.parseInt(futureEvents.get(i).getId()), builder.build());
                        //break;
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void remindLater(Event event) {
    }

    public void dismiss(Event event) {
        futureEvents.remove(event);
        DAO.addDismissEvent(event);
    }

    public void cancel(final Event event) {

        futureEvents.remove(event);
        DAO.removeEvent(event.getId());

    }

    private void initialise() {
        futureEvents = new ArrayList<>();
        for(Event event: DAO.getFutureEvents()){
            futureEvents.add(event);
        }
        futureEvents.removeAll(DAO.getDismissEvents());
    }

    public static NotificationJobService getInstance() {
        notificationJobService = new NotificationJobService();
        return notificationJobService;
    }

    public NotificationJobService() {
        initialise();
    }

}
