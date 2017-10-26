package project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import project.taras.ua.adrenalincity.Activity.MovieMVC.ValueBarView.ValueBar;

/**
 * Created by Taras on 15.03.2017.
 */

public class RootView extends RelativeLayout {

    private ValueBar verticalBar;

    public RootView(Context context) {
        super(context);
    }

    public RootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVerticalBar(ValueBar verticalBar) {
        this.verticalBar = verticalBar;
    }
}
