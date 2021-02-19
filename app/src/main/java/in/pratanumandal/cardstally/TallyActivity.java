package in.pratanumandal.cardstally;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Collections;

public class TallyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        String playersString = getIntent().getExtras().getString("PLAYERS");
        Player[] players = new Gson().fromJson(playersString, Player[].class);

        tally(players);

        FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
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
                Commons.showInfo(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void tally(Player... players) {
        sortPlayers(players);

        int[][] scores = new int[Constants.PLAYERS][Constants.PLAYERS];

        for (int i = 0; i < Constants.PLAYERS - 1; i++) {
            int cardI = players[i].cards;
            for (int j = i + 1; j < Constants.PLAYERS; j++) {
                int cardJ = players[j].cards;
                scores[i][j] = cardI - cardJ;
            }
        }

        for (int k = 0; k < Constants.PLAYERS - 1; k++) {
            for (int i = k + 1; i < Constants.PLAYERS - 1; i++) {
                for (int j = i + 1; j < Constants.PLAYERS && scores[k][i] > 0; j++) {
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
        TableLayout table = findViewById(R.id.tallyTable);

        boolean found = false;

        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores.length; j++) {
                if (scores[i][j] != 0) {
                    TableRow row = new TableRow(this);

                    TextView from = new TextView(this);
                    from.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    from.setText(players[i].name.isEmpty() ? players[i].defaultName : players[i].name);
                    row.addView(from);

                    TextView to = new TextView(this);
                    to.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    to.setText(players[j].name.isEmpty() ? players[j].defaultName : players[j].name);
                    row.addView(to);

                    TextView cards = new TextView(this);
                    cards.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    cards.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    cards.setText(String.valueOf(scores[i][j]));
                    row.addView(cards);

                    table.addView(row);

                    found = true;
                }
            }
        }

        if (!found) {
            TableRow row = new TableRow(this);

            TextView noData = new TextView(this);
            noData.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            noData.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noData.setText("No Data");
            row.addView(noData);

            TableRow.LayoutParams params = (TableRow.LayoutParams) noData.getLayoutParams();
            params.span = 3;
            noData.setLayoutParams(params);

            table.addView(row);
        }
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