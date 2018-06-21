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
    private float progress=30;
    private float max=100;
    private int angle=0;


    private final String def_borderColor="#239936";
    private final String def_progressColor=def_borderColor;

    private float borderViewWidth;
    private float borderViewHeight;

    private float progressViewWidth;
    private float progressViewHeight;

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
        int mWidth =400+getPaddingLeft() + getPaddingRight();
        int mHeight = 20+getPaddingTop()+getPaddingBottom();
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
        borderViewWidth = w - getPaddingLeft() - getPaddingRight()-borderWidth;
        borderViewHeight = h-getPaddingTop()-getPaddingBottom()-borderWidth;

        initPaint();
    }

    private void initPaint() {
        borderPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);


        bgPaint  =new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);


        progressPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(borderColor);
        progressPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getPaddingLeft()+borderWidth/2,getHeight()/2);
        drawBg(canvas);
        drawBorder(canvas);
        drawProgress(canvas);
    }

    private void drawBg(Canvas canvas) {
        RectF rectF=new RectF(0,-borderViewHeight /2, borderViewWidth, borderViewHeight /2);
        if(isRound){
            canvas.drawRoundRect(rectF, borderViewHeight /2, borderViewHeight /2,bgPaint);
        }else{
            canvas.drawRect(rectF,bgPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        float progressWidth=borderViewWidth;
        float progressHeight=borderViewHeight;
        float leftOffset=leftInterval;
        float topOffset=topInterval;
        float rightOffset=rightInterval;
        float bottomOffset=bottomInterval;
        if(allInterval>0){
            progressWidth=borderViewWidth-allInterval*2;
            progressHeight=borderViewHeight-allInterval*2;
            leftOffset=allInterval;
            topOffset=allInterval;
            rightOffset=allInterval;
            bottomOffset=allInterval;
        }else{
            progressWidth=borderViewWidth-leftInterval-rightInterval;
            progressHeight=borderViewHeight-topInterval-bottomInterval;
        }

        RectF rectF=new RectF(leftOffset,-borderViewHeight /2+topOffset,(borderViewWidth-rightOffset)*progress/max, borderViewHeight /2-bottomOffset);

        canvas.save();
        if(isRound){
            canvas.drawRoundRect(rectF, progressHeight /2, progressHeight /2,progressPaint);
        }else{
            canvas.drawRect(rectF,progressPaint);
        }
        canvas.restore();
    }

    private void drawBorder(Canvas canvas) {
        RectF rectF=new RectF(0,-borderViewHeight /2, borderViewWidth, borderViewHeight /2);

        if(isRound){
            canvas.drawRoundRect(rectF, borderViewHeight /2, borderViewHeight /2,borderPaint);
        }else{
            canvas.drawRect(rectF,borderPaint);
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

    public void setProgressColor(@ColorInt int progressColor) {
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

}
