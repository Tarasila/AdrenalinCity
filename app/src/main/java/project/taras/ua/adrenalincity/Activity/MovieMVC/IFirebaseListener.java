package project.taras.ua.adrenalincity.Activity.MovieMVC;

/**
 * Created by Taras on 16.05.2017.
 */

public interface IFirebaseListener {

    interface IRatingListener{
        void onMovieRatingCalculated(float movieRating);
    }
}
