package in.pratanumandal.cardstally;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int ROWS = 50;
    private static final int PLAYERS = 4;

    public int[][] cardData = new int[ROWS][PLAYERS];
    public EditText[][] editData = new EditText[ROWS][PLAYERS];

    public Player[] players = new Player[PLAYERS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        setTheme(R.style.Theme_CardsTally);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        initializePlayers();
        initializeTally();
        initializeReset();

        fillTable(ROWS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                TextView title = new TextView(this);
                SpannableString titleString = new SpannableString("Cards Tally");
                titleString.setSpan(new StyleSpan(Typeface.BOLD), 0, titleString.length(), 0);
                title.setText(titleString);
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
                title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                title.setPadding(10, 50, 10, 5);

                TextView message = new TextView(this);
                SpannableString messageString = new SpannableString(getVersion() + "\n\nPratanu Mandal\npratanumandal.in");
                messageString.setSpan(new StyleSpan(Typeface.BOLD), 0, messageString.toString().indexOf("\n"), 0);
                messageString.setSpan(new RelativeSizeSpan(1.15f), 0, messageString.toString().indexOf("\n"), 0);
                Linkify.addLinks(messageString, Linkify.WEB_URLS);
                message.setText(messageString);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                message.setPadding(10, 5, 10, 80);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(message);
                alertDialogBuilder.setCustomTitle(title);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to exit Cards Tally?");
        alertDialogBuilder.setTitle("Exit");
        alertDialogBuilder.setPositiveButton("Yes", (dialog, id) -> super.onBackPressed());
        alertDialogBuilder.setNegativeButton("No", (dialog, id) -> {});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void initializePlayers() {
        for (int i = 0; i < PLAYERS; i++) {
            Player player = players[i] = new Player("Player " + (i + 1), 0);
            EditText name = getNameField(i);

            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        player.name = player.defaultName;
                    }
                    else if (s.toString().trim().isEmpty()) {
                        name.setText("");
                        player.name = player.defaultName;
                    }
                    else {
                        player.name = s.toString();
                    }
                }
            });
        }
    }

    private void fillTable(int rows) {
        TableLayout tableLayout = findViewById(R.id.scoreTable);

        tableLayout.removeAllViews();

        for (int i = 0; i < rows; i++) {
            TableRow row = new TableRow(this);

            TextView rowNo = new TextView(this);
            rowNo.setText(String.valueOf(i + 1));
            rowNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            rowNo.setPadding(10, 0, 10, 0);
            rowNo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(rowNo);

            if (i == rows - 1) {
                Space headerSpace = findViewById(R.id.headerSpace);
                rowNo.measure(0, 0);
                headerSpace.setMinimumWidth(rowNo.getMeasuredWidth());
            }

            for (int j = 0; j < PLAYERS; j++) {
                EditText edit = editData[i][j] = new EditText(this);
                edit.setInputType(InputType.TYPE_CLASS_NUMBER);
                edit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                edit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                edit.setHint("0");
                row.addView(edit);

                if (i == rows - 1) {
                    TextView score = getScoreField(j);
                    score.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {}

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            score.measure(0, 0);
                            edit.setMinimumWidth(score.getMeasuredWidth());
                        }
                    });
                }

                cardData[i][j] = 0;

                Player player = players[j];
                TextView score = getScoreField(j);

                int finalI = i;
                int finalJ = j;
                edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().length() > 2) {
                            Toast.makeText(getApplicationContext(), "Value too large", Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return;
                        }

                        int value = 0;
                        if (s.length() != 0) value = Integer.valueOf(s.toString());

                        int total = Integer.valueOf(score.getText().toString()) - cardData[finalI][finalJ] + value;
                        score.setText(String.valueOf(total));

                        player.cards = total;

                        cardData[finalI][finalJ] = value;
                    }
                });
            }

            tableLayout.addView(row);
        }
    }

    public void initializeReset() {
        Button button = findViewById(R.id.resetButton);
        button.setOnClickListener((v) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Do you want to reset the table?");
            alertDialogBuilder.setTitle("Reset");
            alertDialogBuilder.setPositiveButton("Yes", (dialog, id) -> {
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < PLAYERS; j++) {
                        editData[i][j].setText("");
                    }
                }

                for (int i = 0; i < PLAYERS; i++) {
                    players[i].cards = 0;
                }
            });
            alertDialogBuilder.setNegativeButton("No", (dialog, id) -> {});
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    public void initializeTally() {
        Button button = findViewById(R.id.tallyButton);
        button.setOnClickListener((v) -> tally(players));
    }

    public void tally(Player... players) {
        sortPlayers(players);

        int[][] scores = new int[PLAYERS][PLAYERS];

        for (int i = 0; i < PLAYERS - 1; i++) {
            int cardI = players[i].cards;
            for (int j = i + 1; j < PLAYERS; j++) {
                int cardJ = players[j].cards;
                scores[i][j] = cardI - cardJ;
            }
        }

        for (int k = 0; k < PLAYERS - 1; k++) {
            for (int i = k + 1; i < PLAYERS - 1; i++) {
                for (int j = i + 1; j < PLAYERS && scores[k][i] > 0; j++) {
                    if (scores[k][i] > scores[i][j]) {
                        scores[k][i] -= scores[i][j];
                        scores[k][j] += scores[i][j];
                        scores[i][j] = 0;
                    }
                    else {
                        scores[i][j] -= scores[k][i];
                        scores[k][j] += scores[k][i];
                        scores[k][i] = 0;
                    }
                }
            }
        }

        showTally(scores, players);
    }

    public void sortPlayers(Player... players) {
        Arrays.sort(players, Collections.reverseOrder());
    }

    public void showTally(int[][] scores, Player... players) {
        String tally = "";

        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores.length; j++) {
                if (scores[i][j] != 0) {
                    tally += players[i].name + "   >   " + players[j].name + ":   " + scores[i][j] + "  cards\n";
                }
            }
        }

        if (tally.isEmpty())
            tally = "Nothing to tally";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(tally);
        alertDialogBuilder.setTitle("Tally");
        alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> {});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public EditText getNameField(int playerNo) {
        EditText name = null;

        switch (playerNo) {
            case 0:
                name = findViewById(R.id.player1Name);
                break;

            case 1:
                name = findViewById(R.id.player2Name);
                break;

            case 2:
                name = findViewById(R.id.player3Name);
                break;

            case 3:
                name = findViewById(R.id.player4Name);
                break;
        }

        return name;
    }

    public TextView getScoreField(int playerNo) {
        TextView score = null;

        switch (playerNo) {
            case 0:
                score = findViewById(R.id.player1Cards);
                break;

            case 1:
                score = findViewById(R.id.player2Cards);
                break;

            case 2:
                score = findViewById(R.id.player3Cards);
                break;

            case 3:
                score = findViewById(R.id.player4Cards);
                break;
        }

        return score;
    }

    public String getVersion() {
        String version = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

}
