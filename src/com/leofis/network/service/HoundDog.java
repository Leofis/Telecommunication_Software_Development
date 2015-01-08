package com.leofis.network.service;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import com.leofis.network.database.DatabaseAdapter;
import com.leofis.network.server.StatisticalReports;
import com.leofis.network.server.WebServiceAction;

import java.util.ArrayList;
import java.util.List;

public class HoundDog implements Runnable {

    private long sleepTime = 20 * 1000; /*property value*/
    private volatile boolean stillRun;

    private String username = null;
    private String password = null;

    private ArrayList<String> offlineData;

    private DatabaseAdapter adapter;
    private Context context;

    public HoundDog(boolean stillRun) {

        this.stillRun = stillRun;
        offlineData = new ArrayList<String>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setStillRun(boolean stillRun) {
        this.stillRun = stillRun;
    }

    public void setAdapter(DatabaseAdapter adapter) {
        this.adapter = adapter;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void run() {
        while (stillRun) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException x) {
                x.printStackTrace();
            }
            AsyncTaskRetrieve taskRetrieve = new AsyncTaskRetrieve();
            taskRetrieve.execute();
            getOfflineWork();
            if (!offlineData.isEmpty() && isOnline()) {
                for (int i = 0; i < offlineData.size(); i++) {
                    String[] parts = offlineData.get(i).split(" ");

                    if (parts[0].equals("i")) {
                        AsyncTaskAddPattern taskAddPattern = new AsyncTaskAddPattern(parts[1], null);
                        taskAddPattern.execute();
                        jobDone(offlineData.get(i));
                    } else if (parts[0].equals("w")) {
                        AsyncTaskAddPattern taskAddPattern = new AsyncTaskAddPattern(null, parts[1]);
                        taskAddPattern.execute();
                        jobDone(offlineData.get(i));
                    } else if (parts[0].equals("l")) {
                        AsyncTaskDelete taskDelete = new AsyncTaskDelete(parts[1]);
                        taskDelete.execute();
                        jobDone(offlineData.get(i));
                    }
                }
                offlineData.clear();
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected class AsyncTaskRetrieve extends AsyncTask<String, Void, List<StatisticalReports>> {
        @Override
        protected List<StatisticalReports> doInBackground(String... params) {
            WebServiceAction webservice = new WebServiceAction(context);
            List<StatisticalReports> reportList = webservice.retrieveStatistical(username, password);
            return reportList;
        }

        @Override
        protected void onPostExecute(List<StatisticalReports> reportList) {
            super.onPostExecute(reportList);

            String ipFreq;
            String patternFreq;
            String liveInterfaces;

            for (int i = 0; i < reportList.size(); i++) {
                StatisticalReports report = reportList.get(i);
                ipFreq = report.getIpFreqS();
                databaseMirror(ipFreq, "ip");
                patternFreq = report.getPatternFreqS();
                liveInterfaces = report.getLiveInterfacesS();
                saveLiveInstance(liveInterfaces);
                databaseMirror(patternFreq, "malicious");
            }
        }
    }

    protected class AsyncTaskAddPattern extends AsyncTask<String, Integer, Boolean> {
        private String pattern;
        private String patternTwo;

        public AsyncTaskAddPattern(String pattern, String patternTwo) {
            this.pattern = pattern;
            this.patternTwo = patternTwo;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            WebServiceAction webservice = new WebServiceAction(context);
            boolean result = webservice.insertPattern(username, password, pattern, patternTwo);
            return result;
        }
    }

    protected class AsyncTaskDelete extends AsyncTask<String, Void, Boolean> {
        private String nodeID;

        public AsyncTaskDelete(String nodeID) {
            this.nodeID = nodeID;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            WebServiceAction webservice = new WebServiceAction(context);
            boolean result = webservice.delete(nodeID);
            return result;
        }
    }

    private void databaseMirror(String string, String malicious) {
        if (string.isEmpty()) return;

        adapter.open();
        String chains = string.substring(1, string.length() - 1);
        //System.out.println(chains);
        String[] chain = chains.split(", ");
        for (int i = 0; i < chain.length; i++) {
            //System.out.println(chain[i]);
            String[] record = chain[i].split(" ");
            if (record.length != 9) return;
            String genericID = record[0];
            String interfaceName = record[2];
            String interfaceIP = record[4];
            String maliciousField = record[6];
            Integer count = Integer.parseInt(record[8].substring(1));
            //System.out.println("Inserting");
            if (malicious.equals("malicious")) {
                Boolean newEntry = adapter.updatePatternCount(genericID, interfaceName, interfaceIP,
                        maliciousField, count);
                if (!newEntry) adapter.insertPatternCount(genericID, interfaceName, interfaceIP,
                        maliciousField, count);
            } else if (malicious.equals("ip")) {
                Boolean newEntry = adapter.updateIPCount(genericID, interfaceName, interfaceIP,
                        maliciousField, count);
                if (!newEntry) adapter.insertIPCount(genericID, interfaceName, interfaceIP,
                        maliciousField, count);
            }
        }
        adapter.close();
    }

    private void getOfflineWork() {
        adapter.open();
        Cursor cursor = adapter.getAllOfflineWork();
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String requestedJob = cursor.getString(cursor.getColumnIndex("RequestedJob"));
                if (!offlineData.contains(requestedJob)) offlineData.add(requestedJob);
                cursor.moveToNext();
            }
        }
        cursor.close();
        adapter.close();
    }

    private void jobDone(String job) {
        adapter.open();
        adapter.deleteJob(job);
        adapter.close();
    }

    private synchronized void saveLiveInstance(String string) {
        String corners = string.substring(1, string.length() - 1);
        String[] parts = corners.split(", ");
        adapter.open();
        for (int i = 0; i < parts.length; i++) {
            String[] field = parts[i].split("=");
            if (field.length == 1) {
                adapter.close();
                return;
            } else {
                String[] part = field[0].split(" ");
                String genericID = part[0];
                String interfaceName = part[1];
                String state = field[1];

                Boolean newEntry = adapter.updateInterfaceState(genericID, interfaceName, state);
                if (!newEntry) adapter.insertInterfaceState(genericID, interfaceName, state);
            }
        }
        adapter.close();
    }
}


