package com.yscall.slide.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yscall.slide.R;
import com.yscall.slide.view.slide.SlideLayout;

/**
 * Created by 你的样子 on 2018/8/20.
 * 滑动Activity
 * @author gerile
 */

public class SlideActivity extends Activity implements SlideLayout.OnSlideListener{

    private TextView titleTime;
    private TextView titleDate;
    private TextView titleWeather;
    private ImageView titleWeatherIcon;
    private RelativeLayout.MarginLayoutParams titleParams;

    private final int titleMoveRate = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        onImmersionStatusBar();
        titleTime = findViewById(R.id.title_time);
        titleDate = findViewById(R.id.title_date);
        titleWeather = findViewById(R.id.title_weather_text);
        titleWeatherIcon = findViewById(R.id.title_weather_icon);
        RelativeLayout titleLayout = findViewById(R.id.title_layout);
        titleParams = (RelativeLayout.MarginLayoutParams) titleLayout.getLayoutParams();
        SlideLayout slideLayout = findViewById(R.id.slide_layout);
        slideLayout.setListener(this);
    }

    @Override
    public void onOffset(int offset) {
        if (offset < 765) {
            int value = offset / titleMoveRate;
            titleParams.topMargin = -value;
            titleTime.setTextColor(Color.argb((255 - value), 250, 250, 250));
            titleDate.setTextColor(Color.argb((255 - value), 250, 250, 250));
            titleWeather.setTextColor(Color.argb((255 - value), 250, 250, 250));
            titleWeatherIcon.setAlpha(255 - value);
        }
    }

    @Override
    public void onActionUp(int offset) {
        int value = offset / titleMoveRate;
        titleParams.topMargin = value;
        titleTime.setTextColor(Color.argb((255 + value), 250, 250, 250));
        titleDate.setTextColor(Color.argb((255 + value), 250, 250, 250));
        titleWeather.setTextColor(Color.argb((255 + value), 250, 250, 250));
        titleWeatherIcon.setAlpha(255 + value);
    }

    @Override
    public void onFinish() {
        finish();
    }

    public void onImmersionStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            View view = findViewById(R.id.status_padding);
            view.getLayoutParams().height = getStatusBarHeight(this);
        }
    }

    private int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
