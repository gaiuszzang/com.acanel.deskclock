package com.acanel.deskclock

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import okhttp3.OkHttpClient

//for Debug
object AppInitializer {
    lateinit var defaultOkHttpClient: OkHttpClient

    fun init(context: Context) {
        flipperInit(context)
    }

    private fun flipperInit(context: Context) {
        SoLoader.init(context, false)
        if (FlipperUtils.shouldEnableFlipper(context)) {
            val client = AndroidFlipperClient.getInstance(context)
            client.addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            client.start()

            //Layout Inspector
            val descriptorMapping = DescriptorMapping.withDefaults()
            client.addPlugin(InspectorFlipperPlugin(context, descriptorMapping))

            //Network
            val networkFlipperPlugin = NetworkFlipperPlugin()
            client.addPlugin(networkFlipperPlugin)
            defaultOkHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
                .build()
        }
    }
}