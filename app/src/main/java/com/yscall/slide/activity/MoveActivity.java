package com.yscall.slide.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yscall.slide.R;

/**
 * Created by 你的样子 on 2018/8/24.
 * 可随意移动
 * @author gerile
 */

public class MoveActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
    }
}
