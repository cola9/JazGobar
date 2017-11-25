package com.example.nikolajcolic.jazgobar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ActivitySlika extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slika);
        //getActionBar().hide();
        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView image = (ImageView) findViewById(R.id.slika);

        image.setImageBitmap(bmp);
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(image);
        photoViewAttacher.update();
    }

}
