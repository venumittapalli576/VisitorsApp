package com.developementapps.visitorsapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Shows a list that can be filtered in-place with a SearchView in non-iconified mode.
 */
public class SearchViewFilterMode extends Activity
        implements SearchView.OnQueryTextListener {
    GMailSender sender;
    String string;
    TextView email;

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    EditText ename, purpose,personname;
    Button button;
    private String TAG = " ";
    private ProgressDialog pDialog;
    private SearchView mSearchView;
    private ListView mListView;
    //https://api.myjson.com/bins/naugi
    //https://api.myjson.com/bins/13ka8a
    private static String url = "https://api.myjson.com/bins/naugi";

    ArrayList<HashMap<String, String>> contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_search_view_filter_mode);
        contactList = new ArrayList<>();
        ename = (EditText) findViewById(R.id.visitorname);
        purpose = (EditText) findViewById(R.id.purpose);
        personname=(EditText)findViewById(R.id.personname);
        button = (Button) findViewById(R.id.button);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.list_view);
        sender = new GMailSender("venumittapalli7@gmail.com", "9505135123");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.

                Builder().permitAll().build();


        StrictMode.setThreadPolicy(policy);
       new  GetContacts().execute();

        button.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub
                if(ename.getText().toString().equals("") || purpose.getText().toString().equals("") || personname.getText().toString().equals("") ){
                    if(ename.getText().toString().equals("")){
                        ename.requestFocus();
                        ename.setError("Required");
                    }else if(purpose.getText().toString().equals("")){
                       purpose.requestFocus();
                       purpose.setError("Required");
                    }else if(personname.getText().toString().equals("")){
                        personname.requestFocus();
                        personname.setError("Required");
                    }
                    Toast.makeText(getApplicationContext(),"Please fill all the fields",Toast.LENGTH_LONG).show();
                }else{
                    try {

                        new MyAsyncClass().execute();


                    } catch (Exception ex) {

                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

                    }
                }



            }

        });


    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchViewFilterMode.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                string = ename.getText().toString();
                String Purpose=purpose.getText().toString();
                // Add subject, Body, your mail Id, and receiver mail Id.
                String body = string + " " + "has come to visit you.\n"+"Reason for comming is" +
                        " "+Purpose;
                String Email=email.getText().toString();
                sender.sendMail("vistitor", body, "venumittapalli7@gmail.coml", Email);
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(getApplicationContext(), "Email send", Toast.LENGTH_LONG).show();
        }
    }
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SearchViewFilterMode.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            // Updating parsed JSON data into ListView

            mListView.setAdapter(new SimpleAdapter(SearchViewFilterMode.this, contactList, R.layout.activity_list_item,
                    new String[]{"name", "email", "mobile"}, new int[]{R.id.name, R.id.email, R.id.mobile}));
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   contactList.get(position);
                   // personname.setText( );
                    TextView name=(TextView) view.findViewById(R.id.name);
                    email=(TextView) view.findViewById(R.id.email);


                     personname.setText(name.getText());





                }
            });
            mListView.setTextFilterEnabled(true);
            setupSearchView();


        }

    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
    }

    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
