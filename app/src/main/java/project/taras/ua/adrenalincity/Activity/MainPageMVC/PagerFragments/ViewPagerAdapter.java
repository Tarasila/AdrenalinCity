package project.taras.ua.adrenalincity.Activity.MainPageMVC.PagerFragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taras on 26.06.2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragment_list;
    private List<String> listString = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragment_list = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragment_list.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listString.get(position);
    }

    @Override
    public int getCount() {
        return fragment_list.size();
    }

    public void add(Fragment fragment, String title){
        fragment_list.add(fragment);
        listString.add(title);
    }
}
