package project.taras.ua.adrenalincity.Activity.BookRules;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
import project.taras.ua.adrenalincity.R;

public class BookRulesActivity extends DrawerActivity {

    private Toolbar toolbar;
    private TextView tvRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_book_rules);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.setNavigationOnClickListener(toolbarClickListener);

        tvRules = (TextView) findViewById(R.id.book_rules_tv_rules);
    }

    private View.OnClickListener toolbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

}
