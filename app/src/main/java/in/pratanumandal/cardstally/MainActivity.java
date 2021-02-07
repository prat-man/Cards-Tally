package in.pratanumandal.cardstally;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public GameInfo gameInfo;
    public List<EditText[]> editData = new ArrayList<>();
    public List<TextView> rowNoData = new ArrayList<>();
    public int rowCount = 0;
    public boolean persistenceOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        setTheme(R.style.Theme_CardsTally);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        gameInfo = Persistence.loadGame(getApplicationContext());

        initializePlayers();
        initializeTally();
        initializeReset();
        initializeAdd();

        fillTable();
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
        for (int i = 0; i < Constants.PLAYERS; i++) {
            EditText name = getNameField(i);
            TextView score = getScoreField(i);

            if (gameInfo.players[i] == null) {
                gameInfo.players[i] = new Player("Player " + (i + 1), 0);
            }
            else {
                name.setText(gameInfo.players[i].name);
                score.setText(String.valueOf(gameInfo.players[i].cards));
            }

            Player player = gameInfo.players[i];

            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        player.name = "";
                    }
                    else if (s.toString().trim().isEmpty()) {
                        name.setText("");
                        player.name = "";
                    }
                    else {
                        player.name = s.toString();
                    }

                    Persistence.saveGame(getApplicationContext(), gameInfo);
                }
            });
        }
    }

    private void fillTable() {
        int maxRows = Math.max(Constants.DEFAULT_ROWS, gameInfo.rowCount);
        for (int i = 0; i < maxRows; i++) {
            addRow();
        }
        adjustHeaderSpace();
    }

    private void addRow() {
        TableLayout tableLayout = findViewById(R.id.scoreTable);

        int i = this.rowCount;

        if (gameInfo.cardData.size() == i) {
            gameInfo.cardData.add(new Integer[Constants.PLAYERS]);
            for (int j = 0; j < Constants.PLAYERS; j++) {
                gameInfo.cardData.get(i)[j] = 0;
            }
        }

        if (editData.size() == i) {
            editData.add(new EditText[Constants.PLAYERS]);
            rowNoData.add(null);
        }

        TableRow row = new TableRow(this);

        TextView rowNo = new TextView(this);
        rowNo.setText(String.valueOf(i + 1));
        rowNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        rowNo.setPadding(10, 0, 10, 0);
        rowNo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row.addView(rowNo);

        rowNoData.set(i, rowNo);

        for (int j = 0; j < Constants.PLAYERS; j++) {
            EditText edit = editData.get(i)[j] = new EditText(this);
            edit.setInputType(InputType.TYPE_CLASS_NUMBER);
            edit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            edit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            edit.setHint("0");
            row.addView(edit);

            if (gameInfo.cardData.get(i)[j] != 0) {
                edit.setText(String.valueOf(gameInfo.cardData.get(i)[j]));
            }

            if (i == 0) {
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

            Player player = gameInfo.players[j];
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

                    int total = player.cards - gameInfo.cardData.get(finalI)[finalJ] + value;
                    score.setText(String.valueOf(total));

                    player.cards = total;

                    gameInfo.cardData.get(finalI)[finalJ] = value;

                    if (!persistenceOff) {
                        Persistence.saveGame(getApplicationContext(), gameInfo);
                    }
                }
            });
        }

        tableLayout.addView(row);

        this.rowCount++;
    }

    public void initializeReset() {
        Button button = findViewById(R.id.resetButton);
        button.setOnClickListener((v) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            LinearLayout checkBoxWrapper = new LinearLayout(this);
            checkBoxWrapper.setOrientation(LinearLayout.VERTICAL);
            checkBoxWrapper.setPadding(40, 0, 40, 0);

            CheckBox checkBox1 = new CheckBox(this);
            checkBox1.setChecked(Persistence.getDeleteNames(getApplicationContext()));
            checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Persistence.setDeleteNames(getApplicationContext(), isChecked);
            });
            checkBox1.setText("Delete player names");
            checkBoxWrapper.addView(checkBox1);

            CheckBox checkBox2 = new CheckBox(this);
            checkBox2.setChecked(Persistence.getResetRowCount(getApplicationContext()));
            checkBox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Persistence.setResetRowCount(getApplicationContext(), isChecked);
            });
            checkBox2.setText("Reset row count");
            checkBoxWrapper.addView(checkBox2);

            alertDialogBuilder.setMessage("Do you want to reset the table?");
            alertDialogBuilder.setView(checkBoxWrapper);
            alertDialogBuilder.setTitle("Reset");
            alertDialogBuilder.setPositiveButton("Yes", (dialog, id) -> {
                if (Persistence.getDeleteNames(getApplicationContext())) {
                    for (int i = 0; i < Constants.PLAYERS; i++) {
                        EditText name = getNameField(i);
                        name.setText("");
                    }
                }

                persistenceOff = true;

                for (int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < Constants.PLAYERS; j++) {
                        editData.get(i)[j].setText("");
                    }
                }

                persistenceOff = false;

                if (Persistence.getResetRowCount(getApplicationContext())) {
                    TableLayout tableLayout = findViewById(R.id.scoreTable);
                    for (int i = rowCount - 1; i >= Constants.DEFAULT_ROWS; i--) {
                        gameInfo.cardData.remove(i);

                        tableLayout.removeView((TableRow) rowNoData.get(i).getParent());
                        rowNoData.remove(i);
                        editData.remove(i);
                    }

                    rowCount = Constants.DEFAULT_ROWS;
                    gameInfo.rowCount = Constants.DEFAULT_ROWS;

                    adjustHeaderSpace();
                }

                Persistence.saveGame(getApplicationContext(), gameInfo);
            });
            alertDialogBuilder.setNegativeButton("No", (dialog, id) -> {});
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    public void initializeAdd() {
        FloatingActionButton button = findViewById(R.id.addButton);
        button.setOnClickListener(view -> {
            addRow();
            adjustHeaderSpace();

            gameInfo.rowCount++;
            Persistence.saveGame(getApplicationContext(), gameInfo);

            ScrollView tableVScroll = findViewById(R.id.tableVScroll);
            tableVScroll.fullScroll(View.FOCUS_DOWN);
            editData.get(rowCount - 1)[0].requestFocus();
        });
    }

    public void initializeTally() {
        Button button = findViewById(R.id.tallyButton);
        button.setOnClickListener((v) -> {
            Intent intent = new Intent(getBaseContext(), TallyActivity.class);
            String playersString = new Gson().toJson(gameInfo.players);
            intent.putExtra("PLAYERS", playersString);
            startActivity(intent);
        });
    }

    public void adjustHeaderSpace() {
        Space headerSpace = findViewById(R.id.headerSpace);
        TextView rowNo = rowNoData.get(rowCount - 1);
        rowNo.measure(0, 0);
        headerSpace.setMinimumWidth(rowNo.getMeasuredWidth());
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
