package com.example.androidchatapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidchatapp.utils.DownloadFileFromHTTP
import com.github.chrisbanes.photoview.PhotoView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class FullScreenImageActivity : AppCompatActivity() {

    lateinit var image_view: PhotoView
    lateinit var toolbar: Toolbar
    lateinit var btn_download: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_full_screen_image)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + v.paddingLeft,
                systemBars.top + v.paddingTop,
                systemBars.right + v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }

        image_view = findViewById(R.id.image_view)
        toolbar = findViewById(R.id.toolbar)
        btn_download = findViewById(R.id.btn_download)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(this, R.drawable.round_close_24)
        )
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val imageUrl = intent?.getStringExtra("image_url")
        val imageName = intent?.getStringExtra("image_name")

        thread {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                runOnUiThread {
                    image_view.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btn_download.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        if (imageUrl != null && imageName != null) {
                            DownloadFileFromHTTP(
                                imageUrl,
                                imageName
                            ){ success ->
                                runOnUiThread {
                                    if (success) {
                                        Toast.makeText(this@FullScreenImageActivity, "Image Saved", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@FullScreenImageActivity, "Failed to Save Image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }.execute()
                        }
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        TODO("Not yet implemented")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        TODO("Not yet implemented")
                    }

                }).check()

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}