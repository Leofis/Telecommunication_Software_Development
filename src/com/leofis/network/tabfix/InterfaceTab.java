package com.leofis.network.tabfix;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Switch;
import com.leofis.network.R;
import com.leofis.network.UserActivity;
import com.leofis.network.database.DatabaseAdapter;
import com.leofis.network.visual_tools.ExpandableListAdapter;
import com.leofis.network.visual_tools.StatsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InterfaceTab extends Fragment {

    public static ExpandableListView computerListExView;
    public static Switch switcher;
    private SwipeRefreshLayout refreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.interface_tab_layout, container, false);

        Button logout = (Button) view.findViewById(R.id.register_button);
        computerListExView = (ExpandableListView) view.findViewById(R.id.expandableListViewComputer);
        switcher = (Switch) view.findViewById(R.id.switchPCs);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        refreshLayout.setColorScheme(R.color.oliveOil,
                R.color.darkGreen);

        if (checkSuperUser() || checkSuperUserLogin()) switcher.setEnabled(false);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        return view;
    }

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return super.getLayoutInflater(savedInstanceState);
    }

    private void getData() {

        ExpandableListAdapter exListAdapter;
        final List<String> listComputerTitles = new ArrayList<String>();
        final HashMap<String, List<String>> listComputerInterfaces = new HashMap<String, List<String>>();
        //HashMap<String, List<String>> tempHash = new HashMap<String, List<String>>();

        DatabaseAdapter adapter = new DatabaseAdapter(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String registeredPCs = preferences.getString("RegisteredPCs", "Null");
        adapter.open();
        Cursor cursor = adapter.getAllInterfaceTable();
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String nodeID = cursor.getString(cursor.getColumnIndex("GenericID"));
                if (!listComputerTitles.contains(nodeID)) {
                    if (switcher.isChecked()) {
                        if (registeredPCs.contains(nodeID)) listComputerTitles.add(nodeID);
                    } else listComputerTitles.add(nodeID);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();

        for (int i = 0; i < listComputerTitles.size(); i++) {
            Cursor cursorTwo = adapter.interfaceStatePerCo(listComputerTitles.get(i));
            List<String> tempList = new ArrayList<String>();

            while (!cursorTwo.isAfterLast()) {
                String interfaceName = cursorTwo.getString(cursorTwo.getColumnIndex("InterfaceName"));
                String state = cursorTwo.getString(cursorTwo.getColumnIndex("State"));
                tempList.add(interfaceName + " " + state);
                cursorTwo.moveToNext();
            }
            listComputerInterfaces.put(listComputerTitles.get(i), tempList);
            cursorTwo.close();
        }
        adapter.close();

        /****** Listeners ******/

        exListAdapter = new ExpandableListAdapter(getActivity(), listComputerTitles, listComputerInterfaces);
        // setting list adapter
        InterfaceTab.computerListExView.setAdapter(exListAdapter);

        // ExListView Group click listener
        InterfaceTab.computerListExView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        // ExListView Group expanded listener
        InterfaceTab.computerListExView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        // ExListView Group collapsed listener
        InterfaceTab.computerListExView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        // ExListView on child click listener
        InterfaceTab.computerListExView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String genericID;
                String interfaceName;

                genericID = listComputerTitles.get(groupPosition);
                interfaceName = listComputerInterfaces.get(listComputerTitles.get(groupPosition)).get(childPosition).split(" ")[0];

                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
                databaseAdapter.open();

                Cursor cursor = databaseAdapter.getStatisticsPerIPInterface(genericID, interfaceName);
                StatisticalTab.gridViewOne.setAdapter(new StatsAdapter(getActivity(), cursor, 'i'));

                Cursor cursorTwo = databaseAdapter.getStatisticsPerPatternInterface(genericID, interfaceName);
                StatisticalTab.gridViewTwo.setAdapter(new StatsAdapter(getActivity(), cursorTwo, 'w'));

                if ((cursor.getCount() == 0) && (cursorTwo.getCount() == 0)) noStatsShow();
                else {
                    StatisticalTab.textViewOne.setText("Hits with Malicious IPs");
                    StatisticalTab.textViewTwo.setText("Hits with Malicious Patterns");
                    StatisticalTab.textViewOneDe.setText("Interface IP             Malicious IP       Count");
                    StatisticalTab.textViewTwoDe.setText("Interface IP             Pattern                Count");
                    UserActivity.viewPager.setCurrentItem(1);
                }

                cursor.close();
                cursorTwo.close();
                databaseAdapter.close();
                return false;
            }
        });
    }

    private void noStatsShow() {
        new AlertDialog.Builder(getActivity())
                .setTitle("No traffic")
                .setMessage("The current interface you choose doesn't seem to have any malicious IP or pattern in the database.")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.warning)
                .show();
    }

    private boolean checkSuperUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.getInt("User_Type", 0) == 1) return true;
        else return false;
    }

    private boolean checkSuperUserLogin() {
        Intent intent = getActivity().getIntent();
        int userType = intent.getIntExtra("User_Type", 0);
        if (!intent.hasExtra("User_Type")) return false;
        if (userType == 1) return true;
        else return false;
    }
}
