package com.dpridoy.imageupload;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks{
    
    private ImageView image_view;
    private Button button_upload;

    public static final int PICK_IMAGE = 1;
    private String selectedImagePath;
    private ProgressBar progressBar;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_view=(ImageView)findViewById(R.id.image_view);
        button_upload=(Button)findViewById(R.id.button_upload);

        progressBar = findViewById(R.id.progress_bar);
        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void upload() {
        Retrofit retrofit=RetrofitClient.getRetrofitClient(this);
        Api api=retrofit.create(Api.class);
        File file = new File(selectedImagePath);
        ProgressRequestBody fileBody = new ProgressRequestBody(file,this);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), fileBody);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        Call<ResponseBody> call = api.uploadImage(filePart, description);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.e("Upload success:", response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });

    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                selectedImageUri = data.getData();
                selectedImagePath = getRealPathFromURI(selectedImageUri);
                Log.e("Image Path",selectedImagePath);
                image_view.setImageURI(selectedImageUri);
            }
        }
    }



    // This works now
    private String getRealPathFromURI(Uri contentUri) {
        File file = new File(contentUri.getPath());
        String imagePath="";
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];
        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return imagePath;
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressBar.setProgress(percentage);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        progressBar.setProgress(100);
    }
}
