
package se.slide.skypewidget;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

//http://android-ui-utils.googlecode.com/hg/asset-studio/dist/icons-launcher.html#foreground.type=text&foreground.space.trim=0&foreground.space.pad=-0.1&foreground.text.text=S&foreground.text.font=Roboto&foreColor=fff%2C0&crop=0&backgroundShape=square&backColor=0097ff%2C100

public class ConfigurationActivity extends Activity {
    
    private static final String PREFS_NAME = "se.slide.skypewidget.SkypeAppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "prefix_";
    
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    
    EditText mEditUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_configuration);
        
        setResult(RESULT_CANCELED);
        
        // Bind views
        mEditUsername = (EditText) findViewById(R.id.editUsername);
        
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.configuration, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        String username = mEditUsername.getText().toString();
        
        if (item.getItemId() == R.id.action_ok && username != null && username.trim().length() > 0) {
            // Set widget result
            final Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            
            saveTitlePref(this, mAppWidgetId, username);

            // Request widget update
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            
            SkypeAppWidgetProvider.updateAppWidget(this, appWidgetManager, mAppWidgetId, username);
            
            // Destroy activity
            finish();
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    public void onStart() {
      super.onStart();
      EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance(this).activityStop(this);
    }
    
    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String prefix = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return context.getString(R.string.username);
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }
}
