package project.taras.ua.adrenalincity.Activity.MovieMVC;

/**
 * Created by Taras on 27.07.2017.
 */

public interface IMyMovie {

    interface IClickListener{
        void onClick(int childPosition);
    }

    interface IPaymentListener{
        void onSuccessfulOrderAction(int requestType);
    }
}
