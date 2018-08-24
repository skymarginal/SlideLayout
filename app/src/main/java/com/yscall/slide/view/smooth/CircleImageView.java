package com.yscall.slide.view.smooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by 你的样子 on 2018/8/23.
 * 带圆圈的ImageView
 *
 * @author geriel
 */

public class CircleImageView extends AppCompatImageView {

    private boolean isCircleShow = false;
    private int circleColor = Color.WHITE;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        if (isCircleShow) {
            paint.setColor(circleColor);
        } else {
            paint.setColor(Color.TRANSPARENT);
        }

        int diameter = Math.min(getWidth(), getHeight());
        canvas.drawCircle(diameter / 2, diameter / 2, (diameter / 2) - 3, paint);
    }

    public void showCircle(int color) {
        isCircleShow = true;
        circleColor = color;
        invalidate();
    }

    public void hideCircle() {
        isCircleShow = false;
        invalidate();
    }

}
