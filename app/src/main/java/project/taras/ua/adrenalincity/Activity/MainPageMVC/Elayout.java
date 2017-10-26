package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 18.04.2017.
 */

public enum Elayout {

    ZERO(R.id.zero),
    ONE(R.id.one),
    TWO(R.id.two),
    THREE(R.id.three),
    FOUR(R.id.four),
    FIVE(R.id.five),
    SIX(R.id.six),
    SEVEN(R.id.seven),
    EIGHT(R.id.eight),
    NINE(R.id.nine),
    TEN(R.id.ten),
    ELEVEN(R.id.eleven),
    TWELFTH(R.id.twelfth),
    THIRTEEN(R.id.thirteen),
    FOURTEEN(R.id.fourteen),
    FIFTEEN(R.id.fifteen),
    SIXTEEN(R.id.sixteen),
    SEVENTEEN(R.id.seventeen),
    EIGHTEEN(R.id.eighteen),
    NINETEEN(R.id.nineteen),
    TWENTY(R.id.twenty),
    TWENTYONE(R.id.twentyone),
    TWENTYTWO(R.id.twentytwo),
    TWENTYTHREE(R.id.twentythree),
    TWENTYFOUR(R.id.twentyfour);

    int layoutResId;

    Elayout(int layoutResId) {
        this.layoutResId = layoutResId;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

}
