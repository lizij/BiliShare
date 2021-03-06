/*
 * Copyright (c) 2015. BiliBili Inc.
 */

package com.bilibili.socialize.helper;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import com.bilibili.socialize.share.core.BiliShare;
import com.bilibili.socialize.share.core.BiliShareConfiguration;
import com.bilibili.socialize.share.core.SocializeListeners;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;
import com.bilibili.socialize.selector.BaseSharePlatformSelector;
import com.bilibili.socialize.selector.BaseSharePlatformSelector.ShareTarget;
import com.bilibili.socialize.selector.DialogSharePlatformSelector;
import com.bilibili.socialize.selector.PopFullScreenSharePlatformSelector;
import com.bilibili.socialize.selector.PopWrapSharePlatformSelector;

/**
 * Helper
 * 分享管理
 * @author yrom & Jungly.
 */
public final class ShareHelper {
    public static final String QQ_APPID = "";
    public static final String WECHAT_APPID = "";
    public static final String SINA_APPKEY = "";

    static final String APP_URL = "http://app.bilibili.com";
    private FragmentActivity mContext;
    private Callback mCallback;
    private BaseSharePlatformSelector mPlatformSelector;

    public static ShareHelper instance(FragmentActivity context, Callback callback) {
        return new ShareHelper(context, callback);
    }

    private ShareHelper(FragmentActivity context, Callback callback) {
        mContext = context;
        mCallback = callback;
        if (context == null) {
            throw new NullPointerException();
        }

        // 构建配置文件
        BiliShareConfiguration configuration = new BiliShareConfiguration.Builder(context)
                .imageDownloader(new ShareFrescoImageDownloader())
                .qq(QQ_APPID)
                .weixin(WECHAT_APPID)
                .sina(SINA_APPKEY, null, null)
                .build();

        // 注册配置文件
        shareClient().config(configuration);
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public void showShareDialog() {
        // 弹出Dialog分享面板
        mPlatformSelector = new DialogSharePlatformSelector(mContext, new BaseSharePlatformSelector.OnShareSelectorDismissListener() {
            @Override
            public void onDismiss() {
                onShareSelectorDismiss();
            }
        }, mShareItemClick);
        mPlatformSelector.show();
    }

    public void showShareWarpWindow(View anchor) {
        // 弹出Wrap分享面板
        mPlatformSelector = new PopWrapSharePlatformSelector(mContext, anchor, new BaseSharePlatformSelector.OnShareSelectorDismissListener() {
            @Override
            public void onDismiss() {
                onShareSelectorDismiss();
            }
        }, mShareItemClick);
        mPlatformSelector.show();
    }

    public void showShareFullScreenWindow(View anchor) {
        // 弹出Fullscreen分享面板
        mPlatformSelector = new PopFullScreenSharePlatformSelector(mContext, anchor, new BaseSharePlatformSelector.OnShareSelectorDismissListener() {
            @Override
            public void onDismiss() {
                onShareSelectorDismiss();
            }
        }, mShareItemClick);
        mPlatformSelector.show();
    }

    private void onShareSelectorDismiss() {
        mCallback.onDismiss(this);
    }

    private void hideShareWindow() {
        // 隐藏分享面板
        if (mPlatformSelector != null)
            mPlatformSelector.dismiss();
    }

    // 分享按钮监听器
    private AdapterView.OnItemClickListener mShareItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ShareTarget item = (ShareTarget) parent.getItemAtPosition(position);
            shareTo(item);
            hideShareWindow();
        }
    };

    private void shareTo(ShareTarget item) {
        // 回调接口，获取分享内容
        BaseShareParam content = mCallback.getShareContent(ShareHelper.this, item.media);
        if (content == null) {
            return;
        }
        // 开始分享，从mContext分享content到平台item.media，并回调接口shareListener
        shareClient().share(mContext, item.media, content, shareListener);
    }

    private SocializeListeners.ShareListener shareListener = new SocializeListeners.ShareListenerAdapter() {

        @Override
        public void onStart(SocializeMedia type) {
            if (mCallback != null)
                // 回调接口，开始分享
                mCallback.onShareStart(ShareHelper.this);
        }

        @Override
        protected void onComplete(SocializeMedia type, int code, Throwable error) {
            if (mCallback != null)
                // 回调接口，完成分享
                mCallback.onShareComplete(ShareHelper.this, code);
        }
    };

    public Context getContext() {
        return mContext;
    }

    public void release() {
        // 释放资源
        if (mPlatformSelector != null) {
            mPlatformSelector.release();
            mPlatformSelector = null;
        }
        mShareItemClick = null;
    }

    public static BiliShare shareClient()  {
        // 获取全局分享单例
        return BiliShare.global();
    }

    // 分享反馈
    public interface Callback {
        // 获取分享内容
        BaseShareParam getShareContent(ShareHelper helper, SocializeMedia target);

        // 分享开始
        void onShareStart(ShareHelper helper);

        // 分享完成
        void onShareComplete(ShareHelper helper, int code);

        // 取消
        void onDismiss(ShareHelper helper);
    }

}
