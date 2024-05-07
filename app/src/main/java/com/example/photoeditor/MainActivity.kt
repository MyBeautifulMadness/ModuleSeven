package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
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

    fun kyrsibtn(view: View?) {
        val intent= Intent(this,SecondaryActivity::class.java)
        startActivity(intent)
    }
    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
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

        //chose image when clicked
        BSelectImage.setOnClickListener(View.OnClickListener { imageChooser() })
        //create rotated image
        BRotate.setOnClickListener {
            if(ETText.text.isNotEmpty()){
                val RotationAngle = (ETText.text.toString() + "f").toFloat()
                val rotatedBitmap = BMBitmap.rotate(RotationAngle)
                IVPreviewImage!!.setImageBitmap(rotatedBitmap)
                BMBitmap=rotatedBitmap
            } else {
                Toast.makeText(applicationContext, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun imageChooser() {

        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)

        // pass the constant to compare it
        // with the returned requestCode
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
        // Create a directory to store images
        val directory = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "YourDirectoryName"
        )
        if (!directory.exists()) {
            directory.mkdirs() // Create directories if they don't exist
        }
        // Create a unique file name
        val fileName = "image_" + System.currentTimeMillis() + ".png"
        // Create the file in the directory
        val file = File(directory, fileName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Tell the media scanner about the new file so that it is immediately available in the gallery
            MediaScannerConnection.scanFile(
                this, arrayOf(file.toString()),
                null,
                null
            )

            // Optional: Display a toast message to indicate successful saving
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle error
        }
    }
}
