package org.team.eye;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import eu.kudan.kudan.ARActivity;

public class ARCameraActivity extends ARActivity {

    @Override
    public void setup() {
        super.setup();

        Button cameraButton = findViewById(R.id.ARCameraActivity_CameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getARView().screenshot();

                String filename = System.currentTimeMillis() + ".png";
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, filename , "Eye");
            }
        });
    }
}
