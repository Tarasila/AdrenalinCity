package project.taras.ua.adrenalincity.Activity.TodayMovieMVC;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 27.04.2017.
 */

public enum EContainerId {
    ZERO(R.id.container_youTube_player0),
    ONE(R.id.container_youTube_player1),
    TWO(R.id.container_youTube_player2),
    THREE(R.id.container_youTube_player3),
    FOUR(R.id.container_youTube_player4),
    FIVE(R.id.container_youTube_player5),
    SIX(R.id.container_youTube_player6),
    SEVEN(R.id.container_youTube_player7),
    EIGHT(R.id.container_youTube_player8),
    NINE(R.id.container_youTube_player9),
    TEN(R.id.container_youTube_player10),
    ELEVEN(R.id.container_youTube_player11),
    TWELFTH(R.id.container_youTube_player12),
    THIRTEEN(R.id.container_youTube_player13),
    FOURTEEN(R.id.container_youTube_player14),
    FIFTEEN(R.id.container_youTube_player15);

    int layoutResId;

    EContainerId(int resId) {
        this.layoutResId = resId;
    }

    public int getLayoutResId(){
        return layoutResId;
    }
}
