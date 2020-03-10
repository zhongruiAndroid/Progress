package com.test.progress;

import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.progress.MyProgress;
import com.github.selectcolordialog.SelectColorDialog;
import com.github.selectcolordialog.SelectColorListener;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, OnClickListener {
    MyProgress mp;
    SeekBar sb_angle;
    SeekBar sb_round;
    SeekBar sb_left;
    SeekBar sb_top;
    SeekBar sb_right;
    SeekBar sb_bottom;
    Context context;

    Button btLookBitmapProgress;

    private SeekBar sbProgress;
    private SeekBar sbBorderWidth;
    private TextView tvBgColor;
    private TextView tvBorderColor;
    private TextView tvProgressColor;
    private CheckBox cbShowAnim;
    private SelectColorDialog selectColorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initView();

        btLookBitmapProgress = findViewById(R.id.btLookBitmapProgress);
        btLookBitmapProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestBitmapActivity.class));
            }
        });
        sb_angle = (SeekBar) findViewById(R.id.sb_angle);
        sb_round = (SeekBar) findViewById(R.id.sb_round);
        sb_left = (SeekBar) findViewById(R.id.sb_left);
        sb_top = (SeekBar) findViewById(R.id.sb_top);
        sb_right = (SeekBar) findViewById(R.id.sb_right);
        sb_bottom = (SeekBar) findViewById(R.id.sb_bottom);

        mp = (MyProgress) findViewById(R.id.mp);
        mp.setUseAnimation(cbShowAnim.isClickable());
        LinearGradient linearGradient = new LinearGradient(-mp.getViewWidth() / 2, -mp.getViewHeight() / 2, mp.getViewWidth() / 2, mp.getViewHeight() / 2,
                ContextCompat.getColor(MainActivity.this, R.color.green),
                ContextCompat.getColor(MainActivity.this, R.color.blue),
                Shader.TileMode.MIRROR);
        mp.setNowProgress(30).setMaxProgress(100).setProgressShader(linearGradient).setRadius(mp.getViewHeight() / 2).complete();
        mp.setBgColor(ContextCompat.getColor(context, R.color.white));
        mp.setOnProgressInter(new MyProgress.OnProgressInter() {
            @Override
            public void progress(float animProgress, float progress, float max) {
                Log.i("======", animProgress + "==" + progress + "===" + max);
            }
        });

        sb_round.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mp.setRadius(mp.getViewHeight() / 2 * progress / 100).complete();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sb_angle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mp.setAngle(progress * 360 / 100).complete();
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

    }

    @NonNull
    private SeekBar.OnSeekBarChangeListener getL(final int flag) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (flag) {
                    case 0:
                        mp.setLeftInterval(progress * dip2px(context, 10) / 100).complete();
                        break;
                    case 1:
                        mp.setTopInterval(progress * dip2px(context, 10) / 100).complete();
                        break;
                    case 2:
                        mp.setRightInterval(progress * dip2px(context, 10) / 100).complete();
                        break;
                    case 3:
                        mp.setBottomInterval(progress * dip2px(context, 10) / 100).complete();
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
        return (int) (dipValue * scale + 0.5F);
    }

    private void initView() {
        sbProgress = (SeekBar) findViewById(R.id.sbProgress);
        sbProgress.setOnSeekBarChangeListener(this);


        cbShowAnim = findViewById(R.id.cbShowAnim);
        cbShowAnim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mp.setUseAnimation(isChecked);
            }
        });

        sbBorderWidth = (SeekBar) findViewById(R.id.sbBorderWidth);
        sbBorderWidth.setOnSeekBarChangeListener(this);

        tvBgColor = (TextView) findViewById(R.id.tvBgColor);
        tvBgColor.setOnClickListener(this);

        tvBorderColor = (TextView) findViewById(R.id.tvBorderColor);
        tvBorderColor.setOnClickListener(this);

        tvProgressColor = (TextView) findViewById(R.id.tvProgressColor);
        tvProgressColor.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvProgressColor:
                selectColorDialog = new SelectColorDialog(this);
                selectColorDialog.setListener(new SelectColorListener() {
                    @Override
                    public void selectColor(int color) {
                        mp.setProgressColor(color).complete();
                        tvProgressColor.setBackgroundColor(color);
                    }
                });
                selectColorDialog.show();
                break;
            case R.id.tvBgColor:
                selectColorDialog = new SelectColorDialog(this);
                selectColorDialog.setListener(new SelectColorListener() {
                    @Override
                    public void selectColor(int color) {
                        mp.setBgColor(color).complete();
                        tvBgColor.setBackgroundColor(color);
                    }
                });
                selectColorDialog.show();
                break;
            case R.id.tvBorderColor:
                selectColorDialog = new SelectColorDialog(this);
                selectColorDialog.setListener(new SelectColorListener() {
                    @Override
                    public void selectColor(int color) {
                        mp.setBorderColor(color).complete();
                        tvBorderColor.setBackgroundColor(color);
                    }
                });
                selectColorDialog.show();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sbProgress:
                mp.setNowProgress(progress).complete();
                break;
            case R.id.sbBorderWidth:
                mp.setBorderWidth(progress / 3).complete();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
