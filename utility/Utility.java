package com.example.nilarnab.mystats.utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.example.nilarnab.mystats.App;
import com.example.nilarnab.mystats.Constants;
import com.example.nilarnab.mystats.DetailsActivity;
import com.example.nilarnab.mystats.MainActivity;
import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.events.LocationFetchedEvent;
import com.example.nilarnab.mystats.services.LocationListenerService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

/**
 * Created by nilarnab on 6/8/16.
 */
public class Utility {
    private static final String PREFIX_MAIL = "mailto:";
    private static final String PREFIX_SMS = "smsto:";
    private static final String PREFIX_SMS_BODY = "sms_body";

    private static Context context = App.getAppContext();
    private static SharedPreferences preferences = App.getAppPreference();

    public static String getStringPreference(String key, String defaultVal) {
        return preferences.getString(key, defaultVal);
    }

    public static String getStringPreference(int key, String defaultVal) {
        return getStringPreference(context.getResources().getString(key), defaultVal);
    }

    public static void storeStringPreference(String key, String val) {
        preferences.edit().putString(key, val).apply();
    }

    public static float getFloatPreference(String key, float defaultVal) {
        return preferences.getFloat(key, defaultVal);
    }

    public static void storeFloatPreference(String key, float val) {
        preferences.edit().putFloat(key, val).apply();
    }

    public static void storeFloatPreference(int keyRes, float val) {
        storeFloatPreference(context.getString(keyRes), val);
    }

    public static float getFloatPreference(int keyRes, float val) {
        return getFloatPreference(context.getString(keyRes), val);
    }

    /**
     * Gets the tinted drawable in backwards compatible way
     *
     * @param drawableResource the drawable
     * @param tintColor        Color ID for the tint
     * @return Tinted drawable
     */
    public static Drawable getTintedDrawableCompat(Drawable drawableResource, int tintColor) {
        Resources r = App.getAppContext().getResources();
        Drawable drawable = DrawableCompat.wrap(drawableResource);
        DrawableCompat.setTint(drawable, r.getColor(tintColor));
        return drawable;
    }

    /**
     * Used to get a share intent with the passed-in string
     *
     * @param shareText Text to be shared
     * @return An intent to share the share string.
     */
    public static Intent buildShareIntent(String shareText) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    /**
     * Opens the email intent
     *
     * @param context Context
     * @param to      To address
     */
    public static void sendEmail(Context context, String to) {
        Intent emailIntent = getEmailIntentInternal(to, "", "");

        context.startActivity(Intent.createChooser(emailIntent, "Email"));
    }

    /**
     * Sends the email intent
     *
     * @param to      To address
     * @param subject Subject
     * @param text    Body
     */
    public static void sendEmail(Context context, String to, String subject, String text) {
        Intent emailIntent = getEmailIntentInternal(to, subject, text);

        context.startActivity(Intent.createChooser(emailIntent, "Email"));
    }

    private static Intent getEmailIntentInternal(String to, String subject, String text) {
        Intent emailIntent = new Intent();
        emailIntent.setType("text/html");
        emailIntent.setAction(Intent.ACTION_SENDTO);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(text));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        Uri uri = Uri.parse(PREFIX_MAIL);
        emailIntent.setData(uri);

        return emailIntent;
    }

    /**
     * Opens a share intent for whatsapp
     *
     * @param context Context
     * @param text    Text to share
     */
    public static void shareViaWhatsApp(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        //User may not have whatsApp installed
        try {
            context.startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "WhatsApp not found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Returns a SMS intent
     *
     * @param context Context
     * @param text    the sms body
     * @param to      the number to whom it may concern
     */
    public static Intent sendSmsIntent(Context context, String text, String to) {
        Uri uri = Uri.parse(PREFIX_SMS + to);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        if (text != null) {
            intent.putExtra(PREFIX_SMS_BODY, text);
        }

        context.startActivity(Intent.createChooser(intent, "Text"));
        return intent;
    }

    /**
     * Opens a call phone number intent
     *
     * @param context Context
     * @param phone   Phone
     */
    public static void sendCallIntent(Context context, String phone) {
        String uri = "tel:" + phone;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));

        context.startActivity(intent);
    }

    public static String getUserLocation() {
        return getStringPreference(R.string.pref_pin_code, null);
    }

    public static String getPreferredUnit() {
        return getStringPreference(R.string.pref_unit_key, context.getString(R.string.unit_metric));
    }

    public static void showWeatherNotification(CharSequence text, Bitmap largeIcon, Uri uri, CharSequence title) {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);
        notifBuilder.setContentTitle(title);
        notifBuilder.setContentText(text);
        notifBuilder.setSmallIcon(R.drawable.ic_stat_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notifBuilder.setLargeIcon(largeIcon);
        }

        Intent mainActivity = new Intent(context,MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent detailsActivity = Utility.getWeatherDetailsIntent(uri);

        PendingIntent notifPendingIntent = PendingIntent.getActivities(
                context,
                Constants.WEATHER_NOTIF_REQUEST_CODE,
                new Intent[]{mainActivity,detailsActivity},
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        notifBuilder.setContentIntent(
               notifPendingIntent
        );

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.APP_NOTIFICATION_ID);
        manager.notify(Constants.APP_NOTIFICATION_ID, notifBuilder.build());
    }

    public static Intent getWeatherDetailsIntent(Uri uri) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.setData(uri);
        intent.setAction(DetailsActivity.ACTION_WEATHER);

        return intent;
    }

    public static void storeUserLocation(Location location) {
        if(location != null) {
            float lat = (float) location.getLatitude();
            float lng = (float) location.getLongitude();

            storeUserLatitude(lat);
            storeUserLongitude(lng);
            storeUserPin();
        }
    }

    public static void stopLocationTracking() {
        context.stopService(new Intent(context, LocationListenerService.class));
    }

    private static void storeUserPin() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                boolean hasChanged = false;
                Geocoder geocoder = new Geocoder(context);
                float lat = getFloatPreference(R.string.pref_user_latitude, Constants.DEFAULT_LAT);
                float lng = getFloatPreference(R.string.pref_user_longitude, Constants.DEFAULT_LNG);

                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    if(addresses.size() > 0) {
                        String pin = addresses.get(0).getPostalCode();
                        if(pin != null) {
                            if(!pin.equals(getUserLocation())) {
                                Log.d(App.TAG,pin);
                                storeStringPreference(context.getString(R.string.pref_pin_code), pin);
                                hasChanged = true;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    hasChanged = false;
                }
                EventBus.getDefault().postSticky(new LocationFetchedEvent(hasChanged));
                return null;
            }
        }.execute();
    }

    private static void storeUserLongitude(float lng) {
        storeFloatPreference(R.string.pref_user_longitude, lng);
    }

    private static void storeUserLatitude(float lat) {
        storeFloatPreference(R.string.pref_user_latitude, lat);
    }
}
