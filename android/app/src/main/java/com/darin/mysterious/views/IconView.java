package com.darin.mysterious.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.darin.mysterious.utils.PathUtils;

public class IconView extends View {

    private static final String PATH_OUTER = "M 159.1 150.6 L 96 41.3 L 32.9 150.6 L 43.7 150.6 L 49 141.3 L 49 141.3 L 50.2 139.2 L 56.4 128.4 L 56.5 128.4 L 96 59.8 L 143.1 141.3 L 103.2 141.3 L 103.2 139.4 C 112.9 136.2 119.9 127.1 119.9 116.3 C 119.9 102.9 109 92 95.6 92 C 82.2 92 71.3 103 71.3 116.4 L 71.3 118.1 L 80.3 118.1 L 80.3 116.4 C 80.3 108 87.2 101.1 95.6 101.1 C 104 101.1 110.9 108 110.9 116.4 C 110.9 124.5 104.5 131.2 96.6 131.7 L 93.9 131.7 L 93.9 150.7 L 103.2 150.7 L 103.2 150.7 L 159.1 150.7 Z";
    private static final String PATH_INNER = "M 103.3 122.3 L 104.6 121 L 94.6 109 L 93.7 109 C 90.2 109 87.4 111.8 87.4 115.3 C 87.4 117 88.1 120.1 93.2 121.5 C 97.3 122.7 99.7 123.1 101.1 123.1 C 102.6 123 103 122.6 103.3 122.3 Z M 94.2 117.7 C 92.3 117.2 91.3 116.3 91.3 115.3 C 91.3 114.3 91.9 113.4 92.9 113.1 L 97.5 118.6 C 96.4 118.3 95.3 118 94.2 117.7 Z";

    private Paint outerPaint, innerPaint;
    private Paint outerFillPaint, innerFillPaint;
    private Path outerPath, innerPath;
    private Rect pathSize;

    float[] outerDashes, innerDashes;
    float outerLength, innerLength;

    public IconView(Context context) {
        this(context, null, 0);
    }

    public IconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);
        outerPaint.setDither(true);
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setColor(Color.BLACK);

        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        innerPaint.setDither(true);
        innerPaint.setStyle(Paint.Style.STROKE);
        innerPaint.setColor(Color.BLACK);

        outerFillPaint = new Paint();
        outerFillPaint.setAntiAlias(true);
        outerFillPaint.setDither(true);
        outerFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outerFillPaint.setColor(Color.BLACK);
        outerFillPaint.setAlpha(0);

        innerFillPaint = new Paint();
        innerFillPaint.setAntiAlias(true);
        innerFillPaint.setDither(true);
        innerFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        innerFillPaint.setColor(Color.BLACK);
        innerFillPaint.setAlpha(0);

        outerPath = PathUtils.createPathFromPathData(PATH_OUTER);
        innerPath = PathUtils.createPathFromPathData(PATH_INNER);
        pathSize = new Rect(0, 0, 192, 192);

        outerDashes = new float[]{0f, Float.MAX_VALUE};
        innerDashes = new float[]{0f, Float.MAX_VALUE};
        outerLength = new PathMeasure(outerPath, true).getLength();
        innerLength = new PathMeasure(innerPath, true).getLength();
    }

    private Path getScaledPath(Path origPath, Rect origRect, int width, int height) {
        Rect newRect = new Rect(0, 0, width, height);
        int origWidth = origRect.right - origRect.left;
        int origHeight = origRect.bottom - origRect.top;

        Matrix matrix = new Matrix();
        matrix.postScale((float) (newRect.right - newRect.left) / origWidth, (float) (newRect.bottom - newRect.top) / origHeight);

        Path newPath = new Path();
        origPath.transform(matrix, newPath);
        return newPath;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Path outerPath = getScaledPath(this.outerPath, pathSize, canvas.getWidth(), canvas.getHeight());
        Path innerPath = getScaledPath(this.innerPath, pathSize, canvas.getWidth(), canvas.getHeight());

        if (Math.abs(outerDashes[0] - (outerLength * 2)) > outerLength * 0.2) {
            outerDashes[0] += ((outerLength * 2) - outerDashes[0]) * 0.065;
            outerPaint.setPathEffect(new DashPathEffect(outerDashes, 0));
        } else if (outerFillPaint.getAlpha() < 255) {
            outerPaint.setAlpha(0);
            outerFillPaint.setAlpha((int) (outerFillPaint.getAlpha() + ((254 - outerFillPaint.getAlpha()) * 0.15)));
        }

        if (Math.abs(innerDashes[0] - (innerLength * 2)) > innerLength * 0.2) {
            innerDashes[0] += ((innerLength * 2) - innerDashes[0]) * 0.065;
            innerPaint.setPathEffect(new DashPathEffect(innerDashes, 0));
        } else if (innerFillPaint.getAlpha() < 255) {
            innerPaint.setAlpha(0);
            innerFillPaint.setAlpha((int) (innerFillPaint.getAlpha() + ((254 - innerFillPaint.getAlpha()) * 0.15)));
        }

        canvas.drawPath(outerPath, outerPaint);
        canvas.drawPath(innerPath, innerPaint);
        canvas.drawPath(outerPath, outerFillPaint);
        canvas.drawPath(innerPath, innerFillPaint);

        postInvalidate();
    }
}
