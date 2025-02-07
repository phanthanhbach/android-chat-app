package com.example.androidchatapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidchatapp.interfaces.AttachmentInterface
import com.github.chrisbanes.photoview.PhotoView
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class FullScreenImageActivity : AppCompatActivity(), AttachmentInterface {

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

        val imageUrl = intent.getStringExtra("image_url")



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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDownload(view: View) {

    }
}