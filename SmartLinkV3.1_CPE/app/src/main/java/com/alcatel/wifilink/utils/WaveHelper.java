package com.alcatel.wifilink.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.LinearInterpolator;

import com.gelitenight.waveview.library.WaveView;

import java.util.ArrayList;
import java.util.List;

public class WaveHelper {
    private WaveView mWaveView;// 波浪视图
    private AnimatorSet mAnimatorSet;// 动画内置集合
    private List<Animator> tempAnimtorList;// 临时集合(存放所有动画对象:含空对象)
    private List<Animator> animatorList;// 有效集合(提出空对象)

    private int shape = 0;// 默认圆环
    private int width = 10;// 默认圆环宽度
    private String hexColor = "#009688";// 默认波浪颜色

    private int shiftHorizotolDuration = 20;// 默认水平移动时间

    private int startLevel = 70;// 默认起始水位
    private int endLevel = 70;// 默认末端水位
    private int levelDuration = 100;// 默认水位变化时间

    private int startAmplitude = 50;// 默认起始振幅
    private int endAmplitude = 50;// 默认末端振幅 
    private int amplitudeDuration = 100;// 默认振幅变化时间

    private TimeInterpolator horizotolinterpolator = new LinearInterpolator();// 默认水平插值器(线性)
    private TimeInterpolator Levelinterpolator = new LinearInterpolator();// 默认水位插值器(线性)
    private TimeInterpolator amplitudeInterpolator = new LinearInterpolator();// 默认振幅插值器(线性)

    public WaveHelper(WaveView waveView) {
        mWaveView = waveView;
        tempAnimtorList = new ArrayList<>();
        animatorList = new ArrayList<>();
    }

    /**
     * 设置波浪水平偏移程度(0 ~ 100,建议:0)
     *
     * @param shift
     */
    public void setShiftHorizotol(int shift) {
        shift = shift < 0 ? 0 : shift;
        shift = shift > 100 ? 100 : shift;
        float shifts = shift * 1f / 100;
        mWaveView.setWaveShiftRatio(shifts);
    }

    /**
     * 设置水位(0 ~ 100,建议:50)
     *
     * @param level
     */
    public void setLevel(int level) {
        level = level < 0 ? 0 : level;
        level = level > 100 ? 100 : level;
        float levels = level * 1f / 100;
        mWaveView.setWaterLevelRatio(levels);
    }

    /**
     * 设置振幅(0 ~ 200,建议:100)
     *
     * @param amplitude
     */
    public void setAmplitude(int amplitude) {
        amplitude = amplitude < 0 ? 0 : amplitude;
        amplitude = amplitude > 200 ? 200 : amplitude;
        float amplitudes = amplitude * 1f / 1000;
        mWaveView.setAmplitudeRatio(amplitudes);
    }

    /**
     * 设置波浪颜色
     *
     * @param behindColor 背景色
     * @param frontColor  前景色
     */
    public void setWaveColor(String behindColor, String frontColor) {
        mWaveView.setWaveColor(Color.parseColor(behindColor), Color.parseColor(frontColor));
    }

    /**
     * 设置波浪长度(0 ~ 100)
     *
     * @param length
     */
    public void setWaveLength(int length) {
        length = length < 0 ? 0 : length;
        length = length > 100 ? 100 : length;
        float lengths = length * 1f / 100;
        mWaveView.setWaveLengthRatio(lengths);
    }

    /**
     * 显示波浪(true:显示,如果调用了startAnim则该方法可以不调用)
     *
     * @param isShow
     */
    public void showWave(boolean isShow) {
        mWaveView.setShowWave(isShow);
    }

    /**
     * 设置圆环大小以及形状
     *
     * @param shape    形状--> 0:circle ,1:rectange
     * @param width    圆环宽度
     * @param hexColor 圆环颜色
     */
    public void setBorderAndShape(int shape, int width, String hexColor) {
        this.shape = shape;
        this.width = width;
        this.hexColor = hexColor;
        // 限定最大值|最小值
        width = width > 100 ? 100 : width;
        width = width < 0 ? 0 : width;
        mWaveView.setBorder(width, Color.parseColor(hexColor));
        mWaveView.setShapeType(shape == 0 ? WaveView.ShapeType.CIRCLE : WaveView.ShapeType.SQUARE);
    }

    /**
     * 水平移动动画
     *
     * @param shiftHorizotolDuration     越小越快
     * @param shiftHorizotolinterpolator 加速插值器
     */
    public ObjectAnimator setShiftHorizotolAnim(int shiftHorizotolDuration, TimeInterpolator shiftHorizotolinterpolator) {
        this.shiftHorizotolDuration = shiftHorizotolDuration;
        this.horizotolinterpolator = shiftHorizotolinterpolator;
        shiftHorizotolDuration = shiftHorizotolDuration > 200 ? 200 : shiftHorizotolDuration;
        shiftHorizotolDuration = shiftHorizotolDuration < 10 ? 10 : shiftHorizotolDuration;
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(mWaveView, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(shiftHorizotolDuration * 100);
        waveShiftAnim.setInterpolator(shiftHorizotolinterpolator == null ? new LinearInterpolator() : shiftHorizotolinterpolator);
        tempAnimtorList.add(waveShiftAnim);
        return waveShiftAnim;
    }

    /**
     * 水位上升动画
     *
     * @param startLevel    起始水位
     * @param endLevel      最大水位
     * @param levelDuration 动画时间(越小越快)
     */
    public ObjectAnimator setLevelAnim(int startLevel, int endLevel, int levelDuration, TimeInterpolator levelinterpolator) {
        this.startLevel = startLevel;
        this.endLevel = endLevel;
        this.levelDuration = levelDuration;
        this.Levelinterpolator = levelinterpolator;
        // 限定最大值|最小值|时间
        startLevel = startLevel > 100 ? 100 : startLevel;
        startLevel = startLevel <= 0 ? 0 : startLevel;
        endLevel = endLevel > 100 ? 100 : endLevel;
        endLevel = endLevel < 0 ? 0 : endLevel;
        levelDuration = levelDuration > 200 ? 200 : levelDuration;
        levelDuration = levelDuration < 10 ? 10 : levelDuration;
        // 换算
        float start = startLevel * 1f / 100;
        float end = endLevel * 1f / 100;
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(mWaveView, "waterLevelRatio", start, end);
        waterLevelAnim.setDuration(levelDuration * 100);
        waterLevelAnim.setInterpolator(Levelinterpolator == null ? new LinearInterpolator() : Levelinterpolator);
        tempAnimtorList.add(waterLevelAnim);
        return waterLevelAnim;
    }

    /**
     * 振幅动画
     *
     * @param startAmplitude    起始振幅
     * @param endAmplitude      末端振幅
     * @param amplitudeDuration 动画时间(越小越快)
     * @return
     */
    public ObjectAnimator setAmplitudeAnim(int startAmplitude, int endAmplitude, int amplitudeDuration, TimeInterpolator amplitudeInterpolator) {
        this.startAmplitude = startAmplitude;
        this.endAmplitude = endAmplitude;
        this.amplitudeDuration = amplitudeDuration;
        this.amplitudeInterpolator = amplitudeInterpolator;
        // 限定最大值|最小值|时间
        startAmplitude = startAmplitude > 100 ? 100 : startAmplitude;
        startAmplitude = startAmplitude < 0 ? 0 : startAmplitude;
        endAmplitude = endAmplitude > 100 ? 100 : endAmplitude;
        endAmplitude = endAmplitude < 0 ? 0 : endAmplitude;
        amplitudeDuration = amplitudeDuration > 100 ? 100 : amplitudeDuration;
        amplitudeDuration = amplitudeDuration < 100 ? 100 : amplitudeDuration;
        // 换算
        float start = startAmplitude * 1f / 1000;
        float end = endAmplitude * 1f / 1000;
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(mWaveView, "amplitudeRatio", start, end);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(amplitudeDuration * 100);
        amplitudeAnim.setInterpolator(Levelinterpolator == null ? new LinearInterpolator() : Levelinterpolator);
        tempAnimtorList.add(amplitudeAnim);
        return amplitudeAnim;
    }

    /**
     * 启动动画
     */
    public void startAnim() {
        if (tempAnimtorList.size() <= 0) {
            return;
        }
        // 过滤空对象
        for (Animator animator : tempAnimtorList) {
            if (animator == null) {
                continue;
            }
            this.animatorList.add(animator);
        }
        if (animatorList.size() <= 0) {
            return;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animatorList);
        // 显示波浪
        mWaveView.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    /**
     * 取消动画
     */
    public void cancelAnim() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }
}
