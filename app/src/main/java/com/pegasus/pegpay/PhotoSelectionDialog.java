package com.pegasus.pegpay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import com.androidquery.AQuery;

import java.io.File;

/**
 * Created by Zed on 4/26/2016.
 */
public class PhotoSelectionDialog extends DialogFragment {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    public static File photoFile;
    AQuery aq;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.selection_dialog, null);
        aq = new AQuery(view);
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            aq.id(R.id.txt_camera).gone();
        }
        builder.setView(view);

        aq.id(R.id.txt_camera).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        aq.id(R.id.txt_gallery).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    private void openCamera(){
        dismiss();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            photoFile = SignUpFragment.createImageFile();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGallery(){
        dismiss();
        Utils.log("openGallery()");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
}
