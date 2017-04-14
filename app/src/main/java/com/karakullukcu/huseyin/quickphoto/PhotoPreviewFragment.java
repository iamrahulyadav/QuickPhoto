package com.karakullukcu.huseyin.quickphoto;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class PhotoPreviewFragment extends Fragment {


    public PhotoPreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_preview, container, false);
        ImageView imagePreviewView = (ImageView) rootView.findViewById(R.id.mainImageView);
        Bitmap imageBitmap = getArguments().getParcelable(getString(R.string.taken_picture_bitmap));
        imagePreviewView.setImageBitmap(imageBitmap);

        return rootView;
    }

}
