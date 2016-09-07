package com.example.filecloud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class LoginActivity extends Activity {
    EditText nameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEditText = (EditText)findViewById(R.id.username);
        passwordEditText = (EditText)findViewById(R.id.password);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }
    public void onLogin(View v){
        String userName = nameEditText.getText().toString();
        String passWord = passwordEditText.getText().toString();
        if (userName.isEmpty() || passWord.isEmpty()){
            Toast.makeText(LoginActivity.this,"Please Entre UserName and Password",Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] user_info = new String[]{userName, passWord,};
        new doLogin().execute(user_info);

    }
    public void checkLogin(String result){
        if (result == null) {
            Toast.makeText(LoginActivity.this,"Server Connection Error",Toast.LENGTH_SHORT).show();
            return;
        }

        String resultNumber="", resultMessage="";
        try{
            JSONObject jsonRootObject = XML.toJSONObject(result);
            JSONObject jsonCommandObject = jsonRootObject.getJSONObject("commands");
            if(jsonCommandObject == null){
                Toast.makeText(LoginActivity.this,"Server Connection Error",Toast.LENGTH_SHORT).show();
                return;
            }
            resultNumber = jsonCommandObject.getJSONObject("command").getString("result");
            resultMessage = jsonCommandObject.getJSONObject("command").getString("message");
        } catch (JSONException e){
            e.printStackTrace();
        }

        if (resultNumber.equals("1")){

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this,resultMessage,Toast.LENGTH_SHORT).show();
        }
    }
    private String addRmcData(){
        String rmcData="";
        UUID deviceUuid = UUID.randomUUID();

        String dispName = "";
        try {
            final String[] SELF_PROJECTION = new String[] {Phone._ID, Phone.DISPLAY_NAME,};
            Cursor cursor = getContentResolver().query(Profile.CONTENT_URI, SELF_PROJECTION, null, null, null);
            cursor.moveToFirst();
            dispName = cursor.getString(1);
            if (dispName == null || dispName.isEmpty()) {
                dispName =  "Android-" +  android.os.Build.BRAND + "-" + android.os.Build.MODEL ;
            }
            else {
                dispName += " - "  + android.os.Build.MODEL;
            }
        }
        catch (Exception e) {
            dispName =  "Android-" +  android.os.Build.BRAND + "-" + android.os.Build.MODEL ;

        }
        String appVersion="";
        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        String osVersion = ""+android.os.Build.VERSION.RELEASE;
        String apiLevel = "4.0";
        rmcData = "&remote_client_id="+deviceUuid.toString()+"&remote_client_api_level="+apiLevel+"&remote_client_disp_name="+dispName+"&remote_client_os_type=Android&remote_client_app_version="+appVersion+"&remote_client_os_version="+osVersion;
        return rmcData;
    }
    private class doLogin extends AsyncTask<String,Void, String> {
        ProgressDialog searching;

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            if(searching == null){
                searching = ProgressDialog.show(LoginActivity.this, "Please Wait...",null,true,true);
            }
        }
        @Override
        protected String doInBackground(String... arg0){

            String uri = Global.SERVER_URL + Global.LOGIN_API;
            String paramString = "userid=" + arg0[0] + "&password=" + arg0[1] + addRmcData();
            try{
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", "" + Integer.toString(paramString.getBytes().length));
                conn.setUseCaches(false);

                DataOutputStream output = new DataOutputStream(conn.getOutputStream());
                output.writeBytes(paramString);
                output.flush();
                output.close();

                InputStream stream = conn.getInputStream();
                InputStreamReader isReader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(isReader);
                String result="";
                String line;
                while ((line = bufferedReader.readLine())!=null){
                    result += line;
                }
                conn.disconnect();
                bufferedReader.close();

                return result;
            } catch (Exception e){
                e.printStackTrace();
            }finally {

            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (searching != null){
                if (searching.isShowing()){
                    searching.dismiss();
                    searching = null;
                }
            }
            Global.userid=nameEditText.getText().toString();
            Log.d("login",result);
            checkLogin(result);
        }
    }
}
