package com.example.cookbookapp.Utility;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {

    public static final long MAX_IMAGE_BYTES = 1572864; // 1.5 MB

    public static MultipartBody.Part getImageAsMultipart(File originalFile) {
        RequestBody filePart = RequestBody.create(MediaType.parse("image/*"), originalFile);
        MultipartBody.Part file = MultipartBody.Part
                .createFormData("image", originalFile.getName(), filePart);

        return file;
    }

}
