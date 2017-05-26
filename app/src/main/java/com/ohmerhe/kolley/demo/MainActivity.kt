/*
 * Copyright (c) 2016  Ohmer.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohmerhe.kolley.demo

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.kotlinthree.andex.component.findView
import com.ohmerhe.kolley.image.Image
import com.ohmerhe.kolley.image.ImageDisplayOption
import com.ohmerhe.kolley.request.Http
import java.io.File
import java.nio.charset.Charset
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore


class MainActivity : AppCompatActivity() {
    val RESULT_LOAD_IMAGE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val _imageView: ImageView? = findView(R.id.image_view)
        val _imageView2: ImageView? = findView(R.id.image_view2)
        val upload: Button = findView(R.id.upload)

        Http.init(this)
        Http.get {

            url = "http://api.openweathermap.org/data/2.5/weather"

            tag = this@MainActivity

            params {
                "q" - "shanghai"
                "appid" - "d7a98cf22463b1c0c3df4adfe5abbc77"
            }

            onStart { log("on start") }

            onSuccess { bytes ->
                log("on success ${bytes.toString(Charset.defaultCharset())}")
            }

            onFail { error ->
                log("on fail ${error.toString()}")
            }

            onFinish { log( "on finish") }

        }

        val cacheImagePath = "$externalCacheDir/image/"
        log("cacheImagePath = $cacheImagePath")

        Image.init(this) {
            // these values are all default value , you do not need specific them if you do not want to custom
            memoryCacheEnabled = true
            memoryCacheSize = (Runtime.getRuntime().maxMemory() / 8).toInt()
            diskCacheDir = File(cacheImagePath)
            diskCacheSize = 200 * 1024 * 1024
            diskCacheEnabled = true
            compressFormat = Bitmap.CompressFormat.JPEG
            compressQuality = 80
        }

        Image.display {
            url = "http://7xpox6.com1.z0.glb.clouddn.com/android_bg.jpg"
            imageView = _imageView
            options {
                // these values are all default value , you do not need specific them if you do not want to custom
                imageResOnLoading = R.drawable.default_image
                imageResOnLoading = R.drawable.default_image
                imageResOnFail = R.drawable.default_image
                decodeConfig = Bitmap.Config.RGB_565
                scaleType = ImageView.ScaleType.CENTER_CROP
                maxWidth = ImageDisplayOption.DETAULT_IMAGE_WIDTH_MAX
                maxHeight = ImageDisplayOption.DETAULT_IMAGE_HEIGHT_MAX
            }
        }

        Http.post {
            url = "http://192.168.199.110:3000"
            raw = "{\"key\":\"value\"}"

            onSuccess {
                log("on success ${it.toString(Charset.defaultCharset())}")
            }
        }

        Image.load {
            url = "http://7xpox6.com1.z0.glb.clouddn.com/android_bg.jpg"
            options {
                scaleType = ImageView.ScaleType.CENTER_CROP
                maxWidth = ImageDisplayOption.DETAULT_IMAGE_WIDTH_MAX
                maxHeight = ImageDisplayOption.DETAULT_IMAGE_HEIGHT_MAX
            }
            onSuccess { bitmap ->
                _imageView2?.setImageBitmap(bitmap)
            }
            onFail { error ->
                log(error.toString())
            }
        }

        upload.setOnClickListener {
            val i = Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            uploadImage(picturePath!!)

            // String picturePath contains the path of selected Image
        }
    }

    private fun uploadImage(picturePath: String) {
        Http.upload{
            url = "http://192.168.199.110:3000"
            files {
                "image" - picturePath
            }
            onSuccess {
                log("on success ${it.toString(Charset.defaultCharset())}")
            }
            onFail { error ->
                log("on fail ${error.toString()}")
            }
        }
    }


    fun log(text: String) {
        Log.d("MainActivity", text)
    }
}
