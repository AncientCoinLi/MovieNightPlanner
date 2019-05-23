package au.edu.rmit.movienightplanner.model;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Utils {
    public static void showDateTimePickerDialog(final Context context, final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        final StringBuffer dateTime = new StringBuffer();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                //month range : 0-11
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(){

                    /**
                     * Called when the user is done setting a new time and the dialog
                     * has closed.
                     *
                     * @param view      the view associated with this listener
                     * @param hourOfDay the hour that was set
                     * @param minute    the minute that was set
                     */
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        System.out.println(calendar.getTime().toString());
                        editText.setText(MovieNightPlannerDateFormat.dateFormat.format(calendar.getTime()));

                    }
                }, 0, 0, true).show();

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                             calendar.get(Calendar.DAY_OF_MONTH)).show();

        editText.setText(dateTime.toString());
    }




    public static void readContacts(Context context, List<String> contacts, ArrayAdapter<String> adapter) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contacts.add(displayName);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static int getIdByName(Context context, String imageName){
        String[] temp = imageName.toLowerCase().split("\\.");
        int id = context.getResources().getIdentifier(temp[0], "drawable",
                                                      context.getPackageName());
        return id;
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                               dpVal, context.getResources().getDisplayMetrics());
    }


    public static HashMap<Integer, Event> updateCalendar(Calendar calendar,
    List<String> list, ArrayAdapter adapter) {
        HashMap<Integer, Event> events = new HashMap<>();
        int month = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int lastDay = calendar.get(Calendar.DAY_OF_MONTH);

        list.clear();
        for(int i = 1; i < firstDayOfMonth; i++){
            list.add("");
        }
        StringBuffer content = new StringBuffer();
        for(int i = 1; i <= lastDay; i++){
            content.append(i + "\n");
            Calendar eventCalendar = Calendar.getInstance();
            for(int j = 0; j < DAO.getSortedEvent().size(); j++){
                eventCalendar.setTime(DAO.getSortedEvent().get(j).getStartDate());
                if(eventCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    if (eventCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
                        if (eventCalendar.get(Calendar.DAY_OF_MONTH) == i) {
                            content.append(DAO.getSortedEvent().get(j).getTitle() + "\n");
                            events.put(i, DAO.getSortedEvent().get(j));
                        }else continue;
                    } else continue;
                }else continue;
            }
            list.add(content.toString());
            content.setLength(0);
        }
        adapter.notifyDataSetChanged();
        calendar.set(Calendar.MONTH, month);
        return events;
    }

}
