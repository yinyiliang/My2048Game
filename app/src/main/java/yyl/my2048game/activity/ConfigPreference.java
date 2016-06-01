package yyl.my2048game.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import yyl.my2048game.R;
import yyl.my2048game.config.Config;

/**
 * Created by Administrator on 2016/6/1 0001.
 */
public class ConfigPreference extends Activity implements View.OnClickListener {

    //设定GridView行列数
    private Button mBtnGameLines;
    //设置游戏目标值
    private Button mBtnGameGoal;
    //返回游戏界面
    private Button mBtnBack;
    //确定设置
    private Button mBtnDone;

    //游戏行列数 数组
    private String[] mGameLineList;
    //游戏目标值 数字
    private String[] mGameGoalList;

    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_preference);
        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        //初始化按钮
        mBtnGameLines = (Button) findViewById(R.id.btn_game_line);
        mBtnGameGoal = (Button) findViewById(R.id.btn_target_goal);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnDone = (Button) findViewById(R.id.btn_done);

        mBtnGameLines.setText(""+ Config.mSp.getInt(Config.KEY_GAME_LINES,4));
        mBtnGameGoal.setText(""+ Config.mSp.getInt(Config.KEY_GAME_GOAL,2048));

        //设置点击事件
        mBtnGameLines.setOnClickListener(this);
        mBtnGameGoal.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);

        mGameLineList = new String[]{"4","5","6"};
        mGameGoalList = new String[]{"1024","2048","4096"};
    }

    /**
     * 保存改变后的参数设置
     */
    private void saveConfig() {

        SharedPreferences.Editor editor = Config.mSp.edit();
        editor.putInt(Config.KEY_GAME_LINES,Integer.parseInt(mBtnGameLines.getText().toString()));
        editor.putInt(Config.KEY_GAME_GOAL,Integer.parseInt(mBtnGameGoal.getText().toString()));
        editor.apply();
    }

    /**
     * 设置按钮点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_game_line:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("选择格子数")
                        .setItems(mGameLineList, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnGameLines.setText(mGameLineList[which]);
                            }
                        });
                builder.create().show();
                break;
            case R.id.btn_target_goal:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("选择游戏目标值")
                        .setItems(mGameGoalList, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnGameGoal.setText(mGameGoalList[which]);
                            }
                        });
                builder.create().show();
                break;
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_done:
                saveConfig();
                setResult(RESULT_OK);
                this.finish();
                break;
            default:
                break;
        }

    }
}
