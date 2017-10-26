package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.HelperClasses.AppDatabase;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.FirebaseManager;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.PlaceView;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.OrderManager;
import project.taras.ua.adrenalincity.Activity.HelperClasses.Pref;
import project.taras.ua.adrenalincity.R;

public class BasketActivity extends DrawerActivity implements IBookPayment.IBookPaymentListener {

    private OrderManager orderManager;
    private FirebaseManager fbManager;
    private AppDatabase appDb;
    private Pref pref;

    private AdapterTicketBasket adapterTicketBasket;
    private ViewBasketMVC viewMVC;

    private Map<String, Object> mapOrder;
    private List<PlaceView> seatsList;

    private RequestManager request_manager;

    private String getCurrentUser() {
        JSONObject jsonUser = pref.getCurrentUser();
        try {
            return jsonUser.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("tag_lifecycle", "onCreate");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_basket);

        pref = Pref.getInstance(this);
        appDb = AppDatabase.getInstance(this);
        orderManager = new OrderManager(this);
        fbManager = new FirebaseManager(this);
        request_manager = new RequestManager(this, Constants.MY_BASKET_ACTIVITY);
        //fbManager = new FirebaseManager(this);
        adapterTicketBasket = new AdapterTicketBasket(this, appDb.orderDao(), Constants.MY_BASKET_ACTIVITY);
        adapterTicketBasket.setClickListener(adapterClickListener);
        viewMVC = new ViewBasketMVC(this);
        viewMVC.initRvTicketBasket(adapterTicketBasket);
        //viewMVC.setAmountToPay("150 uah");
        viewMVC.setClickListener(payClickListener);
        viewMVC.setToolbarClickListener(toolbarClickListener);

        //TODO: left off over here...
        dao = appDb.orderDao();
        if (pref.isUserLoggedIn()) {
            FactoryAsync.setSelectInterface(selectListener);
            FactoryAsync.selectNotPaidOrders(getCurrentUser(), dao, new Date(), "booked");
        }else {
            adapterIsEmpty();
        }
    }

    OrderDao dao;
    private IAsync.ISelect selectListener = new IAsync.ISelect() {
        @Override
        public void doInBackground() {

        }

        @Override
        public void onPostExecute(OrderModel[] arrayOrderModel) {
            Log.v("keeptrack", "called");
            if (arrayOrderModel.length != 0) {
                adapterTicketBasket.setData(Collections.synchronizedList(new LinkedList(Arrays.asList(arrayOrderModel))));
            }
            else
                adapterIsEmpty();

            if (adapterTicketBasket.getItemCount() == 0){
                adapterIsEmpty();
            }
        }
    };

    @Override
    protected void onStart() {
        Log.v("tag_lifecycle", "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("tag_lifecycle", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("tag_lifecycle", "onDestroy");
    }

    @Override
    protected void onResume() {
        Log.v("tag_lifecycle", "onResume");
        super.onResume();
    }

    //gets called from AdapterTicketBasket
    public void adapterIsEmpty() {
        viewMVC.setButtonsContainerVisibility(View.INVISIBLE);
    }

    private View.OnClickListener toolbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private IBookPayment.IClickListener adapterClickListener = new IBookPayment.IClickListener() {
        @Override
        public void onClick(int childPosition) {
            PlaceView seat = seatsList.get(childPosition);
            Log.v("seatclick", "" + seat.getPlace());
        }
    };

    private View.OnClickListener payClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.my_basket_b_pay:
                    viewMVC.displayPaymentBeingProcessed(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            orderManager.payForTicketsWithLiqPay(adapterTicketBasket.getData());
                        }
                    }).start();
                    break;
                /*case R.id.my_basket_b_book:
                    orderManager.validateBookAttempt(adapterTicketBasket.getData());
                    break;*/
            }
        }
    };

    @Override
    public void onOrderDismiss(Map<String, String> map_book_to_dismiss) {
        request_manager.sendRequest(Constants.CANCEL_BOOK_SEAT, Constants.TYPE_CANCEL_BOOK_SEAT, map_book_to_dismiss);
    }

    /**
     * validation callbacks from fbManager
     * to be convinced that user doesn't exceed booking limit
     */
    /*@Override
    public void onBookAllowed() {
        Toast.makeText(this, "You are allowed to book tickets", Toast.LENGTH_LONG).show();
        viewMVC.displayBookBeingProcessed(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    orderManager.bookTickets();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/

    /*@Override
    public void onBookRejected(int code, int params) {
        switch (code){
            case Constants.EXCEEDING_ORDER_LIMIT:
                viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                Toast.makeText(this, "Rejected!Please delete " + params
                        + " from your basket", Toast.LENGTH_LONG).show();
                break;
            case Constants.ALREADY_BOOKED_ORDER:
                viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                Toast.makeText(this, "Order is already booked", Toast.LENGTH_LONG).show();
                break;
            case Constants.DIFFERENT_SHOWS_ON_THE_SAME_MOVIE:
                viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                Toast.makeText(this, "different shows on the same movie", Toast.LENGTH_LONG).show();
                break;
        }
    }*/



    private boolean savingEticketsToFirebase = false;

    @Override
    public void onSuccessfulOrderAction(int requestType) {
        synchronized (orderManager) {
            orderManager.orderCounter++;
            orderManager.orderIsBeingProcessed = false;
            Log.v("tag_thread", "order completed!");
            Log.v("tag_thread", "order counter - " + orderManager.orderCounter);
            orderManager.notify();

            //potential collapse
            Log.v("payment_tag", "ordersToProccedWith.size() - " + orderManager.getOrdersToProceedWith().size());
            Log.v("payment_tag", "orderManager.orderCounter - " + orderManager.orderCounter);
            if (orderManager.orderCounter == orderManager.getOrdersToProceedWith().size()) {
                switch (requestType) {
                    case Constants.BUY_TICKETS:
                        //send all paid orders to Firebase to make up tickets
                        savingEticketsToFirebase = true;
                        orderManager.fbManager.savePaidOrders(adapterTicketBasket.getData());

                        //clean up all paid orders from RecyclerView
                        Message msgBuy = handler.obtainMessage();
                        msgBuy.arg1 = Constants.BUY_TICKETS;
                        msgBuy.sendToTarget();

                        break;
                    /*case Constants.BOOK_TICKETS:
                        orderManager.fbManager.saveBookedTicket(orderManager.getOrdersToProceedWith());

                        Message msgBook = handler.obtainMessage();
                        msgBook.arg1 = Constants.BOOK_TICKETS;
                        msgBook.sendToTarget();
                        break;*/
                }
            }
        }
    }

    //notify controller on successful result to allow user to press back button
    //@Override
    public void onSuccessfulEticketsSaving(){
        this.savingEticketsToFirebase = false;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case Constants.BUY_TICKETS:
                    adapterTicketBasket.markOrdersAsPaid();
                    FactoryAsync.setOrdersStatus(appDb.orderDao(), adapterTicketBasket.getDataAsArray());

                    adapterTicketBasket.getData().clear();
                    adapterTicketBasket.notifyDataSetChanged();
                    viewMVC.displayPaymentBeingProcessed(View.INVISIBLE);
                    break;
                /*case Constants.BOOK_TICKETS:
                    adapterTicketBasket.markOrdersAsBooked();
                    FactoryAsync.setOrdersStatus(appDb.orderDao(), adapterTicketBasket.getDataAsArray());
                    viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                    break;*/
            }
        }
    };

    @Override
    public void onBackPressed() {

        synchronized (orderManager){
           /* if (orderManager.orderIsBeingProcessed){
                Toast.makeText(this, "Бронюю, будь ласка зачекайте", Toast.LENGTH_LONG).show();
            }else */if (savingEticketsToFirebase){
                Toast.makeText(this, "Зберігаю електронні квитки, будь ласка зачекайте", Toast.LENGTH_LONG).show();
            }else {
                super.onBackPressed();
            }
        }
    }

    public void onBackPressedFromLiqPay(){
        viewMVC.displayPaymentBeingProcessed(View.INVISIBLE);
    }
}
