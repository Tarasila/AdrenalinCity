package project.taras.ua.adrenalincity.Activity;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.facebook.FacebookSdk;

import ua.privatbank.paylibliqpay.CheckoutActivity;

/**
 * Created by Taras on 30.03.2017.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true);
        WebView.getDefaultSize(600, View.MeasureSpec.EXACTLY);

    }
}
