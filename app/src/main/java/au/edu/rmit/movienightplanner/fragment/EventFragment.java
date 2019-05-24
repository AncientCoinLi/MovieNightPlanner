package au.edu.rmit.movienightplanner.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import au.edu.rmit.movienightplanner.MainActivity;
import au.edu.rmit.movienightplanner.R;
import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Event;

public class EventFragment extends Fragment {

    private LinearLayout eventList;


    private void init() {
        eventList = getView().findViewById(R.id.event_list);
        Button scheduleEvent = getView().findViewById(R.id.event_btn_schedule);
        scheduleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new ScheduleEventFragment()).commit();
                ((MainActivity) getActivity()).setSelectedStatus(0);
            }
        });

        Button order = getView().findViewById(R.id.event_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO.changeOrder();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EventFragment()).commit();
            }
        });

        EditEventFragment editEventFragment = new EditEventFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }


    /**
     * onViewCreated is called after onCreateView, so modifying fragment can be implemented here
     * @param view the view onCreatedView() returned
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        List<Event> events = DAO.getSortedEvent();
        View eventView;
        if (DAO.isAscending()) {
            for (int i = events.size()-1; i >= 0; i--) {
                eventView = createEventView(events.get(i));
                eventList.addView(eventView, 0);
            }
        }else {
            for (int i = 0; i < events.size(); i++) {
                eventView = createEventView(events.get(i));
                eventList.addView(eventView, 0);
            }
        }

    }


    /**
     * create a relevant view for an event
     * @param event
     * @return
     */
    private View createEventView(final Event event) {
        final LinearLayout mainLinearLayout = new LinearLayout(getContext());
        mainLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                       LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLinearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout eventInfoLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams eventInfoLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        eventInfoLinearLayout.setLayoutParams(eventInfoLayoutParams);
        eventInfoLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(getContext());
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setTextSize(20);
        title.setTextColor(Color.BLUE);
        title.setText(event.getTitle());
        TextView startDate = new TextView(getContext());
        startDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        startDate.setText("Start Date: "+event.getStartDateString());
        TextView endDate = new TextView(getContext());
        endDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        endDate.setText("End Date:   " + event.getEndDateString());
        TextView venue = new TextView(getContext());
        venue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        venue.setText("Venue:         " + event.getVenue());
        //TextView location = new TextView(getContext());
        //location.setText("Location:    " + event.getLocationString());
        //TextView numOfAttendees = new TextView(getContext());
        //numOfAttendees.setText("Attendees:  " + event.getNumOfAttendees());

        eventInfoLinearLayout.addView(title);
        eventInfoLinearLayout.addView(startDate);
        eventInfoLinearLayout.addView(endDate);
        eventInfoLinearLayout.addView(venue);
        //eventInfoLinearLayout.addView(location);
        //eventInfoLinearLayout.addView(numOfAttendees);
        mainLinearLayout.addView(eventInfoLinearLayout);

        eventInfoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO.setCurrentEvent(event);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EventDetailFragment()).commit();
                ((MainActivity) getActivity()).setSelectedStatus(0);
            }
        });

        LinearLayout buttonLinearLayout = new LinearLayout(getContext());
        buttonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                         LinearLayout.LayoutParams.WRAP_CONTENT));
        Button unscheduleButton, editButton, editMovieButton;
        unscheduleButton = new Button(getContext());
        editButton = new Button(getContext());
        editMovieButton = new Button(getContext());

        unscheduleButton.setText("Unschedule");
        unscheduleButton.setSingleLine();
        unscheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 every time removing or adding a view dynamically, getActicity() and create a new
                 EventFragment. But it may affect the efficiency when amount of events getting
                 larger.
                */
                //mainLinearLayout.removeView(eventMap.get(event.getId()));
                DAO.removeEvent(event.getId());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EventFragment()).commit();
            }
        });

        editButton.setText("Edit Details");
        editButton.setSingleLine();
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO.setCurrentEvent(event);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EditEventFragment()).commit();
                ((MainActivity) getActivity()).setSelectedStatus(0);
            }
        });

        editMovieButton.setText("Add/Edit Moive");
        editMovieButton.setSingleLine();
        editMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO.setCurrentEvent(event);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EditMovieFragment()).commit();
                ((MainActivity) getActivity()).setSelectedStatus(0);
            }
        });

        LinearLayout.LayoutParams buttonLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        unscheduleButton.setLayoutParams(buttonLayoutParams);
        editButton.setLayoutParams(buttonLayoutParams);
        editMovieButton.setLayoutParams(buttonLayoutParams);

        buttonLinearLayout.addView(editButton);
        buttonLinearLayout.addView(editMovieButton);
        mainLinearLayout.addView(buttonLinearLayout);
        buttonLinearLayout.addView(unscheduleButton);
        return mainLinearLayout;
    }


}
