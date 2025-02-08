package com.example.androidchatapp.utils

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadFileFromHTTP(
    var imgPath: String,
    var fileName: String,
    private val callback: (Boolean) -> Unit
) : AsyncTask<Void, Void, Boolean>() {
    var cachePath: String = ""

    override fun doInBackground(vararg params: Void?): Boolean {
        return try {
            val root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val directoryPath = File(root.absolutePath + "/")
            if (!directoryPath.exists()) {
                directoryPath.mkdirs()
            }

            val cachePath = File("$directoryPath/$fileName")
            cachePath.createNewFile()

            val buffer = ByteArray(1024)
            var url = URL(imgPath)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.doOutput = false
            urlConnection.connect()

            val inputStream = urlConnection.inputStream
            val fileOutput = FileOutputStream(cachePath)
            var bufferLength: Int

            while (inputStream.read(buffer).also {
                    bufferLength = it
                } > 0) {
                fileOutput.write(buffer, 0, bufferLength)
            }
            fileOutput.write(buffer)

            inputStream.close()
            fileOutput.close()

            Log.i("DownloadFileFromHTTP", "cachePath: $cachePath")

            this.cachePath = cachePath.toString()

            true
        } catch (exp: Exception) {
            Log.i("DownloadFileFromHTTP", "Exception: $exp")

            false
        }
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result != null) {
            callback(result)
        }
    }
}