package com.example.androidchatapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.net.URL

class FetchImageFromInternet(
    private val imageView: ImageView
): AsyncTask<String, Void, Bitmap?>() {

    override fun doInBackground(vararg params: String?): Bitmap? {
        val url = params[0]
        var image: Bitmap? = null
        try {
            val stream = URL(url).openStream()
            image = BitmapFactory.decodeStream(stream)

        } catch (exp: Exception) {

        }
        return image
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)

        imageView.setImageBitmap(result)
    }
}