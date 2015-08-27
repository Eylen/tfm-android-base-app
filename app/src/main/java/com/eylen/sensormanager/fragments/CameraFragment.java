package com.eylen.sensormanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eylen.sensormanager.R;
import com.eylen.sensormanager.activities.MainActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Saioa on 12/08/2015.
 */
public class CameraFragment extends PlaceholderFragment {
    private static final String TAG = "CameraFragment";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private TextView mImageUrl;
    private Button mTakePictureButton;
    private ImageView mThumbnail;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public CameraFragment() {
        super();
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CameraFragment newInstance(int sectionNumber) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mImageUrl = (TextView) getView().findViewById(R.id.image_url);
        mThumbnail = (ImageView) getView().findViewById(R.id.thumbnail);
        mTakePictureButton = (Button) getView().findViewById(R.id.take_picture);
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // getOutputMediaFileUri creará el archive donde almacenar la imagen y devolverá la Uri correspondiente
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                MainActivity activity = (MainActivity) getActivity();
                activity.setCapturedImageURI(fileUri);
                activity.setCurrentPhotoPath(fileUri.getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // indicar la información extra

                // arrancar el Intent de captura de imagen
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_fragment, container, false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                MainActivity activity = (MainActivity) getActivity();
                mImageUrl.setText(activity.getCurrentPhotoPath());
                mThumbnail.setImageURI(activity.getmCapturedImageURI());
                // Imagen capturada y almacenada en el fileUri indicado en el Intent
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // El usuario ha cancelado la captura de imagen
                Toast.makeText(getActivity(), "Image capture has been canceled", Toast.LENGTH_LONG).show();
            } else {
                // La captura de imagen falló, avisar al usuario
                Toast.makeText(getActivity(), "Oh oh... Something happened while capturing image", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SensorManager");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("SensorManager", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
