package com.biubiustudio.widget;

import android.graphics.Color;
import android.view.View;

public class Tag {
    private static final int DEFAULT_POPULARITY = 5;
    private float[] argb;
    private float loc2DX;
    private float loc2DY;
    private float locX;
    private float locY;
    private float locZ;
    private View mView;
    private int popularity;
    private float scale;

    public Tag() {
        this(0f, 0f, 0f, 1f, 0);
    }

    public Tag(float locX, float locY, float locZ, float scale, int popularity) {
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
        this.loc2DX = 0f;
        this.loc2DY = 0f;
        this.argb = new float[]{1f, 0.5f, 0.5f, 0.5f};
        this.scale = scale;
        this.popularity = popularity;
    }

    public Tag(float locX, float locY, float locZ) {
        this(locX, locY, locZ, 1f, 5);
    }

    public Tag(float locX, float locY, float locZ, float scale) {
        this(locX, locY, locZ, scale, 5);
    }

    public Tag(int popularity) {
        this(0f, 0f, 0f, 1f, popularity);
    }

    public int getColor() {
        int[] arr = new int[4];
        for(int i = 0; i < 4; ++i) {
            arr[i] = (int)(this.argb[i] * 255f);
        }

        return Color.argb(arr[0], arr[1], arr[2], arr[3]);
    }

    public float getLoc2DX() {
        return this.loc2DX;
    }

    public float getLoc2DY() {
        return this.loc2DY;
    }

    public float getLocX() {
        return this.locX;
    }

    public float getLocY() {
        return this.locY;
    }

    public float getLocZ() {
        return this.locZ;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public float getScale() {
        return this.scale / 2f;
    }

    public View getView() {
        return this.mView;
    }

    public void setAlpha(float alpha) {
        this.argb[0] = alpha;
    }

    public void setColorByArray(float[] arr) {
        if(arr != null) {
            System.arraycopy(arr, 0, this.argb, this.argb.length - arr.length, arr.length);
        }
    }

    public void setLoc2DX(float loc2DX) {
        this.loc2DX = loc2DX;
    }

    public void setLoc2DY(float loc2DY) {
        this.loc2DY = loc2DY;
    }

    public void setLocX(float locX) {
        this.locX = locX;
    }

    public void setLocY(float locY) {
        this.locY = locY;
    }

    public void setLocZ(float locZ) {
        this.locZ = locZ;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setView(View view) {
        this.mView = view;
    }
}

