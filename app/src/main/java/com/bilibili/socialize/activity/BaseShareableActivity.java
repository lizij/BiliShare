/*
 * Copyright (c) 2015. BiliBili Inc.
 */

package com.bilibili.socialize.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bilibili.socialize.sample.R;
import com.bilibili.socialize.helper.ShareHelper;
import com.bilibili.socialize.share.core.error.BiliShareStatusCode;


/**
 * Share Helper Activity
 *
 * @author yrom
 */
public abstract class BaseShareableActivity extends AppCompatActivity implements ShareHelper.Callback {
    protected ShareHelper mShare;

    public void startShare(@Nullable View anchor) {
        startShare(anchor, false);
    }

    public void startShare(@Nullable View anchor, boolean isWindowFullScreen) {
        if (mShare == null) {
            // 获取ShareHelper实例
            mShare = ShareHelper.instance(this, this);
        }
        if (anchor == null) {
            // 显示弹窗分享面板
            mShare.showShareDialog();
        } else {
            if (isWindowFullScreen) {
                // 显示全屏遮挡分享面板
                mShare.showShareFullScreenWindow(anchor);
            } else {
                // 显示不遮挡分享面板
                mShare.showShareWarpWindow(anchor);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 释放资源
        if (mShare != null) {
            mShare.release();
            mShare = null;
        }
        super.onDestroy();
    }

    @Override
    public void onShareStart(ShareHelper helper) {

    }

    @Override
    public void onShareComplete(ShareHelper helper, int code) {
        // 根据分享结果弹toast
        if (code == BiliShareStatusCode.ST_CODE_SUCCESSED)
            Toast.makeText(this, R.string.bili_share_sdk_share_success, Toast.LENGTH_SHORT).show();
        else if (code == BiliShareStatusCode.ST_CODE_ERROR)
            Toast.makeText(this, R.string.bili_share_sdk_share_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(ShareHelper helper) {
    }

}
