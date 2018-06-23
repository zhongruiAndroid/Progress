package com.test.progress;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.progress.MyProgress;

public class MainActivity extends AppCompatActivity {
    SeekBar seekbar;
    MyProgress mp;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekbar= (SeekBar) findViewById(R.id.seekbar);
        mp= (MyProgress) findViewById(R.id.mp);
        mp.setOnProgressInter(new MyProgress.OnProgressInter() {
            @Override
            public void progress(float scaleProgress, float progress, float max) {

                Log.i("======","===" + scaleProgress + "===" + progress+"=="+max);
            }
        });
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.setProgress(0,false);
            }
        });
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.setProgress(100,false);
                Log.i("","===" + mp.getViewWidth() + "===" + mp.getViewHeight());
                LinearGradient linearGradient = new LinearGradient(-mp.getViewWidth()/2,-mp.getViewHeight()/2,mp.getViewWidth()/2,mp.getViewHeight()/2,
                        new int[]{ContextCompat.getColor(MainActivity.this,R.color.green),
                                ContextCompat.getColor(MainActivity.this,R.color.blue)},
                        new float[]{0f,1},
                        Shader.TileMode.MIRROR);
                mp.setProgressShader(linearGradient);
                mp.complete();
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mp.setProgress(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
