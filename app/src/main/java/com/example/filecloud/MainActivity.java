package com.example.filecloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private SearchListAdapter mAdapter;
    private ArrayList<FileInfoClass> mSearchFileList = new ArrayList<FileInfoClass>();
    private ListView mListView;
    private Button logoBtn;
    private Button searchBtn;
    private SearchView searchView;
    private TextView noResultText;
    private boolean isSearchState;
    private SearchFiles searchFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoBtn = (Button)findViewById(R.id.btn_logo);
        searchBtn = (Button)findViewById(R.id.btn_search);
        searchView = (SearchView)findViewById(R.id.text_searchview);
        noResultText = (TextView)findViewById(R.id.text_no_result);
        mSearchFileList = new ArrayList<FileInfoClass>();
        mAdapter = new SearchListAdapter(this,mSearchFileList);
        isSearchState = false;
        mListView = (ListView)findViewById(R.id.list_search_files);
        searchView = (SearchView)findViewById(R.id.text_searchview);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener(){
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

               return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                logoBtn.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                searchBtn.setBackgroundResource(R.drawable.icon_search);
                isSearchState = !isSearchState;
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {

                mSearchFileList.clear();
                String[] params = new String[]{Global.SERVER_URL+"search/", "keyword=" + query + "&location=/" + Global.userid,}; // api, params
              //  String[] params = new String[]{Global.SERVER_URL2+"searchext/", "op=text&searchname=text&searchtext=text&searchloc=/andrei",};
                if (searchFiles == null)
                    searchFiles = new SearchFiles();
                searchFiles.execute(params);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public void goSearch(View v){
        if (isSearchState){
            logoBtn.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            clearSearchResult();
            searchBtn.setBackgroundResource(R.drawable.icon_search);
        } else {
            logoBtn.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
            searchBtn.setBackgroundResource(R.drawable.icon_refresh);
        }
        mListView.setVisibility(View.VISIBLE);
        noResultText.setVisibility(View.GONE);
        isSearchState = !isSearchState;
    }

    private void clearSearchResult(){
        mSearchFileList.clear();
        mAdapter.setListItems(mSearchFileList);
        mListView.setAdapter(mAdapter);
    }
    private void showSearchResult(String result){
        searchFiles = null;
        clearSearchResult();
        if (result.isEmpty())
        {
            Toast.makeText(MainActivity.this,"Server connection error",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonRootObject = null;
        JSONObject jsonEntryObject = null;

        try{
            jsonRootObject = XML.toJSONObject(result);
            jsonEntryObject = jsonRootObject.getJSONObject("entries");
            if (jsonEntryObject == null)
            {
                Toast.makeText(MainActivity.this,"Server connection error",Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject jsonMetaObject = jsonEntryObject.getJSONObject("meta");
            if (jsonMetaObject.getInt("totalmatch") == 0)
            {
                noResultText.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                return;
            }
            mListView.setVisibility(View.VISIBLE);
            noResultText.setVisibility(View.GONE);
        } catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("RootJson",jsonRootObject.toString());

        JSONArray jsonArray = jsonEntryObject.optJSONArray("entry");
        if (jsonArray == null) return;
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mSearchFileList.add(new FileInfoClass(jsonObject.getString("path"),jsonObject.getString("dirpath"),jsonObject.getString("name"),jsonObject.getString("ext"),jsonObject.getInt("isshareable"),jsonObject.getInt("canrename"),jsonObject.getInt("showprev"),jsonObject.getInt("canfavorite"),jsonObject.getString("fullfilename"),jsonObject.getString("size"),jsonObject.getInt("fullsize"),jsonObject.getString("type"),jsonObject.getInt("favoritelistid"),jsonObject.getInt("favoriteid"),jsonObject.getInt("order"),jsonObject.getString("modifiedepoch")));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        mAdapter.setListItems(mSearchFileList);
        mListView.setAdapter(mAdapter);

    }


    private class SearchFiles extends AsyncTask<String,Void, String> {
        ProgressDialog searchingDial;

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            if(searchingDial == null){
                searchingDial = ProgressDialog.show(MainActivity.this, "Please Wait...",null,true,true);
            }
        }
        @Override
        protected String doInBackground(String... arg0){

            String uri =arg0[0];
            String paramString = arg0[1];
           try{
               URL url = new URL(uri);
               HttpURLConnection conn = (HttpURLConnection)url.openConnection();
               conn.setDoOutput(true);
               conn.setDoInput(true);
               conn.setRequestMethod("GET");
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
            if (searchingDial != null){
                if (searchingDial.isShowing()){
                    searchingDial.dismiss();
                    searchingDial = null;
                }
            }
          Log.d("result",result);
          showSearchResult(result);
        }
    }
}
