package com.pusheenicorn.safetyapp.activities;

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
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pusheenicorn.safetyapp.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VideoActivity extends BaseActivity implements DialogInterface.OnClickListener, SurfaceHolder.Callback {
    public static final String LOGTAG = "VIDEOCAPTURE";

    //declare variable for media recorder (used to record video from app directly)
    private MediaRecorder mRecorder;
    //interface for someone holding a display surface
    private SurfaceHolder holder;
    //provides preset qualities
    private CamcorderProfile camcorderProfile;
    private Camera camera;
    ImageButton ibRecord;
    TextView tvStop;

    //booleans to keep track of whether or not the video attributes should be shown
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

    //variable for checking the length of the video
    long duration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //ask system to exclude title at the top of the screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set the orientation of the screen to be portrait, avoiding the auto-adjust to landscape for the app
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //set the camera quality
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

        //initialize the surfaceview
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        //create buffers for the camera device
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        tvStop = findViewById(R.id.tvStop);
        tvStop.setVisibility(View.INVISIBLE);

        //initialize and set the button that starts and ends recorder
        ibRecord = findViewById(R.id.ibRecord);
        ibRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the recorder is recording, then the recorder should be stopped on click
                if (recording) {
                    //check to make sure that the video is not too short
                    if (duration != 0) {
                        try {
                            mRecorder.stop();
                            releaseMediaRecorder();
                            recording = false;
                            //finish recording and notify user, then return to main activity
                            Toast.makeText(VideoActivity.this, "Successfully recorded video", Toast.LENGTH_SHORT).show();
                            Intent finishRecording = new Intent(VideoActivity.this, MainActivity.class);
                            startActivity(finishRecording);
                        }
                        catch (IllegalStateException e)
                        {
                            Toast.makeText(VideoActivity.this, "Successfully recorded video", Toast.LENGTH_SHORT).show();
                            Intent finishRecording = new Intent(VideoActivity.this, MainActivity.class);
                            startActivity(finishRecording);
                        }
                    }
                    if (usecamera) {
                        try {
                            camera.reconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.v(LOGTAG, "Recording Stopped");
                    //finish recording and notify user, then return to main activity
                    Toast.makeText(VideoActivity.this, "Successfully recorded video", Toast.LENGTH_SHORT).show();
                    Intent finishRecording = new Intent(VideoActivity.this, MainActivity.class);
                    startActivity(finishRecording);
                } else if (!recording) {
                    if (duration == 0) {
                        ibRecord.setVisibility(View.INVISIBLE);
                        try {
                            mRecorder.start();
                            tvStop.setVisibility(View.VISIBLE);
                            ibRecord.setVisibility(View.VISIBLE);
                            Log.v(LOGTAG, "Recording Started");
                            recording = true;
                        }
                        catch (IllegalStateException e)
                        {
                            Toast.makeText(VideoActivity.this, "Successfully recorded video", Toast.LENGTH_SHORT).show();
                            Intent finishRecording = new Intent(VideoActivity.this, MainActivity.class);
                            startActivity(finishRecording);
                        }
                    }
                }
            }
        });

    }

    /**
     * set up the recorder and the place to save the recorder to based on file format
     */
    private void prepareRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setPreviewDisplay(holder.getSurface());

        if (usecamera) {
            //set the camera to be portrait mode
            camera.setDisplayOrientation(90); // use for set the orientation of the preview
            mRecorder.setOrientationHint(90); // use for set the orientation of output video
            camera.unlock();
            mRecorder.setCamera(camera);
        }

        //allow recorder to have audio and video
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        mRecorder.setProfile(camcorderProfile);

        //saving file based on the format
        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
            try {
                File newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStorageDirectory());
                duration = newFile.length();
                mRecorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            try {
                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
                duration = newFile.length();
                mRecorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else {
            try {
                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
                duration = newFile.length();
                mRecorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
                finish();
            }

        }

        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    /**
     * function for discovering when the surface is created (called after surface is first created)
     * @param holder pass in the interface for the surface
     */
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceCreated");

        if (usecamera) {
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            } catch (IOException e) {
                Log.e(LOGTAG, e.getMessage());
                e.printStackTrace();
            }
        }

    }

    /**
     * called after a structural change has been made
     * @param holder surfaceholder that has a holder changed
     * @param format new pixelformat of the screen
     * @param width new width
     * @param height new height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(LOGTAG, "surfaceChanged");

        if (!recording && usecamera) {
            if (previewRunning) {
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
            } catch (IOException e) {
                Log.e(LOGTAG, e.getMessage());
                e.printStackTrace();
            }

            prepareRecorder();
        }
    }


    /**
     * function for destorying the surface as the window is hidden
     * @param holder pass in the interface for the surface
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceDestroyed");
        if (recording) {
            mRecorder.stop();
            recording = false;
        }
        mRecorder.release();
        if (usecamera) {
            previewRunning = false;
            camera.lock();
            camera.release();
        }
        finish();
    }

    /**
     * default function that is necessary to the class
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    /**
     * function to release the media recorder after a video has finished recording
     */
    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.reset();   // clear recorder configuration
            mRecorder.release(); // release the recorder object
            mRecorder = null;
        }

    }

}