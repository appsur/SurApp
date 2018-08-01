package com.pusheenicorn.safetyapp;//package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AlarmController implements Parcelable {
    Context context;
    MediaPlayer mp;
    AudioManager mAudioManager;
    int userVolume;


    public AlarmController(Context c) { // constructor for my alarm controller class
        this.context = c;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //remeber what the user's volume was set to before we change it.
        userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        mp = new MediaPlayer();
    }

    protected AlarmController(android.os.Parcel in) {
        userVolume = in.readInt();
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(userVolume);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlarmController> CREATOR = new Creator<AlarmController>() {
        @Override
        public AlarmController createFromParcel(android.os.Parcel in) {
            return new AlarmController(in);
        }

        @Override
        public AlarmController[] newArray(int size) {
            return new AlarmController[size];
        }
    };

    //    public void playSound(String soundURI){
    public void playSound() {
        Uri alarmSound = null;
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


        try{
//            alarmSound = Uri.parse(soundURI);
            InputStream inputStream  = context.getResources().openRawResource(R.raw.policesiren);
            DataInputStream dataInputStream = new DataInputStream(inputStream);
        }catch(Exception e){
            alarmSound = ringtoneUri;
        }
        finally{
            if(alarmSound == null){
                alarmSound = ringtoneUri;
            }
        }



        try {

            if(!mp.isPlaying()){
                mp.setDataSource(context, alarmSound);
                mp.setAudioStreamType(AudioManager.STREAM_ALARM);
                mp.setLooping(true);
                mp.prepare();
                mp.start();
            }


        } catch (IOException e) {
            Toast.makeText(context, "Your alarm sound was unavailable.", Toast.LENGTH_LONG).show();

        }
        // set the volume to what we want it to be.  In this case it's max volume for the alarm stream.
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);

    }

    public void stopSound(){
// reset the volume to what it was before we changed it.
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);
        mp.stop();
        mp.reset();

    }
    public void releasePlayer(){
        mp.release();
    }
}

//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import android.app.Activity;
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioTrack;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//
//import com.pusheenicorn.safetyapp.R;
//
//public class AlarmController extends AppCompatActivity
//{
//    @Override
//    public void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        PlayWaveFile();
//    }
//
//    private void PlayWaveFile()
//    {
//        // define the buffer size for audio track
//        int minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
//        int bufferSize = 512;
//        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000, AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
//
//        Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + ;
//        File file = new File(url.toString());
//
//        int count = 0;
//        byte[] data = new byte[bufferSize];
//
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
//            audioTrack.play();
//
//            while((count = dataInputStream.read(data, 0, bufferSize)) > -1)
//            {
//                audioTrack.write(data, 0, count);
//            }
//
//            audioTrack.stop();
//            audioTrack.release();
//            dataInputStream.close();
//            fileInputStream.close();
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

