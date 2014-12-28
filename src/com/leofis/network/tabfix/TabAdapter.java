package com.leofis.network.tabfix;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

    boolean superUser;

    public TabAdapter(FragmentManager fragmentManager, boolean superUser) {
        super(fragmentManager);
        this.superUser = superUser;
    }

    @Override
    public Fragment getItem(int index) {
        if (!superUser) {
            switch (index) {
                case 0:
                    return new StatisticalTab();
                case 1:
                    return new InterfaceTab();
            }
        } else {
            switch (index) {
                case 0:
                    return new StatisticalTab();
                case 1:
                    return new InterfaceTab();
                case 2:
                    return new MaliciousTab();
                case 3:
                    return new DeleteTab();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        if (!superUser) return 2;
        else return 4;
    }
}
