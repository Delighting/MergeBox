package com.ll.mergebox.view;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CheckerAdapter extends BaseAdapter {

    private int mOrder = 0;
    private Context mContext;

    private int[][] mData = null;

    public CheckerAdapter(Context context, int order, int[][] data) {
        this.mContext = context;
        this.mOrder = order;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mOrder * mOrder;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new TextView(mContext);
        }
        TextView tv = (TextView) view;
        tv.setTextSize(40);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.GRAY);
        tv.setGravity(Gravity.CENTER);

        int x = i / mOrder;
        int y = i % mOrder;
        int content = mData[x][y];
        if (content != 0) {
            tv.setText("" + content);
        } else {
            tv.setText("");
        }

        return view;
    }
}
