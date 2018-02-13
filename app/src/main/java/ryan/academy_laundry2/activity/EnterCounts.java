package ryan.academy_laundry2.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ryan.academy_laundry2.R;
import ryan.academy_laundry2.app.AppConfig;
import ryan.academy_laundry2.app.AppController;
import ryan.academy_laundry2.helper.CustomOnItemSelectedListener;
import ryan.academy_laundry2.helper.SQLiteHandler;
import ryan.academy_laundry2.helper.SessionManager;

public class EnterCounts extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private EditText inputBigbeds, inputBlankets, inputComforters, inputMattresspads, inputPillows,
            inputRobes, inputShowercurtains, inputShowerliners;
    private Button btnSubmitCount;
    private Spinner spinnerCustomersCounts;
    private SQLiteHandler db;
    private SessionManager session;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_counts);
        session = new SessionManager(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager

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
                    spinnerCustomersCounts.setOnItemSelectedListener(new CustomOnItemSelectedListener());

                    inputBigbeds = (EditText) findViewById(R.id.fbig_bed);
                    inputBlankets = (EditText) findViewById(R.id.fblankets);
                    inputComforters = (EditText) findViewById(R.id.fcomforters);
                    inputMattresspads = (EditText) findViewById(R.id.fmattresspads);
                    inputPillows = (EditText) findViewById(R.id.fpillows);
                    inputRobes = (EditText) findViewById(R.id.frobes);
                    inputShowercurtains = (EditText) findViewById(R.id.fshowercurtains);
                    inputShowerliners = (EditText) findViewById(R.id.fshowerliners);
                    btnSubmitCount = (Button) findViewById(R.id.btn_counts);

                    // button click event
                    btnSubmitCount.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // creating new product in background thread
                            String customer = String.valueOf(spinnerCustomersCounts.getSelectedItem());
                            //assign all values here

                            String bigBeds = inputBigbeds.getText().toString();
                            String blankets = inputBlankets.getText().toString();
                            String comforters = inputComforters.getText().toString();
                            String mattressPads = inputMattresspads.getText().toString();
                            String pillows = inputPillows.getText().toString();
                            String robes = inputRobes.getText().toString();
                            String showerCurtains = inputShowercurtains.getText().toString();
                            String showerLiners = inputShowerliners.getText().toString();
                            //check if any are empty if so then make 0
                            if (!customer.isEmpty()) {
                                //if not assigned a value set to zero
                                if(bigBeds.isEmpty()){bigBeds="0";}
                                if(blankets.isEmpty()){blankets="0";}
                                if(comforters.isEmpty()){comforters="0";}
                                if(mattressPads.isEmpty()){mattressPads="0";}
                                if(pillows.isEmpty()){pillows="0";}
                                if(robes.isEmpty()){robes="0";}
                                if(showerCurtains.isEmpty()){showerCurtains="0";}
                                if(showerLiners.isEmpty()){showerLiners="0";}
                                SubmitCount(customer, bigBeds, blankets, comforters, mattressPads, pillows, robes, showerCurtains, showerLiners);
                            } else {
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

    private void SubmitCount(final String customer, final String bigBeds, final String blankets, final String comforters,
                             final String mattressPads, final String pillows, final String robes,
                             final String showerCurtains, final String showerLiners) {
        /**
         * Function to store weight in MySQL database will post params(customer, weight) to storeweight url
         * */

        // Tag used to cancel the request
        String tag_string_req = "req_enter_counts";

        pDialog.setMessage("Entering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_STORE_COUNTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Enter counts Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // weight successfully stored in MySQL
                        Toast.makeText(getApplicationContext(), "Counts succefully entered!", Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Count entry Error: " + error.getMessage());
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
                params.put("bigbeds", bigBeds);
                params.put("blankets", blankets);
                params.put("comforters", comforters);
                params.put("mattresspads", mattressPads);
                params.put("robes", robes);
                params.put("pillows",pillows);
                params.put("showercurtains", showerCurtains);
                params.put("showerliners", showerLiners);


                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

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
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Map<String, String> params = new HashMap<String, String>();
                Map<String, String> user;//= new HashMap<String, String>();
                user = db.getCreds(session.getEmail());
                params.put("email", user.get("email"));
                params.put("uid", user.get("uid"));
                Log.e("Parameters", params.toString());
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

        spinnerCustomersCounts = (Spinner) findViewById(R.id.spinnerCustomersCounts);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomersCounts.setAdapter(dataAdapter);
    }
}
