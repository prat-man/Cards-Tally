package in.pratanumandal.cardstally;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Persistence {

    public static GameInfo loadGame(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String gameData = settings.getString("gameData", null);

        if (gameData == null) return new GameInfo();
        return new Gson().fromJson(gameData, GameInfo.class);
    }

    public static void saveGame(Context context, GameInfo gameInfo) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        String gameData = new Gson().toJson(gameInfo);
        editor.putString("gameData", gameData);

        editor.apply();
    }

    public static Boolean getDeleteNames(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return settings.getBoolean("deleteNames", false);
    }

    public static void setDeleteNames(Context context, boolean deleteNames) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("deleteNames", deleteNames);

        editor.apply();
    }

    public static Boolean getResetRowCount(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return settings.getBoolean("resetRowCount", false);
    }

    public static void setResetRowCount(Context context, boolean resetRowCount) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("resetRowCount", resetRowCount);

        editor.apply();
    }

    public static Boolean getReduceTally(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return settings.getBoolean("reduceTally", true);
    }

    public static void setReduceTally(Context context, boolean reduceTally) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("reduceTally", reduceTally);

        editor.apply();
    }

    public static Boolean getMinimumThreshold(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return settings.getBoolean("minimumThreshold", false);
    }

    public static void setMinimumThreshold(Context context, boolean minimumThreshold) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("minimumThreshold", minimumThreshold);

        editor.apply();
    }

    public static String getMinimumThresholdValue(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return settings.getString("minimumThresholdValue", null);
    }

    public static void setMinimumThresholdValue(Context context, String minimumThresholdValue) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("minimumThresholdValue", minimumThresholdValue);

        editor.apply();
    }

}
