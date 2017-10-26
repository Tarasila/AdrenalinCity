package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import project.taras.ua.adrenalincity.Activity.MyBasketMVC.Swiping.SimpleTouchHelperCallback;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 29.05.2017.
 */

public class ViewBasketMVC {

    private RelativeLayout rlRootContainer;
    private Activity context;
    private Toolbar toolbar;
    private RecyclerView rvTicketBasket;
    private TextView tvAmountToPay;
    private LinearLayout llButtonsContainer;
    private Button bPay;
    //private Button bBook;
    private ItemTouchHelper itemTouchHelper;
    private SimpleTouchHelperCallback touchHelperCallback;
    private FrameLayout flBookFacade;
    private FrameLayout flPaymentFacade;

    public ViewBasketMVC(Activity context) {
        this.context = context;
        init(context);
    }

    private void init(Activity context) {
        rlRootContainer = (RelativeLayout) context.findViewById(R.id.content_basket);
        toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        rvTicketBasket = (RecyclerView) context.findViewById(R.id.my_basket_rv_ticket_basket);
        //tvAmountToPay = (TextView) context.findViewById(R.id.my_basket_tv_total_amount_to_pay);
        llButtonsContainer = (LinearLayout) context.findViewById(R.id.my_basket_ll_container_buttons);
        bPay = (Button) context.findViewById(R.id.my_basket_b_pay);
        //bBook = (Button) context.findViewById(R.id.my_basket_b_book);
        flBookFacade = (FrameLayout) context.findViewById(R.id.my_basket_fl_on_book_progress_container);
        flPaymentFacade = (FrameLayout) context.findViewById(R.id.my_basket_fl_on_payment_progress_container);
    }

    public void initRvTicketBasket(AdapterTicketBasket adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        adapter.setRvRef(rvTicketBasket);
        rvTicketBasket.setHasFixedSize(true);
        rvTicketBasket.setAdapter(adapter);
        rvTicketBasket.setLayoutManager(layoutManager);

        touchHelperCallback = new SimpleTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(rvTicketBasket);
    }

    public void setClickListener(View.OnClickListener payClickListener) {
        bPay.setOnClickListener(payClickListener);
        //bBook.setOnClickListener(payClickListener);
    }

    public void setAmountToPay(String totalCost) {
        //tvAmountToPay.setText("Amount to pay: " + totalCost);
    }

    public void setToolbarClickListener(View.OnClickListener clickListener) {
        toolbar.setNavigationOnClickListener(clickListener);
    }
    ValueAnimator scaleX;
    ValueAnimator scaleY;

    public void displayPaymentBeingProcessed(int visibility) {
        scaleX = ObjectAnimator.ofFloat(flPaymentFacade, "scaleX", 1, 0.9f);
        scaleX.setDuration(500);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleY = ObjectAnimator.ofFloat(flPaymentFacade, "scaleY", 1, 0.9f);
        scaleY.setDuration(500);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        switch (visibility){
            case View.VISIBLE:
                flPaymentFacade.setVisibility(visibility);
                scaleX.start();
                scaleY.start();
                llButtonsContainer.setVisibility(View.INVISIBLE);
                break;
            case View.INVISIBLE:
                flPaymentFacade.setVisibility(visibility);
                scaleX.cancel();
                scaleY.cancel();
                llButtonsContainer.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setButtonsContainerVisibility(int visibility) {
        llButtonsContainer.setVisibility(visibility);

        TextView tvEmptyBasket = new TextView(context);
        tvEmptyBasket.setText("У Вас ще немає заброньованих квитків");
        tvEmptyBasket.setTextColor(Color.WHITE);
        tvEmptyBasket.setTextSize(30f);
        tvEmptyBasket.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvEmptyBasket.setLayoutParams(params);

        rlRootContainer.addView(tvEmptyBasket);
        rlRootContainer.requestLayout();
    }
}
