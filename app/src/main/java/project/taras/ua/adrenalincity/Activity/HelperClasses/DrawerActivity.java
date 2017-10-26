package project.taras.ua.adrenalincity.Activity.HelperClasses;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LifecycleActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import project.taras.ua.adrenalincity.Activity.BookRules.BookRulesActivity;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.Discount.DiscountActivity;
import project.taras.ua.adrenalincity.Activity.Login.LoginActivity;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.BasketActivity;
import project.taras.ua.adrenalincity.Activity.MyTicketsMVC.MyTicketsActivity;
import project.taras.ua.adrenalincity.Activity.SchedulePageMVC.SchedulePageActivity;
import project.taras.ua.adrenalincity.Activity.SoonPageMVC.SoonActivity;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.MovieTodayActivity;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 10.03.2017.
 */

public class DrawerActivity extends LifecycleActivity {
    JSONObject user;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    ImageView ivToolbarLogo;
    ActionBar actionBar;

    RelativeLayout rlUserDrawer;
    RelativeLayout rlUserInfContainer;
    View v_background;
    ImageView iv_logo;
    private CircularImageView civUserPhoto;
    private TextView tvUserName;
    ListView navListView;
    LoginButton bLogOut;
    SignInButton bLogOutGoogle;
    Button bLogOutEmail;

    Pref pref;
    ArrayList<String> navListCont;

    boolean interceptTouchEvent = false;

    protected void onDrawerCreate() {
        pref = Pref.getInstance(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        rlUserDrawer = (RelativeLayout) findViewById(R.id.drawer_rl_user_inf_container_root);
        rlUserInfContainer = (RelativeLayout) findViewById(R.id.drawer_rl_user_inf_container);
        v_background = findViewById(R.id.drawer_v_background_to_for_logo);
        iv_logo = (ImageView) findViewById(R.id.drawer_iv_logo);
        civUserPhoto = (CircularImageView) findViewById(R.id.drawer_cv_user_photo);
        tvUserName = (TextView) findViewById(R.id.drawer_tv_user_name);
        bLogOut = (LoginButton) findViewById(R.id.drawer_b_facebook);
        bLogOutGoogle = (SignInButton) findViewById(R.id.drawer_b_google);
        bLogOutEmail = (Button) findViewById(R.id.drawer_b_email);
        appBarLayout.setExpanded(true);
        navListView = (ListView) findViewById(R.id.navList);
        try {
            navListView.setPadding(20, 20, 20, 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (pref.isUserLoggedIn()) {
            ((RelativeLayout.LayoutParams)rlUserInfContainer.getLayoutParams()).topMargin = 16;
            user = pref.getCurrentUser();
            switch (pref.howUserLoggedIn()) {
                case Constants.FACEBOOK_LOG_IN:
                    bLogOut.setVisibility(View.VISIBLE);
                    bLogOutGoogle.setVisibility(View.INVISIBLE);
                    bLogOutEmail.setVisibility(View.INVISIBLE);
                    break;
                case Constants.GOOGLE_LOG_IN:
                    bLogOut.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) bLogOutGoogle.getChildAt(0);
                    textView.setText("Log out");
                    bLogOutGoogle.setVisibility(View.VISIBLE);
                    bLogOutEmail.setVisibility(View.INVISIBLE);
                    break;
                case Constants.EMAIL_LOG_IN:
                    bLogOut.setVisibility(View.INVISIBLE);
                    bLogOutGoogle.setVisibility(View.INVISIBLE);
                    bLogOutEmail.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    bLogOut.setVisibility(View.INVISIBLE);
                    bLogOutGoogle.setVisibility(View.INVISIBLE);
                    bLogOutEmail.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        navListCont = new ArrayList<>();
        navListCont.add("Акції");
        navListCont.add("Всі сеанси");
        navListCont.add("Незабаром");
        navListCont.add("Найближчі сеанси");
        if (pref.isUserLoggedIn()) {
            navListCont.add("Мої квитки");
            navListCont.add("Мої бронювання");
        }
        navListCont.add("Правила бронювання");

        if (!pref.isUserLoggedIn()) {
            navListCont.add("Увійти");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_navlist_layout, navListCont);
        navListView.setAdapter(arrayAdapter);
        navListView.setOnItemClickListener(onItemClickListener);
        bLogOut.setOnClickListener(logoutClickListener);
        bLogOutGoogle.setOnClickListener(logoutClickListener);
        bLogOutEmail.setOnClickListener(logoutClickListener);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!pref.isUserLoggedIn()) {
            toolbar.inflateMenu(R.menu.menu_main);
        }
        toolbar.getMenu().add(Menu.NONE, R.id.menu_item_we_on_the_map, Menu.NONE, "Ми на мапі");
        toolbar.getMenu().addSubMenu(Menu.NONE, R.id.menu_item_contacts, Menu.NONE, "Контакти");
        sm_contacts = toolbar.getMenu().findItem(R.id.menu_item_contacts).getSubMenu();
        sm_contacts.clear();
        sm_contacts.add(0, R.id.sub_menu_first_number, Menu.NONE, "(0332) 787-220");
        sm_contacts.add(0, R.id.sub_menu_second_number, Menu.NONE, "(068) 716-30-65");
        sm_contacts.add(0, R.id.sub_menu_third_number, Menu.NONE, "(050) 700-31-98");

        toolbar.getMenu().addSubMenu(Menu.NONE, R.id.menu_item_schedule, Menu.NONE, "Графік роботи");
        sm_schedule = toolbar.getMenu().findItem(R.id.menu_item_schedule).getSubMenu();
        sm_schedule.clear();
        sm_schedule.add(0, R.id.sub_menu_schedule_text, Menu.NONE, "ПН-ПТ 9.00 - 23.00\n" +
                "СБ-НД 9.00 - 24.00");

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_18dp);
        toolbar.setNavigationOnClickListener(navigationClickListener);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        final int logo_width = (measureDisplay().widthPixels / 2);

        ivToolbarLogo = new ImageView(this);
        ivToolbarLogo.setId(R.id.logo_adrenalin);
        ivToolbarLogo.setImageResource(R.drawable.kino);
        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivToolbarLogo.getLayoutParams().width = logo_width;
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        toolbar.addView(ivToolbarLogo);



        //setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActionBar().setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //interceptTouchEvent = true;
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        //drawerToggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onDrawerCreate();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserData();
    }

    private void initUserData() {
        Log.v("initUser", "onResume");
        if (pref.isUserLoggedIn()) {
            try {
                String userPhoto = user.getString("userphoto");
                if (!userPhoto.equalsIgnoreCase("nophoto")) {
                    Glide.with(this).load(userPhoto).into(civUserPhoto);
                } else {
                    Glide.with(this).load(R.drawable.man_user).into(civUserPhoto);
                }
                tvUserName.setText(user.getString("username"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            civUserPhoto.setVisibility(View.GONE);
            v_background.setVisibility(View.VISIBLE);
            iv_logo.setVisibility(View.VISIBLE);
            //Glide.with(this).load(R.drawable.man_user).into(civUserPhoto);
            //tvUserName.setText("Невідомий");
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    public View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else
                drawerLayout.openDrawer(Gravity.LEFT);
        }
    };

    public View.OnClickListener logoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent goLoginActivity;
            switch (v.getId()) {
                case R.id.drawer_b_facebook:
                    LoginManager.getInstance().logOut();
                    pref.deleteCurrentUser();
                    goLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    goLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goLoginActivity);
                    break;
                case R.id.drawer_b_google:
                    //FirebaseAuth.getInstance().signOut();
                    pref.deleteCurrentUser();
                    goLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    goLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goLoginActivity);
                    break;
                case R.id.drawer_b_email:
                    pref.deleteCurrentUser();
                    goLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    goLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goLoginActivity);
                    break;
            }
        }
    };

    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("position_list", String.valueOf(position));

            switch (navListCont.get(position)) {
                case "Акції":
                    Intent iDiscount = new Intent(getApplicationContext(), DiscountActivity.class);
                    iDiscount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iDiscount);
                    drawerLayout.closeDrawers();
                    break;
                case "Мої квитки":
                    Intent iTicket = new Intent(getApplicationContext(), MyTicketsActivity.class);
                    iTicket.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iTicket);
                    drawerLayout.closeDrawers();
                    //overridePendingTransition(R.anim.activity_fade_in_anim, R.anim.activity_fade_out_anim);
                    break;
                case "Мої бронювання":
                    Intent iBasket = new Intent(getApplicationContext(), BasketActivity.class);
                    iBasket.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iBasket);
                    drawerLayout.closeDrawers();
                    break;
                case "Правила бронювання":
                    Intent iBookRules = new Intent(getApplicationContext(), BookRulesActivity.class);
                    iBookRules.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iBookRules);
                    drawerLayout.closeDrawers();
                    break;
                case "Увійти":
                    Intent iLogIn = new Intent(getApplicationContext(), LoginActivity.class);
                    iLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iLogIn);
                    drawerLayout.closeDrawers();
                    break;
                case "Всі сеанси":
                    Intent iAllMovies = new Intent(getApplicationContext(), MovieTodayActivity.class);
                    iAllMovies.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iAllMovies);
                    drawerLayout.closeDrawers();
                    break;
                case "Незабаром":
                    Intent iSoonMovies = new Intent(getApplicationContext(), SoonActivity.class);
                    iSoonMovies.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iSoonMovies);
                    drawerLayout.closeDrawers();
                    break;
                case "Найближчі сеанси":
                    Intent iScheduleMovies = new Intent(getApplicationContext(), SchedulePageActivity.class);
                    iScheduleMovies.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iScheduleMovies);
                    drawerLayout.closeDrawers();
                    break;
            }
        }
    };

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_user:
                    Intent iLogIn = new Intent(getApplicationContext(), LoginActivity.class);
                    iLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(iLogIn);
                    break;
                case R.id.menu_item_we_on_the_map:
                    addPopupMap();
                    break;
                case R.id.sub_menu_first_number:
                    String tel1 = "tel:" + "(0332) 787-220";
                    Intent call1 = new Intent(Intent.ACTION_CALL, Uri.parse(tel1));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return true;
                    }
                    startActivity(call1);
                    break;
                case R.id.sub_menu_second_number:
                    String tel2 = "tel:" + "(068) 716-30-65";
                    Intent call2 = new Intent(Intent.ACTION_CALL, Uri.parse(tel2));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return true;
                    }
                    startActivity(call2);
                    break;
                case R.id.sub_menu_third_number:
                    String tel3 = "tel:" + "(050) 700-31-98";
                    Intent call3 = new Intent(Intent.ACTION_CALL, Uri.parse(tel3));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return true;
                    }
                    startActivity(call3);
                    break;
            }
            return true;
        }
    };

    public void setToolbarRef(Toolbar refToolbar) {
        this.toolbar = refToolbar;
    }

    //map
    RelativeLayout rl_map_container;
    ImageButton ibCloseMap;

    MapFragment mapFragment;
    PopupWindow popupWindow;

    Activity context;
    RelativeLayout rlRootMap;

    FrameLayout fl_blurry;
    SubMenu sm_contacts;
    SubMenu sm_schedule;

    public void inflateMapLayout(Activity context, RelativeLayout rlRoot) {
        this.context = context;
        this.rlRootMap = rlRoot;
        rl_map_container = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.custom_google_map_layout, null);
        ibCloseMap = (ImageButton) rl_map_container.findViewById(R.id.g_map_ib_close);
        ibCloseMap.setOnClickListener(onClickListener);
    }

    public void addPopupMap() {
        initMapFragment();

        fl_blurry = (FrameLayout) context.findViewById(R.id.blurryLayout);
        fl_blurry.setVisibility(View.VISIBLE);
        popupWindow = new PopupWindow(rl_map_container, 600, 600);
        popupWindow.showAtLocation(rlRootMap, Gravity.CENTER, 0, 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void initMapFragment() {
        mapFragment = (MapFragment) context.getFragmentManager()
                .findFragmentById(R.id.g_map_f_map);
        mapFragment.getMapAsync
                (new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        //map.setMyLocationEnabled(true);
                        map.setTrafficEnabled(true);
                        map.setIndoorEnabled(true);
                        map.setBuildingsEnabled(true);
                        map.getUiSettings().setZoomControlsEnabled(true);

                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(50.782165, 25.367234))
                                .title("Adrenalin City")
                                .snippet("Cinema"));

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.782165, 25.367234), 15));
                    }
                });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("remove_click", "remove");
            /*context.getFragmentManager()
                    .beginTransaction()
                    .remove(mapFragment)
                    .commit();*/
            popupWindow.dismiss();
            fl_blurry.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    };

    public DisplayMetrics measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

}
