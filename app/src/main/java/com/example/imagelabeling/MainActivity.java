package com.example.imagelabeling;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView t;
    ActivityResultLauncher<Intent> resLaunch;
    StringBuilder sb,c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        t = findViewById(R.id.text);
        resLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Bundle extras = data.getExtras();
                            Bitmap bitmapOriginal = (Bitmap) extras.get("data");
                            DisplayMetrics metrics = new DisplayMetrics();
                            MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int width = bitmapOriginal.getWidth();
                            int height = bitmapOriginal.getHeight();

                            float scaleWidth = metrics.scaledDensity;
                            float scaleHeight = metrics.scaledDensity;

                            // create a matrix for the manipulation
                            Matrix matrix = new Matrix();
                            // resize the bit map
                            matrix.postScale(scaleWidth, scaleHeight);

                            // recreate the new Bitmap
                            scaledBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
                            img.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, 200, 1200, true));
                            //img.setImageBitmap(imageBitmap);
                            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
                            InputImage image = InputImage.fromBitmap(imageBitmap, 0);
                            labeler.process(image)
                                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                        @Override
                                        public void onSuccess(List<ImageLabel> labels) {
                                            sb = new StringBuilder();
                                            for (ImageLabel label : labels) {
                                                String text = label.getText();
                                                float confidence = label.getConfidence();
                                                int index = label.getIndex();
                                                sb.append(label.getText()+" "+label.getConfidence()+"\n");
                                            }
                                            t.setText(sb.toString());
                                        }
                                    });
                        }
                    }
                });

    }
    public void takePic(View v) {
        resLaunch.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
    }
}