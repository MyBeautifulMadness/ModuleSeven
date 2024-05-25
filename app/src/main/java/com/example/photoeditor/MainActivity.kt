package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    fun newAct(view: View?) {
        val intent= Intent(this,SecondaryActivity::class.java)
        startActivity(intent)
    }
    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val width = width
        val height = height
        val rotatedBitmap = Bitmap.createBitmap(height, width, config)
        val sourcePixels = IntArray(width * height)
        getPixels(sourcePixels, 0, width, 0, 0, width, height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val newX = when (degrees) {
                    90f -> y
                    180f -> width - x - 1
                    270f -> height - y - 1
                    else -> x
                }
                val newY = when (degrees) {
                    90f -> width - x - 1
                    180f -> height - y - 1
                    270f -> y
                    else -> height - y - 1
                }
                rotatedBitmap.setPixel(newX, newY, sourcePixels[y * width + x])
            }
        }

        return rotatedBitmap
    }
    // Buttons
    lateinit var BSelectImage: Button
    lateinit var BRotate: Button
    // Text
    lateinit var ETText:EditText
    //Preview Image
    var IVPreviewImage: ImageView? = null
    lateinit var BMBitmap:Bitmap
    var SELECT_PICTURE = 200

    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BSelectImage = findViewById(R.id.SelectImage)
        IVPreviewImage = findViewById(R.id.PreviewImage)
        ETText = findViewById(R.id.EditText)
        BRotate = findViewById(R.id.RotateButton)
        BSelectImage.setOnClickListener(View.OnClickListener { imageChooser() })
        BRotate.setOnClickListener {
            if(ETText.text.isNotEmpty()){
                val RotationAngle = (ETText.text.toString() + "f").toFloat()
                if (RotationAngle % 90 != 0f) {
                    Toast.makeText(applicationContext,"Угол поворота должен быть кратным 90 градусам",Toast.LENGTH_SHORT).show()
                }
                else{
                    val rotatedBitmap = BMBitmap.rotate(RotationAngle)
                    IVPreviewImage!!.setImageBitmap(rotatedBitmap)
                    BMBitmap=rotatedBitmap
                }
            } else {
                Toast.makeText(applicationContext, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun imageChooser() {

        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {
                // get the url of the image from data
                val selectedImageUri = data!!.data
                if (null != selectedImageUri)
                {
                    // update the preview image in the layout
                    IVPreviewImage!!.setImageURI(selectedImageUri)
                    BMBitmap=Bitmap.createBitmap(IVPreviewImage!!.width, IVPreviewImage!!.height,
                        Bitmap.Config.ARGB_8888)
                    BMBitmap= IVPreviewImage!!.getDrawable().toBitmap()
                    //saving picture
                    lateinit var BSave: Button
                    BSave=findViewById(R.id.SaveButton)
                    BSave.setOnClickListener{saveBitmapToGallery(BMBitmap)
                    }
                }
            }
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "YourDirectoryName"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val fileName = "image_" + System.currentTimeMillis() + ".png"
        val file = File(directory, fileName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            MediaScannerConnection.scanFile(
                this, arrayOf(file.toString()),
                null,
                null
            )
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
