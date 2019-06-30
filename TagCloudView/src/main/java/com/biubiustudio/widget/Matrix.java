package com.biubiustudio.widget;

import static java.lang.Math.sqrt;

public class Matrix {
    private float[][] matrix;
    private int column;
    private int row;

    public Matrix() {
        this.matrix = new float[4][4];
    }

    private static Matrix make(int column, int row) {
        Matrix matrix = new Matrix();
        matrix.column = column;
        matrix.row = row;
        for (int i = 0; i < column; ++i) {
            for (int j = 0; j < row; ++j) {
                matrix.matrix[i][j] = 0f;
            }
        }
        return matrix;
    }

    private static Matrix makeFromArray(int column, int row, float[][] arr) {
        Matrix matrix = Matrix.make(column, row);
        for (int i = 0; i < column; ++i) {
            for (int j = 0; j < row; ++j) {
                int position = i * row + j;
                matrix.matrix[i][j] = arr[position / row][position % row];
            }
        }
        return matrix;
    }

    private static Matrix multiply(Matrix m1, Matrix m2) {
        Matrix result = Matrix.make(m1.column, m2.row);
        for (int i = 0; i < m1.column; ++i) {
            for (int j = 0; j < m2.row; ++j) {
                for (int k = 0; k < m1.row; ++k) {
                    result.matrix[i][j] += m1.matrix[i][k] * m2.matrix[k][j];
                }
            }
        }
        return result;
    }

    static Point pointRotation(Point point, Point direction, float angle) {
        if (angle == 0) {
            return point;
        }

        float[][] temp2 = {{point.x, point.y, point.z, 1}};

        Matrix result = Matrix.makeFromArray(1, 4, temp2);
        if (direction.z * direction.z + direction.y * direction.y != 0) {
            float cos1 = (float) (direction.z / sqrt(direction.z * direction.z + direction.y * direction.y));
            float sin1 = (float) (direction.y / sqrt(direction.z * direction.z + direction.y * direction.y));
            float[][] t1 = new float[][]{{1, 0, 0, 0}, {0, cos1, sin1, 0}, {0, -sin1, cos1, 0}, {0, 0, 0, 1}};
            Matrix m1 = Matrix.makeFromArray(4, 4, t1);
            result = Matrix.multiply(result, m1);
        }

        if (direction.x * direction.x + direction.y * direction.y + direction.z * direction.z != 0) {
            float cos2 = (float) (sqrt(direction.y * direction.y + direction.z * direction.z) / sqrt(direction.x * direction.x + direction.y * direction.y + direction.z * direction.z));
            float sin2 = (float) (-direction.x / sqrt(direction.x * direction.x + direction.y * direction.y + direction.z * direction.z));
            float[][] t2 = {{cos2, 0, -sin2, 0}, {0, 1, 0, 0}, {sin2, 0, cos2, 0}, {0, 0, 0, 1}};
            Matrix m2 = Matrix.makeFromArray(4, 4, t2);
            result = Matrix.multiply(result, m2);
        }

        float cos3 = (float) Math.cos(angle);
        float sin3 = (float) Math.sin(angle);
        float[][] t3 = {{cos3, sin3, 0, 0}, {-sin3, cos3, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        Matrix m3 = Matrix.makeFromArray(4, 4, t3);
        result = Matrix.multiply(result, m3);

        if (direction.x * direction.x + direction.y * direction.y + direction.z * direction.z != 0) {
            float cos2 = (float) (sqrt(direction.y * direction.y + direction.z * direction.z) / sqrt(direction.x * direction.x + direction.y * direction.y + direction.z * direction.z));
            float sin2 = (float) (-direction.x / sqrt(direction.x * direction.x + direction.y * direction.y + direction.z * direction.z));
            float[][] t2_ = {{cos2, 0, sin2, 0}, {0, 1, 0, 0}, {-sin2, 0, cos2, 0}, {0, 0, 0, 1}};
            Matrix m2_ = Matrix.makeFromArray(4, 4, t2_);
            result = Matrix.multiply(result, m2_);
        }

        if (direction.z * direction.z + direction.y * direction.y != 0) {
            float cos1 = (float) (direction.z / sqrt(direction.z * direction.z + direction.y * direction.y));
            float sin1 = (float) (direction.y / sqrt(direction.z * direction.z + direction.y * direction.y));
            float[][] t1_ = {{1, 0, 0, 0}, {0, cos1, -sin1, 0}, {0, sin1, cos1, 0}, {0, 0, 0, 1}};
            Matrix m1_ = Matrix.makeFromArray(4, 4, t1_);
            result = Matrix.multiply(result, m1_);
        }

        return Point.make(result.matrix[0][0], result.matrix[0][1], result.matrix[0][2]);
    }
}

