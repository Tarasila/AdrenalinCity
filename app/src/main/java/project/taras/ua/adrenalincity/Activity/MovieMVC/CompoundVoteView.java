package project.taras.ua.adrenalincity.Activity.MovieMVC;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import project.taras.ua.adrenalincity.Activity.MovieMVC.StarView.StarView;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 18.05.2017.
 */

public class CompoundVoteView extends RelativeLayout {

    private RelativeLayout rootView;
    private StarView starView;
    private Button bVoteUp;
    private Button bVoteDown;

    public CompoundVoteView(Context context) {
        super(context);
        init(context);
    }

    public CompoundVoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        rootView = (RelativeLayout) inflate(context, R.layout.compound_vote_view_layout, this);
        starView = (StarView) rootView.findViewById(R.id.compound_star_view);
        bVoteUp = (Button) rootView.findViewById(R.id.compound_b_vote_up);
        bVoteDown = (Button) rootView.findViewById(R.id.compound_b_vote_down);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        makeMeasureSpec();
    }

    private void makeMeasureSpec() {
        bVoteDown.measure(MeasureSpec.makeMeasureSpec(starView.getWidth() / 2, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(starView.getWidth() / 2, MeasureSpec.EXACTLY));
        bVoteUp.measure(MeasureSpec.makeMeasureSpec(starView.getWidth() / 2, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(starView.getWidth() / 2, MeasureSpec.EXACTLY));

        int voteButtonWidth = bVoteUp.getMeasuredWidth() + 10;
        Log.v("voteup", "" + voteButtonWidth);

        int widthSpec = MeasureSpec.makeMeasureSpec(starView.getWidth() + voteButtonWidth, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(starView.getHeight(), MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSpec, heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.v("votedown", ""+bVoteDown.getMeasuredWidth());

        starView.layout(0, 0, starView.getMeasuredWidth(), starView.getMeasuredHeight());
        bVoteUp.layout(starView.getMeasuredWidth() + 10, 0, getMeasuredWidth(), bVoteUp.getMeasuredHeight());
        bVoteDown.layout(starView.getMeasuredWidth() + 10, bVoteUp.getMeasuredHeight(), getMeasuredWidth(), bVoteDown.getMeasuredHeight()*2);
    }

    public void setMovieRatingToStarView(float rating) {
        starView.setRating(rating);
    }

    public void setVoteClickListener(OnClickListener clickListener) {
        bVoteUp.setOnClickListener(clickListener);
        bVoteDown.setOnClickListener(clickListener);
    }
}
