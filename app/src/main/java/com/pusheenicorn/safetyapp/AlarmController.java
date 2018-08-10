package com.pusheenicorn.safetyapp;//package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcelable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AlarmController implements Parcelable {
    Context context;
    MediaPlayer mp;
    private AudioManager mAudioManager;
    int userVolume;
    Uri alarmSound;


    public AlarmController(Context c) { // constructor for my alarm controller class
        this.context = c;
        //allows access to sound settings
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //store what the user's volume was set to
        userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        //used to play sounds
//        mp = new MediaPlayer();
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
        mp = new MediaPlayer();
        Uri alarmSound = null;
        //setting ringtone
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


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
            if (!mp.isPlaying()) {
                mp.setDataSource(context, alarmSound);
                mp.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                );
                mp.setAudioStreamType(AudioManager.STREAM_ALARM);
                mp.setLooping(true);
                mp.prepare();
                mp.start();
            }
        } catch (IOException e) {


        }
        // set the volume to max volume
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                AudioManager.FLAG_PLAY_SOUND);
    }

    public void stopSound() {
// reset the volume to what it was before we changed it.
        if (mp != null && mp.isPlaying()) {
            //return to original user volume
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);
            releaseMediaPlayer();
//            mp.stop();
//            mp.release();
        }
    }

    private void releaseMediaPlayer() {
        try {
            if (mp != null) {
                if (mp.isPlaying())
                    mp.stop();
                mp.release();
                mp.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
