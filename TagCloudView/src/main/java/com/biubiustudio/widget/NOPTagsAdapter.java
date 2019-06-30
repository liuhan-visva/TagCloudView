package com.biubiustudio.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

class NOPTagsAdapter extends TagsAdapter {
    NOPTagsAdapter() {
        super();
    }

    public int getCount() {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public int getPopularity(int popularity) {
        return 0;
    }

    public View getView(Context context, int position, ViewGroup parent) {
        return null;
    }

    public void onThemeColorChanged(View child, int progress) {
    }
}

