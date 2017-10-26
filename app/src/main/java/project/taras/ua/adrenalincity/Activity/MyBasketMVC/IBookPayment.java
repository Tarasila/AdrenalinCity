package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import java.util.Map;

/**
 * Created by Taras on 01.06.2017.
 */

public interface IBookPayment {

    interface IClickListener{
        void onClick(int childPosition);
    }

    interface IBookPaymentListener{
        void onSuccessfulOrderAction(int requestType);
        void onOrderDismiss(Map<String, String> map_ctShowId_with_seatId);

    }
}
