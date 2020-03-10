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
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


/**
 * Created by Administrator on 2018/6/21.
 */

public class MyProgress extends View{
    private OnProgressInter onProgressInter;
    private RectF borderRectF;
    private RectF progressRectF;

    public interface OnProgressInter {
        void progress(float animProgress,float progress, float max);
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
    private float radius;
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
    private float nowProgress =30;
    //用于动画计算
    private float scaleProgress= nowProgress;
    private float maxProgress =100;
    private int angle=0;
    private int duration=1200;



    private boolean noTopLeftRadius;
    private boolean noTopRightRadius;
    private boolean noBottomLeftRadius;
    private boolean noBottomRightRadius;

    private float nowProgressSecond;
    private int progressColorSecond;

    private boolean noTopLeftRadiusSecond;
    private boolean noTopRightRadiusSecond;
    private boolean noBottomLeftRadiusSecond;
    private boolean noBottomRightRadiusSecond;




    private Shader borderShader;
    private Shader bgShader;
    private Shader progressShader;


    private final String def_borderColor="#239936";
    private final String def_bgColor="#00000000";
    private final String def_progressColor=def_borderColor;

    private TimeInterpolator interpolator =new DecelerateInterpolator();

    /*progress*/
    private Paint progressPaint;
    private Path progressPath;

    /*bg*/
    private Paint bgPaint;
    private Path bgPath;

    /*border*/
    private Paint borderPaint;
    private Path borderPath;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
        radius = typedArray.getDimension(R.styleable.MyProgress_radius,0);
        maxProgress =typedArray.getFloat(R.styleable.MyProgress_maxProgress,100);
        nowProgress =typedArray.getFloat(R.styleable.MyProgress_nowProgress,0);
        angle=typedArray.getInt(R.styleable.MyProgress_angle,0);
        duration=typedArray.getInt(R.styleable.MyProgress_duration,1200);

        noTopLeftRadius=typedArray.getBoolean(R.styleable.MyProgress_noTopLeftRadius,false);
        noTopRightRadius=typedArray.getBoolean(R.styleable.MyProgress_noTopRightRadius,false);
        noBottomLeftRadius=typedArray.getBoolean(R.styleable.MyProgress_noBottomLeftRadius,false);
        noBottomRightRadius=typedArray.getBoolean(R.styleable.MyProgress_noBottomRightRadius,false);

        nowProgressSecond=typedArray.getDimension(R.styleable.MyProgress_nowProgressSecond,0);
        progressColorSecond=typedArray.getColor(R.styleable.MyProgress_progressColorSecond,Color.TRANSPARENT);


        noTopLeftRadiusSecond=typedArray.getBoolean(R.styleable.MyProgress_noTopLeftRadiusSecond,false);
        noTopRightRadiusSecond=typedArray.getBoolean(R.styleable.MyProgress_noTopRightRadiusSecond,false);
        noBottomLeftRadiusSecond=typedArray.getBoolean(R.styleable.MyProgress_noBottomLeftRadiusSecond,false);
        noBottomRightRadiusSecond=typedArray.getBoolean(R.styleable.MyProgress_noBottomRightRadiusSecond,false);

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




        initPaint();


        initPath();



    }

    private void initPath() {
        progressPath=new Path();
        borderPath=new Path();
        bgPath=new Path();
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
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth =  400;
        int mHeight = 30;
        if(viewWidth+0>mWidth){
            mWidth= (int) (viewWidth+0);
        }
        if(viewHeight+0>mHeight){
            mHeight= (int) (viewHeight+0);
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
    }

    private void initPaint() {
        borderPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);

        bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);


        progressPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setColor(progressColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        float scaleAngle=angle%360;
        canvas.translate(getWidth()/2,getHeight()/2);
        if(scaleAngle>0){
            canvas.rotate(scaleAngle);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            canvas.clipPath(bgPath);
        }

        drawBg(canvas);
        if(borderWidth>0){
            drawBorder(canvas);
        }
        drawProgress(canvas);
    }
    private void drawBorder(Canvas canvas) {
        borderPath.addRoundRect(getBorderRectF(),getRectFRadius(true), Path.Direction.CW);
        canvas.drawPath(borderPath,borderPaint);
    }
    private void drawBg(Canvas canvas) {
        if(bgShader==null){
            bgPaint.setShader(null);
        }else if(bgPaint.getShader()!=bgShader){
            bgPaint.setShader(bgShader);
        }
        if(bgPath!=null&&!bgPath.isEmpty()){
            bgPath.reset();
        }
        bgPath.addRoundRect(getBorderRectF(),getRectFRadius(true), Path.Direction.CW);
        canvas.drawPath(bgPath,bgPaint);
    }

    private void drawProgress(Canvas canvas) {

        if(progressShader!=null){
            progressPaint.setShader(progressShader);
        }else{
            progressPaint.setShader(null);
        }

        progressPath.reset();
        progressPath.addRoundRect(getProgressRectF(),getRectFRadius(false), Path.Direction.CW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            progressPath.op(bgPath, Path.Op.INTERSECT);
        }
        canvas.drawPath(progressPath,progressPaint);
    }

    private RectF getBorderRectF(){
        if(borderRectF==null){
            borderRectF=new RectF(-viewWidth/2+borderWidth/2,-viewHeight/2+borderWidth/2, viewWidth/2-borderWidth/2, viewHeight /2-borderWidth/2);
        }else{
            borderRectF.set(-viewWidth/2+borderWidth/2,-viewHeight/2+borderWidth/2, viewWidth/2-borderWidth/2, viewHeight /2-borderWidth/2);
        }
        return borderRectF;
    }
    private RectF getProgressRectF(){
        float progressWidth=viewWidth;
        float progressHeight=viewHeight;
        float leftOffset=leftInterval;
        float topOffset=topInterval;
        float rightOffset=rightInterval;
        float bottomOffset=bottomInterval;
        if(allInterval>0){
            progressWidth=viewWidth-allInterval*2-getBorderWidth();
            progressHeight=viewHeight-allInterval*2-getBorderWidth();
            leftOffset=allInterval;
            topOffset=allInterval;
            rightOffset=allInterval;
            bottomOffset=allInterval;
        }else{
            progressWidth=viewWidth-leftOffset-rightOffset-getBorderWidth();
            progressHeight=viewHeight-topInterval-bottomInterval-getBorderWidth();
        }

        float tempBorderW=borderWidth/2;
        if(maxProgress<=0){
            if(progressRectF==null){
                progressRectF=new RectF(-viewWidth/2+leftOffset+tempBorderW,-viewHeight/2+topOffset+tempBorderW,-viewWidth/2+leftOffset-tempBorderW, viewHeight /2-bottomOffset);
            }else{
                progressRectF.set(-viewWidth/2+leftOffset+tempBorderW,-viewHeight/2+topOffset+tempBorderW,-viewWidth/2+leftOffset-tempBorderW, viewHeight /2-bottomOffset);
            }
        }else{
            if(progressRectF==null){
                progressRectF=new RectF(-viewWidth/2+leftOffset+tempBorderW,-viewHeight/2+topOffset+tempBorderW, (progressWidth*scaleProgress/ maxProgress -viewWidth/2+leftOffset), viewHeight /2-bottomOffset-tempBorderW);
            }else{
                progressRectF.set(-viewWidth/2+leftOffset+tempBorderW,-viewHeight/2+topOffset+tempBorderW, (progressWidth*scaleProgress/ maxProgress -viewWidth/2+leftOffset), viewHeight /2-bottomOffset-tempBorderW);
            }
        }
        return progressRectF;
    }
    private float[] getRectFRadius(boolean isBGRadius){
        if(isBGRadius){
            return new float[]{getRadius(),getRadius(),getRadius(),getRadius(),getRadius(),getRadius(),getRadius(),getRadius()};
        }
        float scale=getProgressRectF().height()*1f/getBorderRectF().height();
        float tempRadius=getRadius()*scale;
        return new float[]{tempRadius,tempRadius,tempRadius,tempRadius,tempRadius,tempRadius,tempRadius,tempRadius};
    }
    private void needInvalidate(){
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }
    public int getBorderColor() {
        return borderColor;
    }

    public MyProgress setBorderColor(@ColorInt int borderColor) {
        if(this.borderColor==borderColor){
            return this;
        }
        this.borderColor = borderColor;
        borderPaint.setColor(borderColor);
        return this;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public MyProgress setBorderWidth(float borderWidth) {
        if(this.borderWidth==borderWidth){
            return this;
        }
        this.borderWidth = borderWidth;
        borderPaint.setStrokeWidth(borderWidth);
        return this;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public MyProgress setProgressColor( int progressColor) {
        if(this.progressColor==progressColor){
            return this;
        }
        this.progressColor = progressColor;
        this.progressShader=null;
        progressPaint.setColor(progressColor);
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
        needInvalidate();
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
        float beforeProgress=this.scaleProgress;
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
        if(this.bgColor==bgColor){
            return this;
        }
        this.bgColor = bgColor;
        bgPaint.setColor(bgColor);
        return this;
    }

    public float getRadius() {
        return radius;
    }

    public MyProgress setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public float getNowProgressSecond() {
        return nowProgressSecond;
    }

    public MyProgress setNowProgressSecond(float nowProgressSecond) {
        this.nowProgressSecond = nowProgressSecond;
        return this;
    }

    public int getProgressColorSecond() {
        return progressColorSecond;
    }

    public MyProgress setProgressColorSecond(int progressColorSecond) {
        this.progressColorSecond = progressColorSecond;
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
        if(borderShader!=null){
            borderPaint.setShader(borderShader);
        }else{
            borderPaint.setShader(null);
        }
        return this;
    }

    public Shader getBgShader() {
        return bgShader;
    }

    public MyProgress setBgShader(Shader bgShader) {
        this.bgShader = bgShader;
        if(this.bgColor==Color.parseColor(def_bgColor)){
            /*防止bgPaint设置透明色导致背景shader无效*/
            this.bgColor=Color.WHITE;
        }
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
