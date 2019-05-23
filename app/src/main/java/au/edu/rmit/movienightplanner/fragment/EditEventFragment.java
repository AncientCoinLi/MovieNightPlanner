package au.edu.rmit.movienightplanner.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import au.edu.rmit.movienightplanner.MainActivity;
import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Event;
import au.edu.rmit.movienightplanner.model.Utils;
import au.edu.rmit.movienightplanner.R;

public class EditEventFragment extends Fragment {

    private EditText title;
    private EditText startDate;
    private EditText endDate;
    private EditText venue;
    private EditText locationX;
    private EditText locationY;
    private ListView attendees;
    private Spinner candidates;
    private Button save;
    private Button add;
    private Button remove;

    private ArrayAdapter<String> adapter;
    private List<String> allContacts;
    private ArrayAdapter<String> attendeeAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_edit_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        title = (EditText) view.findViewById(R.id.edit_input_title);
        startDate = (EditText) view.findViewById(R.id.edit_input_start_date);
        endDate = (EditText) view.findViewById(R.id.edit_input_end_date);
        venue = (EditText) view.findViewById(R.id.edit_input_venue);
        locationX = (EditText) view.findViewById(R.id.edit_input_location_x);
        locationY = (EditText) view.findViewById(R.id.edit_input_location_y);
        attendees = (ListView) view.findViewById(R.id.edit_attendee_list);
        candidates = (Spinner) view.findViewById(R.id.edit_event_candidate);
        save = (Button) view.findViewById(R.id.edit_input_btn_save);
        add = (Button)view.findViewById(R.id.edit_event_btn_add_attendee);
        remove = (Button) view.findViewById(R.id.edit_event_btn_remove_attendee);

        Event event = DAO.getCurrentEvent();
        title.setText(event.getTitle());
        startDate.setText(event.getStartDateString());
        endDate.setText(event.getEndDateString());
        venue.setText(event.getVenue());
        locationX.setText(event.getLocation()[0]);
        locationY.setText(event.getLocation()[1]);

        initAttendees();
        initListener();
    }

    public void initAttendees() {
        allContacts = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                                           allContacts);
        candidates.setAdapter(adapter);

        attendeeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                                                                        DAO.getCurrentEvent().getAttendees());
        attendees.setAdapter(attendeeAdapter);

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                                              2);
        }else{
            Utils.readContacts(this.getActivity(), allContacts, adapter);
        }

    }




    private void initListener() {

            startDate.setInputType(InputType.TYPE_NULL);
            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showDateTimePickerDialog(getContext(), startDate);
                }
            });

            startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Utils.showDateTimePickerDialog(getContext(), startDate);
                    }
                }
            });

            endDate.setInputType(InputType.TYPE_NULL);
            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showDateTimePickerDialog(getContext(), endDate);
                }
            });

            endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Utils.showDateTimePickerDialog(getContext(), endDate);
                    }
                }
            });


            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = (String) candidates.getSelectedItem();
                    if(!DAO.getCurrentEvent().getAttendees().contains(item)){
                        DAO.getCurrentEvent().addAttendee(item);
                        attendeeAdapter.notifyDataSetChanged();
                        Utils.setListViewHeightBasedOnChildren(attendees);
                    }
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = (String) candidates.getSelectedItem();
                    if(DAO.getCurrentEvent().getAttendees().contains(item)){
                        DAO.getCurrentEvent().removeAttendee(item);
                        attendeeAdapter.notifyDataSetChanged();
                        Utils.setListViewHeightBasedOnChildren(attendees);
                    }
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DAO.modifyEvent(title.getText().toString(), startDate.getText().toString(),
                                 endDate.getText().toString(),
                                 venue.getText().toString(), locationX.getText().toString(),
                                 locationY.getText().toString());
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EventFragment()).commit();
                    ((MainActivity) getActivity()).setSelectedStatus(2);
                }
            });
    }



}
