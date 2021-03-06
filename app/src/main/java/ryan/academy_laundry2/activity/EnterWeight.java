package ryan.academy_laundry2.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import ryan.academy_laundry2.R;
import ryan.academy_laundry2.app.AppConfig;
import ryan.academy_laundry2.app.AppController;
import ryan.academy_laundry2.helper.CustomOnItemSelectedListener;
import ryan.academy_laundry2.helper.SQLiteHandler;
import ryan.academy_laundry2.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EnterWeight extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private EditText inputWeight;
    private Button btnSubmitWeight;
    private Spinner spinnerCustomers;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_weight);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        getCustomers(new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                //parse result string to list add to spinner then should work?
                try {
                    JSONObject jObj = new JSONObject(result);
                    JSONArray jArray = jObj.getJSONArray("customers");
                    ArrayList<String> customers = new ArrayList<String>();
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            customers.add(jArray.getJSONObject(i).optString("customer"));
                        }
                    }
                    Collections.sort(customers, String.CASE_INSENSITIVE_ORDER);
                    addItemsOnSpinnerCustomers(customers);
                    spinnerCustomers.setOnItemSelectedListener(new CustomOnItemSelectedListener());

                    // Edit Text
                    //inputCustomerName = (EditText) findViewById(R.id.fCompanyName);
                    inputWeight = (EditText) findViewById(R.id.fEnterWeight);

                    // Create button
                    btnSubmitWeight = (Button) findViewById(R.id.btnEnter);

                    // button click event
                    btnSubmitWeight.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // creating new product in background thread
                            String customer = String.valueOf(spinnerCustomers.getSelectedItem());
                            String weight = inputWeight.getText().toString();

                            if (!customer.isEmpty() && !weight.isEmpty()) {
                                SubmitWeight(customer, weight);
                            } else {
                                // Prompt user to enter credentials
                                Toast.makeText(getApplicationContext(),
                                        "Please enter the customer and weight!", Toast.LENGTH_LONG)
                                        .show();
                            }

                        }
                    });
                } catch (JSONException e) {
                    Log.e("Enter Weight", "json error");
                }
            }
        });
    }

    private void SubmitWeight(final String customer, final String weight) {
        /**
         * Function to store weight in MySQL database will post params(customer, weight) to storeweight url
         * */

        // Tag used to cancel the request
        String tag_string_req = "req_enter_weight";

        pDialog.setMessage("Entering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_STORE_WEIGHT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Enter weight Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // weight successfully stored in MySQL
                        Toast.makeText(getApplicationContext(), "Weight succefully entered!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Weight entry Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
               Map<String, String> user;// = new HashMap<String, String>();
                user = db.getCreds(session.getEmail());
                params.put("email", user.get("email"));
                params.put("uid", user.get("uid"));
                params.put("customer", customer);
                params.put("weight", weight);


                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //callback.onSuccess(response);
    public void getCustomers(final VolleyCallback callback) {
        String tag_string_req = "req_get_customers";

        pDialog.setMessage("Retrieving data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_CUSTOMERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Enter weight Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // weight successfully stored in MySQL
                        callback.onSuccess(response);
                        //Toast.makeText(getApplicationContext(), "custs recieved!", Toast.LENGTH_LONG).show();


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Cust retrieval Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })
        {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Map<String, String> params = new HashMap<String, String>();
                Map<String, String> user;// = new HashMap<String, String>();
                user = db.getCreds(session.getEmail());
                params.put("email", user.get("email"));
                Log.e("email is thsi", user.get("email"));
                params.put("uid", user.get("uid"));

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public interface VolleyCallback {
        void onSuccess(String result);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public void addItemsOnSpinnerCustomers(ArrayList<String> list) {

        spinnerCustomers = (Spinner) findViewById(R.id.spinnerCustomers);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomers.setAdapter(dataAdapter);
    }


}


