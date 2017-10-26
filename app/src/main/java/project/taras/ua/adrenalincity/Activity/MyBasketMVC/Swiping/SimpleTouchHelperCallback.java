package project.taras.ua.adrenalincity.Activity.MyBasketMVC.Swiping;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Taras on 01.06.2017.
 */

public class SimpleTouchHelperCallback extends ItemTouchHelper.Callback {

    private ISwipe adapter;

    public SimpleTouchHelperCallback(ISwipe adapter){
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onDismiss(viewHolder.getAdapterPosition());
    }
}
