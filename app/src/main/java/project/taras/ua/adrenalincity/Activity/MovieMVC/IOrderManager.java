package project.taras.ua.adrenalincity.Activity.MovieMVC;

import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderModel;

/**
 * Created by Taras on 04.07.2017.
 */

public interface IOrderManager {

    void onOrderSuccessfullyAddedToSQL(OrderModel[] modelArray);

    void onLimitExceeded(int exceededNumberOfOrders);
}
