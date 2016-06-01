package yyl.my2048game.config;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class Config extends Application {

    /**
     * SP对象
     */
    public static SharedPreferences mSp;

    /**
     *  Game Goal
     */
    public static int mGameGoal;

    /**
     * GameView 行列数
     */
    public static int mGameLines;

    /**
     * Item 宽高
     */
    public static int mItemSize;

    /**
     * 记录分数
     */
    public static int SCORE = 0;

    public static String SP_HIGH_SCORE = "SP_HIGHSCORE";

    public static String KEY_HIGH_SCORE = "KEY_HIGHSCORE";

    public static String KEY_GAME_LINES = "KEY_GAMELINES";

    public static String KEY_GAME_GOAL = "KEY_GAMEGOAL";

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = getSharedPreferences(SP_HIGH_SCORE,0);
        mGameLines = mSp.getInt(KEY_GAME_LINES,4);
        mGameGoal = mSp.getInt(KEY_GAME_GOAL,2048);
        mItemSize = 0;
    }
}
