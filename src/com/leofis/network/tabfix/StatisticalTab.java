package com.leofis.network.tabfix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import com.leofis.network.R;

public class StatisticalTab extends Fragment {

    public static ExpandableListView computerListExView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistical_tab_layout, container, false);
        Button logout = (Button) view.findViewById(R.id.register_button);
        computerListExView = (ExpandableListView) view.findViewById(R.id.expandableListViewComputer);

        return view;
    }
}
