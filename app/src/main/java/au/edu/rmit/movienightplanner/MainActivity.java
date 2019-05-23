package au.edu.rmit.movienightplanner;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import au.edu.rmit.movienightplanner.activity.MapsActivity;
import au.edu.rmit.movienightplanner.fragment.CalendarFragment;
import au.edu.rmit.movienightplanner.fragment.EditEventFragment;
import au.edu.rmit.movienightplanner.fragment.EventDetailFragment;
import au.edu.rmit.movienightplanner.fragment.MovieFragment;
import au.edu.rmit.movienightplanner.fragment.EventFragment;
import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Event;
import au.edu.rmit.movienightplanner.receiver.NetworkInfoReceiver;
import au.edu.rmit.movienightplanner.receiver.NotificationActionReceiver;
import au.edu.rmit.movienightplanner.service.NotificationJobService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String UPDATE_ACTION = "update_action";
    public static final int UPDATE = 1;
    public static final int CHECK_SERVICE = 2;

    /*
    activity is the core of an android app
    it may play the role of a controller
     */

    // from activity_main.xml
    private RelativeLayout main_body;
    private LinearLayout main_bottom_bar;

    private RelativeLayout bottom_bar_btn_1;
    private TextView bottom_bar_text_1;
    private ImageView bottom_bar_image_1;

    private TextView bottom_bar_text_movie;
    private ImageView bottom_bar_image_movie;
    private RelativeLayout bottom_bar_btn_movie;

    private TextView bottom_bar_text_event;
    private ImageView bottom_bar_image_event;
    private RelativeLayout bottom_bar_btn_event;

    private TextView bottom_bar_text_calendar;
    private ImageView bottom_bar_image_calendar;
    private RelativeLayout bottom_bar_btn_calendar;
    private final int movie = 1;
    private final int event = 2;
    private final int calendar = 3;
    private final int map = 4;


    private NetworkInfoReceiver mReceiver;
    private NotificationActionReceiver notificationActionReceiver;
    private NavigationReceiver navigationReceiver;
    private static int currentFragment;

    private EventFragment eventFragment;
    private MovieFragment movieFragment;
    private CalendarFragment calendarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DAO.initData(this);
        initBody();
        initBottomBar();
        initFragments();
        setInitState();
        registerNetworkReceiver();
        registerNotificationReceiver();
        registerNavigationReceiver();
        initPermission();
        startSingleNotificationJobService();
        startLoopNotificationJobService();
    }

    private void registerNavigationReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        navigationReceiver = new NavigationReceiver();
        registerReceiver(navigationReceiver, filter);
    }

    private class NavigationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", -1);
            if (type == UPDATE) {
                eventFragment = new EventFragment();
                if (currentFragment == event) {
                    getSupportFragmentManager().beginTransaction().add(R.id.main_body, eventFragment).commit();
                }
            } else if (type == CHECK_SERVICE) {
                startSingleNotificationJobService();
            }
        }
    }

    private void registerNotificationReceiver() {
        notificationActionReceiver = new NotificationActionReceiver();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(NotificationJobService.REMIND_LATER);
        filter1.addAction(NotificationJobService.CANCEL);
        filter1.addAction(NotificationJobService.DISMISS);
        registerReceiver(notificationActionReceiver, filter1);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println("onNewIntent()");
        super.onNewIntent(intent);
        int intentType = intent.getIntExtra("type", -1);
        String eventId = intent.getStringExtra("event");
        Event event = DAO.getEvents().get(eventId);
        if (intentType == 4 && event != null) {
            DAO.setCurrentEvent(event);
            setSelectedStatus(0);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_body,
                                                                   new EventDetailFragment()).commitAllowingStateLoss();
        }
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        System.out.println("onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart()");
        super.onRestart();
    }
    @Override
    protected void onResume() {
        System.out.println("Enter RESUME");
        super.onResume();
    }
    private void startSingleNotificationJobService() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder singleBuilder = new JobInfo.Builder(1, new ComponentName(this,
                                                                           NotificationJobService.class));
        singleBuilder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(5000));
        singleBuilder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(1000));
        singleBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        singleBuilder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);
        singleBuilder.setRequiresCharging(false);
        singleBuilder.setPersisted(false);
        jobScheduler.schedule(singleBuilder.build());
    }

    private void startLoopNotificationJobService() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(2, new ComponentName(this,
                                                                           NotificationJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);
        builder.setRequiresCharging(false);
        builder.setPersisted(true);
        builder.setPeriodic(DAO.getCheckPeriod());
        jobScheduler.schedule(builder.build());
    }


    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                                                      Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                      Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startSingleNotificationJobService();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                             Manifest.permission.ACCESS_COARSE_LOCATION)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        return;
                    }
                }
                break;
            }
            case 2:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_body,
                                                                           new EditEventFragment()).commit();
                    this.setSelectedStatus(0);
                }
                break;
            }
            case 0:
                System.out.println("Verify RequestCode");
        }
    }

    private void initFragments() {
        eventFragment = new EventFragment();
        movieFragment = new MovieFragment();
        calendarFragment = new CalendarFragment();
    }

    @Override
    public void onBackPressed() {
        if(currentFragment == event) {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Tips：");
            builder.setMessage("Are You Sure To Quit");

            builder.setPositiveButton("Later",null);

            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else {
            clearBottomImageState();
            setSelectedStatus(event);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_body, eventFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterReceiver(notificationActionReceiver);
        unregisterReceiver(navigationReceiver);
        super.onDestroy();
    }

    private void registerNetworkReceiver() {
        mReceiver = new NetworkInfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private void setInitState() {
        setSelectedStatus(event);
        getSupportFragmentManager().beginTransaction().add(R.id.main_body, this.eventFragment).commit();
    }

    private void initBody() {
        main_body = findViewById(R.id.main_body);
    }


    private void initBottomBar() {

        bottom_bar_text_movie = findViewById(R.id.bottom_bar_text_movie);
        bottom_bar_image_movie = findViewById(R.id.bottom_bar_image_movie);
        bottom_bar_btn_movie = findViewById(R.id.bottom_bar_btn_movie);

        bottom_bar_text_event = findViewById(R.id.bottom_bar_text_event);
        bottom_bar_image_event = findViewById(R.id.bottom_bar_image_event);
        bottom_bar_btn_event = findViewById(R.id.bottom_bar_btn_event);

        bottom_bar_text_calendar = findViewById(R.id.bottom_bar_text_calendar);
        bottom_bar_image_calendar = findViewById(R.id.bottom_bar_image_calendar);
        bottom_bar_btn_calendar = findViewById(R.id.bottom_bar_btn_calendar);

        main_bottom_bar = findViewById(R.id.main_bottom_bar);

        setListener();
    }

    private void setListener() {
        for (int i = 0; i < main_bottom_bar.getChildCount(); i++) {
            main_bottom_bar.getChildAt(i).setOnClickListener(this);
        }
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom_bar_btn_movie:
                clearBottomImageState();
                setSelectedStatus(movie);
                // load movie data and generate layout in fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body,
                                                                       movieFragment).commit();
                break;
            case R.id.bottom_bar_btn_event:
                clearBottomImageState();
                setSelectedStatus(event);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body,
                                                                       eventFragment).commit();
                break;
            case R.id.bottom_bar_btn_calendar:
                clearBottomImageState();
                setSelectedStatus(calendar);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body,
                                                                       calendarFragment).commit();
                break;
            case R.id.bottom_bar_btn_map:
                clearBottomImageState();
                setSelectedStatus(map);
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                v.getContext().startActivity(intent);
        }
    }



    public void setSelectedStatus(int i) {
        clearBottomImageState();
        currentFragment = i;
        switch (i) {
            case 1:
                bottom_bar_image_movie.setImageResource(R.drawable.movie_button_selected);
                bottom_bar_text_movie.setTextColor(Color.parseColor("#0097f7"));
                break;
            case 2:
                bottom_bar_image_event.setImageResource(R.drawable.event_button_seleted);
                bottom_bar_text_event.setTextColor(Color.parseColor("#0097f7"));
                break;
            case 3:
                bottom_bar_image_calendar.setImageResource(R.drawable.calendar_button_selected);
                bottom_bar_text_calendar.setTextColor(Color.parseColor("#0097f7"));
                break;
        }
    }

    private void clearBottomImageState() {
        bottom_bar_text_movie.setTextColor(Color.parseColor("#666666"));
        bottom_bar_text_event.setTextColor(Color.parseColor("#666666"));
        bottom_bar_text_calendar.setTextColor(Color.parseColor("#666666"));

        bottom_bar_image_movie.setImageResource(R.drawable.movie_button);
        bottom_bar_image_event.setImageResource(R.drawable.event_button);
        bottom_bar_image_calendar.setImageResource(R.drawable.calendar_button);
    }

    @Override
    public void onStop() {
        DAO.saveDataToDatabase();
        super.onStop();
    }

}
