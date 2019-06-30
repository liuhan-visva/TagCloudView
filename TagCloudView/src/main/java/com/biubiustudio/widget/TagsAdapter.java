package com.biubiustudio.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class TagsAdapter {
    public interface OnDataSetChangeListener {
        void onChange();
    }

    private OnDataSetChangeListener onDataSetChangeListener;

    public abstract int getCount();

    public abstract Object getItem(int position);

    public abstract int getPopularity(int popularity);

    public abstract View getView(Context context, int position, ViewGroup parent);

    public final void notifyDataSetChanged() {
        this.onDataSetChangeListener.onChange();
    }

    public abstract void onThemeColorChanged(View child, int progress);

    protected void setOnDataSetChangeListener(OnDataSetChangeListener listener) {
        this.onDataSetChangeListener = listener;
    }
}

