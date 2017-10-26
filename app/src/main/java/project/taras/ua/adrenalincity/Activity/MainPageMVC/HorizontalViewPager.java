package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import android.content.Context;
import android.util.AttributeSet;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

/**
 * Created by Taras on 18.04.2017.
 */

public class HorizontalViewPager extends HorizontalInfiniteCycleViewPager {

    private Context context;

    public HorizontalViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public HorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }
}
