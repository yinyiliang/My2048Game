package yyl.my2048game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import yyl.my2048game.view.GameView;
import yyl.my2048game.R;
import yyl.my2048game.config.Config;

/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class Game extends Activity implements View.OnClickListener {

    //Activity的引用
    public static Game mGame;

    //记录分数
    private TextView mTvScore;

    //历史分数记录
    private TextView mTvHighScore;
    private int mHighScore;

    //目标分数
    private TextView mTvGoal;
    private int mGoal;

    //重新开始按钮
    private Button mBtnRestart;
    //上一步按钮
    private Button mBtnRevert;
    //设置按钮
    private Button mBtnOptions;

    //游戏面板
    private GameView mGameView;

    public Game() {
        mGame = this;
    }

    /**
     * 获取当前Activity的引用
     *
     * @return this
     */
    public static Game getGameActivity() {return mGame;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化Views
        initViews();

        mGameView = new GameView(this);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.game_panel);
        //为了GameView能够居中
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_panel_rl);
        relativeLayout.addView(mGameView);
    }

    /**
     * 初始化 View
     */
    private void initViews() {
        //得分tv
        mTvScore = (TextView) findViewById(R.id.score);
        //目标tv
        mTvGoal = (TextView) findViewById(R.id.tv_goal);
        //最高分记录tv
        mTvHighScore = (TextView) findViewById(R.id.record);

        //三个按钮
        mBtnOptions = (Button) findViewById(R.id.btn_options);
        mBtnRestart = (Button) findViewById(R.id.btn_restart);
        mBtnRevert = (Button) findViewById(R.id.btn_revert);
        //设置按钮点击事件
        mBtnOptions.setOnClickListener(this);
        mBtnRestart.setOnClickListener(this);
        mBtnRevert.setOnClickListener(this);

        mHighScore = Config.mSp.getInt(Config.KEY_HIGH_SCORE,0);
        mGoal = Config.mSp.getInt(Config.KEY_GAME_GOAL,2048);

        mTvHighScore.setText("" + mHighScore);
        mTvGoal.setText("" + mGoal);
        mTvScore.setText("0");
        setScore(0,0);

    }

    public void setGoal(int num) {mTvGoal.setText(String.valueOf(num));}

    /**
     *  修改得分
     * @param score score
     * @param flag  0 : score 1 : high score
     */
    public void setScore(int score, int flag) {
        switch (flag) {
            case 0:
                mTvScore.setText("" + score);
                break;
            case 1:
                mTvHighScore.setText("" + score);
                break;
            default:
                break;
        }
    }

    /**
     * 按钮点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_revert:
                mGameView.revertGame();
                break;
            case R.id.btn_restart:
                mGameView.startGame();
                setScore(0,0);
                break;
            case R.id.btn_options:
                Intent intent = new Intent(Game.this,ConfigPreference.class);
                startActivityForResult(intent,0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 获取设置界面设置的参数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mGoal = Config.mSp.getInt(Config.KEY_GAME_GOAL,2048);
            mTvGoal.setText(""+mGoal);
            getHighScore();
            mGameView.startGame();
        }
    }

    /**
     * 获取最高记录
     */
    private void getHighScore() {
        int score = Config.mSp.getInt(Config.KEY_HIGH_SCORE,0);
        setScore(score,1);
    }
}
