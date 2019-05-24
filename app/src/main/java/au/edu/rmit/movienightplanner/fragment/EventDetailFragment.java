package au.edu.rmit.movienightplanner.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Event;
import au.edu.rmit.movienightplanner.R;

public class EventDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_event_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        Event event = DAO.getCurrentEvent();
        TextView title = view.findViewById(R.id.event_detail_title);
        TextView startDate = view.findViewById(R.id.event_detail_start_date);
        TextView endDate = view.findViewById(R.id.event_detail_end_date);
        TextView venue = view.findViewById(R.id.event_detail_venue);
        TextView locationX = view.findViewById(R.id.event_detail_location_x);
        TextView locationY = view.findViewById(R.id.event_detail_location_y);
        TextView movie = view.findViewById(R.id.event_detail_movie);
        TextView attendee = view.findViewById(R.id.event_detail_attendee);

        title.setText(event.getTitle());
        startDate.setText(event.getStartDateString());
        endDate.setText(event.getEndDateString());
        venue.setText(event.getVenue());
        locationX.setText(event.getLocation()[0]);
        locationY.setText(event.getLocation()[1]);
        movie.setText(event.getMovieName());
        StringBuffer stringBuffer = new StringBuffer();
        for (String name : event.getAttendees()) {
            stringBuffer.append(name+System.lineSeparator());
        }
        attendee.setText(stringBuffer.toString());
    }

}
