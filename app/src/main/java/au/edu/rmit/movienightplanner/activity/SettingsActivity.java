package au.edu.rmit.movienightplanner.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import au.edu.rmit.movienightplanner.R;
import au.edu.rmit.movienightplanner.fragment.EditEventFragment;
import au.edu.rmit.movienightplanner.model.DAO;

public class SettingsActivity extends Activity {
    private EditText remind_again_duration;
    private EditText notification_thredshold;
    private EditText notificaiton_period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initPermission();


    }


    private int notificationThreshold;
    private int remindAgainDuration;
    private int notificationPeriod;



    private void initialise() {
        notificationPeriod = DAO.getCheckPeriod();
        remindAgainDuration = DAO.getRemindAgainDuration();
        notificationThreshold = DAO.getNotificationThresholdMillis();
    }

    private void initDisplay() {
        remind_again_duration = findViewById(R.id.remind_again_duration);
        notification_thredshold = findViewById(R.id.notification_threshold);
        notificaiton_period = findViewById(R.id.notification_period);

        remind_again_duration.setText(remindAgainDuration / 60000 +"");
        notificaiton_period.setText(notificationPeriod / 60000 + "");
        notification_thredshold.setText(notificationThreshold / 60000 +"");
    }

    private void initButton() {
        Button save = findViewById(R.id.setting_save);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int remind = Integer.parseInt(remind_again_duration.getText().toString()) * 60000;
                int period = Integer.parseInt(notificaiton_period.getText().toString())* 60000 ;
                int threshold = Integer.parseInt(notification_thredshold.getText().toString()) * 60000;
                DAO.saveSettings(remind, period, threshold);
                Toast.makeText(getApplicationContext(), "Settings are saved", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 5;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };

    private void initPermission() {

        try{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                                                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                                  REQUEST_EXTERNAL_STORAGE);
            }
            else{
                initialise();
                initDisplay();
                initButton();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initialise();
                    initDisplay();
                    initButton();
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

        }
    }




}
