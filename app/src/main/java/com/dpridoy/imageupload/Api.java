package com.dpridoy.imageupload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    //     /storage/emulated/0/DCIM/Camera/IMG_20200324_170456.jpg

    @Multipart
    @POST("api.php?apicall=upload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file, @Part("desc") RequestBody requestBody);
}
