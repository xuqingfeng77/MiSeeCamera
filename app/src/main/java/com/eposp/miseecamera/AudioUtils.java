package com.eposp.miseecamera;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by xuqingfeng on 16/5/11.
 */
public class AudioUtils {
    public static void setMinVolume(Context ctx){
        AudioManager audioManager=(AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int minVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,4);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,4);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM,0,4);
    }
}
