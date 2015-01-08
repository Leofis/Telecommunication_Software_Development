package com.leofis.network;

import android.app.*;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import com.leofis.network.crypto.Decryptor;
import com.leofis.network.database.DatabaseAdapter;
import com.leofis.network.server.WebServiceAction;
import com.leofis.network.service.SilentHunter;
import com.leofis.network.tabfix.DeleteTab;
import com.leofis.network.tabfix.MaliciousTab;
import com.leofis.network.tabfix.TabAdapter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;

public class UserActivity extends FragmentActivity implements ActionBar.TabListener {

    // Declaring our tabs and the corresponding fragments.
    ActionBar.Tab maliciousTab, statisticalTab, interfaceTab, deleteTab;

    public static ViewPager viewPager;
    private TabAdapter tabAdapter;
    private ActionBar actionBar;
    private boolean superUser;

    private String username;
    private String password;
    //private final String ADMIN_USERNAME = "admin"; /* must be replaced with property file */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        fillCredentials(); /* fill the username and the password properly */
        superUser = (checkSuperUser() || checkSuperUserLogin());
        if (!isMyServiceRunning(SilentHunter.class)) {
            Intent service = new Intent(getBaseContext(), SilentHunter.class);
            service.putExtra("Username_Key", username);
            service.putExtra("Password_Key", password);
            startService(service);
        }

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.swipe);
        tabAdapter = new TabAdapter(getSupportFragmentManager(), superUser);
        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(3);

        //Button register = (Button) findViewById(R.id.register_button);
        //Button unregister = (Button) findViewById(R.id.unregister_button);
        //delEditText = (EditText) findViewById(R.id.genericID_textfield);

        // Asking for the default ActionBar element that our platform supports.
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);

        // Screen handling while hiding ActionBar icon.
        actionBar.setDisplayShowHomeEnabled(false);

        // Screen handling while hiding Actionbar title.
        actionBar.setDisplayShowTitleEnabled(true);

        // Creating ActionBar tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Setting custom tab icons.
        //bmwTab = actionBar.newTab().setIcon(R.drawable.bmw_logo);
        //toyotaTab = actionBar.newTab().setIcon(R.drawable.toyota_logo);
        //fordTab = actionBar.newTab().setIcon(R.drawable.ford_logo);

        // Setting tab titles.
        statisticalTab = actionBar.newTab().setText("Interfaces");
        interfaceTab = actionBar.newTab().setText("Statistical");
        if (superUser) {
            maliciousTab = actionBar.newTab().setText("Malicious Items");
            deleteTab = actionBar.newTab().setText("Delete IDs");
        }

        // Setting tab listeners.
        statisticalTab.setTabListener(this);
        interfaceTab.setTabListener(this);
        if (superUser) {
            maliciousTab.setTabListener(this);
            deleteTab.setTabListener(this);
        }

        // Adding tabs to the ActionBar.

        actionBar.addTab(statisticalTab);
        actionBar.addTab(interfaceTab);
        if (superUser) {
            actionBar.addTab(maliciousTab);
            actionBar.addTab(deleteTab);
        }

         /* on swiping the viewpager make respective tab selected */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            setURL();
            return true;
        }
        if (id == R.id.menu_logout) {
            logout(getCurrentFocus());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        MaliciousTab.addIPEditText.setError(null);
        MaliciousTab.addPatternEditText.setError(null);
        DeleteTab.delEditText.setError(null);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    public void addMalicious(View view) {
        // clearKeyboard();
        String maliciousIP = MaliciousTab.addIPEditText.getText().toString();
        if (maliciousIP.isEmpty() || maliciousIP.contains(" ")) {
            MaliciousTab.addIPEditText.setText("");
            MaliciousTab.addIPEditText.setError("Wrong Input - Empty Field.");
            return;
        }
        MaliciousTab.addIPEditText.setError(null);
        if (!isOnline()) {
            doOfflineWork("i " + maliciousIP);
            String maliciousMessage = "The IP " + maliciousIP + " will be added when connection ll be established.";
            Toast toast = Toast.makeText(getApplicationContext(), maliciousMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.NO_GRAVITY, 0, 70);
            toast.show();
            MaliciousTab.addIPEditText.setText("");
            MaliciousTab.addPatternEditText.setText("");
            return;
        } else {
            AsyncTaskAddPattern taskAddPattern = new AsyncTaskAddPattern(maliciousIP, null);
            taskAddPattern.execute();
        }

    }

    public void addMaliciousPa(View view) {
        clearKeyboard();
        String maliciousPattern = MaliciousTab.addPatternEditText.getText().toString();
        if (maliciousPattern.isEmpty() || maliciousPattern.contains(" ")) {
            MaliciousTab.addPatternEditText.setText("");
            MaliciousTab.addPatternEditText.setError("Wrong Input - Empty Field.");
            return;
        }
        MaliciousTab.addPatternEditText.setError(null);
        if (!isOnline()) {
            doOfflineWork("w " + maliciousPattern);
            String maliciousMessage = "The word " + maliciousPattern + " will be added when connection ll be established.";
            Toast toast = Toast.makeText(getApplicationContext(), maliciousMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.NO_GRAVITY, 0, 70);
            toast.show();
            MaliciousTab.addIPEditText.setText("");
            MaliciousTab.addPatternEditText.setText("");
            return;
        } else {
            AsyncTaskAddPattern taskAddPattern = new AsyncTaskAddPattern(null, maliciousPattern);
            taskAddPattern.execute();
        }
    }

    public void showAllMalicious(View view) {
        if (!isOnline()) return;
        AsyncTaskShowAll taskShowAll = new AsyncTaskShowAll();
        taskShowAll.execute();
    }

    public void registerComputer(View view) {

        if (!isOnline()) return;
        clearKeyboard();
        String genericID = DeleteTab.delEditText.getText().toString();
        if (genericID.isEmpty() || genericID.contains(" ")) {
            DeleteTab.delEditText.setText("");
            return;
        }
        String yourRegister = "The " + genericID + " successfully registered.";
        Toast toast = Toast.makeText(getApplicationContext(), yourRegister, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.NO_GRAVITY, 0, 90);
        toast.show();
        DeleteTab.delEditText.setText("");
        DeleteTab.delEditText.setError(null);
        AsyncTaskRegister taskRegister = new AsyncTaskRegister(genericID);
        taskRegister.execute();
    }

    public void unregisterComputer(View view) {
        clearKeyboard();
        String genericID = DeleteTab.delEditText.getText().toString();
        if (genericID.isEmpty() || genericID.contains(" ")) {
            DeleteTab.delEditText.setText("");
            DeleteTab.delEditText.setError("Wrong Input - Empty Field.");
            return;
        }
        DeleteTab.delEditText.setText("");
        DeleteTab.delEditText.setError(null);
        if (!isOnline()) {
            doOfflineWork("l " + genericID);
            String maliciousMessage = "The Computer " + genericID + " will be deleted when connection ll be established.";
            Toast toast = Toast.makeText(getApplicationContext(), maliciousMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.NO_GRAVITY, 0, 90);
            toast.show();
            return;
        } else {
            AsyncTaskDelete taskDelete = new AsyncTaskDelete(genericID);
            taskDelete.execute();
        }
    }

    public void showAllComputers(View view) {
        final ArrayList<String> listComputerTitles = new ArrayList<String>();
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();
        Cursor cursor = adapter.getAllInterfaceTable();
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String nodeID = cursor.getString(cursor.getColumnIndex("GenericID"));
                if (!listComputerTitles.contains(nodeID)) listComputerTitles.add(nodeID);
                cursor.moveToNext();
            }
        }
        cursor.close();

        if (!listComputerTitles.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listComputerTitles);
            DeleteTab.listViewComputer.setAdapter(arrayAdapter);
            DeleteTab.listViewComputer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                    DeleteTab.delEditText.setText(listComputerTitles.get(pos));
                    String yourRegister = "Press 'Delete' button to continue the deletion process.";
                    Toast toast = Toast.makeText(getApplicationContext(), yourRegister, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.NO_GRAVITY, 0, 90);
                    toast.show();
                    return true;
                }
            });
        }
    }

    public void logout(View view) {
        AsyncTaskLogout taskLogout = new AsyncTaskLogout(username, password);
        taskLogout.execute();
    }

    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*----------------------------------------------*/

    protected class AsyncTaskAddPattern extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog dialog = new ProgressDialog(UserActivity.this);
        private String pattern;
        private String patternTwo;

        public AsyncTaskAddPattern(String pattern, String patternTwo) {
            this.pattern = pattern;
            this.patternTwo = patternTwo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            WebServiceAction webservice = new WebServiceAction(getApplicationContext());
            boolean result = webservice.insertPattern(username, password, pattern, patternTwo);
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setMessage("Completing Registration...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            super.onPostExecute(successful);

            if (successful) {
                String show;
                if (pattern == null) show = patternTwo;
                else show = pattern;
                String maliciousMessage = "The " + show + " added successfully.";
                Toast toast = Toast.makeText(getApplicationContext(), maliciousMessage, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 60);
                toast.show();
                MaliciousTab.addIPEditText.setText("");
                MaliciousTab.addPatternEditText.setText("");
            } else {
                String show;
                if (pattern == null) show = patternTwo;
                else show = pattern;
                String maliciousMessage = "Error " + show + " couldn't be added.";
                Toast toast = Toast.makeText(getApplicationContext(), maliciousMessage, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 60);
                toast.show();
                MaliciousTab.addIPEditText.setText("");
                MaliciousTab.addPatternEditText.setText("");
            }
        }
    }

    protected class AsyncTaskShowAll extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog = new ProgressDialog(UserActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            WebServiceAction webservice = new WebServiceAction(getApplicationContext());
            String result = webservice.retrievePattern(username, password);
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setMessage("Completing Registration...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String cryptChainPCs) {
            super.onPostExecute(cryptChainPCs);
            String chainPCs = null;
            if (cryptChainPCs == null) return;
            Log.i("All malicious", cryptChainPCs);

            byte[] decodedKey = Base64.decode("95iFzT0xmGc=", Base64.DEFAULT);
            SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");

            try {
                Decryptor decryptor = new Decryptor(key);
                chainPCs = decryptor.decrypt(cryptChainPCs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("All malicious", chainPCs);

            if (!chainPCs.isEmpty()) {
                String[] parts = chainPCs.split(" <|> ");
                if (parts.length != 3) return;
                String maliciousIPs = parts[0];
                String maliciousPatterns = parts[2];

                if (!maliciousIPs.isEmpty()) {
                    String[] items = maliciousIPs.split(" ");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserActivity.this, android.R.layout.simple_list_item_1, items);
                    MaliciousTab.ipListView.setAdapter(adapter);
                }

                if (!maliciousPatterns.isEmpty()) {
                    String[] items = maliciousPatterns.split(" ");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserActivity.this, android.R.layout.simple_list_item_1, items);
                    MaliciousTab.patternListView.setAdapter(adapter);
                }

            } else {
                String message = "Error : No Malicious Patterns found.";
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 60);
                toast.show();
            }
        }
    }

    protected class AsyncTaskRegister extends AsyncTask<String, Void, Void> {
        private String genericID;

        public AsyncTaskRegister(String genericID) {
            this.genericID = genericID;
        }

        @Override
        protected Void doInBackground(String... params) {
            WebServiceAction webservice = new WebServiceAction(getApplicationContext());
            webservice.register(genericID);
            return null;
        }
    }

    protected class AsyncTaskDelete extends AsyncTask<String, Void, Boolean> {
        private String genericID;

        public AsyncTaskDelete(String genericID) {
            this.genericID = genericID;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            WebServiceAction webservice = new WebServiceAction(getApplicationContext());
            boolean result = webservice.delete(genericID);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean deleted) {
            super.onPostExecute(deleted);

            if (deleted) {
                String yourRegister = "The Computer " + genericID + " successfully deleted from database.";
                Toast toast = Toast.makeText(getApplicationContext(), yourRegister, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 90);
                toast.show();

                DatabaseAdapter adapter = new DatabaseAdapter(getApplicationContext());
                adapter.open();
                adapter.deletePC(genericID);
                adapter.close();
            } else {
                String yourRegister = "The Computer  " + genericID + " doesn't exists.";
                Toast toast = Toast.makeText(getApplicationContext(), yourRegister, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 90);
                toast.show();
            }
        }
    }

    protected class AsyncTaskLogout extends AsyncTask<String, Void, Boolean> {

        private String username;
        private String password;

        public AsyncTaskLogout(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            WebServiceAction webservice = new WebServiceAction(getApplicationContext());
            boolean result = webservice.logout(username, password);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean logoutIndeed) {
            super.onPostExecute(logoutIndeed);

            if (logoutIndeed) {
                SharedPreferences loginPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.remove("Username_Key");
                editor.remove("Password_Key");
                editor.remove("isLoggedIn");
                editor.remove("User_Type");
                editor.commit();
                stopService(new Intent(getBaseContext(), SilentHunter.class));
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean checkSuperUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getInt("User_Type", 0) == 1) return true;
        else return false;
    }

    private boolean checkSuperUserLogin() {
        Intent intent = getIntent();
        int userType = intent.getIntExtra("User_Type", 0);
        if (!intent.hasExtra("User_Type")) return false;
        if (userType == 1) return true;
        else return false;
    }

    private void fillCredentials() {
        Intent intent = getIntent();
        if (!intent.hasExtra("Username_Key") && !intent.hasExtra("Password_Key")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            username = preferences.getString("Username_Key", "default");
            password = preferences.getString("Password_Key", "default");
        } else {
            username = intent.getStringExtra("Username_Key");
            password = intent.getStringExtra("Password_Key");
        }
    }

    private void doOfflineWork(String requestedJob) {
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();
        adapter.insertOfflineWork(requestedJob);
        adapter.close();
    }

    private void setURL() {
        final EditText txtUrl = new EditText(this);
        txtUrl.setInputType(InputType.TYPE_CLASS_PHONE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String string = preferences.getString("URL", "Empty");
        if (!string.equals("Empty")) {
            String[] part = string.split("/");
            string = part[2].split(":")[0];
        }

        new AlertDialog.Builder(this)
                .setTitle("Web Services Initialization")
                .setMessage("Please type the desired IP for the normal operation of the Web methods. (" + string + ")")
                .setView(txtUrl)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = txtUrl.getText().toString();
                        if (url.isEmpty()) return;
                        SharedPreferences loginPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = loginPreferences.edit();
                        editor.putString("URL", "http://" + url + ":9999/LeofisService/LeofisService?WSDL");
                        editor.commit();
                    }
                })
                .show();
    }

    private void clearKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(DeleteTab.delEditText.getWindowToken(), 0);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SilentHunter.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}