package com.yscall.slide.view.slide;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by Develop_John on 2017/6/2.
 * 上滑控件
 *
 * @author gerile
 */
public class SlideLayout extends RelativeLayout implements GestureDetector.OnGestureListener {

    Context context;
    OnSlideListener listener;
    GestureDetector gestureDetector;
    MarginLayoutParams marginParams;

    /**
     * view的高度
     */
    int height = 0;

    /**
     * 点击的位置
     */
    int lastY = 0;

    /**
     * 手指相对控件滑动距离
     */
    int offY = 0;

    /**
     * 距父布局顶部距离
     */
    int top = 0;

    /**
     * 距父布局底部距离
     */
    int bottom = 0;

    /**
     * 动画位移距离
     */
//    int left = 0;
//    int right = 0;
    int toYDelta = 0;

    /**
     * 上下位移动画分界线
     */
    int moveDivide = 4;

    /**
     * 快速滑动距离
     */
    int fastOffset = 25;

    /**
     * 向上快速滑动
     */
    boolean isFastSliding = false;

    public SlideLayout(Context context) {
        super(context);
        this.context = context;
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //设置为可点击
        setClickable(true);
        //初始化手势类，同时设置手势监听
        gestureDetector = new GestureDetector(context, this);
    }

    //view.getLeft()表示的是view左侧以其父View的左上角为原点的水平坐标位置
    //view.getRight()表示的view右侧以其父View的左上角为原点的水平坐标位置
    //view.getTop()表示的是view顶部以父View的左上角为原点的垂直坐标位置
    //view.getBottom()表示的是view底部以父View的左上角为原点的垂直坐标位置
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureHeight(heightMeasureSpec);
    }

    private void measureHeight(int measureSpec) {
        marginParams = (MarginLayoutParams) getLayoutParams();
        height = MeasureSpec.getSize(measureSpec);
//        left = getLeft();
//        right = getRight();
    }

    public void setListener(OnSlideListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
        if (Math.abs(velocityY) > 200) {
            //速度超过200px/s
            isFastSliding = true;
        }
        return false;
    }

    //移动控件时尽量避免使用layout();不会真正记录改变的位置
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 从点击位置开始滑动距离 -为上 +为下
                int y = (int) event.getY();
                offY = y - lastY;
                top = getTop() + offY;
                bottom = getBottom() + offY;
                if (bottom > height) {
                    top = 0;
                    bottom = height;
                }
                marginParams.topMargin = top;
                marginParams.bottomMargin = -top;
                setLayoutParams(marginParams);
                if (listener != null) {
                    listener.onOffset(-top);
                }
                //    layout(left, top, right, bottom);
                break;
            case MotionEvent.ACTION_UP:
                //    top = getTop();
                int marginTop;
                int duration;
                boolean isUp;

                if (-top > height / moveDivide) {
                    if (offY > fastOffset) {
                        //手势操作向下正常滑动超出25px 开始向下位移动画
                        marginTop = 0;
                        toYDelta = -top;
                        duration = (int) (toYDelta / 4.5f);
                        isUp = false;
                    } else {
                        //开始向上位移动画
                        marginTop = -height - getStatusBarHeight();
                        toYDelta = height + top;
                        duration = (int) (toYDelta / 5.5f);
                        isUp = true;
                    }
                } else {
                    if (isFastSliding && offY < -fastOffset) {
                        //手势操作向上快速滑动超出25px 开始向上位移动画
                        marginTop = -height - getStatusBarHeight();
                        toYDelta = height + top;
                        duration = (int) (toYDelta / 5.5f);
                        isUp = true;
                    } else {
                        //开始向下位移动画
                        marginTop = 0;
                        toYDelta = -top;
                        duration = toYDelta;
                        isUp = false;
                    }
                }

                moveAnimation(isUp, top, marginTop, duration);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void moveAnimation(final boolean direction, int startValue, int endValue, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int marginTop = (int) animation.getAnimatedValue();
                marginParams.topMargin = marginTop;
                marginParams.bottomMargin = -marginTop;
                setLayoutParams(marginParams);
                if (listener != null) {
                    listener.onActionUp(marginTop);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setClickable(false);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setClickable(true);
                if (direction) {
                    if (listener != null) {
                        listener.onFinish();
                    }
                }
            }
        });
        animator.setDuration(duration);
        animator.start();

//                if(-top > height/6){
//                    toYDelta = -height-top;
//                    isUp = true;
//                }else {
//                    toYDelta = -top;
//                    isUp = false;
//                }
//                listener.onActionUp(toYDelta);
//                Animation animation = new android.view.animation.TranslateAnimation(
//                        0, 0, 0, toYDelta);
//                animation.setDuration((isUp ? Math.abs(toYDelta)/6 : Math.abs(toYDelta)/2));
//                animation.setFillAfter(true);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        if(isUp){
//                            listener.onFinish();
//                        }else {
//                            TranslateAnimation anim = new TranslateAnimation(0,0,0,0);
//                            setAnimation(anim);
//                            layout(left, 0, right, height);
//                        }
//                    }
//                });
//                startAnimation(animation);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    private int getStatusBarHeight() {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            return getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public interface OnSlideListener {
        void onOffset(int offset);

        void onActionUp(int offset);

        void onFinish();
    }

}
