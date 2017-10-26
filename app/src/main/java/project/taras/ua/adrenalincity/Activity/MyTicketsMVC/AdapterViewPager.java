package project.taras.ua.adrenalincity.Activity.MyTicketsMVC;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.List;

import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderModel;

/**
 * Created by Taras on 09.05.2017.
 */

public class AdapterViewPager extends PagerAdapter {

    private QrGenerator qrGenerator;
    private Context context;
    private List<OrderModel> orders;
    private boolean dataIsLoaded = false;
    private int numberOfLoadedImages;

    public AdapterViewPager(Context context){
        this.context = context;
        this.qrGenerator = new QrGenerator();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        OrderModel order = orders.get(position);
        String userName = order.getUserName();
        String movieId = order.getMovieId();
        String orderId = String.valueOf(order.getId());
        String raw = order.getSeatRaw();
        String place = order.getSeatPlace();
        String moviePoster = order.getMoviePoster();
        String movieTitle = order.getMovieTitle();
        String movieDate = order.getDate();
        String date = order.getDate();
        String time = order.getShowTime();
        String hall = order.getHall();
        String price = order.getSeatPrice();

        String stringToEncode = userName + raw + place;
        Bitmap btmQrCode = null;
        Bitmap btmBarCode = null;
        try {
            btmQrCode = qrGenerator.encodeAsBitmap(stringToEncode);
            btmBarCode = qrGenerator.encodeAsBarCodeBitmap(stringToEncode, BarcodeFormat.CODE_128, 600, 300);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        TicketView ticketView = new TicketView(context);
        ViewGroup.LayoutParams ticketView_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ticketView.setLayoutParams(ticketView_params);
        ticketView.setImage(moviePoster);
        ticketView.setQrCode(btmQrCode);
        ticketView.setBarCode(btmBarCode);
        ticketView.setMovieTitle(movieTitle);
        ticketView.setDate(date, time);
        ticketView.setHall(hall);
        ticketView.setRawAndPlace(raw, place);
        ticketView.setPrice(price);

        container.addView(ticketView);

        return ticketView;
    }

    public void setData(List<OrderModel> orders){
        this.orders = orders;
        dataIsLoaded = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (!dataIsLoaded) {
            return 0;
        }
        else return this.orders.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
