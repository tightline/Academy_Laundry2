package ryan.academy_laundry2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import ryan.academy_laundry2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import ryan.academy_laundry2.app.AppConfig;
import ryan.academy_laundry2.app.AppController;
import ryan.academy_laundry2.helper.SessionManager;

public class ViewPreviousWeights extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_previous_weights);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);


        String tag_string_req = "req_retrieve_weight";

        pDialog.setMessage("Retrieving ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_WEIGHT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Retrieve weight Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONArray jarray;
                        try {
                            jarray = jObj.getJSONArray("weights");
                            for(int i =0;i<50;i++) {
                                TextView textView1 = new TextView(ViewPreviousWeights.this);
                                textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                textView1.setText(i+": "+jarray.getJSONObject(i).optString("customer")+":  "
                                        +jarray.getJSONObject(i).optString("weight"));
                                //textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
                                textView1.setPadding(50, 20, 20, 20);// in pixels (left, top, right, bottom)
                                linearLayout.addView(textView1);
                            }
                        }catch(JSONException e){

                        }

                       // Intent intent = new Intent(
                        //        ViewPreviousWeights.this,
                          //      HomeScreen.class);
                        //startActivity(intent);
                        //finish();
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
                Log.e(TAG, "Previoue Weights Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
//put code here for username pass entry to send
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

}
