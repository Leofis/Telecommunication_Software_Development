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
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.leofis.network.server.WebServiceAction;

public class LoginActivity extends Activity {

    private Button loginButton, registerButton;
    private EditText usernameText;
    private EditText passwordText;
    private CheckBox rememberMe;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        usernameText = (EditText) findViewById(R.id.username_textfield);
        passwordText = (EditText) findViewById(R.id.password_textfield);
        rememberMe = (CheckBox) findViewById(R.id.loginCheckBox);

        loginPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loginEditor = loginPreferences.edit();

//        MyListener x = new MyListener(getApplicationContext());
//        registerButton.setOnClickListenerx);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                //finish();
            }
        });
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
            setURL();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    class MyListener implements View.OnClickListener {
//        Activity activity;
//
//        MyListener(Activity activity) {
//            this.activity = activity;
//        }
//
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(activity, RegisterActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    private void checkSaveOption(String username, String password,int status) {
        if (rememberMe.isChecked()) saveCredentials(username, password,status);
        else {
            loginEditor.remove("Username_Key");
            loginEditor.remove("Password_Key");
            loginEditor.remove("isLoggedIn");
            loginEditor.remove("User_Type");
            loginEditor.commit();
        }
    }

    private void saveCredentials(String username, String password,int status) {
        loginEditor.putString("Username_Key", username);
        loginEditor.putString("Password_Key", password);
        loginEditor.putString("isLoggedIn", "yes");
        loginEditor.putInt("User_Type",status);
        loginEditor.commit();
    }

    public void loginUser(View view) {

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);

        if (!isOnline()) {
            Toast toast = Toast.makeText(getApplicationContext(), "No Network connection", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.NO_GRAVITY, 0, 150);
            toast.show();
        } else {
            if (!username.isEmpty() && !password.isEmpty()) {
                usernameText.setText("");
                passwordText.setText("");
                usernameText.setHintTextColor(Color.GRAY);
                passwordText.setHintTextColor(Color.GRAY);
                usernameText.setHint("Username");
                passwordText.setHint("Password");
                Log.i("Username", username);
                Log.i("Password", password);
                AsyncTaskLogin taskLogin = new AsyncTaskLogin(username, password);
                taskLogin.execute();
            } else if (username.isEmpty() && !password.isEmpty()) {
                //usernameText.setHintTextColor(Color.RED);
                // usernameText.setHint("Username is Empty");
                usernameText.setError("Username is Empty");
            } else if (!username.isEmpty() && password.isEmpty()) {
                //passwordText.setHintTextColor(Color.RED);
                // passwordText.setHint("Password is Empty");
                passwordText.setError("Password is Empty");
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.empty_username_password, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 150);
                usernameText.setHintTextColor(Color.GRAY);
                passwordText.setHintTextColor(Color.GRAY);
                usernameText.setHint("Username");
                passwordText.setHint("Password");
                toast.show();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void errorLogin() {
        new AlertDialog.Builder(this)
                .setTitle("Wrong Credentials")
                .setMessage("Do you want to continue with a fresh Registration ?")
                .setPositiveButton("Yes, sure", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);
                    }
                })
                /*android.R.string.no*/
                .setNegativeButton("No, thanks.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setURL()
    {
        final EditText txtUrl = new EditText(this);
        txtUrl.setInputType(InputType.TYPE_CLASS_PHONE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String string = preferences.getString("URL", "Empty");
        if(!string.equals("Empty"))
        {
            String[] part = string.split("/");
            string = part[2].split(":")[0];
        }

        new AlertDialog.Builder(this)
                .setTitle("Web Services Initialization")
                .setMessage("Please type the desired IP for the normal operation of the Web methods. ("+string+")")
                .setView(txtUrl)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = txtUrl.getText().toString();
                        if(url.isEmpty()) return;
                        loginEditor.putString("URL", "http://" + url + ":9999/LeofisService/LeofisService?WSDL");
                        loginEditor.commit();
                    }
                })
                .show();
    }

    protected class AsyncTaskLogin extends AsyncTask<String, Integer, Integer> {

        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        private String username;
        private String password;

        public AsyncTaskLogin(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            publishProgress();
            WebServiceAction webservice = new WebServiceAction(getApplicationContext());
            int result = webservice.login(username, password);
            System.out.println(result);
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setMessage("Please wait while logging...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Integer login_successful) {
            super.onPostExecute(login_successful);

            if (login_successful == 1 || login_successful == 2) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                checkSaveOption(username, password,login_successful);
                intent.putExtra("Username_Key", username);
                intent.putExtra("Password_Key", password);
                intent.putExtra("User_Type", login_successful);
                startActivity(intent);
            } else {
                dialog.dismiss();
                errorLogin();
            }
        }
    }
}
