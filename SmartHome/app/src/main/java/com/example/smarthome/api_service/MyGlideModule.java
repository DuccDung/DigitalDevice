package com.example.smarthome.api_service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;


@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // Sử dụng OkHttpClient tùy chỉnh từ SSLUtils
        OkHttpClient client = SSLUtils.getUnsafeOkHttpClient();

        // Thay thế cách sử dụng GlideUrl loader bằng OkHttpClient
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));

    }
}


