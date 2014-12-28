package com.leofis.network.visual_tools;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.leofis.network.R;

import java.util.ArrayList;
import java.util.List;

public class StatsAdapter extends BaseAdapter {
    private List<Item> items = new ArrayList<Item>();
    private LayoutInflater inflater;

    public StatsAdapter(Context context, Cursor cursor, char c) {
        inflater = LayoutInflater.from(context);

        int id = 0;
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String interfaceIP = cursor.getString(cursor.getColumnIndex("InterfaceIP"));
                String malicious = null;
                if (c == 'i') malicious = cursor.getString(cursor.getColumnIndex("MaliciousIP"));
                if (c == 'w') malicious = cursor.getString(cursor.getColumnIndex("MaliciousPattern"));
                int count = cursor.getInt(cursor.getColumnIndex("Count"));
                Item item = new Item(++id, interfaceIP, malicious, count);
                items.add(item);
                cursor.moveToNext();
            }
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
            view.setTag(R.id.gridOne, view.findViewById(R.id.gridOne));
            view.setTag(R.id.gridTwo, view.findViewById(R.id.gridTwo));
            view.setTag(R.id.gridThree, view.findViewById(R.id.gridThree));
        }

        TextView tv1 = (TextView) view.getTag(R.id.gridOne);
        TextView tv2 = (TextView) view.getTag(R.id.gridTwo);
        TextView tv3 = (TextView) view.getTag(R.id.gridThree);

        Item item = (Item) getItem(i);

        tv1.setText(item.interfaceIP);
        tv2.setText(item.malicious);
        tv3.setText(String.valueOf(item.count));

        return view;
    }

    private class Item {
        int id;
        String interfaceIP;
        String malicious;
        int count;

        Item(int id, String interfaceIP, String malicious, int count) {
            this.id = id;
            this.interfaceIP = interfaceIP;
            this.malicious = malicious;
            this.count = count;
        }
    }
}
