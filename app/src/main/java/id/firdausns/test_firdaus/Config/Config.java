package id.firdausns.test_firdaus.Config;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;

public class Config {

    public static final String IMAGE_DIRECTORY_NAME = "Test Firdaus";

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getSample(String uri){
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(uri);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MediaFormat mf = mex.getTrackFormat(0);
//        int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
        int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        String sam = String.valueOf(sampleRate);
        sam = sam.substring(0,2);
        if (sam.substring(0).equals("16")){
            return "16";
        } else {
            return "8";
        }
    }
}
