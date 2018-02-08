package ryan.academy_laundry2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

import ryan.academy_laundry2.R;
import ryan.academy_laundry2.helper.SQLiteHandler;
import ryan.academy_laundry2.helper.SessionManager;


public class HomeScreen extends Activity {
    //private static final int ENTER_WEIGHT_RETURN=0;
    Button btnEnterWeight;
    Button btnViewRecent;
    Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        btnEnterWeight = (Button) findViewById(R.id.btnWeightHome);
        btnViewRecent = (Button) findViewById(R.id.btnRecent);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }


        btnEnterWeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View V) {
                Intent intent = new Intent(HomeScreen.this, EnterWeight.class);
                startActivity(intent);
                finish();
            }


        });

        btnViewRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent intent = new Intent(HomeScreen.this, ViewPreviousWeights.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(HomeScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
