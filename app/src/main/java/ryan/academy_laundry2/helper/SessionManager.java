package ryan.academy_laundry2.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	private SharedPreferences pref;

	private Editor editor;
	private Context _context;

	// Shared pref mode
	private int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "Academy";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

	private static final String KEY_EMAIL = "uid";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
		editor.commit();
	}

	public void setLogin(boolean isLoggedIn,String email) {

		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
		editor.putString(KEY_EMAIL, email);
		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}

	public String getEmail(){return pref.getString(KEY_EMAIL,"no");}
	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}
}
