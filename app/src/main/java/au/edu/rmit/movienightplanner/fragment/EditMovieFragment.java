package au.edu.rmit.movienightplanner.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import au.edu.rmit.movienightplanner.MainActivity;
import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.R;

public class EditMovieFragment extends Fragment {

    private Button confirm;
    private Spinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_edit_event_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setOnListener();
    }

    private void setOnListener() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO.getCurrentEvent().setMovie(spinner.getSelectedItem().toString());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new EventFragment()).commit();
                ((MainActivity) getActivity()).setSelectedStatus(0);
            }
        });
    }

    private void init(View view) {
        confirm = (Button) view.findViewById(R.id.edit_event_movie_confirm);
        spinner = (Spinner) view.findViewById(R.id.edit_event_movie_spinner);
        List<String> movies = new ArrayList<>();
        for (String id : DAO.getMovies().keySet()) {
            movies.add(DAO.getMovies().get(id).getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                                                R.layout.support_simple_spinner_dropdown_item, movies);
        spinner.setAdapter(adapter);
    }
}
