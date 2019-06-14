package com.test.progress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.progress.MyProgress;

public class TestBitmapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bitmap);

        MyProgress myProgress=findViewById(R.id.myProgress);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.simple_bitmap);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() - 11);
        Shader shader=new BitmapShader(newBitmap,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
        myProgress.setProgressShader(shader);


        Bitmap bitmapBG = BitmapFactory.decodeResource(getResources(), R.drawable.gray_progress);
        Bitmap newBitmapBG = Bitmap.createBitmap(bitmapBG, 0, 0, bitmapBG.getWidth(), bitmapBG.getHeight() - 9);
        Shader shaderBG=new BitmapShader(newBitmapBG,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
        myProgress.setBgShader(shaderBG);

        myProgress.setMaxProgress(200).setNowProgress(100).setRadius(80).complete();
    }
}
