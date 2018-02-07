package ryan.academy_laundry2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.TypedArrayUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterWeight extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private EditText inputCustomerName, inputWeight;
    private Button btnSubmitWeight;
   // private Spinner spinnerCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_weight);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());


       // List<String> customers;
       // addItemsOnSpinnerCustomers(customers);
       // spinnerCustomers.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        // Edit Text
        inputCustomerName = (EditText) findViewById(R.id.fCompanyName);
        inputWeight = (EditText) findViewById(R.id.fEnterWeight);

        // Create button
        btnSubmitWeight = (Button) findViewById(R.id.btnEnter);

        // button click event
        btnSubmitWeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                String customer = inputCustomerName.getText().toString();
                //String customer = String.valueOf(spinnerCustomers.getSelectedItem());
                String weight = inputWeight.getText().toString();


                if (!customer.isEmpty() && !weight.isEmpty()) {
                    SubmitWeight( customer, weight);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the customer and weight!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(
                EnterWeight.this,
                HomeScreen.class);
        startActivity(intent);
        finish();
    }
    private void SubmitWeight(final String customer, final String weight)  {
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

                            Intent intent = new Intent(
                                    EnterWeight.this,
                                    HomeScreen.class);
                            startActivity(intent);
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
                    params.put("customer", customer);
                    params.put("weight", weight);


                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


  /*  public void addItemsOnSpinnerCustomers(List<String> list) {

        spinnerCustomers = (Spinner) findViewById(R.id.spinnerCustomers);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomers.setAdapter(dataAdapter);
    }
*/

}


