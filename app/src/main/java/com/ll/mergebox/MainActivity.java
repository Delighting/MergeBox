package com.ll.mergebox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ll.mergebox.view.CheckerView;

public class MainActivity extends Activity implements CheckerView.ScoreChangeListener,
        CheckerView.GameOverListener, CheckerView.OrderChangeListener, View.OnClickListener {

    private CheckerView mCheckerView = null;
    private TextView mScore = null;
    private TextView mOrder = null;
    private Button mResetButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckerView = (CheckerView) findViewById(R.id.checker);
        mScore = (TextView) findViewById(R.id.score);
        mScore.setText("当前得分：0");
        mOrder = (TextView) findViewById(R.id.order);
        mResetButton = (Button) findViewById(R.id.reset);

        mCheckerView.setScoreChangeListener(this);
        mCheckerView.setGameOverListener(this);
        mCheckerView.setOrderChangeListener(this);

        mResetButton.setOnClickListener(this);

        mOrder.setText("当前阶数：" + mCheckerView.getOrder());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reset) {
            mCheckerView.reset();
            mCheckerView.setFocusable(true);
            mCheckerView.requestFocus();
            mResetButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onScoreChange(int score) {
        mScore.setText("当前得分：" + score);
    }

    @Override
    public void onOrderChange(int order) {
        mOrder.setText("当前阶数：" + order);
    }

    @Override
    public void onGameOver() {
        mCheckerView.setFocusable(false);
        mResetButton.setVisibility(View.VISIBLE);
        mResetButton.requestFocus();
    }
}