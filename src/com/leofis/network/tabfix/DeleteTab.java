package com.leofis.network.tabfix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import com.leofis.network.R;

public class DeleteTab extends Fragment {

    public static EditText delEditText;
    public static ListView listViewComputer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)

    {
        View view = inflater.inflate(R.layout.delete_tab_layout, container, false);
        //Button register = (Button) view.findViewById(R.id.register_button);
        //Button unregister = (Button) view.findViewById(R.id.unregister_button);
        //Button showComputerButton = (Button) view.findViewById(R.id.showAllComputers);
        delEditText = (EditText) view.findViewById(R.id.genericID_textfield);
        listViewComputer = (ListView) view.findViewById(R.id.computer_listView);
        return view;
    }
}
