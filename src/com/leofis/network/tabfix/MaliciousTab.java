package com.leofis.network.tabfix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import com.leofis.network.R;

public class MaliciousTab extends Fragment {

    public static EditText addIPEditText;
    public static EditText addPatternEditText;
    public static ListView ipListView;
    public static ListView patternListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.malicious_tab_layout, container, false);
        //Button addIP = (Button) view.findViewById(R.id.add_ip_button);
        //Button addPattern = (Button) view.findViewById(R.id.add_pattern_button);
        addPatternEditText = (EditText) view.findViewById(R.id.add_pattern_textfield);
        addIPEditText = (EditText) view.findViewById(R.id.add_ip_textfield);
        ipListView = (ListView) view.findViewById(R.id.ip_listView);
        patternListView = (ListView) view.findViewById(R.id.pattern_listView);

        return view;
    }
}
