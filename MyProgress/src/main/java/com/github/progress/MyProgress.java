package com.github.progress;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Administrator on 2018/6/21.
 */

public class MyProgress extends View{
    private OnProgressInter onProgressInter;

    public interface OnProgressInter {
        void progress(float progress, float max);
    }
    public OnProgressInter getOnProgressInter() {
        return onProgressInter;
    }
    public void setOnProgressInter(OnProgressInter onProgressInter) {
        this.onProgressInter = onProgressInter;
    }
    private void setNowProgress(float progress, float max) {
        if(onProgressInter !=null){
            onProgressInter.progress(progress,max);
        }
    }
    //进度条border长度
    private float viewWidth;
    //进度条border高度
    private float viewHeight;
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
    private float maxProgress =100;
    private int angle=0;
    private int duration=1000;

    //进度条长度
    private float progressFullWidth;
    //进度条高度
    private float progressFullHeight;

    private float progressNowWidth;


    private final String def_borderColor="#239936";
    private final String def_progressColor=def_borderColor;

    private TimeInterpolator interpolator =new DecelerateInterpolator();

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

        viewWidth=  typedArray.getDimension(R.styleable.MyProgress_viewWidth,0);
        viewHeight=  typedArray.getDimension(R.styleable.MyProgress_viewHeight,0);
        bgColor=typedArray.getColor(R.styleable.MyProgress_bgColor,Color.parseColor("#ffffff"));
        borderColor=typedArray.getColor(R.styleable.MyProgress_borderColor,Color.parseColor(def_borderColor));
        borderWidth=  typedArray.getDimension(R.styleable.MyProgress_borderWidth,4);
        progressColor=typedArray.getColor(R.styleable.MyProgress_progressColor,borderColor);
        allInterval=(int)typedArray.getDimension(R.styleable.MyProgress_allInterval,0);
        leftInterval=(int)typedArray.getDimension(R.styleable.MyProgress_leftInterval,0);
        topInterval=(int)typedArray.getDimension(R.styleable.MyProgress_topInterval,0);
        rightInterval=(int)typedArray.getDimension(R.styleable.MyProgress_rightInterval,0);
        bottomInterval=(int)typedArray.getDimension(R.styleable.MyProgress_bottomInterval,0);
        showAnimation=typedArray.getBoolean(R.styleable.MyProgress_showAnimation,true);
        isRound=typedArray.getBoolean(R.styleable.MyProgress_isRound,true);
        radius=typedArray.getDimension(R.styleable.MyProgress_radius,0);
        maxProgress =typedArray.getFloat(R.styleable.MyProgress_maxProgress,100);
        progress=typedArray.getFloat(R.styleable.MyProgress_progress,30);
        angle=typedArray.getInt(R.styleable.MyProgress_angle,0);
        duration=typedArray.getInt(R.styleable.MyProgress_duration,1000);


        typedArray.recycle();

    }
    private boolean isHorizontal(int angle){
        if(angle%180==0){
            return true;
        }else{
            return false;
        }
    }
    private boolean isVertical(int angle){
        if(isHorizontal(angle)){
            return false;
        }else{
            if(angle%90==0){
                return true;
            }else{
                return false;
            }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth =  400;
        int mHeight = 30;
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
        if(viewWidth==0&&viewHeight==0){
            if(isHorizontal(angle)){
                viewWidth=getWidth()-borderWidth;
                viewHeight=getHeight()-borderWidth;
            }else if(isVertical(angle)){
                viewWidth=getHeight()-borderWidth;
                viewHeight=getWidth()-borderWidth;
            }else{
                viewWidth=300;
                viewHeight=20;
            }
        }else if(viewWidth!=0){
            viewHeight=getHeight()-borderWidth;
        }else if(viewHeight!=0){
            viewWidth=getWidth()-borderWidth;
        }

        setProgressWidthAndHeight();

        initPaint();
    }

    //获取进度条宽高
    private void setProgressWidthAndHeight() {
        progressFullWidth = viewWidth;
        progressFullHeight = viewHeight;
        if(allInterval>0){
            progressFullWidth =viewWidth-allInterval*2;
            progressFullHeight =viewHeight-allInterval*2;
        }else{
            progressFullWidth =viewWidth-leftInterval-rightInterval;
            progressFullHeight =viewHeight-topInterval-bottomInterval;
        }
        progressNowWidth=progress*progressFullWidth/maxProgress;
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
        if(borderWidth>0){
            drawBorder(canvas);
        }
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
        float leftOffset=leftInterval;
        float topOffset=topInterval;
        float rightOffset=rightInterval;
        float bottomOffset=bottomInterval;
        if(allInterval>0){
            leftOffset=allInterval;
            topOffset=allInterval;
            rightOffset=allInterval;
            bottomOffset=allInterval;
        }
        //progressFullWidth*progress/ maxProgress
        RectF rectF=new RectF(-viewWidth/2+leftOffset,-viewHeight/2+topOffset, (progressNowWidth -viewWidth/2+leftOffset), viewHeight /2-bottomOffset);

        if(isRound){
            if(radius>0){
                canvas.drawRoundRect(rectF,radius, radius,progressPaint);
            }else{
                canvas.drawRoundRect(rectF, progressFullHeight /2, progressFullHeight /2,progressPaint);
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
        setProgressWidthAndHeight();
    }

    public int getLeftInterval() {
        return leftInterval;
    }

    public void setLeftInterval(int leftInterval) {
        this.leftInterval = leftInterval;
        setProgressWidthAndHeight();
    }

    public int getTopInterval() {
        return topInterval;
    }

    public void setTopInterval(int topInterval) {
        this.topInterval = topInterval;
        setProgressWidthAndHeight();
    }

    public int getRightInterval() {
        return rightInterval;
    }

    public void setRightInterval(int rightInterval) {
        this.rightInterval = rightInterval;
        setProgressWidthAndHeight();
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

    public float getMaxProgress() {
        return maxProgress;
    }
    public void complete(){
        invalidate();
    }
    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        setProgress(progress,showAnimation);
    }
    public void setProgress(float progress, boolean useAnimation) {
        float beforeProgress=this.progressNowWidth;
        if(progress> maxProgress){
            this.progress= maxProgress;
        }else if(progress<0){
            this.progress=0;
        }else{
            this.progress = progress;
        }
        if(useAnimation){
            float scaleWidth=progressFullWidth *progress/ maxProgress;
            ValueAnimator valueAnimator=ValueAnimator.ofFloat(beforeProgress,scaleWidth);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    MyProgress.this.progressNowWidth= (float) animation.getAnimatedValue();
                    MyProgress.this.progress=MyProgress.this.progressNowWidth/progressFullWidth*maxProgress;
                    Log.e("==",MyProgress.this.progress+"===scaleWidth=" + MyProgress.this.progressNowWidth);
                    invalidate();
                    setNowProgress(MyProgress.this.progress,MyProgress.this.maxProgress);
                }
            });
            valueAnimator.setInterpolator(interpolator);
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }else{
            invalidate();
            setNowProgress(this.progress,this.maxProgress);
        }
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }
}
