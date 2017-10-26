package project.taras.ua.adrenalincity.Activity.MyTicketsMVC;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Taras on 08.05.2017.
 */

public interface IMyTicket {

    interface IBasketListener{
        void onTicketsUpdated();
    }

    interface FirebaseListener {
        void onMyTicketsLoaded(List<HashMap<String, String>> listTickets);
    }

    interface PaymentBookValidationListener{
        void onBookAllowed();
        void onBookRejected(int code, int params);
        void onSuccessfulEticketsSaving();
    }
}
