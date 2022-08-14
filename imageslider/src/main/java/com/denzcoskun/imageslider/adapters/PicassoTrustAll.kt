package com.denzcoskun.imageslider.adapters

import android.content.Context
import android.util.Log
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class PicassoTrustAll private constructor(context: Context) {
    companion object {
        private var mInstance: Picasso? = null
        fun getInstance(context: Context): Picasso? {
            if (mInstance == null) {
                PicassoTrustAll(context)
            }
            return mInstance
        }
    }

    init {
        var client: OkHttpClient? = null
        val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                x509Certificates: Array<X509Certificate?>?,
                s: String?
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                x509Certificates: Array<X509Certificate?>?,
                s: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }

        })
        try {
            val sc: SSLContext = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, SecureRandom())
            client = OkHttpClient.Builder()
                .hostnameVerifier { _, _ -> true }
                .sslSocketFactory(sc.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mInstance = Picasso.Builder(context)
            .downloader(OkHttp3Downloader(client))
            .listener { picasso, uri, exception -> Log.e("PICASSO", exception.toString()) }.build()
    }
}