package com.example.nilarnab.mystats.utility;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.widget.Toast;

import com.example.nilarnab.mystats.App;
import com.example.nilarnab.mystats.DetailsActivity;
import com.example.nilarnab.mystats.R;

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

    public static String getPreferredLocation() {
        return getStringPreference(R.string.pref_pin_code, "712235");
    }

    public static String getPreferredUnit() {
        return getStringPreference(R.string.pref_unit_key, context.getString(R.string.unit_metric));
    }

    public static void showWeatherNotification(CharSequence text, Uri uri, long id) {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);
        notifBuilder.setContentText(text);
        notifBuilder.setSmallIcon(R.mipmap.ic_launcher);

//        PendingIntent resultIntent = PendingIntent.getActivity(context, Constants.WEATHER_NOTIF_REQUEST_CODE,getWeatherDetailsIntent(uri),
//                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) id, notifBuilder.build());
    }

    public static Intent getWeatherDetailsIntent(Uri uri) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.setData(uri);
        intent.setAction(DetailsActivity.ACTION_WEATHER);

        return intent;
    }
}
