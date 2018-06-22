package com.github.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/6/21.
 */

public class MyProgress extends View{
    private float viewWidth=300;
    private float viewHeight=20;
    private int bgColor;
    private int borderColor;
    private float borderWidth=4;
    private int progressColor;
    private int allInterval=0;
    private int leftInterval;
    private int topInterval;
    private int rightInterval;
    private int bottomInterval;
    private boolean showAnimation=true;
    private boolean isRound=true;
    private float radius=0;
    private float progress=30;
    private float max=100;
    private int angle=0;


    private final String def_borderColor="#239936";
    private final String def_progressColor=def_borderColor;



    private Paint bgPaint;
    private Paint borderPaint;
    private Paint progressPaint;

    public MyProgress(Context context) {
        super(context);
        init(null);
    }

    public MyProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        bgColor= Color.parseColor("#ffffff");
        borderColor= Color.parseColor(def_borderColor);
        progressColor= Color.parseColor(def_progressColor);

        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyProgress);

        viewWidth=  typedArray.getDimension(R.styleable.MyProgress_viewWidth,300);
        viewHeight=  typedArray.getDimension(R.styleable.MyProgress_viewHeight,20);
        bgColor=typedArray.getColor(R.styleable.MyProgress_bgColor,Color.parseColor("#ffffff"));
        borderColor=typedArray.getColor(R.styleable.MyProgress_borderColor,Color.parseColor(def_borderColor));
        borderWidth=  typedArray.getDimension(R.styleable.MyProgress_borderWidth,1);
        progressColor=typedArray.getColor(R.styleable.MyProgress_progressColor,borderColor);
        allInterval=(int)typedArray.getDimension(R.styleable.MyProgress_allInterval,0);
        leftInterval=(int)typedArray.getDimension(R.styleable.MyProgress_leftInterval,0);
        topInterval=(int)typedArray.getDimension(R.styleable.MyProgress_topInterval,0);
        rightInterval=(int)typedArray.getDimension(R.styleable.MyProgress_rightInterval,0);
        bottomInterval=(int)typedArray.getDimension(R.styleable.MyProgress_bottomInterval,0);
        showAnimation=typedArray.getBoolean(R.styleable.MyProgress_showAnimation,true);
        isRound=typedArray.getBoolean(R.styleable.MyProgress_isRound,true);
        radius=typedArray.getDimension(R.styleable.MyProgress_radius,0);
        max=typedArray.getFloat(R.styleable.MyProgress_max,100);
        progress=typedArray.getFloat(R.styleable.MyProgress_progress,30);
        angle=typedArray.getInt(R.styleable.MyProgress_angle,0);

        typedArray.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = (int) (viewWidth+borderWidth);
        int mHeight = (int) (viewHeight+borderWidth);
        if(getLayoutParams().width== ViewGroup.LayoutParams.WRAP_CONTENT&&getLayoutParams().height==ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,mHeight);
        }else if(getLayoutParams().width== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,heightSize);
        }else if(getLayoutParams().height== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(widthSize,mHeight);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        initPaint();
    }

    private void initPaint() {
        borderPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);


        bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);


        progressPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(borderColor);
        progressPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scaleAngle=angle%360;
        canvas.translate(getWidth()/2,getHeight()/2);
        if(scaleAngle>0){
            canvas.rotate(scaleAngle);
        }
        drawBg(canvas);
        drawBorder(canvas);
        drawProgress(canvas);
    }
    private void drawBorder(Canvas canvas) {
        RectF rectF=new RectF(-viewWidth/2,-viewHeight/2, viewWidth/2, viewHeight /2);

        if(isRound){
            if(radius>0){
                canvas.drawRoundRect(rectF, radius,radius,borderPaint);
            }else{
                canvas.drawRoundRect(rectF, viewHeight /2, viewHeight /2,borderPaint);
            }
        }else{
            canvas.drawRect(rectF,borderPaint);
        }
    }
    private void drawBg(Canvas canvas) {
        RectF rectF=new RectF(-viewWidth/2,-viewHeight/2, viewWidth/2, viewHeight /2);
        if(isRound){
            if(radius>0){
                canvas.drawRoundRect(rectF, radius,radius,bgPaint);
            }else{
                canvas.drawRoundRect(rectF, viewHeight /2, viewHeight /2,bgPaint);
            }
        }else{
            canvas.drawRect(rectF,bgPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        float progressWidth=viewWidth;
        float progressHeight=viewHeight;
        float leftOffset=leftInterval;
        float topOffset=topInterval;
        float rightOffset=rightInterval;
        float bottomOffset=bottomInterval;
        if(allInterval>0){
            progressWidth=viewWidth-allInterval*2;
            progressHeight=viewHeight-allInterval*2;
            leftOffset=allInterval;
            topOffset=allInterval;
            rightOffset=allInterval;
            bottomOffset=allInterval;
        }else{
            progressWidth=viewWidth-leftOffset-rightOffset;
            progressHeight=viewHeight-topInterval-bottomInterval;
        }

        RectF rectF=new RectF(-viewWidth/2+leftOffset,-viewHeight/2+topOffset, (progressWidth*progress/max-viewWidth/2+leftOffset), viewHeight /2-bottomOffset);

        if(isRound){
            if(radius>0){
                canvas.drawRoundRect(rectF,radius, radius,progressPaint);
            }else{
                canvas.drawRoundRect(rectF, progressHeight /2, progressHeight /2,progressPaint);
            }
        }else{
            canvas.drawRect(rectF,progressPaint);
        }
    }



    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        this.borderColor = borderColor;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor( int progressColor) {
        this.progressColor = progressColor;
    }

    public int getAllInterval() {
        return allInterval;
    }

    public void setAllInterval(int allInterval) {
        this.allInterval = allInterval;
    }

    public int getLeftInterval() {
        return leftInterval;
    }

    public void setLeftInterval(int leftInterval) {
        this.leftInterval = leftInterval;
    }

    public int getTopInterval() {
        return topInterval;
    }

    public void setTopInterval(int topInterval) {
        this.topInterval = topInterval;
    }

    public int getRightInterval() {
        return rightInterval;
    }

    public void setRightInterval(int rightInterval) {
        this.rightInterval = rightInterval;
    }

    public int getBottomInterval() {
        return bottomInterval;
    }

    public void setBottomInterval(int bottomInterval) {
        this.bottomInterval = bottomInterval;
    }

    public boolean isShowAnimation() {
        return showAnimation;
    }

    public void setShowAnimation(boolean showAnimation) {
        this.showAnimation = showAnimation;
    }

    public boolean isRound() {
        return isRound;
    }

    public void setRound(boolean round) {
        isRound = round;
    }

    public float getMax() {
        return max;
    }
    public void complete(){
        invalidate();
    }
    public void setMax(float max) {
        this.max = max;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(float viewWidth) {
        this.viewWidth = viewWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(float viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
