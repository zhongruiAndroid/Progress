package com.test.progress;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.progress.MyProgress;

public class MainActivity extends AppCompatActivity {
    MyProgress mp;
    TextView tv_progress0, tv_progress1, tv_progress2;
    CheckBox cb_around;
    SeekBar sb_angle;
    SeekBar sb_round;
    SeekBar sb_left;
    SeekBar sb_top;
    SeekBar sb_right;
    SeekBar sb_bottom;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        cb_around = (CheckBox) findViewById(R.id.cb_around);
        sb_angle = (SeekBar) findViewById(R.id.sb_angle);
        sb_round = (SeekBar) findViewById(R.id.sb_round);
        sb_left = (SeekBar) findViewById(R.id.sb_left);
        sb_top = (SeekBar) findViewById(R.id.sb_top);
        sb_right = (SeekBar) findViewById(R.id.sb_right);
        sb_bottom = (SeekBar) findViewById(R.id.sb_bottom);

        mp = (MyProgress) findViewById(R.id.mp);
        LinearGradient linearGradient = new LinearGradient(-mp.getViewWidth() / 2, -mp.getViewHeight() / 2, mp.getViewWidth() / 2, mp.getViewHeight() / 2,
                ContextCompat.getColor(MainActivity.this, R.color.green),
                ContextCompat.getColor(MainActivity.this, R.color.blue),
                Shader.TileMode.MIRROR);
        mp.setProgressShader(linearGradient);
        mp.setBgColor(ContextCompat.getColor(context,R.color.white));

        sb_round.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(cb_around.isChecked()){
                    mp.setRadius(mp.getViewHeight()/2*progress/100).complete();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cb_around.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mp.setRound(isChecked).setRadius(mp.getViewHeight()/2*sb_round.getProgress()/100).complete();
            }
        });
        sb_angle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mp.setAngle(progress*360/100).complete();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sb_left.setOnSeekBarChangeListener(getL(0));
        sb_top.setOnSeekBarChangeListener(getL(1));
        sb_right.setOnSeekBarChangeListener(getL(2));
        sb_bottom.setOnSeekBarChangeListener(getL(3));


        findViewById(R.id.tv_progress0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.setProgress(0);
            }
        });
        findViewById(R.id.tv_progress1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.setProgress(50);
            }
        });
        findViewById(R.id.tv_progress2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.setProgress(100);
            }
        });

    }

    @NonNull
    private SeekBar.OnSeekBarChangeListener getL(final int flag) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (flag){
                    case 0:
                        mp.setLeftInterval(progress*dip2px(context,10)/100).complete();
                    break;
                    case 1:
                        mp.setTopInterval(progress*dip2px(context,10)/100).complete();
                    break;
                    case 2:
                        mp.setRightInterval(progress*dip2px(context,10)/100).complete();
                    break;
                    case 3:
                        mp.setBottomInterval(progress*dip2px(context,10)/100).complete();
                    break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    public int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

}
