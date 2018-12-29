package com.yscall.slide.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscall.slide.R;
import com.yscall.slide.utils.CommonUtils;

/**
 * Created by 你的样子 on 2018/12/29.
 * 弹出按钮
 *
 * @author gerile
 */

public class HomeMakeDialog extends Dialog {

    private HomeMakeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        private HomeMakeDialog dialog;

        private Context context;
        private Handler handler;
        private View layout;
        private ImageView makeVideoIcon;
        private TextView makeVideoText;
        private ImageView makeRingIcon;
        private TextView makeRingText;

        private View.OnClickListener makeVideoOnClickListener;
        private View.OnClickListener makeRingOnClickListener;

        private final int UP_TIME = 450;
        private final int DOWN_TIME = 400;

        private boolean t1 = false;
        private boolean t2 = false;

        public Builder(Context context) {
            this.context = context;
            handler = new Handler();
        }

        public Builder setMakeVideoOnClickListener(View.OnClickListener makeVideoOnClickListener) {
            this.makeVideoOnClickListener = makeVideoOnClickListener;
            return this;
        }

        public Builder setMakeRingOnClickListener(View.OnClickListener makeRingOnClickListener) {
            this.makeRingOnClickListener = makeRingOnClickListener;
            return this;
        }

        public HomeMakeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dialog = new HomeMakeDialog(context, R.style.Dialog);
            layout = inflater.inflate(R.layout.dialog_home_make, null);
            makeVideoIcon = (ImageView) layout.findViewById(R.id.make_video_icon);
            makeVideoText = (TextView) layout.findViewById(R.id.make_video_text);
            makeRingIcon = (ImageView) layout.findViewById(R.id.make_ring_icon);
            makeRingText = (TextView) layout.findViewById(R.id.make_ring_text);
            makeVideoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (makeVideoOnClickListener != null) {
                        makeVideoOnClickListener.onClick(v);
                    }
                    close();
                }
            });
            makeRingIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (makeRingOnClickListener != null) {
                        makeRingOnClickListener.onClick(v);
                    }
                    close();
                }
            });
            t1 = false;
            t2 = false;

            Window window = dialog.getWindow();
            if (window != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //5.0 全透明实现
                    dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                } else {
                    //4.4 全透明状态栏
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    }
                }
            }

            dialog.setCanceledOnTouchOutside(false);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            startAnimation();
            return dialog;
        }

        public void startAnimation() {
            ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(layout, "alpha", 0.0f, 1.0f);
            backgroundAnimator.setDuration(UP_TIME);
            backgroundAnimator.start();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("translationY", CommonUtils.dip2px(context, 244), 0);
                    PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("alpha", 0.0f, 0.1f, 0.3f, 0.7f, 1.0f);
                    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(makeVideoIcon, pvh1, pvh2);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (!t1 && (float) animation.getAnimatedValue("translationY") < 150) {
                                t1 = true;
                                AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
                                alphaAnimation.setDuration(200);
                                makeVideoText.startAnimation(alphaAnimation);
                                makeVideoText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            makeVideoIcon.setVisibility(View.VISIBLE);
                        }
                    });
                    animator.setInterpolator(new OvershootInterpolator());
                    animator.setDuration(UP_TIME);
                    animator.start();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("translationY", CommonUtils.dip2px(context, 244), 0);
                            PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("alpha", 0.0f, 0.1f, 0.3f, 0.7f, 1.0f);
                            ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(makeRingIcon, pvh1, pvh2);
                            animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    if (!t2 && (float) animation.getAnimatedValue("translationY") < 150) {
                                        t2 = true;
                                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
                                        alphaAnimation.setDuration(200);
                                        makeRingText.startAnimation(alphaAnimation);
                                        makeRingText.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            animator2.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    makeRingIcon.setVisibility(View.VISIBLE);
                                }
                            });
                            animator2.setInterpolator(new OvershootInterpolator());
                            animator2.setDuration(UP_TIME);
                            animator2.start();
                        }
                    }, 45);
                }
            }, 100);
        }

        public void cancel() {
            ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(layout, "alpha", 1.0f, 0.0f);
            backgroundAnimator.setDuration(DOWN_TIME);
            backgroundAnimator.start();

            PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("translationY", 0, CommonUtils.dip2px(context, 212));
            PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
            ObjectAnimator animator1 = ObjectAnimator.ofPropertyValuesHolder(makeVideoIcon, pvh1, pvh2);
            animator1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    makeRingText.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    makeRingIcon.setVisibility(View.GONE);
                }
            });
            animator1.setDuration(DOWN_TIME);
            animator1.start();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("translationY", 0, CommonUtils.dip2px(context, 212));
                    PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
                    ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(makeRingIcon, pvh1, pvh2);
                    animator2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            makeVideoText.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            makeVideoIcon.setVisibility(View.GONE);
                            close();
                        }
                    });
                    animator2.setInterpolator(new OvershootInterpolator());
                    animator2.setDuration(UP_TIME);
                    animator2.start();
                }
            }, 45);
        }

        public void close() {
            if (dialog != null) {
                dialog.cancel();
            }
        }
    }
}
