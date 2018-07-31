package com.pusheenicorn.safetyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VideoActivity extends BaseActivity implements DialogInterface.OnClickListener, SurfaceHolder.Callback {

    public static final String LOGTAG = "VIDEOCAPTURE";

    //declare variable for media recorder
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private CamcorderProfile camcorderProfile;
    private Camera camera;

    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;

    // Define bottom navigation view.
    BottomNavigationView bottomNavigationView;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //TODO- see if camcorder profile quality can be increased without crashing the application
        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);

        setContentView(R.layout.activity_video);

        // Logic for bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation_video);
        setNavigationDestinations(VideoActivity.this, bottomNavigationView);

        initializeNavItems(mNavItems);
        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position, mDrawerList, mDrawerPane, mDrawerLayout,
                        VideoActivity.this, mNavItems);
            }
        });

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        prepareRecorder();

        cameraView.setClickable(true);
        cameraView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recording) {
                    recorder.stop();
                    if (usecamera) {
                        try {
                            camera.reconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // recorder.release();
                    recording = false;
                    Log.v(LOGTAG, "Recording Stopped");
                    Toast.makeText(VideoActivity.this, "Successfully recorded video", Toast.LENGTH_SHORT).show();
                    Intent finishRecording = new Intent(VideoActivity.this, MainActivity.class);
                    startActivity(finishRecording);
                    // Let's prepareRecorder so we can record again
//                    prepareRecorder();
                } else {
                    recording = true;
                    recorder.start();
                    Log.v(LOGTAG, "Recording Started");
                }
            }
        });

    }

        private void prepareRecorder() {
            recorder = new MediaRecorder();
            recorder.setPreviewDisplay(holder.getSurface());

            if (usecamera) {
                camera.setDisplayOrientation(90); // use for set the orientation of the preview
                recorder.setOrientationHint(90); // use for set the orientation of output video
                camera.unlock();
                recorder.setCamera(camera);
            }

            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

            recorder.setProfile(camcorderProfile);

            // This is all very sloppy
            if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
                try {
                    File newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStorageDirectory());
                    recorder.setOutputFile(newFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.v(LOGTAG,"Couldn't create file");
                    e.printStackTrace();
                    finish();
                }
            } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
                try {
                    File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
                    recorder.setOutputFile(newFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.v(LOGTAG,"Couldn't create file");
                    e.printStackTrace();
                    finish();
                }
            } else {
                try {
                    File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
                    recorder.setOutputFile(newFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.v(LOGTAG,"Couldn't create file");
                    e.printStackTrace();
                    finish();
                }

            }
            //recorder.setMaxDuration(50000); // 50 seconds
            //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes

            try {
                recorder.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceCreated");

        if (usecamera) {
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }
        }

    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(LOGTAG, "surfaceChanged");

        if (!recording && usecamera) {
            if (previewRunning){
                camera.stopPreview();
            }

            try {
                Camera.Parameters p = camera.getParameters();

                p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
                p.setPreviewFrameRate(camcorderProfile.videoFrameRate);

                camera.setParameters(p);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }

            prepareRecorder();
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceDestroyed");
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        if (usecamera) {
            previewRunning = false;
            //camera.lock();
            camera.release();
        }
        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
