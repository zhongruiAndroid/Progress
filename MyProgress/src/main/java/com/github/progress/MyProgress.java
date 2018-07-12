package com.github.progress;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


/**
 * Created by Administrator on 2018/6/21.
 */

public class MyProgress extends View{
    private OnProgressInter onProgressInter;
    public interface OnProgressInter {
        void progress(float scaleProgress,float progress, float max);
    }
    public OnProgressInter getOnProgressInter() {
        return onProgressInter;
    }
    public void setOnProgressInter(OnProgressInter onProgressInter) {
        this.onProgressInter = onProgressInter;
    }
    private void setProgressToInter(float scaleProgress,float progress, float max) {
        if(onProgressInter !=null){
            onProgressInter.progress(scaleProgress,progress,max);
        }
    }
    private float viewWidth;
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
    private boolean useAnimation =true;
    private float radius=0;
    private float nowProgress =30;
    //用于动画计算
    private float scaleProgress= nowProgress;
    private float maxProgress =100;
    private int angle=0;
    private int duration=1200;
    private Shader borderShader;
    private Shader bgShader;
    private Shader progressShader;


    private final String def_borderColor="#239936";
    private final String def_bgColor="#00000000";
    private final String def_progressColor=def_borderColor;

    private TimeInterpolator interpolator =new DecelerateInterpolator();

    private Paint bgPaint;
    private Paint borderPaint;
    private Paint progressPaint;
    private Path progressPath;
    private Path resultPath ;

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
        bgColor= Color.parseColor(def_bgColor);
        borderColor= Color.parseColor(def_borderColor);
        progressColor= Color.parseColor(def_progressColor);

        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyProgress);

        viewWidth=  typedArray.getDimension(R.styleable.MyProgress_viewWidth,0);
        viewHeight=  typedArray.getDimension(R.styleable.MyProgress_viewHeight,0);
        bgColor=typedArray.getColor(R.styleable.MyProgress_bgColor,Color.parseColor(def_bgColor));
        borderColor=typedArray.getColor(R.styleable.MyProgress_borderColor,Color.parseColor(def_borderColor));
        borderWidth=  typedArray.getDimension(R.styleable.MyProgress_borderWidth,4);
        progressColor=typedArray.getColor(R.styleable.MyProgress_progressColor,borderColor);
        allInterval=(int)typedArray.getDimension(R.styleable.MyProgress_allInterval,0);
        leftInterval=(int)typedArray.getDimension(R.styleable.MyProgress_leftInterval,0);
        topInterval=(int)typedArray.getDimension(R.styleable.MyProgress_topInterval,0);
        rightInterval=(int)typedArray.getDimension(R.styleable.MyProgress_rightInterval,0);
        bottomInterval=(int)typedArray.getDimension(R.styleable.MyProgress_bottomInterval,0);
        useAnimation =typedArray.getBoolean(R.styleable.MyProgress_useAnimation,true);
        radius=typedArray.getDimension(R.styleable.MyProgress_radius,0);
        maxProgress =typedArray.getFloat(R.styleable.MyProgress_maxProgress,100);
        nowProgress =typedArray.getFloat(R.styleable.MyProgress_nowProgress,30);
        angle=typedArray.getInt(R.styleable.MyProgress_angle,0);
        duration=typedArray.getInt(R.styleable.MyProgress_duration,1200);


        if(maxProgress<=0){
            this.maxProgress=0;
        }
        if(nowProgress> maxProgress){
            this.nowProgress = maxProgress;
        }else if(nowProgress<0){
            this.nowProgress =0;
        }

        scaleProgress= nowProgress;
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
        if(viewWidth+borderWidth>mWidth){
            mWidth= (int) (viewWidth+borderWidth);
        }
        if(viewHeight+borderWidth>mHeight){
            mHeight= (int) (viewHeight+borderWidth);
        }
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
        }else if(viewHeight==0){
            viewHeight=getHeight()-borderWidth;
        }else if(viewWidth==0){
            viewWidth=getWidth()-borderWidth;
        }
        radius=viewHeight/2;
        progressPath=new Path();
        resultPath  =new Path();
        initPaint();
    }

    private void initPaint() {
        borderPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);

        bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);


        progressPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);

        bgPaint.setColor(bgColor);

        progressPaint.setColor(progressColor);


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
        if(borderShader!=null){
            borderPaint.setShader(borderShader);
        }else{
            borderPaint.setShader(null);
        }
        if(radius>0){
//            if(radius>0){
                canvas.drawRoundRect(rectF, radius,radius,borderPaint);
//            }else{
//                canvas.drawRoundRect(rectF, viewHeight /2, viewHeight /2,borderPaint);
//            }
        }else{
            canvas.drawRect(rectF,borderPaint);
        }
    }
    private void drawBg(Canvas canvas) {
        if(bgShader!=null){
            bgPaint.setShader(bgShader);
        }else{
            bgPaint.setShader(null);
        }

        RectF rectF=new RectF(-viewWidth/2,-viewHeight/2, viewWidth/2, viewHeight /2);
        resultPath.reset();
        if(radius>0){
//            if(radius>0){
                canvas.drawRoundRect(rectF, radius,radius,bgPaint);
                resultPath.addRoundRect(rectF,radius, radius, Path.Direction.CW);
//            }else{
//                canvas.drawRoundRect(rectF, viewHeight /2, viewHeight /2,bgPaint);
//                resultPath.addRoundRect(rectF,viewHeight /2, viewHeight /2, Path.Direction.CW);
//            }
        }else{
            resultPath.addRect(rectF, Path.Direction.CW);
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

        if(progressShader!=null){
            progressPaint.setShader(progressShader);
        }else{
            progressPaint.setShader(null);
        }


        RectF rectF;
        if(maxProgress<=0){
            rectF=new RectF(-viewWidth/2+leftOffset,-viewHeight/2+topOffset,-viewWidth/2+leftOffset, viewHeight /2-bottomOffset);
        }else{
            rectF=new RectF(-viewWidth/2+leftOffset,-viewHeight/2+topOffset, (progressWidth*scaleProgress/ maxProgress -viewWidth/2+leftOffset), viewHeight /2-bottomOffset);
        }

        progressPath.reset();

        if(radius>0){
//            if(radius>0){
// //               canvas.drawRoundRect(rectF,radius, radius,progressPaint);
                progressPath.addRoundRect(rectF,radius, radius, Path.Direction.CW);
//            }else{
// //               canvas.drawRoundRect(rectF, progressHeight /2, progressHeight /2,progressPaint);
//                progressPath.addRoundRect(rectF, progressHeight /2, progressHeight /2, Path.Direction.CW);
//            }
        }else{
// //            canvas.drawRect(rectF,progressPaint);
            progressPath.addRect(rectF, Path.Direction.CW);
        }

        progressPath.op(resultPath, Path.Op.INTERSECT);
        canvas.drawPath(progressPath,progressPaint);
    }



    public int getBorderColor() {
        return borderColor;
    }

    public MyProgress setBorderColor(@ColorInt int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public MyProgress setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public MyProgress setProgressColor( int progressColor) {
        this.progressColor = progressColor;
        this.progressShader=null;
        return this;
    }

    public int getAllInterval() {
        return allInterval;
    }

    public MyProgress setAllInterval(int allInterval) {
        this.allInterval = allInterval;
        return this;
    }

    public int getLeftInterval() {
        return leftInterval;
    }

    public MyProgress setLeftInterval(int leftInterval) {
        this.leftInterval = leftInterval;
        return this;
    }

    public int getTopInterval() {
        return topInterval;
    }

    public MyProgress setTopInterval(int topInterval) {
        this.topInterval = topInterval;
        return this;
    }

    public int getRightInterval() {
        return rightInterval;
    }

    public MyProgress setRightInterval(int rightInterval) {
        this.rightInterval = rightInterval;
        return this;
    }

    public int getBottomInterval() {
        return bottomInterval;
    }

    public MyProgress setBottomInterval(int bottomInterval) {
        this.bottomInterval = bottomInterval;
        return this;
    }

    public boolean isUseAnimation() {
        return useAnimation;
    }

    public MyProgress setUseAnimation(boolean useAnimation) {
        this.useAnimation = useAnimation;
        return this;
    }

    public float getMaxProgress() {
        return maxProgress;
    }
    public void complete(){
        invalidate();
    }
    public MyProgress setMaxProgress(float maxProgress) {
        if(maxProgress<=0){
            this.maxProgress=0;
        }else{
            this.maxProgress = maxProgress;
        }
        return this;
    }

    public float getNowProgress() {
        return nowProgress;
    }

    public MyProgress setNowProgress(float progress) {
        return setNowProgress(progress, useAnimation);
    }
    public MyProgress setNowProgress(float progress, boolean useAnimation) {
        float beforeProgress=this.nowProgress;
        if(progress> maxProgress){
            this.nowProgress = maxProgress;
        }else if(progress<0){
            this.nowProgress =0;
        }else{
            this.nowProgress = progress;
        }
        if(useAnimation){
            ValueAnimator valueAnimator=ValueAnimator.ofFloat(beforeProgress,this.nowProgress);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    MyProgress.this.scaleProgress= (float) animation.getAnimatedValue();
                    invalidate();
                    setProgressToInter(MyProgress.this.scaleProgress,MyProgress.this.nowProgress,MyProgress.this.maxProgress);
                }
            });
            valueAnimator.setInterpolator(interpolator);
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }else{
            MyProgress.this.scaleProgress=this.nowProgress;
            invalidate();
            setProgressToInter(MyProgress.this.scaleProgress,this.nowProgress,this.maxProgress);
        }
        return this;
    }

    public int getAngle() {
        return angle;
    }

    public MyProgress setAngle(int angle) {
        this.angle = angle;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public MyProgress setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public MyProgress setViewWidth(float viewWidth) {
        this.viewWidth = viewWidth;
        return this;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public MyProgress setViewHeight(float viewHeight) {
        this.viewHeight = viewHeight;
        return this;
    }

    public int getBgColor() {
        return bgColor;
    }

    public MyProgress setBgColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public float getRadius() {
        return radius;
    }

    public MyProgress setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public MyProgress setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public Shader getBorderShader() {
        return borderShader;
    }

    public MyProgress setBorderShader(Shader borderShader) {
        this.borderShader = borderShader;
        return this;
    }

    public Shader getBgShader() {
        return bgShader;
    }

    public MyProgress setBgShader(Shader bgShader) {
        this.bgShader = bgShader;
        return this;
    }

    public Shader getProgressShader() {
        return progressShader;
    }

    public MyProgress setProgressShader(Shader progressShader) {
        this.progressShader = progressShader;
        return this;
    }
}
