
package se.slide.skypewidget;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.google.analytics.tracking.android.Log;

import java.util.List;

public class SkypeLauncherIntentService extends IntentService {

    public static final String USERNAME = "username";

    private static final String INTENT_NAME = "SkypeLauncherIntentService";

    public SkypeLauncherIntentService() {
        super(INTENT_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make sure the Skype for Android client is installed
        if (!isSkypeClientInstalled(this)) {
            goToMarket(this);
            return;
        }

        // If Skype is not running, we need to open if first before we send any
        // call intents. I think this is a bug in Skype.
        if (!isSkypeAppRunning(this)) {
            Log.e("Skype was not running, trying to start it");

            String startSkypeUri = "skype:";
            Intent startSkypeIntent = createSkypeUri(this, startSkypeUri);
            startActivity(startSkypeIntent);

            // Alright, this is ugly but we need to sleep for some time to allow
            // the Skype app to open up
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String username = intent.getStringExtra(USERNAME);

        String mySkypeUri = "skype:" + username + "?call&video=true";
        Intent myIntent = createSkypeUri(this, mySkypeUri);
        startActivity(myIntent);
    }

    /**
     * http://stackoverflow.com/questions/4212992/how-can-i-check-if-an-app-
     * running-in-android
     * 
     * @param context
     * @return
     */
    public static boolean isSkypeAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals("com.skype.raider")) {
                return true;
            }
        }

        return false;
    }

    // http://developer.skype.com/skype-uris/skype-uri-tutorial-android#uriTutorialAndroidIsClientInstalled

    /**
     * Initiate the actions encoded in the specified URI.
     */
    public static Intent createSkypeUri(Context myContext, String mySkypeUri) {

        // Make sure the Skype for Android client is installed
        if (!isSkypeClientInstalled(myContext)) {
            goToMarket(myContext);
            return null;
        }

        // Create the Intent from our Skype URI
        Uri skypeUri = Uri.parse(mySkypeUri);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);

        // Restrict the Intent to being handled by the Skype for Android client
        // only
        myIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Initiate the Intent. It should never fail since we've already
        // established the
        // presence of its handler (although there is an extremely minute window
        // where that
        // handler can go away...)

        // Let's not do that for this app, instead return the intent
        // myContext.startActivity(myIntent);

        return myIntent;
    }

    /**
     * Determine whether the Skype for Android client is installed on this
     * device.
     */
    public static boolean isSkypeClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo("com.skype.raider", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }

    /**
     * Install the Skype client through the market: URI scheme.
     */
    public static void goToMarket(Context myContext) {
        Uri marketUri = Uri.parse("market://details?id=com.skype.raider");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);

        return;
    }
}
