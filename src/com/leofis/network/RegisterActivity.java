package com.leofis.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.leofis.network.server.WebServiceAction;

import java.util.ArrayList;

public class RegisterActivity extends Activity {

    private ArrayList<String> myComputers = new ArrayList<String>();
    private Button addComputerButton;
    private EditText addComputerText;
    private ListView addedComputers;

    private EditText usernameText;
    private EditText passwordText;
    private EditText passwordConfText;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        addComputerButton = (Button) findViewById(R.id.computer_ID_button);
        addComputerText = (EditText) findViewById(R.id.computer_ID_text);
        addedComputers = (ListView) findViewById(R.id.computer_list);

        usernameText = (EditText) findViewById(R.id.register_username_text);
        passwordText = (EditText) findViewById(R.id.register_pass_text);
        passwordConfText = (EditText) findViewById(R.id.register_passConfirm_text);

        loginPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loginEditor = loginPreferences.edit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void addComputer(View view) {
        String pcName = addComputerText.getText().toString();
        if (!pcName.isEmpty() && !pcName.contains(" ") && !pcName.contains(System.getProperty("line.separator"))) {
            if (myComputers.contains(pcName)) {
                addComputerText.setText("");
                Toast.makeText(this.getApplicationContext(), "You have already added this before", Toast.LENGTH_SHORT).show();
            } else {
                myComputers.add(pcName);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myComputers);
                addedComputers.setAdapter(adapter);
                ((EditText) findViewById(R.id.computer_ID_text)).setText("");
            }
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(addComputerText.getWindowToken(), 0);
        } else addComputerText.setText("");
    }

    public void registerUser(View view) {

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordConf = passwordConfText.getText().toString();

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);

        if (!isOnline()) {

            Toast toast = Toast.makeText(getApplicationContext(), "No Network connection", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.NO_GRAVITY,0, 150);
            toast.show();
        } else if (checkCredentials()) {
            usernameText.setText("");
            passwordText.setText("");
            passwordConfText.setText("");
            usernameText.setHintTextColor(Color.GRAY);
            passwordText.setHintTextColor(Color.GRAY);
            passwordConfText.setHintTextColor(Color.GRAY);
            usernameText.setHint("Username");
            passwordText.setHint("Password");
            passwordConfText.setHint("Confirm Password");
            addedComputers.setAdapter(null);
            Log.i("Username", username);
            Log.i("Password", password);
            Log.i("Password Repeat", passwordConf);
            AsyncTaskRegister taskRegister = new AsyncTaskRegister(username, password, myComputers.toArray(new String[myComputers.size()]));
            taskRegister.execute();
            myComputers.clear();
        }
    }

    private boolean checkCredentials() {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordConf = passwordConfText.getText().toString();
        boolean result = true;
        boolean identical = false;

        if (username.isEmpty()) {
            if (password.isEmpty() && passwordConf.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Please complete all the fields in the form.", Toast.LENGTH_LONG);
                // toast.setGravity(Gravity.NO_GRAVITY,0, 150);
                usernameText.setHintTextColor(Color.GRAY);
                passwordText.setHintTextColor(Color.GRAY);
                passwordConfText.setHintTextColor(Color.GRAY);
                usernameText.setHint("Username");
                passwordText.setHint("Password");
                passwordConfText.setHint("Confirm Password");
                toast.show();
                result = false;
                return result;
            } else {
                // usernameText.setHintTextColor(Color.RED);
                // usernameText.setHint("Username cannot be Blank");
                usernameText.setError("Username cannot be Blank");
                result = false;
            }
        } else usernameText.setError(null);

        if (password.isEmpty()) {
            //passwordText.setHintTextColor(Color.RED);
            //passwordText.setHint("Password cannot be Blank");
            passwordText.setError("Password cannot be Blank");
            result = false;
        } else passwordText.setError(null);

        if (passwordConf.isEmpty()) {
            //passwordConfText.setHintTextColor(Color.RED);
            // passwordConfText.setHint("Confirmation cannot be Blank");
            passwordConfText.setError("Confirmation cannot be Blank");
            result = false;
        } else passwordConfText.setError(null);

        if (username.contains(" ") || password.contains(" ") || passwordConf.contains(" ")) {
            if (username.contains(" ")) {
                usernameText.setText("");
                //usernameText.setHintTextColor(Color.RED);
                //usernameText.setHint("Username has gaps");
                usernameText.setError("Username has gaps");
            } else usernameText.setError(null);
            if (password.contains(" ")) {
                passwordText.setText("");
                // passwordText.setHintTextColor(Color.RED);
                // passwordText.setHint("Password has gaps");
                passwordText.setError("Password has gaps");
            } else passwordText.setError(null);

            if (passwordConf.contains(" ")) {
                passwordConfText.setText("");
                //passwordConfText.setHintTextColor(Color.RED);
                //passwordConfText.setHint("Confirmation has gaps");
                passwordConfText.setError("Confirmation has gaps");
            } else passwordConfText.setError(null);

            return false;
        }

        if (password.equals(passwordConf)) identical = true;

        if (myComputers.isEmpty() || !identical) {
            if (myComputers.isEmpty()) {
                addComputerText.setError("Choose at least one Computer");
                Toast toast = Toast.makeText(getApplicationContext(), "You didn't choose your Computers.", Toast.LENGTH_LONG);
                // toast.setGravity(Gravity.NO_GRAVITY,0, 150);
                toast.show();
            } else {
                addComputerText.setError(null);
                passwordConfText.setText("");
                // passwordConfText.setHintTextColor(Color.RED);
                // passwordConfText.setHint("Not Match with the Password");
                passwordConfText.setError("Not Match with the Password");
                Toast toast = Toast.makeText(getApplicationContext(), "Password and Confirm Password don't match ", Toast.LENGTH_LONG);
                // toast.setGravity(Gravity.NO_GRAVITY,0, 150);
                toast.show();
            }
            return false;
        }
        return result;
    }

    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void errorRegister() {
        new AlertDialog.Builder(this)
                .setTitle("Registration Failed!")
                .setMessage("Your username is taken or some of your computers is managed by another member!")
                /*android.R.string.no*/
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void saveCredentials(String username, String password) {
        loginEditor.putString("Username_Key", username);
        loginEditor.putString("Password_Key", password);
        loginEditor.putString("isLoggedIn", "yes");
        loginEditor.commit();
    }

    protected class AsyncTaskRegister extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        private String username;
        private String password;
        private String[] computers;

        public AsyncTaskRegister(String username, String password, String[] computers) {
            this.username = username;
            this.password = password;
            this.computers = computers;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            publishProgress();
            WebServiceAction webservice = new WebServiceAction();
            boolean result = webservice.registerAndroid(username, password, computers);
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
        protected void onPostExecute(Boolean register_successful) {
            super.onPostExecute(register_successful);

            if (register_successful) {
                dialog.dismiss();
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Easy Login Choice :")
                        .setMessage("Do you want to automatically login next time?")
                        .setPositiveButton("Yea sure!", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                saveCredentials(username, password);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No, thanks.", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                intent.putExtra("Username_Key", username);
                                intent.putExtra("Password_Key", password);
                                startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

                // Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                //  checkSaveOption(username,password);
                //  intent.putExtra("Username_Key", username);
                // startActivity(intent);
            } else {
                dialog.dismiss();
                errorRegister();
            }
        }
    }
}