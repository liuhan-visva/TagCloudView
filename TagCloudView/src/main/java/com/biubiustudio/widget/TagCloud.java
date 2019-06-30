package com.biubiustudio.widget;


import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TagCloud {


    private static class TagComparator implements Comparator<Tag> {
        private TagComparator() {
        }

        public int compare(Tag tag, Tag tag2) {
            return tag.getScale() > tag2.getScale() ? 1 : -1;
        }
    }


    private static final float[] DEFAULT_COLOR_DARK = new float[]{0.886f, 0.725f, 0.188f, 1f};
    private static final float[] DEFAULT_COLOR_LIGHT = new float[]{0.3f, 0.3f, 0.3f, 1f};
    private static final int DEFAULT_RADIUS = 30;
    private float cos_mAngleX;
    private float cos_mAngleY;
    private float cos_mAngleZ;
    private boolean distrEven;
    private int largest;
    private float mAngleX;
    private float mAngleY;
    private float mAngleZ;
    private int radius;
    private float sin_mAngleX;
    private float sin_mAngleY;
    private float sin_mAngleZ;
    private int smallest;
    private List<Tag> tagCloud;
    private float[] tagColorDark;
    private float[] tagColorLight;
    private ArrayList youngPoints;

    public TagCloud() {
        this(DEFAULT_RADIUS);
    }

    public TagCloud(int radius) {
        this(new ArrayList<Tag>(), radius);
    }

    public TagCloud(List<Tag> tags, int radius) {
        this(tags, radius, TagCloud.DEFAULT_COLOR_DARK, TagCloud.DEFAULT_COLOR_LIGHT);
    }

    public TagCloud(List<Tag> tags) {
        this(tags, DEFAULT_RADIUS);
    }

    public TagCloud(List<Tag> tags, int radius, float[] tagColorLight, float[] tagColorDark) {
        this.mAngleZ = 0f;
        this.mAngleX = 0f;
        this.mAngleY = 0f;
        this.distrEven = true;
        this.tagCloud = tags;
        this.radius = radius;
        this.tagColorLight = tagColorLight;
        this.tagColorDark = tagColorDark;
    }

    public void add(Tag tag) {
        this.initTag(tag);
        this.tagCloud.add(tag);
    }

    public void clear() {
        this.tagCloud.clear();
    }

    public void create(boolean distrEven) {
        this.distrEven = distrEven;
        this.positionAll(distrEven);
        this.smallest = 9999;
        this.largest = 0;
        for (int i = 0; i < this.tagCloud.size(); ++i) {
            int popularity = this.tagCloud.get(i).getPopularity();
            this.largest = Math.max(this.largest, popularity);
            this.smallest = Math.min(this.smallest, popularity);

            this.initTag(this.tagCloud.get(i));
        }
    }

    public Tag get(int position) {
        return this.tagCloud.get(position);
    }

    private float[] getColorFromGradient(float progress) {
        float[] arr = new float[4];
        arr[0] = 1f;
        float v1 = 1f - progress;
        arr[1] = this.tagColorDark[0] * progress + this.tagColorLight[0] * v1;
        arr[2] = this.tagColorDark[1] * progress + this.tagColorLight[1] * v1;
        arr[3] = progress * this.tagColorDark[2] + v1 * this.tagColorLight[2];
        return arr;
    }

    private float getPercentage(Tag tag) {
        int popularity = tag.getPopularity();
        return this.smallest == this.largest ? 1f : (popularity - this.smallest) * 10.F / (this.largest - this.smallest);
    }

    public List<Tag> getTagList() {
        return this.tagCloud;
    }

    public Tag getTop() {
        return this.get(this.tagCloud.size() - 1);
    }

    public int indexOf(Tag tag) {
        return this.tagCloud.indexOf(tag);
    }

    private void initTag(Tag tag) {
        tag.setColorByArray(this.getColorFromGradient(this.getPercentage(tag)));
    }

    private void position(boolean drilEven, Tag tag) {
        this.tagCloud.size();
        double r1 = Math.random() * Math.PI;
        double r2 = Math.random() * Math.PI * 2;
        tag.setLocX((float) (this.radius * Math.cos(r2) * Math.sin(r1)));
        tag.setLocY((float) (this.radius * Math.sin(r2) * Math.sin(r1)));
        tag.setLocZ((float) (this.radius * Math.cos(r1)));
    }

    private void positionAll(boolean drilEven) {
        double pos;
        int size = this.tagCloud.size();
        for (int i = 1; i < size + 1; ++i) {
            double doublePI;
            if (drilEven) {
                pos = Math.acos((i * 2.0F - 1) / size - 1);
                doublePI = Math.sqrt(size * Math.PI) * pos;
            } else {
                pos = Math.random() * Math.PI;
                doublePI = Math.random() * Math.PI * 2;
            }

            int position = i - 1;
            this.tagCloud.get(position).setLocX((float) (this.radius * Math.cos(doublePI) * Math.sin(pos)));
            this.tagCloud.get(position).setLocY((float) (this.radius * Math.sin(doublePI) * Math.sin(pos)));
            this.tagCloud.get(position).setLocZ((float) (this.radius * Math.cos(pos)));
        }
    }

    public void reset() {
        this.create(this.distrEven);
    }

    public void setAngleX(float angleX) {
        this.mAngleX = angleX;
    }

    public void setAngleY(float angleY) {
        this.mAngleY = angleY;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setTagColorDark(float[] tagColorDark) {
        this.tagColorDark = tagColorDark;
    }

    public void setTagColorLight(float[] tagColorLight) {
        this.tagColorLight = tagColorLight;
    }

    public void setTagList(List tag) {
        this.tagCloud = tag;
    }

    private void sineCosine(float x, float y, float z) {
        double v0 = (((double) x)) * Math.PI / 180;
        this.sin_mAngleX = (float) Math.sin(v0);
        this.cos_mAngleX = (float) Math.cos(v0);
        double radian = y * Math.PI / 180;
        this.sin_mAngleY = (float) Math.sin(radian);
        this.cos_mAngleY = (float) Math.cos(radian);
        radian = z * Math.PI / 180;
        this.sin_mAngleZ = (float) Math.sin(radian);
        this.cos_mAngleZ = (float) Math.cos(radian);
    }

    public void sortTagByScale() {
        Collections.sort(this.tagCloud, new TagComparator());
    }

    public void update() {
        if (Math.abs(this.mAngleX) > 0.1 || Math.abs(this.mAngleY) > 0.1) {
            this.sineCosine(this.mAngleX, this.mAngleY, this.mAngleZ);
            this.updateAll();
        }
    }

    private void updateAll() {
        for (int i = 0; i < this.tagCloud.size(); ++i) {
            float locX = this.tagCloud.get(i).getLocX();
            float locY = this.tagCloud.get(i).getLocY() * this.cos_mAngleX + this.tagCloud.get(i).getLocZ() * -this.sin_mAngleX;
            float locYa = this.tagCloud.get(i).getLocY() * this.sin_mAngleX + this.tagCloud.get(i).getLocZ() * this.cos_mAngleX;
            float locXa = this.cos_mAngleY * locX + this.sin_mAngleY * locYa;
            locX = locX * -this.sin_mAngleY + locYa * this.cos_mAngleY;
            locYa = this.cos_mAngleZ * locXa + -this.sin_mAngleZ * locY;
            locXa = locXa * this.sin_mAngleZ + locY * this.cos_mAngleZ;
            this.tagCloud.get(i).setLocX(locYa);
            this.tagCloud.get(i).setLocY(locXa);
            this.tagCloud.get(i).setLocZ(locX);
            locY = ((float) (this.radius * 2));
            float te = locY / 1f / (locY + locX);
            this.tagCloud.get(i).setLoc2DX(locYa * te);
            this.tagCloud.get(i).setLoc2DY(locXa * te);
            this.tagCloud.get(i).setScale(te);
            this.tagCloud.get(i).setAlpha(te / 2f);
        }

        if (Build.VERSION.SDK_INT < 21) {
            this.sortTagByScale();
        }

    }
}

