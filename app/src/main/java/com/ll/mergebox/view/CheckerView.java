package com.ll.mergebox.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CheckerView extends GridView {

    public static final String TAG = "CheckerView";
    public static final int DEFAULT_ORDER = 5;

    public static final int DEFAULT_WIN_VALUE = 2048;

    private int mOrder = DEFAULT_ORDER;
    private Context mContext = null;
    private CheckerAdapter mAdapter = null;
    private int[][] mData = null;
    private Random mRandom = new Random();

    private int mScore = 0;

    private int mWinValue = DEFAULT_WIN_VALUE;
    //是否提示过胜利
    private boolean mNoticeWin = false;

    private ScoreChangeListener mScoreChangeListener = null;
    private GameOverListener mGameOverListener = null;
    private OrderChangeListener mOrderChangeListener = null;

    private static final int MOVE_DISTANCE = 20;
    private int dx = -1;
    private int dy = -1;
    private boolean processTouch = true;

    public CheckerView(Context context) {
        super(context);
        init(context);
    }

    public CheckerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setNumColumns(mOrder);
        setVerticalSpacing(5);
        setHorizontalSpacing(5);
        mData = new int[mOrder][mOrder];
        random();
        random();
        mAdapter = new CheckerAdapter(mContext, mOrder, mData);
        setAdapter(mAdapter);
    }

    public void reset() {
        mScore = 0;
        for (int i = 0; i < mOrder; i++) {
            for (int j = 0; j < mOrder; j++) {
                mData[i][j] = 0;
            }
        }
        random();
        random();
        mAdapter.notifyDataSetChanged();
        if (mScoreChangeListener != null) {
            mScoreChangeListener.onScoreChange(0);
        }
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        setOrder(order, true);
    }

    public void setOrder(final int order, boolean check) {
        if (!(order >= 3 && order <= 8)) {
            Toast.makeText(mContext, "阶数最小为3，最大为8", Toast.LENGTH_LONG).show();
            return;
        }

        if (check) {
            int contentNum = 0;
            for (int i = 0; i < mOrder; i++) {
                for (int j = 0; j < mOrder; j++) {
                    if (mData[i][j] != 0) {
                        contentNum++;
                    }
                }
            }
            if (contentNum > 2) {
                //user has operation
                new AlertDialog.Builder(mContext)
                        .setMessage("当前已经有移动操作，是否继续").
                        setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setOrder(order, false);
                            }
                        }).show();
                return;
            }
        }

        this.mOrder = order;
        this.mData = new int[mOrder][mOrder];
        random();
        random();
        setNumColumns(mOrder);
        mAdapter = new CheckerAdapter(mContext, mOrder, mData);
        setAdapter(mAdapter);
        if (mOrderChangeListener != null) {
            mOrderChangeListener.onOrderChange(mOrder);
        }
        this.mScore = 0;
        if (mOrderChangeListener != null) {
            mScoreChangeListener.onScoreChange(0);
        }
    }

    public void setWinValue(int winValue) {
        this.mWinValue = winValue;
    }

    public interface ScoreChangeListener {
        void onScoreChange(int score);
    }

    public void setScoreChangeListener(ScoreChangeListener listener) {
        this.mScoreChangeListener = listener;
    }

    public interface GameOverListener {
        void onGameOver();
    }

    public void setOrderChangeListener(OrderChangeListener listener) {
        this.mOrderChangeListener = listener;
    }

    public interface OrderChangeListener {
        void onOrderChange(int order);
    }

    public void setGameOverListener(GameOverListener listener) {
        this.mGameOverListener = listener;
    }

    private void random() {
        List<Integer> notEmpty = new ArrayList<>();
        for (int i = 0; i < mOrder; i++) {
            for (int j = 0; j < mOrder; j++) {
                if (mData[i][j] == 0) {
                    notEmpty.add(i * 10 + j);
                }
            }
        }
        if (notEmpty.size() > 0) {
            int pos = mRandom.nextInt(notEmpty.size());
            int value = notEmpty.get(pos);
            int i = value / 10;
            int j = value % 10;
            mData[i][j] = randomValue();
        }
    }

    private int randomValue() {
        if (mRandom.nextInt(100) > 89) {
            return 2;
        } else {
            return 4;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                processTouch = true;
                dx = (int) ev.getX();
                dy = (int) ev.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!processTouch) {
                    return true;
                }
                int mx = (int) ev.getX() - dx;
                int my = (int) ev.getY() - dy;
                if (Math.abs(mx) > Math.abs(my) && Math.abs(mx) > MOVE_DISTANCE) {
                    if (mx < 0) {
                        onKeyMove(KeyEvent.KEYCODE_DPAD_LEFT);
                    } else {
                        onKeyMove(KeyEvent.KEYCODE_DPAD_RIGHT);
                    }
                    processTouch = false;
                } else if (Math.abs(mx) < Math.abs(my) && Math.abs(my) > MOVE_DISTANCE) {
                    if (my < 0) {
                        onKeyMove(KeyEvent.KEYCODE_DPAD_UP);
                    } else {
                        onKeyMove(KeyEvent.KEYCODE_DPAD_DOWN);
                    }
                    processTouch = false;
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                processTouch = true;
                dx = -1;
                dy = -1;
                return true;
            default:
                return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyMove(keyCode)) return true;
        return super.onKeyDown(keyCode, event);
    }

    private boolean onKeyMove(int keyCode) {
        int score = mScore;
        boolean change = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (left_movable()) {
                    onLeft();
                    change = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (down_movable()) {
                    onDown();
                    change = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (right_movable()) {
                    onRight();
                    change = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (up_movable()) {
                    onUp();
                    change = true;
                }
                break;

            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                setOrder(mOrder - 1);
                return true;

            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
                setOrder(mOrder + 1);
                return true;
        }
        if (change) {
            random();
            mAdapter.notifyDataSetChanged();

            if (isWin() && !mNoticeWin) {
                Toast.makeText(mContext, "You Win!!!", Toast.LENGTH_LONG).show();
                mNoticeWin = true;
            }
            if (isGameOver()) {
                Toast.makeText(mContext, "Game Over!!!", Toast.LENGTH_SHORT).show();
                if (mGameOverListener != null) {
                    mGameOverListener.onGameOver();
                }
            }
            if (mScoreChangeListener != null && score != mScore) {
                mScoreChangeListener.onScoreChange(mScore);
            }
        }
        return false;
    }

    private void onLeft() {
        for (int i = 0; i < mOrder; i++) {
            mData[i] = tighten(merge(tighten(mData[i])));
        }
    }

    //把非空值靠在一起移到左边
    private int[] tighten(int[] row) {
        int[] newRow = new int[row.length];
        int index = 0;
        for (int i = 0; i < row.length; i++) {
            if (row[i] != 0) {
                newRow[index] = row[i];
                index++;
            }
        }
        return newRow;
    }

    //合并相邻且相同的值
    private int[] merge(int[] row) {
        int[] newRow = new int[row.length];

        boolean paired = false;
        for (int i = 0; i < row.length; i++) {
            if (paired) {
                newRow[i] = 2 * row[i];
                if (mScore < 2 * row[i]) {
                    mScore = 2 * row[i];
                }
                paired = false;
            } else {
                if (i + 1 < row.length && row[i] == row[i + 1]) {
                    paired = true;
                    newRow[i] = 0;
                } else {
                    newRow[i] = row[i];
                }
            }
        }
        return newRow;
    }

    //矩阵转置，角标交换
    private void transpose() {
        int tmp;
        for (int i = 0; i < mOrder; i++) {
            for (int j = i; j < mOrder; j++) {
                tmp = mData[i][j];
                mData[i][j] = mData[j][i];
                mData[j][i] = tmp;
            }
        }
    }

    //矩阵横向倒序
    private void inverse() {
        int tmp;
        for (int i = 0; i < mOrder; i++) {
            for (int start = 0, end = mOrder - 1; start < end; start++, end--) {
                tmp = mData[i][start];
                mData[i][start] = mData[i][end];
                mData[i][end] = tmp;
            }
        }
    }

    private void print() {
        for (int i = 0; i < mOrder; i++) {
            Log.i(TAG, Arrays.toString(mData[i]));
        }
    }

    //水平翻转，左移，翻转
    private void onRight() {
        inverse();
        onLeft();
        inverse();
    }

    //转置，左移，转置
    private void onUp() {
        transpose();
        onLeft();
        transpose();
    }

    //转置，右移，转置
    private void onDown() {
        transpose();
        onRight();
        transpose();
    }

    public boolean isWin() {
        for (int i = 0; i < mOrder; i++) {
            for (int j = 0; j < mOrder; j++) {
                if (mData[i][j] >= mWinValue) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return !left_movable() && !right_movable() && !up_movable() && !down_movable();
    }

    //单行是否可以左移
    private boolean row_left_movable(int[] row) {
        for (int i = 0; i < mOrder - 1; i++) {
            if (row[i] == 0 && row[i + 1] != 0)
                // Move
                return true;
            if (row[i] != 0 && row[i + 1] == row[i])
                // Merge
                return true;
        }
        return false;
    }

    private boolean left_movable() {
        for (int i = 0; i < mOrder; i++) {
            if (row_left_movable(mData[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean right_movable() {
        inverse();
        boolean movable = left_movable();
        inverse();
        return movable;
    }

    private boolean up_movable() {
        transpose();
        boolean movable = left_movable();
        transpose();
        return movable;
    }

    private boolean down_movable() {
        transpose();
        boolean movable = right_movable();
        transpose();
        return movable;
    }

}
