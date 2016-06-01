package yyl.my2048game.bean;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import yyl.my2048game.config.Config;

/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class GameItem extends FrameLayout {

    //Item显示数字
    private int mCardShowNum;

    //显示数字的TextView
    private TextView mTvNum;

    private LayoutParams mParams;

    public GameItem(Context context, int mCardShowNum) {
        super(context);
        this.mCardShowNum = mCardShowNum;
        //初始化Item
        initCardIem();
    }

    /**
     * 初始化Item
     */
    private void initCardIem() {
        //设置面板背景色，是由Fame拼起来的
        setBackgroundColor(Color.GRAY);
        mTvNum = new TextView(getContext());
        setNum(mCardShowNum);

        //修改5 x 5 时字体太大
        int gameLines = Config.mSp.getInt(Config.KEY_GAME_LINES, 4);
        if (gameLines == 4) {
            mTvNum.setTextSize(35);
        } else if (gameLines == 5) {
            mTvNum.setTextSize(25);
        } else {
            mTvNum.setTextSize(20);
        }

        //更改字体样式 设置为粗体
        TextPaint tp = mTvNum.getPaint();
        tp.setFakeBoldText(true);
        //设置字体居中
        mTvNum.setGravity(Gravity.CENTER);

        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mParams.setMargins(5, 5, 5, 5);
        addView(mTvNum, mParams);
    }

    public TextView getItemView() {
        return mTvNum;
    }

    public int getNum() {
        return mCardShowNum;
    }

    /**
     * 设置显示数字  显示背景
     *
     * @param num
     */
    public void setNum(int num) {
        this.mCardShowNum = num;
        if (num == 0) {
            mTvNum.setText("");
        } else {
            mTvNum.setText("" + num);
        }

        //设置背景颜色
        switch (num) {
            case 0:
                mTvNum.setBackgroundColor(0x00000000);
                break;
            case 2:
                mTvNum.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                mTvNum.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                mTvNum.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                mTvNum.setBackgroundColor(0xfff59667);
                break;
            case 32:
                mTvNum.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                mTvNum.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                mTvNum.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                mTvNum.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                mTvNum.setBackgroundColor(0xffedc854);
                break;
            case 1024:
                mTvNum.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                mTvNum.setBackgroundColor(0xffedc32e);
                break;
            default:
                mTvNum.setBackgroundColor(0xff3c4a34);
                break;
        }

    }


}
