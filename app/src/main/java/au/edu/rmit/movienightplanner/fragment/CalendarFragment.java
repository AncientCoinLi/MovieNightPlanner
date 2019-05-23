package au.edu.rmit.movienightplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import au.edu.rmit.movienightplanner.model.Event;
import au.edu.rmit.movienightplanner.model.Utils;
import au.edu.rmit.movienightplanner.R;

public class CalendarFragment extends Fragment {
    private GridView gridView;
    private TextView calendarDate;
    private SimpleDateFormat simpleDateFormat;
    private ArrayAdapter adapter;
    private List<String> list;
    private Button prev;
    private Button next;


    public CalendarFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setOnListener();

    }

    private void setOnListener() {
        final Calendar calendar = Calendar.getInstance();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                Utils.updateCalendar(calendar, list, adapter);
                calendarDate.setText(calendar.get(Calendar.MONTH)+1 + " / " +calendar.get(Calendar.YEAR));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                Utils.updateCalendar(calendar, list, adapter);
                calendarDate.setText(calendar.get(Calendar.MONTH)+1 + " / " +calendar.get(Calendar.YEAR));

            }
        });
    }

    private void init() {
        gridView = (GridView) getView().findViewById(R.id.calendar_grid_view);
        calendarDate = (TextView) getView().findViewById(R.id.calendar_date);
        Date date = new Date(System.currentTimeMillis());
        simpleDateFormat = new SimpleDateFormat("M / yyyy");
        calendarDate.setText(simpleDateFormat.format(date));
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), R.layout.calendar_item, R.id.item_text, list);
        gridView.setAdapter(adapter);
        prev = (Button) getView().findViewById(R.id.fragment_calendar_btn_prev);
        next = (Button) getView().findViewById(R.id.fragment_calendar_btn_next);
        HashMap<Integer, Event> events = Utils.updateCalendar(Calendar.getInstance(), list,
                                                              adapter);
    }


}
