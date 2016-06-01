package yyl.my2048game.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yyl.my2048game.activity.Game;
import yyl.my2048game.bean.GameItem;
import yyl.my2048game.config.Config;

/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class GameView extends GridLayout implements View.OnTouchListener {

    //GameView对应矩阵
    private GameItem[][] mGameMatrix;
    //空格List
    private List<Point> mBlanks;
    //矩阵行列数
    private int mGameLines;
    //记录坐标
    private int mStartX, mEndX, mStartY, mEndY;

    //辅助数组
    private List<Integer> mCalList;
    private int mKeyItemNum = -1;

    //历史记录数组
    private int[][] mGameMatrixHistory;
    //历史记录分数
    private int mScoreHistory;

    //最高记录
    private int mHighScore;

    //目标分数
    private int mTarget;

    public GameView(Context context) {
        super(context);
        mTarget = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);
        initGameMatrix();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameMatrix();
    }

    public void startGame() {
        initGameMatrix();
        initGameView(Config.mItemSize);
        Config.SCORE = 0;
    }

    /**
     * 初始化View
     */
    private void initGameMatrix() {
        //初始化矩阵
        removeAllViews();
        mScoreHistory = 0;
        Config.SCORE = 0;
        Config.mGameLines = Config.mSp.getInt(Config.KEY_GAME_LINES, 4);
        mGameLines = Config.mGameLines;
        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mCalList = new ArrayList<>();
        mBlanks = new ArrayList<>();
        mHighScore = Config.mSp.getInt(Config.KEY_HIGH_SCORE, 0);
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);

        //初始化View参数
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        Config.mItemSize = metrics.widthPixels / Config.mGameLines;
        initGameView(Config.mItemSize);
    }

    /**
     * 游戏矩阵初始化
     * <p/>
     * 将所有的小方块都设置为0，根据游戏规则，随机添加两个数字到面板中
     *
     * @param cardSize
     */
    private void initGameView(int cardSize) {
        removeAllViews();
        GameItem card;

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                card = new GameItem(getContext(), 0);
                addView(card, cardSize, cardSize);
                //初始化GameMatrix 全部为0，空格List为所有
                mGameMatrix[i][j] = card;
                mBlanks.add(new Point(i, j));
            }
        }
        //添加随机数字
        addRandomNum();
        addRandomNum();
    }

    /**
     * 添加随机数字
     */
    private void addRandomNum() {
        getBanks();
        if (mBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y].setNum(Math.random() > 0.2d ? 2 : 4);
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    /**
     * 获取空格Item数组
     */
    private void getBanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mBlanks.add(new Point(i, j));
                }
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //保存上一步的矩阵
                saveHistoryMatrix();
                //获取开始位置
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                //获取终点位置
                mEndX = (int) event.getX();
                mEndY = (int) event.getY();
                //根据位移量判断移动方向
                judgeDirection(mEndX - mStartX, mEndY - mStartY);
                if (isMoved()) {
                    addRandomNum();
                    //修改显示分数
                    Game.getGameActivity().setScore(Config.SCORE, 0);
                }
                checkCompleted();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 判断是否移动过(是否需要增加Item)
     *
     * @return 是否移动
     */
    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 撤销上次移动
     */
    public void revertGame() {
        //第一次不能撤销
        int sum = 0;
        for (int[] element : mGameMatrixHistory) {
            for (int i : element) {
                sum += i;
            }
        }
        if (sum != 0) {
            Game.getGameActivity().setScore(mScoreHistory, 0);
            Config.SCORE = mScoreHistory;
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                }
            }
        }
    }

    /**
     * 获取像素密度
     */
    private int getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.density;
    }

    /**
     * 判断是否结束
     * <p/>
     * 0:结束  1:正常 2:成功
     */
    private void checkCompleted() {
        int result = checkNums();
        if (result == 0) {
            //游戏结束
            if (Config.SCORE > mHighScore) {
                //更改已经保存了的最高分数
                SharedPreferences.Editor editor = Config.mSp.edit();
                editor.putInt(Config.KEY_HIGH_SCORE, Config.SCORE);
                editor.apply();
                Game.getGameActivity().setScore(Config.SCORE, 1);
                Config.SCORE = 0;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Game over！！").setPositiveButton("再来一次！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            }).create().show();
            // Config.SCORE = 0;
        } else if (result == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("恭喜过关！").setPositiveButton("再来一次！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            }).setNegativeButton("下一关", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //继续游戏  修改target
                    SharedPreferences.Editor editor = Config.mSp.edit();
                    if (mTarget == 1024) {
                        editor.putInt(Config.KEY_GAME_GOAL, 2048);
                        mTarget = 2048;
                        Game.getGameActivity().setGoal(2048);
                    } else if (mTarget == 2048) {
                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                        mTarget = 4096;
                        Game.getGameActivity().setGoal(4096);
                    } else {
                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                        mTarget = 4096;
                        Game.getGameActivity().setGoal(4096);
                        Toast.makeText(getContext(), "大侠你已通关，恭喜！！", Toast.LENGTH_SHORT).show();
                    }
                    editor.apply();
                }
            }).create().show();
            Config.SCORE = 0;
        }
    }

    /**
     * 检查所有数字是否满足条件
     *
     * @return 0:结束  1:正常 2:成功
     */
    private int checkNums() {
        getBanks();
        if (mBlanks.size() == 0) {
            //当整个面板中空格数组的大小为0时，判断游戏状态
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    if (j < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1].getNum()) {
                            //如果在任何一列上有两个相邻数字相等，则表示游戏正常
                            return 1;
                        }
                    }
                    if (i < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum()) {
                            //如果在任何一行上有两个相邻数字相等，则表示游戏正常
                            return 1;
                        }
                    }
                }
            }
            return 0; //以上都不满足说明游戏结束
        }
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == mTarget) {
                    //在所有面板数字中，有一个数等于目标值，则游戏过关
                    return 2;
                }
            }
        }
        return 1;
    }

    /**
     * 添加显示数字出现的动画
     *
     * @param target
     */
    private void animCreate(GameItem target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getItemView().startAnimation(sa);
    }

    /**
     * 超级用户权限下添加一个指定的数字
     */
    private void addSuperNum(int num) {
        if (checkSuperNum(num)) {
            getBanks();
            if (mBlanks.size() > 0) { //如果空格数大于0
                int randomNum = (int) (Math.random() * mBlanks.size());
                Point randomPoint = mBlanks.get(randomNum);
                mGameMatrix[randomPoint.x][randomPoint.y].setNum(num);
                animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }

    /**
     * 检查添加的数字是否符合规则
     */
    private boolean checkSuperNum(int num) {
        boolean flag = (num == 2 || num == 4 || num == 8
                || num == 16 || num == 32 || num == 64
                || num == 128 || num == 256 || num == 512
                || num == 1024);
        return flag;
    }

    /**
     * 保存历史记录
     */
    private void saveHistoryMatrix() {
        mScoreHistory = Config.SCORE;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    /**
     * 根据偏移量判断移动方向
     *
     * @param offsetX
     * @param offsetY
     */
    private void judgeDirection(int offsetX, int offsetY) {
        int density = getDeviceDensity();
        //设置滑动最小距离和最大范围
        int slideDis = 5 * density;
        int maxDis = 200 * density;

        //利用滑动距离的绝对值设置分为两种情况，在正常范围内的，和超过预设最大值的
        // （后面可以利用其设置一个隐藏的超级用户权限）
        boolean flagNormal = (Math.abs(offsetX) > slideDis || Math.abs(offsetY) > slideDis) && (Math.abs(offsetX) < maxDis || Math.abs(offsetY) < maxDis);
        boolean flagSuper = Math.abs(offsetX) > maxDis || Math.abs(offsetY) > maxDis;


        if (flagNormal && !flagSuper) {
            //如果滑动距离的绝对值在正常范围内
            if (Math.abs(offsetX) > Math.abs(offsetY)) {
                //如果在横坐标滑动的距离大于纵坐标滑动的距离 判断是向左滑动还是向右滑动
                if (offsetX > slideDis) {
                    //如果移动距离大于最小滑动距离，就是向X轴正方向滑动，即向右滑动
                    swipeRight();
                } else {
                    //向左滑动
                    swipeLeft();
                }
            } else {
                //如果在纵坐标滑动的距离大于横坐标滑动的距离  判断是向上滑动还是向下滑动
                if (offsetY > slideDis) {
                    swipeDown();
                } else {
                    swipeUp();
                }
            }
        } else if (flagSuper) { //启动超级用户权限来添加自定义数字

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText ex = new EditText(getContext());
            ex.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setTitle("任意门").setView(ex).setPositiveButton("好了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(ex.getText())) {
                        addSuperNum(Integer.parseInt(ex.getText().toString()));
                        checkCompleted();
                    }
                }
            }).setNegativeButton("不了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();

        }
    }

    /**
     * 滑动事件：左
     */
    private void swipeLeft() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) { //如果当前这个数不为空
                    if (mKeyItemNum == -1) { //如果还没有进行合并  就把当前的值赋给mKeyItemNum
                        mKeyItemNum = currentNum;
                    } else {  //如果合并过了，就从mKeyItemNum中获取被合并的值*2,放入辅助数组中，并更新已获得的分数
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;

                            mKeyItemNum = -1;//控制在一次滑动中，已经合并过的数字不能进行第二次合并
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else { //为空格则不作处理
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            //改变Item的值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[i][j].setNum(mCalList.get(j));
            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：右
     */
    private void swipeRight() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) { //如果当前这个数不为空
                    if (mKeyItemNum == -1) { //如果还没有进行合并  就把当前的值赋给mKeyItemNum
                        mKeyItemNum = currentNum;
                    } else {  //如果合并过了，就从mKeyItemNum中获取被合并的值*2,放入辅助数组中，并更新已获得的分数
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;

                            mKeyItemNum = -1;//控制在一次滑动中，已经合并过的数字不能进行第二次合并
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else { //为空格则不作处理
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            //改变Item的值
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                mGameMatrix[i][j].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int m = mGameLines - mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(mCalList.get(index));
                index--;
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }

    /**
     * 滑动事件：上
     */
    private void swipeUp() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) { //如果当前这个数不为空
                    if (mKeyItemNum == -1) { //如果还没有进行合并  就把当前的值赋给mKeyItemNum
                        mKeyItemNum = currentNum;
                    } else {  //如果合并过了，就从mKeyItemNum中获取被合并的值*2,放入辅助数组中，并更新已获得的分数
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;

                            mKeyItemNum = -1;//控制在一次滑动中，已经合并过的数字不能进行第二次合并
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else { //为空格则不作处理
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            //改变Item的值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[j][i].setNum(mCalList.get(j));
            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：下
     */
    private void swipeDown() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                mGameMatrix[j][i].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int m = mGameLines - mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(mCalList.get(index));
                index--;
            }
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }
}
