package project.taras.ua.adrenalincity.Activity.Discount;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
import project.taras.ua.adrenalincity.R;

public class DiscountActivity extends DrawerActivity {

    private Toolbar toolbar;
    private RecyclerView rv;
    private LinearLayoutManager linearLayoutManager;

    private DiscountAdapter adapterDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_discount);
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(toolbarClickListener);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        rv = (RecyclerView) findViewById(R.id.rv_discount);
        rv.setNestedScrollingEnabled(false);
        setRVadapter();
    }

    public void setRVadapter() {
        adapterDiscount = new DiscountAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setAdapter(adapterDiscount);
        rv.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> listDiscount = new ArrayList<>();
        listDiscount.add("http://cinema.adrenalin.lutsk.ua/images/aktsiyi/1.jpg");
        listDiscount.add("http://cinema.adrenalin.lutsk.ua/images/aktsiyi/2.jpg");
        adapterDiscount.setDiscountList(listDiscount);
    }

    private View.OnClickListener toolbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

}
