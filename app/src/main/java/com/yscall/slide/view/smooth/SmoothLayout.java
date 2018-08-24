package com.yscall.slide.view.smooth;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.yscall.slide.R;
import com.yscall.slide.utils.CommonUtils;


/**
 * Created by 你的样子 on 2018/8/22.
 * 来电滑动接听挂断
 *
 * @author gerile
 */

public class SmoothLayout extends RelativeLayout implements CircleView.OnCircleOffsetListener {

    private OnCallOperateListener operateListener;

    private CircleView circle;
    private CircleImageView hangup, answer;
    private ImageView gif;

    private int margin;
    private int hideLeft;
    private int hideRight;

    public SmoothLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_smooth, this);
        circle = findViewById(R.id.smooth_circle);
        circle.setOnOffsetListener(this);
        hangup = findViewById(R.id.smooth_hangup);
        answer = findViewById(R.id.smooth_answer);
        gif = findViewById(R.id.smooth_gif);
        margin = CommonUtils.dip2px(context, 28);
        Glide.with(context).load(R.drawable.anim_25).into(gif);
    }

    public void setOnCallOperateListener(OnCallOperateListener operateListener) {
        this.operateListener = operateListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LayoutParams hangupParams = (LayoutParams) hangup.getLayoutParams();
        hangupParams.leftMargin = margin;
        hangup.setLayoutParams(hangupParams);

        LayoutParams answerParams = (LayoutParams) answer.getLayoutParams();
        answerParams.rightMargin = margin;
        answer.setLayoutParams(answerParams);

        hideLeft = margin;
        hideRight = getWidth() - margin;

        circle.setHideLeft(hideLeft);
        circle.setHideRight(hideRight);
    }

    @Override
    public void onCircleOffset(int left, int right) {
        if (left > hideLeft && left < circle.getOriginalLeft()) {
            int current = left - hideLeft;
            int total = circle.getOriginalLeft() - hideLeft;
            answer.setAlpha((int) (((float) current / total) * 255));
            hangup.setAlpha(255);
        }

        if (right > circle.getOriginalRight() && right < hideRight) {
            int current = right - circle.getOriginalRight();
            int total = hideRight - circle.getOriginalRight();
            hangup.setAlpha(255 - (int) (((float) current / total) * 255));
            answer.setAlpha(255);
        }

        if (left <= hideLeft) {
            hangup.showCircle(Color.parseColor("#F2184B"));
        } else {
            hangup.hideCircle();
        }

        if (right >= hideRight) {
            answer.showCircle(Color.parseColor("#0CE593"));
        } else {
            answer.hideCircle();
        }
    }

    @Override
    public void onCircleDown() {
        gif.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCircleReset() {
        gif.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHangup() {
        answer.setAlpha(0);
        if (operateListener != null) {
            operateListener.onHangup();
        }
    }

    @Override
    public void onAnswer() {
        hangup.setAlpha(0);
        if (operateListener != null) {
            operateListener.onAnswer();
        }
    }

    public interface OnCallOperateListener {
        void onHangup();

        void onAnswer();
    }
}
