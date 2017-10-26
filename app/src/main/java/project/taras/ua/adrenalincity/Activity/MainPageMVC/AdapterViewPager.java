package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;

/**
 * Created by Taras on 18.04.2017.
 */

public class AdapterViewPager extends PagerAdapter {

    private Context context;
    private List<Movie> listMovie;
    private boolean dataIsLoaded = false;

    private IAdapterListener.IAdapterViewPager iListener;

    public void setOnAdapterListener(IAdapterListener.IAdapterViewPager listener){
        this.iListener = listener;
    }

    public AdapterViewPager(Context context){
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Elayout enumId = Elayout.values()[position];
        int id = enumId.getLayoutResId();

        final ViewMovieSoon imageView = new ViewMovieSoon(context);
        imageView.setId(id);
        imageView.setTag("vp"+position);

        ViewGroup.LayoutParams img_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(img_params);

        if (dataIsLoaded) {
            Log.v("tag_adapter", "listSize- "+listMovie.size());
            final Movie movie = listMovie.get(position);
            Picasso.with(context).load(movie.getImgUrl()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    if (position == listMovie.size() - 1) {
                        //the end
                            iListener.onImgLoaded();
                        //Otherwise we wait for schedule rv to be loaded
                    }
                }

                @Override
                public void onError() {
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iListener.onMovieClicked(movie, imageView, movie.getStartDate());
                }
            });
        }
        container.addView(imageView);

        return imageView;
    }

    public void setData(List<Movie> listMovie){
        this.listMovie = listMovie;
        dataIsLoaded = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (!dataIsLoaded) {
            return Elayout.values().length;
        }
        else return listMovie.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
