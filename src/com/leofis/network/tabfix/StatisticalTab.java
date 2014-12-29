package com.leofis.network.tabfix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.leofis.network.R;

public class StatisticalTab extends Fragment {
    public static GridView gridViewTwo;
    public static GridView gridViewOne;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistical_tab_layout, container, false);
        gridViewOne = (GridView) view.findViewById(R.id.gridViewOne);
        gridViewTwo = (GridView) view.findViewById(R.id.gridViewTwo);

        return view;
    }
}
