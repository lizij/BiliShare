/*
 * Copyright (c) 2015. BiliBili Inc.
 */

package com.bilibili.socialize.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.bilibili.socialize.sample.R;
import com.bilibili.socialize.helper.ShareHelper;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;
import com.bilibili.socialize.share.core.shareparam.ShareAudio;
import com.bilibili.socialize.share.core.shareparam.ShareImage;
import com.bilibili.socialize.share.core.shareparam.ShareParamAudio;
import com.bilibili.socialize.share.core.shareparam.ShareParamImage;
import com.bilibili.socialize.share.core.shareparam.ShareParamText;
import com.bilibili.socialize.share.core.shareparam.ShareParamVideo;
import com.bilibili.socialize.share.core.shareparam.ShareParamWebPage;
import com.bilibili.socialize.share.core.shareparam.ShareVideo;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 这个类只是为了演示如何分享，如果想体验分享效果，请下载哔哩哔哩动画app。
 *
 * @author Jungly
 * @email jungly.ik@gmail.com
 * @since 2016/4/13
 */
public class MainActivity extends BaseShareableActivity {
    private static final String TITLE = "哔哩哔哩2016拜年祭";
    private static final String CONTENT = "【哔哩哔哩2016拜年祭】 UP主: 哔哩哔哩弹幕网 #哔哩哔哩动画# ";
    private static final String TARGET_URL = "http://www.bilibili.com/video/av3521416";
    private static final String IMAGE_URL = "http://i2.hdslb.com/320_200/video/85/85ae2b17b223a0cd649a49c38c32dd10.jpg";

    @Bind(R.id.text)
    RadioButton mTextRB;
    @Bind(R.id.image)
    RadioButton mImageRB;
    @Bind(R.id.webpage)
    RadioButton mWebPageRB;
    @Bind(R.id.audio)
    RadioButton mAudioRB;
    @Bind(R.id.video)
    RadioButton mVideoRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public BaseShareParam getShareContent(ShareHelper helper, SocializeMedia target) {
        BaseShareParam param;
        if (mImageRB.isChecked()) {
            // 图片
            param = new ShareParamImage(TITLE, CONTENT, TARGET_URL);
            ShareParamImage paramImage = (ShareParamImage) param;
            paramImage.setImage(generateImage());
        } else if (mWebPageRB.isChecked()) {
            // 网页
            param = new ShareParamWebPage(TITLE, CONTENT, TARGET_URL);
            ShareParamWebPage paramWebPage = (ShareParamWebPage) param;
            paramWebPage.setThumb(generateImage());
        } else if (mAudioRB.isChecked()) {
            // 音频
            param = new ShareParamAudio(TITLE, CONTENT, TARGET_URL);
            ShareParamAudio paramAudio = (ShareParamAudio) param;
            ShareAudio audio = new ShareAudio(generateImage(), TARGET_URL, TITLE);
            paramAudio.setAudio(audio);
        } else if (mVideoRB.isChecked()) {
            // 视频
            param = new ShareParamVideo(TITLE, CONTENT, TARGET_URL);
            ShareParamVideo paramVideo = (ShareParamVideo) param;
            ShareVideo video = new ShareVideo(generateImage(), TARGET_URL, TITLE);
            paramVideo.setVideo(video);
        } else {
            // 文字+链接
            param = new ShareParamText(TITLE, CONTENT, TARGET_URL);
        }

        if (target == SocializeMedia.SINA)
            param.setContent(String.format(Locale.CHINA, "%s #哔哩哔哩动画# ", CONTENT));
        else if (target == SocializeMedia.GENERIC || target == SocializeMedia.COPY) {
            param.setContent(CONTENT + " " + TARGET_URL);
        }

        return param;
    }

    private ShareImage generateImage() {
        return new ShareImage(IMAGE_URL);
    }

    @OnClick(R.id.share_with_dialog_bt)
    void shareWithDialogSelector() {
        startShare(null);
    }

    @OnClick(R.id.share_with_full_pop_bt)
    void shareWithFullPopSelector(View clickView) {
        startShare(clickView, true);
    }

    @OnClick(R.id.share_with_wrap_pop_bt)
    void shareWithWrapPopSelector(View clickView) {
        startShare(clickView, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
