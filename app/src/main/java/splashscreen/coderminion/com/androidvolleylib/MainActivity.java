package splashscreen.coderminion.com.androidvolleylib;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    String URL_TO_DOWNLOAD_FILE= "https://github.com/coderminion/AndroidVolleyLib/raw/master/got_dracarys.mp4";
    String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private final static int REQUEST_CODE = 1010;
    private final static int REQUEST_PERMISSION_SETTING = 1211;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("downloading.....");
        progressDialog.setCancelable(false);


        //TODO DownloadFile only if Permission is Granted
        if(ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            downloadFile();
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            if(ActivityCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                //ALL OKAY
                downloadFile();
            }
            else
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission
                boolean showRationale = shouldShowRequestPermissionRationale( WRITE_EXTERNAL_STORAGE );
                if (! showRationale) {
                    // user also CHECKED "never ask again"
                    //Take user to settings screen
                    Toast.makeText(getApplicationContext(),"Storage Permission is required to complete the task",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

                } else {
                    // user did NOT check "never ask again"
                    Toast.makeText(getApplicationContext(),"Storage Permission is required to complete the task",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                }
            }
        }
    }

    private void downloadFile() {
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, URL_TO_DOWNLOAD_FILE,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        progressDialog.dismiss();

                        // TODO handle the response
                        try {
                            if (response!=null) {

                                String name="File.mp4";

                                File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/directory1/directory2/");
                                dir.mkdirs();

                                File videoFile = new File(dir.getAbsoluteFile()+"/"+name);

                                FileOutputStream stream = new FileOutputStream(videoFile);

                                try {
                                    stream.write(response);
                                } finally {
                                    stream.close();
                                }
                                Toast.makeText(MainActivity.this, "Download complete.", Toast.LENGTH_LONG).show();

                                showVideo(videoFile);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("ERROR!!", "NOT DOWNLOADED");
                            e.printStackTrace();
                        }
                    }
                } ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                error.printStackTrace();
                progressDialog.dismiss();
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        mRequestQueue.add(request);
        progressDialog.show();
    }

    // TO Show Video in ViewView
    private void showVideo(File videoFile) {
        Uri video = Uri.fromFile(videoFile);
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(video);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==REQUEST_PERMISSION_SETTING)
        {
            if(ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                downloadFile();
            }
            else
            {
                //Permission is not granted from settings
                ActivityCompat.requestPermissions(this,new String[]{WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }
}
