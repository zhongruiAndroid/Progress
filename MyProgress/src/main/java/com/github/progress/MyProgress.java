package com.github.progress;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

public class MyProgress extends View {
    private OnProgressInter onProgressInter;
    private OnProgressInter onProgressInterSecond;
    private RectF borderRectF;
    private RectF progressRectF;
    private ValueAnimator valueAnimator;
    private ValueAnimator valueAnimatorSecond;

    public interface OnProgressInter {
        void progress(float animProgress, float progress, float maxProgress);
    }

    public OnProgressInter getOnProgressInter() {
        return onProgressInter;
    }

    public void setOnProgressInter(OnProgressInter onProgressInter) {
        this.onProgressInter = onProgressInter;
    }

    public OnProgressInter getOnProgressInterSecond() {
        return onProgressInterSecond;
    }

    public void setOnProgressInterSecond(OnProgressInter onProgressInterSecond) {
        this.onProgressInterSecond = onProgressInterSecond;
    }

    private void setProgressToInter(float scaleProgress, float progress, float max) {
        if (onProgressInter != null) {
            onProgressInter.progress(scaleProgress, progress, max);
        }
    }

    private void setProgressToInterSecond(float scaleProgress, float progress, float max) {
        if (onProgressInterSecond != null) {
            onProgressInterSecond.progress(scaleProgress, progress, max);
        }
    }

    private float viewWidth;
    private float viewHeight;
    private float radius;
    private int bgColor;
    private int borderColor;
    private float borderWidth = 4;
    private int progressColor;
    private int progressColorSecond;
    private int allInterval = 0;
    private int leftInterval;
    private int topInterval;
    private int rightInterval;
    private int bottomInterval;
    private boolean useAnimation = true;
    private float nowProgress = 0;
    //用于动画计算
    private float scaleProgress = nowProgress;

    private float nowProgressSecond = 0;
    //用于动画计算
    private float scaleProgressSecond = nowProgressSecond;


    private float maxProgress = 100;
    private int angle = 0;
    private int rotateAngle = 0;
    private int duration = 1200;


    private boolean noTopLeftRadius;
    private boolean noTopRightRadius;
    private boolean noBottomLeftRadius;
    private boolean noBottomRightRadius;


    private boolean noTopLeftRadiusSecond;
    private boolean noTopRightRadiusSecond;
    private boolean noBottomLeftRadiusSecond;
    private boolean noBottomRightRadiusSecond;


    private Shader borderShader;
    private Shader bgShader;
    private Shader progressShader;
    private Shader progressShaderSecond;


    private final String def_borderColor = "#239936";
    private final String def_bgColor = "#00000000";
    private final String def_progressColor = def_borderColor;

    private TimeInterpolator interpolator = new DecelerateInterpolator();

    private Paint helperPaint;
    /*progress*/
    private Paint progressPaint;
    private Path progressPath;

    private Path clipPath;

    /*progressSecond*/
    private Paint progressPaintSecond;
    private Path progressPathSecond;


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
        bgColor = Color.parseColor(def_bgColor);
        borderColor = Color.parseColor(def_borderColor);
        progressColor = Color.parseColor(def_progressColor);

        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyProgress);

        viewWidth = typedArray.getDimension(R.styleable.MyProgress_viewWidth, 0);
        viewHeight = typedArray.getDimension(R.styleable.MyProgress_viewHeight, 0);
        bgColor = typedArray.getColor(R.styleable.MyProgress_bgColor, Color.parseColor(def_bgColor));
        borderColor = typedArray.getColor(R.styleable.MyProgress_borderColor, Color.parseColor(def_borderColor));
        borderWidth = typedArray.getDimension(R.styleable.MyProgress_borderWidth, 4);
        progressColor = typedArray.getColor(R.styleable.MyProgress_progressColor, borderColor);
        allInterval = (int) typedArray.getDimension(R.styleable.MyProgress_allInterval, 0);
        leftInterval = (int) typedArray.getDimension(R.styleable.MyProgress_leftInterval, 0);
        topInterval = (int) typedArray.getDimension(R.styleable.MyProgress_topInterval, 0);
        rightInterval = (int) typedArray.getDimension(R.styleable.MyProgress_rightInterval, 0);
        bottomInterval = (int) typedArray.getDimension(R.styleable.MyProgress_bottomInterval, 0);
        useAnimation = typedArray.getBoolean(R.styleable.MyProgress_useAnimation, true);
        radius = typedArray.getDimension(R.styleable.MyProgress_radius, 0);
        maxProgress = typedArray.getFloat(R.styleable.MyProgress_maxProgress, 100);
        nowProgress = typedArray.getFloat(R.styleable.MyProgress_nowProgress, 0);
        angle = typedArray.getInt(R.styleable.MyProgress_angle, 0);
        duration = typedArray.getInt(R.styleable.MyProgress_duration, 1200);

        noTopLeftRadius = typedArray.getBoolean(R.styleable.MyProgress_noTopLeftRadius, false);
        noTopRightRadius = typedArray.getBoolean(R.styleable.MyProgress_noTopRightRadius, false);
        noBottomLeftRadius = typedArray.getBoolean(R.styleable.MyProgress_noBottomLeftRadius, false);
        noBottomRightRadius = typedArray.getBoolean(R.styleable.MyProgress_noBottomRightRadius, false);

        nowProgressSecond = typedArray.getDimension(R.styleable.MyProgress_nowProgressSecond, 0);
        progressColorSecond = typedArray.getColor(R.styleable.MyProgress_progressColorSecond, Color.parseColor("#4ed3fd"));


        noTopLeftRadiusSecond = typedArray.getBoolean(R.styleable.MyProgress_noTopLeftRadiusSecond, false);
        noTopRightRadiusSecond = typedArray.getBoolean(R.styleable.MyProgress_noTopRightRadiusSecond, false);
        noBottomLeftRadiusSecond = typedArray.getBoolean(R.styleable.MyProgress_noBottomLeftRadiusSecond, false);
        noBottomRightRadiusSecond = typedArray.getBoolean(R.styleable.MyProgress_noBottomRightRadiusSecond, false);

        if (maxProgress <= 0) {
            this.maxProgress = 0;
        }
        if (nowProgress > maxProgress) {
            this.nowProgress = maxProgress;
        } else if (nowProgress < 0) {
            this.nowProgress = 0;
        }

        scaleProgress = nowProgress;
        typedArray.recycle();


        initPaint();


        initPath();


    }

    private void initPath() {
        progressPath = new Path();
        progressPathSecond = new Path();
        borderPath = new Path();
        bgPath = new Path();
    }

    private boolean isHorizontal(int angle) {
        if (angle % 180 == 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isVertical(int angle) {
        if (isHorizontal(angle)) {
            return false;
        } else {
            if (angle % 90 == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = 400;
        int mHeight = 30;
        if (viewWidth + 0 > mWidth) {
            mWidth = (int) (viewWidth + 0);
        }
        if (viewHeight + 0 > mHeight) {
            mHeight = (int) (viewHeight + 0);
        }
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    private void updateBGPath() {
        if (bgPath != null) {
            bgPath.reset();
        }
        bgPath.addRoundRect(getBorderRectF(), getRectFRadius(true), Path.Direction.CW);
    }

    private void initPaint() {
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth * 2);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);


        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setColor(progressColor);

        progressPaintSecond = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaintSecond.setStyle(Paint.Style.FILL);
        progressPaintSecond.setColor(progressColorSecond);


        helperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        helperPaint.setStyle(Paint.Style.FILL);
        helperPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (viewWidth == 0 && viewHeight == 0) {
            if (isHorizontal(angle)) {
                viewWidth = getWidth();
                viewHeight = getHeight();
            } else if (isVertical(angle)) {
                viewWidth = getHeight();
                viewHeight = getWidth();
            } else {
                viewWidth = 300;
                viewHeight = 20;
            }
        } else if (viewHeight == 0) {
            viewHeight = getHeight();
        } else if (viewWidth == 0) {
            viewWidth = getWidth();
        }


        updateBGPath();
        updateBorderPath();
        updateProgressPath();
        updateProgressPathSecond();
    }


    private void updateClipPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (clipPath == null) {
            clipPath = new Path();
        } else {
            clipPath.reset();
        }

        RectF rectF = new RectF(-viewWidth / 2, -viewHeight / 2, viewWidth / 2, viewHeight / 2);
        clipPath.addRoundRect(rectF, getRectFRadius(true), Path.Direction.CW);
    }

    private void updateProgressPath() {
        progressPath.reset();
        progressPath.addRoundRect(getProgressRectF(scaleProgress), getRectFRadius(false), Path.Direction.CW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            progressPath.op(bgPath, Path.Op.INTERSECT);
        }
    }

    private void updateProgressPathSecond() {
        progressPathSecond.reset();
        progressPathSecond.addRoundRect(getProgressRectF(scaleProgressSecond), getRectFRadiusSecond(false), Path.Direction.CW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            progressPathSecond.op(bgPath, Path.Op.INTERSECT);
        }
    }

    private void updateBorderPath() {
        borderPath.reset();
        borderPath.addRoundRect(getBorderRectF(), getRectFRadius(true), Path.Direction.CW);
        updateClipPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(getWidth() / 2, getHeight() / 2);
        if (rotateAngle > 0) {
            canvas.rotate(rotateAngle);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && clipPath != null) {
            canvas.clipPath(clipPath);
        }


        canvas.drawPath(bgPath, bgPaint);

        if (borderWidth > 0) {
            int count = canvas.saveLayer(-viewWidth / 2, -viewHeight / 2, viewWidth / 2, viewHeight / 2, null, Canvas.ALL_SAVE_FLAG);
            canvas.drawPath(borderPath, borderPaint);

            helperPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.drawPath(bgPath, helperPaint);

            canvas.restoreToCount(count);
        }
        canvas.drawPath(progressPathSecond, progressPaintSecond);

        canvas.drawPath(progressPath, progressPaint);


    }


    private float getScaleAngle() {
        return angle % 360;
    }

    private RectF getBorderRectF() {
        if (borderRectF == null) {
            borderRectF = new RectF(-viewWidth / 2 + borderWidth, -viewHeight / 2 + borderWidth, viewWidth / 2 - borderWidth, viewHeight / 2 - borderWidth);
        } else {
            borderRectF.set(-viewWidth / 2 + borderWidth, -viewHeight / 2 + borderWidth, viewWidth / 2 - borderWidth, viewHeight / 2 - borderWidth);
        }
        return borderRectF;
    }

 /*   private RectF getProgressRectF() {
        return getProgressRectF(scaleProgress);
    }*/

    private RectF getProgressRectF(float scaleProgress) {
        float leftOffset = leftInterval;
        float topOffset = topInterval;
        float rightOffset = rightInterval;
        float bottomOffset = bottomInterval;
        if (allInterval > 0) {
            leftOffset = allInterval;
            topOffset = allInterval;
            rightOffset = allInterval;
            bottomOffset = allInterval;
        }
        float progressWidth = viewWidth - leftOffset - rightOffset - getBorderWidth() * 2;

        float tempBorderW = borderWidth;
        if (maxProgress <= 0) {
            if (progressRectF == null) {
                progressRectF = new RectF(
                        -viewWidth / 2 + leftOffset + tempBorderW,
                        -viewHeight / 2 + topOffset + tempBorderW,
                        viewWidth / 2 - leftOffset - tempBorderW,
                        viewHeight / 2 - bottomOffset - tempBorderW);
            } else {
                progressRectF.set(
                        -viewWidth / 2 + leftOffset + tempBorderW,
                        -viewHeight / 2 + topOffset + tempBorderW,
                        viewWidth / 2 - leftOffset - tempBorderW,
                        viewHeight / 2 - bottomOffset - tempBorderW);
            }
        } else {
            if (progressRectF == null) {
                progressRectF = new RectF(
                        -viewWidth / 2 + leftOffset + tempBorderW,
                        -viewHeight / 2 + topOffset + tempBorderW,
                        (progressWidth * scaleProgress / maxProgress - viewWidth / 2 + leftOffset) + tempBorderW,
                        viewHeight / 2 - bottomOffset - tempBorderW);
            } else {

                progressRectF.set(
                        -viewWidth / 2 + leftOffset + tempBorderW,
                        -viewHeight / 2 + topOffset + tempBorderW,
                        (progressWidth * scaleProgress / maxProgress - viewWidth / 2 + leftOffset) + tempBorderW,
                        viewHeight / 2 - bottomOffset - tempBorderW);
            }
        }
        return progressRectF;
    }

    private float[] getRectFRadius(boolean isBGRadius) {
        if (isBGRadius) {
            return new float[]{getRadius(), getRadius(), getRadius(), getRadius(), getRadius(), getRadius(), getRadius(), getRadius()};
        }
        float scale = getProgressRectF(scaleProgress).height() * 1f / getBorderRectF().height();
        float tempRadius = getRadius() * scale;
        return new float[]{
                noTopLeftRadius ? 0 : tempRadius, noTopLeftRadius ? 0 : tempRadius,
                noTopRightRadius ? 0 : tempRadius, noTopRightRadius ? 0 : tempRadius,
                noBottomRightRadius ? 0 : tempRadius, noBottomRightRadius ? 0 : tempRadius,
                noBottomLeftRadius ? 0 : tempRadius, noBottomLeftRadius ? 0 : tempRadius
        };
    }

    private float[] getRectFRadiusSecond(boolean isBGRadius) {
        if (isBGRadius) {
            return new float[]{getRadius(), getRadius(), getRadius(), getRadius(), getRadius(), getRadius(), getRadius(), getRadius()};
        }
        float scale = getProgressRectF(scaleProgressSecond).height() * 1f / getBorderRectF().height();
        float tempRadius = getRadius() * scale;
        return new float[]{
                noTopLeftRadiusSecond ? 0 : tempRadius, noTopLeftRadiusSecond ? 0 : tempRadius,
                noTopRightRadiusSecond ? 0 : tempRadius, noTopRightRadiusSecond ? 0 : tempRadius,
                noBottomRightRadiusSecond ? 0 : tempRadius, noBottomRightRadiusSecond ? 0 : tempRadius,
                noBottomLeftRadiusSecond ? 0 : tempRadius, noBottomLeftRadiusSecond ? 0 : tempRadius
        };
    }

    private void needInvalidate() {
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
        if (this.borderColor == borderColor) {
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
        if (this.borderWidth == borderWidth) {
            return this;
        }
        this.borderWidth = borderWidth;
        borderPaint.setStrokeWidth(borderWidth * 2);

        updateBGPath();
        updateBorderPath();
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public MyProgress setProgressColor(int progressColor) {
        if (this.progressColor == progressColor) {
            return this;
        }
        this.progressColor = progressColor;
        this.progressShader = null;
        progressPaint.setColor(progressColor);
        progressPaint.setShader(null);
        return this;
    }

    public int getAllInterval() {
        return allInterval;
    }

    public MyProgress setAllInterval(int allInterval) {
        if (allInterval == this.allInterval) {
            return this;
        }
        this.allInterval = allInterval;
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public int getLeftInterval() {
        return leftInterval;
    }

    public MyProgress setLeftInterval(int leftInterval) {
        if (leftInterval == this.leftInterval) {
            return this;
        }
        this.leftInterval = leftInterval;
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public int getTopInterval() {
        return topInterval;
    }

    public MyProgress setTopInterval(int topInterval) {
        if (topInterval == this.topInterval) {
            return this;
        }
        this.topInterval = topInterval;
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public int getRightInterval() {
        return rightInterval;
    }

    public MyProgress setRightInterval(int rightInterval) {
        if (rightInterval == this.rightInterval) {
            return this;
        }
        this.rightInterval = rightInterval;
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public int getBottomInterval() {
        return bottomInterval;
    }

    public MyProgress setBottomInterval(int bottomInterval) {
        if (bottomInterval == this.bottomInterval) {
            return this;
        }
        this.bottomInterval = bottomInterval;
        updateProgressPath();
        updateProgressPathSecond();
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

    public void complete() {
        needInvalidate();
    }

    public MyProgress setMaxProgress(float maxProgress) {
        if (maxProgress <= 0) {
            this.maxProgress = 0;
        } else {
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
        float beforeProgress = this.scaleProgress;
        if (progress > maxProgress) {
            this.nowProgress = maxProgress;
        } else if (progress < 0) {
            this.nowProgress = 0;
        } else {
            this.nowProgress = progress;
        }
        if (useAnimation) {
            valueAnimator = ValueAnimator.ofFloat(beforeProgress, this.nowProgress);
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    MyProgress.this.scaleProgress = (float) animation.getAnimatedValue();
                    updateProgressPath();
                    invalidate();
                    setProgressToInter(MyProgress.this.scaleProgress, MyProgress.this.nowProgress, MyProgress.this.maxProgress);
                }
            });
            valueAnimator.setInterpolator(interpolator);
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        } else {
            MyProgress.this.scaleProgress = this.nowProgress;
            updateProgressPath();
            invalidate();
            setProgressToInter(MyProgress.this.scaleProgress, this.nowProgress, this.maxProgress);
        }
        return this;
    }

    public int getAngle() {
        return angle;
    }

    public MyProgress setAngle(int angle) {
        this.angle = angle;
        setRotateAngle(angle % 360);
        return this;
    }

    private void setRotateAngle(int rotateAngle) {
        this.rotateAngle = rotateAngle;
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
        if (viewWidth == this.viewWidth) {
            return this;
        }
        this.viewWidth = viewWidth;
        updateBGPath();
        updateBorderPath();
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public MyProgress setViewHeight(float viewHeight) {
        if (viewHeight == this.viewHeight) {
            return this;
        }
        this.viewHeight = viewHeight;
        updateBGPath();
        updateBorderPath();
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public int getBgColor() {
        return bgColor;
    }

    public MyProgress setBgColor(int bgColor) {
        if (this.bgColor == bgColor) {
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
        if (radius == this.radius) {
            return this;
        }
        this.radius = radius;
        updateBGPath();
        updateBorderPath();
        updateProgressPath();
        updateProgressPathSecond();
        return this;
    }

    public float getNowProgressSecond() {
        return nowProgressSecond;
    }

    public MyProgress setNowProgressSecond(float nowProgressSecond) {
        return setNowProgressSecond(nowProgressSecond, useAnimation);
    }

    public MyProgress setNowProgressSecondByProgress(float nowProgressSecond) {
        return setNowProgressSecond(getNowProgress() + nowProgressSecond, useAnimation);
    }

    public MyProgress setNowProgressSecond(float nowProgressSecond, boolean useAnimation) {
        float beforeProgress = this.scaleProgressSecond;
        if (nowProgressSecond > maxProgress) {
            this.nowProgressSecond = maxProgress;
        } else if (nowProgressSecond < 0) {
            this.nowProgressSecond = 0;
        } else {
            this.nowProgressSecond = nowProgressSecond;
        }
        if (useAnimation) {
            valueAnimatorSecond = ValueAnimator.ofFloat(beforeProgress, this.nowProgressSecond);
            valueAnimatorSecond.removeAllUpdateListeners();
            valueAnimatorSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    MyProgress.this.scaleProgressSecond = (float) animation.getAnimatedValue();
                    updateProgressPathSecond();
                    invalidate();
                    setProgressToInterSecond(MyProgress.this.scaleProgressSecond, MyProgress.this.nowProgressSecond, MyProgress.this.maxProgress);
                }
            });
            valueAnimatorSecond.setInterpolator(interpolator);
            valueAnimatorSecond.setDuration(duration);
            valueAnimatorSecond.start();
        } else {
            MyProgress.this.scaleProgressSecond = this.nowProgressSecond;
            updateProgressPathSecond();
            invalidate();
            setProgressToInterSecond(MyProgress.this.scaleProgressSecond, this.nowProgressSecond, this.maxProgress);
        }
        return this;

    }

    public int getProgressColorSecond() {
        return progressColorSecond;
    }

    public MyProgress setProgressColorSecond(int progressColorSecond) {
        if (this.progressColorSecond == progressColorSecond) {
            return this;
        }
        this.progressShaderSecond = null;
        progressPaintSecond.setColor(progressColorSecond);
        progressPaintSecond.setShader(null);

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
        if (borderShader != null) {
            borderPaint.setShader(borderShader);
        } else {
            borderPaint.setShader(null);
        }
        return this;
    }

    public Shader getBgShader() {
        return bgShader;
    }

    public MyProgress setBgShader(Shader bgShader) {
        if (bgShader == this.bgShader) {
            return this;
        }
        this.bgShader = bgShader;
        if (this.bgColor == Color.parseColor(def_bgColor)) {
            /*防止bgPaint设置透明色导致背景shader无效*/
            this.bgColor = Color.WHITE;
        }
        bgPaint.setShader(bgShader);
        return this;
    }

    public Shader getProgressShader() {
        return progressShader;
    }

    public MyProgress setProgressShader(Shader progressShader) {
        if (progressShader == this.progressShader) {
            return this;
        }
        this.progressShader = progressShader;
        progressPaint.setShader(progressShader);
        return this;
    }

    public Shader getProgressShaderSecond() {
        return progressShaderSecond;
    }

    public MyProgress setProgressShaderSecond(Shader progressShaderSecond) {
        if (progressShaderSecond == this.progressShaderSecond) {
            return this;
        }
        this.progressShaderSecond = progressShaderSecond;
        progressPaintSecond.setShader(progressShaderSecond);
        return this;
    }

    public boolean isNoTopLeftRadius() {
        return noTopLeftRadius;
    }

    public MyProgress setNoTopLeftRadius(boolean noTopLeftRadius) {
        this.noTopLeftRadius = noTopLeftRadius;
        updateProgressPath();
        return this;
    }

    public boolean isNoTopRightRadius() {
        return noTopRightRadius;
    }

    public MyProgress setNoTopRightRadius(boolean noTopRightRadius) {
        this.noTopRightRadius = noTopRightRadius;
        updateProgressPath();
        return this;
    }

    public boolean isNoBottomLeftRadius() {
        return noBottomLeftRadius;
    }

    public MyProgress setNoBottomLeftRadius(boolean noBottomLeftRadius) {
        this.noBottomLeftRadius = noBottomLeftRadius;
        updateProgressPath();
        return this;
    }

    public boolean isNoBottomRightRadius() {
        return noBottomRightRadius;
    }

    public MyProgress setNoBottomRightRadius(boolean noBottomRightRadius) {
        this.noBottomRightRadius = noBottomRightRadius;
        updateProgressPath();
        return this;
    }

    public boolean isNoTopLeftRadiusSecond() {
        return noTopLeftRadiusSecond;
    }

    public MyProgress setNoTopLeftRadiusSecond(boolean noTopLeftRadiusSecond) {
        this.noTopLeftRadiusSecond = noTopLeftRadiusSecond;
        updateProgressPathSecond();
        return this;
    }

    public boolean isNoTopRightRadiusSecond() {
        return noTopRightRadiusSecond;
    }

    public MyProgress setNoTopRightRadiusSecond(boolean noTopRightRadiusSecond) {
        this.noTopRightRadiusSecond = noTopRightRadiusSecond;
        updateProgressPathSecond();
        return this;
    }

    public boolean isNoBottomLeftRadiusSecond() {
        return noBottomLeftRadiusSecond;
    }

    public MyProgress setNoBottomLeftRadiusSecond(boolean noBottomLeftRadiusSecond) {
        this.noBottomLeftRadiusSecond = noBottomLeftRadiusSecond;
        updateProgressPathSecond();
        return this;
    }

    public boolean isNoBottomRightRadiusSecond() {
        return noBottomRightRadiusSecond;
    }

    public MyProgress setNoBottomRightRadiusSecond(boolean noBottomRightRadiusSecond) {
        this.noBottomRightRadiusSecond = noBottomRightRadiusSecond;
        updateProgressPathSecond();
        return this;
    }
}
