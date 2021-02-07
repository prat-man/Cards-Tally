package in.pratanumandal.cardstally;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Persistence {

    public static GameInfo loadGame(Context context) {
        SharedPreferences settings = context.getSharedPreferences("GAME_INFO", 0);
        String gameData = settings.getString("gameData", null);

        if (gameData == null) return new GameInfo();
        return new Gson().fromJson(gameData, GameInfo.class);
    }

    public static void saveGame(Context context, GameInfo gameInfo) {
        SharedPreferences settings = context.getSharedPreferences("GAME_INFO", 0);
        SharedPreferences.Editor editor = settings.edit();

        String gameData = new Gson().toJson(gameInfo);
        editor.putString("gameData", gameData);

        editor.apply();
    }

    public static Boolean getDeleteNames(Context context) {
        SharedPreferences settings = context.getSharedPreferences("SETTINGS", 0);
        return settings.getBoolean("deleteNames", false);
    }

    public static void setDeleteNames(Context context, Boolean deleteNames) {
        SharedPreferences settings = context.getSharedPreferences("SETTINGS", 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("deleteNames", deleteNames);

        editor.apply();
    }

}
