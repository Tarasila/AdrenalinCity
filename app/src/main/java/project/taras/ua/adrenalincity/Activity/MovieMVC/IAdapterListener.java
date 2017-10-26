package project.taras.ua.adrenalincity.Activity.MovieMVC;

/**
 * Created by Taras on 12.05.2017.
 */

public interface IAdapterListener {

    interface IAdapterClickListener{
        void onDateClicked(String keyDay, int position);
        void onTimeClicked(int position);
        void onTimeAdapterIsReady();
    }
}
