package project.taras.ua.adrenalincity.Activity.MainPageMVC.PagerFragments;


import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MainActivity;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MovieModel;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MovieSchedule;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.AdapterTodayAndSoon;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import project.taras.ua.adrenalincity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieSoonFragment extends LifecycleFragment {

    public RecyclerView rvToday;
    private LinearLayoutManager rvManager;
    private AdapterTodayAndSoon adapterTodayAndSoon;

    private MovieModel movieModel;

    public MovieSoonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_today, container, false);

        adapterTodayAndSoon = new AdapterTodayAndSoon(getActivity(), new ArrayList<Movie>());
        adapterTodayAndSoon.setRecyclerType(Constants.PLANE_RECYCLER, Constants.RV_FRAGMENT_MOVIE_SOON);
        adapterTodayAndSoon.setOnAdapterClickListener(((MainActivity)getActivity()).iAdapterMovieTodayAndSoonListener);

        rvManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        rvToday = (RecyclerView) view.findViewById(R.id.rv_main_activity);
        rvToday.setLayoutManager(rvManager);

        adapterTodayAndSoon.setRecyclerView(rvToday);
        rvToday.setAdapter(adapterTodayAndSoon);

        movieModel = ViewModelProviders.of(getActivity()).get(MovieModel.class);
        movieModel.getMovieSoonList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movieList) {
                adapterTodayAndSoon.setMovieListToAdapter(movieList);
            }
        });

        //MovieSchedules are used for displaying all show time
        //probably redundant
        movieModel.getDayScheduleList().observe(this, new Observer<List<MovieSchedule>>() {
            @Override
            public void onChanged(@Nullable List<MovieSchedule> movieSchedules) {
                adapterTodayAndSoon.setMovieScheduleList(movieSchedules);
            }
        });

        movieModel.getMovieIdScheduleForMovieActivity().observe(this, new Observer<LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>>>() {
            @Override
            public void onChanged(@Nullable LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> integerLinkedHashMapLinkedHashMap) {
                adapterTodayAndSoon.setShowListTodayForAllMovies(integerLinkedHashMapLinkedHashMap);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
