package com.biubiustudio.widget;

public class Point {
    float x;
    float y;
    float z;

    public static Point make(float x, float y, float z) {
        Point point = new Point();
        point.x = x;
        point.y = y;
        point.z = z;
        return point;
    }
}

