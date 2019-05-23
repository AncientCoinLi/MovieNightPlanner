package au.edu.rmit.movienightplanner.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Movie;
import au.edu.rmit.movienightplanner.model.Utils;
import au.edu.rmit.movienightplanner.R;

public class MovieFragment extends Fragment {

    private LinearLayout movieList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        return view;

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        movieList = (LinearLayout) view.findViewById(R.id.movie_list);


        /**
         * read movie information from DAO and add them into the LinearLayout of ScrollView in a
         * loop.
         */
        HashMap<String, Movie> movies = DAO.getMovies();
        for (String id : movies.keySet()) {
            movieList.addView(createMovieView(movies.get(id)));
        }

    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                               dpVal, getContext().getResources().getDisplayMetrics());
    }

    private View createMovieView(Movie movie) {
        final LinearLayout mainLinearLayout = new LinearLayout(getContext());
        mainLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                       LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLinearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout movieInfoLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams movieInfoLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        movieInfoLinearLayout.setOrientation(LinearLayout.VERTICAL);
        movieInfoLayoutParams.setMarginStart(dp2px(60));
        movieInfoLayoutParams.setMarginEnd(dp2px(60));
        movieInfoLinearLayout.setLayoutParams(movieInfoLayoutParams);


        TextView title = new TextView(getContext());
        title.setText(movie.getTitle());
        title.setTextColor(Color.BLUE);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setTextSize(28);
        TextView year = new TextView(getContext());
        year.setText(movie.getYear()+"");
        year.setTextSize(24);
        year.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ImageView posterImage = new ImageView(getContext());
        if(Utils.getIdByName(getContext(), movie.getPoster()) == 0){
            posterImage.setImageResource(R.drawable.image_not_found);
        }else {
            posterImage.setImageResource(Utils.getIdByName(getContext(), movie.getPoster()));
        }
        TextView divider = new TextView(getContext());
        divider.setText("\n\n");


        movieInfoLinearLayout.addView(title);
        movieInfoLinearLayout.addView(year);
        movieInfoLinearLayout.addView(posterImage);
        movieInfoLinearLayout.addView(divider);
        mainLinearLayout.addView(movieInfoLinearLayout);


        return mainLinearLayout;
    }

}
