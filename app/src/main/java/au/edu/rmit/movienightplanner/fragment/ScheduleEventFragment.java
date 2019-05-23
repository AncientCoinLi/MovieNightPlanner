package au.edu.rmit.movienightplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Utils;
import au.edu.rmit.movienightplanner.R;

public class ScheduleEventFragment extends Fragment {

    private EditText title;
    private EditText startDate;
    private EditText endDate;
    private EditText venue;
    private EditText locationX;
    private EditText locationY;
    private Button submit;
    private List<EditText> widgets;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }

    private void init(View view) {
        title = (EditText) view.findViewById(R.id.schedule_input_title);
        startDate = (EditText) view.findViewById(R.id.schedule_input_start_date);
        endDate = (EditText) view.findViewById(R.id.schedule_input_end_date);
        venue = (EditText) view.findViewById(R.id.schedule_input_venue);
        locationX = (EditText) view.findViewById(R.id.schedule_input_location_x);
        locationY = (EditText) view.findViewById(R.id.schedule_input_location_y);
        submit = (Button) view.findViewById(R.id.schedule_input_btn_submit);
        widgets = new ArrayList<>();
        widgets.add(title);
        widgets.add(startDate);
        widgets.add(endDate);
        widgets.add(venue);
        widgets.add(locationX);
        widgets.add(locationY);
        setOnListener();
    }

    private void setOnListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                submit.setEnabled(Boolean.TRUE);
                for (EditText editText : widgets) {
                    if (TextUtils.isEmpty(editText.getText())) {
                        submit.setEnabled(Boolean.FALSE);
                        break;
                    }
                }
            }
        };
        for (EditText editText : widgets) {
            editText.addTextChangedListener(textWatcher);
        }
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO.addEvent(title.getText().toString(), startDate.getText().toString(),
                             endDate.getText().toString(),
                             venue.getText().toString(), locationX.getText().toString(),
                             locationY.getText().toString());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EventFragment()).commit();
            }
        });
    }




}
