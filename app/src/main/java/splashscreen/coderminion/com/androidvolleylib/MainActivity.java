package splashscreen.coderminion.com.androidvolleylib;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String mUrl= "https://raw.githubusercontent.com/coderminion/SearchDataFromListView/master/device-2017-08-18-125137.mp4";
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
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
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        mRequestQueue.add(request);

    }

}
