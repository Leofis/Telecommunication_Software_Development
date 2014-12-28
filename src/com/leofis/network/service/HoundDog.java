package com.leofis.network.service;

import android.os.AsyncTask;
import com.leofis.network.database.DatabaseAdapter;
import com.leofis.network.server.StatisticalReports;
import com.leofis.network.server.WebServiceAction;

import java.util.List;

public class HoundDog implements Runnable {
    private long sleepTime = 20 * 1000; /*property value*/
    private volatile boolean stillRun;

    private String username = null;
    private String password = null;

    private DatabaseAdapter adapter;

    public HoundDog(boolean stillRun) {
        this.stillRun = stillRun;
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
        }
    }

    protected class AsyncTaskRetrieve extends AsyncTask<String, Void, List<StatisticalReports>> {
        @Override
        protected List<StatisticalReports> doInBackground(String... params) {
            WebServiceAction webservice = new WebServiceAction();
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


