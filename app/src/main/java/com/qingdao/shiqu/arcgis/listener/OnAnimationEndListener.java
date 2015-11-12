package com.qingdao.shiqu.arcgis.listener;

import android.view.animation.Animation;

/**
 * Created by Administrator on 2015-11-12.
 */
public abstract class OnAnimationEndListener implements Animation.AnimationListener {
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public abstract void onAnimationEnd(Animation animation);

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
