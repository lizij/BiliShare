/*
 * Copyright (C) 2015 Bilibili <jungly.ik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bilibili.socialize.helper;

import com.bilibili.socialize.share.download.AbsImageDownloader;
import com.bilibili.socialize.share.util.FileUtil;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;
import java.io.IOException;

/**
 * 使用fresco实现的分享图片下载器
 * @author Jungly
 * @email jungly.ik@gmail.com
 * @since 2016/2/7
 */
public class ShareFrescoImageDownloader extends AbsImageDownloader {

    @Override
    protected void downloadDirectly(final String imageUrl, final String filePath, final OnImageDownloadListener listener) {
        if (listener != null)
            listener.onStart();

        // ImageRequest存储着Image Pipeline处理被请求图片所需要的有用信息(Uri、是否渐进式图片、是否返回缩略图、缩放、是否自动旋转等)。
        // fromUri使用了一个简单的builder创建ImageRequest
        final ImageRequest request = ImageRequest.fromUri(imageUrl);

        // 获取已解码的图片
        DataSource<CloseableReference<CloseableImage>> dataSource =
                Fresco.getImagePipeline().fetchDecodedImage(request, null);

        // dataSource被BaseDataSubscriber订阅，保证dataSource在使用后可以被自动关闭
        // 参考https://www.fresco-cn.org/docs/datasources-datasubscribers.html
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {

            @Override
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                // 回调执行后，dataSource就会被释放，尽量不要使用外部引用，防止内存泄漏
                if (!dataSource.isFinished()) {
                    return;
                }
                CloseableReference<CloseableImage> result = dataSource.getResult();
                if (result != null) {
//                    ImageRequest imageRequest = ImageRequest.fromUri(imageUrl);
                    ImageRequest imageRequest = request;

                    // 从未解码缓存中获取key
                    CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                            .getEncodedCacheKey(imageRequest);

                    // 根据key，从磁盘中获取资源的BinaryResource二进制流
                    BinaryResource resource = Fresco.getImagePipelineFactory()
                            .getMainDiskStorageCache()
                            .getResource(cacheKey);

                    if (resource instanceof FileBinaryResource) {
                        // 如果是文件二进制流，转换为File对象，并写入filePath所在目录
                        File cacheFile = ((FileBinaryResource) resource).getFile();
                        try {
                            FileUtil.copyFile(cacheFile, new File(filePath));
                            if (listener != null)
                                listener.onSuccess(filePath);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (listener != null)
                    listener.onFailed(imageUrl);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (listener != null)
                    listener.onFailed(imageUrl);
            }
        // 在UI线程上执行
        }, UiThreadImmediateExecutorService.getInstance());
    }

}
