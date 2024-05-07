package com.example.photoeditor
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Canvas


class SecondaryActivity : AppCompatActivity() {
    fun kyrsibtn(view: View?) {
        val intent: Intent
        val cl = Class.forName("com.example.photoeditor.testName")
        intent = Intent(this@SecondaryActivity, cl)
        startActivity(intent)
    }
    // One Button
    lateinit var BSelectImage: Button
    lateinit var CCanvas: Canvas
    // One Preview Image
    var VPreviewImage: View? = null

    // constant to compare
    // the activity result code
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.SelectImage2)
        VPreviewImage = findViewById(R.id.PreviewView2)
        //VPreviewImage=CCanvas
        /*VPreviewImage!!.setOnClickListener{
            @Override
            public boolean onTouch(View v, MotionEvent event){

                Log.d("coords", event.getX() + " : " + event.getY();
                if(..){
                // Do stuff..
            }
            }
        });*/
    }

}

