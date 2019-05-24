package au.edu.rmit.movienightplanner.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import java.lang.reflect.Method;

import au.edu.rmit.movienightplanner.MainActivity;
import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Event;
import au.edu.rmit.movienightplanner.service.NotificationJobService;

public class NotificationActionReceiver extends BroadcastReceiver {

    private final int RemindLater = 0;
    private final int Dismiss = 1;
    private final int Cancel = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        doJob(context, intent);
    }

    private void doJob(final Context context, Intent intent) {
        int i = intent.getIntExtra("action", -1);
        final Event event = DAO.getEvents().get(intent.getStringExtra("event"));
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Integer.parseInt(event.getId()));
        switch (i) {
            case RemindLater:
                NotificationJobService.getInstance().remindLater(event);
                break;
            case Dismiss:
                NotificationJobService.getInstance().dismiss(event);
                break;
            case Cancel:
                // collapse notification bar
                context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Tips：");
                builder.setMessage("Do you want to cancel " + event.getTitle() + "?");

                builder.setPositiveButton("No",null);

                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationJobService.getInstance().cancel(event);
                        Intent intent1 = new Intent(MainActivity.UPDATE_ACTION);
                        intent1.putExtra("type", MainActivity.UPDATE);
                        context.sendBroadcast(intent1);
                    }
                });
                builder.show();
                break;
        }

    }

}

