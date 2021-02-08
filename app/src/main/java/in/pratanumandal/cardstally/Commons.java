package in.pratanumandal.cardstally;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class Commons {

    public static void showInfo(Context context) {
        TextView title = new TextView(context);
        SpannableString titleString = new SpannableString("Cards Tally");
        titleString.setSpan(new StyleSpan(Typeface.BOLD), 0, titleString.length(), 0);
        title.setText(titleString);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setPadding(10, 50, 10, 5);

        TextView message = new TextView(context);
        SpannableString messageString = new SpannableString(getVersion(context) + "\n\nPratanu Mandal\npratanumandal.in");
        messageString.setSpan(new StyleSpan(Typeface.BOLD), 0, messageString.toString().indexOf("\n"), 0);
        messageString.setSpan(new RelativeSizeSpan(1.15f), 0, messageString.toString().indexOf("\n"), 0);
        Linkify.addLinks(messageString, Linkify.WEB_URLS);
        message.setText(messageString);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        message.setPadding(10, 5, 10, 80);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(message);
        alertDialogBuilder.setCustomTitle(title);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static String getVersion(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

}
