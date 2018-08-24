package com.yscall.slide.view.smooth;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by 你的样子 on 2018/8/22.
 * 圆圈
 *
 * @author gerile
 */

public class CircleView extends View {

    private OnCircleOffsetListener offsetListener;
    private RelativeLayout.LayoutParams layoutParams;

    private int parentWidth;
    private int width;
    private int lastX;
    private int marginLeft;
    private int marginRight;
    private int hideLeft = 0;
    private int hideRight = 0;
    private int originalLeft = 0;
    private int originalRight = 0;

    private boolean isShow = false;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnOffsetListener(OnCircleOffsetListener offsetListener) {
        this.offsetListener = offsetListener;
    }

    public void setHideLeft(int hideLeft) {
        this.hideLeft = hideLeft;
    }

    public void setHideRight(int hideRight) {
        this.hideRight = hideRight;
    }

    public int getOriginalLeft() {
        return originalLeft;
    }

    public int getOriginalRight() {
        return originalRight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ViewGroup viewGroup = (ViewGroup) getParent();
        width = getWidth();
        if (null != viewGroup) {
            parentWidth = viewGroup.getWidth();
            originalLeft = (parentWidth - width) / 2;
            originalRight = originalLeft + width;
            layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            layoutParams.leftMargin = originalLeft;
            setLayoutParams(layoutParams);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        if (isShow) {
            paint.setColor(Color.WHITE);
        } else {
            paint.setColor(Color.TRANSPARENT);
        }
        int diameter = Math.min(getWidth(), getHeight());
        /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
        canvas.drawCircle(diameter / 2, diameter / 2, (diameter / 2) - 3, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int left = getLeft();
        int right = getRight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                marginLeft = left;
                marginRight = right;
                if (marginLeft > hideLeft && marginRight < hideRight) {
                    show();
                }
                if (offsetListener != null) {
                    offsetListener.onCircleDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                int offsetX = x - lastX;

                marginLeft = left + offsetX;
                marginRight = right + offsetX;

                if (marginLeft < 0) {
                    marginLeft = 0;
                    marginRight = width;
                }
                if (marginRight > parentWidth) {
                    marginLeft = parentWidth - width;
                    marginRight = parentWidth;
                }

                layoutParams.leftMargin = marginLeft;
                setLayoutParams(layoutParams);

                if (marginLeft <= hideLeft || marginRight >= hideRight) {
                    hide();
                } else {
                    isShow = true;
                }
                if (offsetListener != null) {
                    offsetListener.onCircleOffset(marginLeft, marginRight);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (marginLeft == originalLeft) {
                    hide();
                    if (offsetListener != null) {
                        offsetListener.onCircleReset();
                    }
                    return true;
                }

                if (marginLeft > hideLeft && marginLeft < originalLeft) {
                    int current = originalLeft - marginLeft;
                    int total = originalLeft - hideLeft;
                    int duration = (int) (250 * ((float) current / total));
                    startResetAnimation(marginLeft, originalLeft, duration);
                    return true;
                }

                if (marginRight > originalRight && marginRight < hideRight) {
                    int current = marginRight - originalRight;
                    int total = hideRight - originalRight;
                    int duration = (int) (250 * ((float) current / total));
                    startResetAnimation(marginLeft, originalLeft, duration);
                    return true;
                }

                if (marginLeft <= hideLeft) {
                    if (offsetListener != null) {
                        offsetListener.onHangup();
                    }
                    return true;
                }

                if (marginRight >= hideRight) {
                    if (offsetListener != null) {
                        offsetListener.onAnswer();
                    }
                    return true;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void startResetAnimation(int startValue, int endValue, int duration) {
        setClickable(false);
        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                layoutParams.leftMargin = value;
                setLayoutParams(layoutParams);
                if (offsetListener != null) {
                    offsetListener.onCircleOffset(value, value + width);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hide();
                setClickable(true);
                if (offsetListener != null) {
                    offsetListener.onCircleReset();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    private void show() {
        isShow = true;
        invalidate();
    }

    private void hide() {
        isShow = false;
        invalidate();
    }

    public interface OnCircleOffsetListener {

        void onCircleOffset(int left, int right);

        void onCircleDown();

        void onCircleReset();

        void onHangup();

        void onAnswer();
    }

}
