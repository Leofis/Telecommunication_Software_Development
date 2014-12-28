package com.leofis.network.tabfix;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class TabListener implements ActionBar.TabListener {

    private Fragment fragment;
    private ViewPager viewPager;

    // The constructor.
    public TabListener(Fragment fragment, ViewPager viewPager) {
        this.viewPager = viewPager;
        this.fragment = fragment;
    }

    // When a tab is tapped, the FragmentTransaction replaces
    // the content of our main layout with the specified fragment;
    // that's why we declared an id for the main layout.
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
        //transaction.replace(R.id.activity_user,fragment);
        viewPager.setCurrentItem(tab.getPosition());
    }

    // When a tab is unselected, we have to hide it from the user's view.
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
        //transaction.remove(fragment);
    }

    // Nothing special here. Fragments already did the job.
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
}