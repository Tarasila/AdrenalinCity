package project.taras.ua.adrenalincity.Activity.MovieMVC.ValueBarView;

import android.util.Log;

/**
 * Created by Taras on 15.03.2017.
 */

public class ThumbCircle {

    int x;
    int y;
    int radius;

    public ThumbCircle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        Log.v("TAG_Y_CONSTR", ""+y);
        this.radius = radius;
    }

    public void moveCircleY(int y1){
        y = y1;
    }

    public void moveCircleX(int x1){
        x = x1;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
