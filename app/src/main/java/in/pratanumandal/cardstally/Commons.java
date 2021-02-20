package in.pratanumandal.cardstally;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
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

    public static void showSettings(Context context, TallyActivity.Callback callback) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        LinearLayout checkBoxWrapper = new LinearLayout(context);
        checkBoxWrapper.setOrientation(LinearLayout.VERTICAL);
        checkBoxWrapper.setPadding(40, 0, 40, 0);

        Space space1 = new Space(context);
        space1.setMinimumHeight(40);
        checkBoxWrapper.addView(space1);

        CheckBox checkBox1 = new CheckBox(context);
        checkBox1.setChecked(Persistence.getReduceTally(context));
        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Persistence.setReduceTally(context, isChecked);
        });
        checkBox1.setText("Reduce payment tally");
        checkBoxWrapper.addView(checkBox1);

        Space space = new Space(context);
        space.setMinimumHeight(15);
        checkBoxWrapper.addView(space);

        LinearLayout editWrapper = new LinearLayout(context);
        editWrapper.setOrientation(LinearLayout.HORIZONTAL);
        editWrapper.setPadding(15, 0, 15, 0);
        editWrapper.setVisibility(Persistence.getMinimumThreshold(context) ? View.VISIBLE : View.GONE);

        TextView label1 = new TextView(context);
        label1.setText("Threshold");
        editWrapper.addView(label1);

        EditText edit = new EditText(context);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        edit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        edit.setHint("0");
        editWrapper.addView(edit);

        TextView label2 = new TextView(context);
        label2.setText("cards");
        editWrapper.addView(label2);

        String value = Persistence.getMinimumThresholdValue(context);
        if (value != null) edit.setText(value);

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = edit.getText().toString();
                Persistence.setMinimumThresholdValue(context, text.isEmpty() ? null : text);
            }
        });

        CheckBox checkBox2 = new CheckBox(context);
        checkBox2.setChecked(Persistence.getMinimumThreshold(context));
        checkBox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editWrapper.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            Persistence.setMinimumThreshold(context, isChecked);
        });
        checkBox2.setText("Minimum payment threshold");
        checkBoxWrapper.addView(checkBox2);

        checkBoxWrapper.addView(editWrapper);

        alertDialogBuilder.setView(checkBoxWrapper);
        alertDialogBuilder.setTitle("Settings");
        alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> {});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener((dialog) -> {
            if (callback != null) callback.reinitialize();
        });
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
