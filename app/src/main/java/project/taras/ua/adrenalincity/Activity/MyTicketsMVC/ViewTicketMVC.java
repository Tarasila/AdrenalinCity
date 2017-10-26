package project.taras.ua.adrenalincity.Activity.MyTicketsMVC;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 08.05.2017.
 */

public class ViewTicketMVC {

    private Activity context;

    private RelativeLayout rl_root;
    private HorizontalInfiniteCycleViewPager viewPager;
    private RecyclerView rvTicketBasket;
    private Toolbar toolbar;

    private TextView tv_empty_tickets;

    public ViewTicketMVC(Activity context) {
        this.context = context;
        init(context);
    }

    private void init(Activity context) {
        toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        rl_root = (RelativeLayout) context.findViewById(R.id.content_my_tickets);
        viewPager = (HorizontalInfiniteCycleViewPager) context.findViewById(R.id.my_tickets_view_scroll_view);
        viewPager.setScrollDuration(500);
        viewPager.setMediumScaled(true);
        viewPager.setMaxPageScale(0.95F);
        viewPager.setMinPageScale(0.75F);
        viewPager.setCenterPageScaleOffset(-80F);
        viewPager.setMinPageScaleOffset(-80.0F);
        //ticketView = (TicketView) context.findViewById(R.id.ticket_view);

        ViewTreeObserver observer = viewPager.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int displayWidth = measureDisplay().widthPixels;
                int displayHeight = measureDisplay().heightPixels;
                viewPager.getLayoutParams().width = (int) (displayWidth * 0.8f);
                viewPager.getLayoutParams().height = (int) (displayHeight * 0.8f);

                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setMovieImg(String imgUrl) {
        //ticketView.setImage(imgUrl);
    }

    public void setQrCode(Bitmap bQr) {
        //ticketView.setQrCode(bQr);
    }

    public void setBarCode(Bitmap btmBarCode) {
        //ticketView.setBarCode(btmBarCode);
    }

    public void setAdapter(AdapterViewPager adapterViewPager) {
        viewPager.setAdapter(adapterViewPager);
    }

    public void setToolbarClickListener(View.OnClickListener toolbarClickListener) {
        toolbar.setNavigationOnClickListener(toolbarClickListener);
    }

    public DisplayMetrics measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public void setNoTicketsVisibility(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                tv_empty_tickets = new TextView(context);
                tv_empty_tickets.setText("У Вас ще немає куплених квитків");
                tv_empty_tickets.setTextColor(Color.WHITE);
                tv_empty_tickets.setTextSize(30f);
                tv_empty_tickets.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                tv_empty_tickets.setLayoutParams(params);

                rl_root.addView(tv_empty_tickets);
                rl_root.requestLayout();
                break;
            case View.INVISIBLE:
                if (tv_empty_tickets != null)
                    tv_empty_tickets.setVisibility(visibility);
                break;
        }

    }
}
