package project.taras.ua.adrenalincity.Activity.MyTicketsMVC;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import project.taras.ua.adrenalincity.Activity.HelperClasses.AppDatabase;
import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.FirebaseManager;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.FactoryAsync;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.IAsync;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderModel;
import project.taras.ua.adrenalincity.R;

public class MyTicketsActivity extends DrawerActivity implements IMyTicket.FirebaseListener{

    private FirebaseManager fbManager;
    private AppDatabase appDb;

    private ViewTicketMVC viewMVC;
    private AdapterViewPager adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_my_tickets);

        viewMVC = new ViewTicketMVC(this);
        adapterViewPager = new AdapterViewPager(this);
        viewMVC.setAdapter(adapterViewPager);
        viewMVC.setToolbarClickListener(toolbarClickListener);
        viewMVC.setNoTicketsVisibility(View.VISIBLE);
        fbManager = new FirebaseManager(this);
        fbManager.setFirebaseListener(this);
        appDb = AppDatabase.getInstance(this);
        FactoryAsync.setSelectInterface(myPaidOrdersListener);
        //FactoryAsync.createSelectExecutor(appDb.orderDao());
        FactoryAsync.selectPaidAndRelevantOrders(appDb.orderDao(), new Date());
        //fbManager.getMyTickets();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMyTicketsLoaded(List<HashMap<String, String>> listTickets) {
        //adapterViewPager.setData(listTickets);
    }

    private IAsync.ISelect myPaidOrdersListener = new IAsync.ISelect() {
        @Override
        public void doInBackground() {

        }

        @Override
        public void onPostExecute(OrderModel[] arrayOrderModel) {
            if (arrayOrderModel == null) {
                Log.v("tag_sqlite", "room is empty");
            }
            adapterViewPager.setData(Arrays.asList(arrayOrderModel));
            viewMVC.setNoTicketsVisibility(View.GONE);
        }
    };

    private View.OnClickListener toolbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };
}
