package id.firdausns.test_firdaus.Fragment;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.firdausns.test_firdaus.Config.Config;
import id.firdausns.test_firdaus.MenuUtamaActivity;
import id.firdausns.test_firdaus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    @BindView(R.id.btn_stop)
    ImageButton btn_stop;
    @BindView(R.id.btn_record)
    ImageView btn_record;
    @BindView(R.id.chron)
    Chronometer chron;
    boolean status = false;
    private MediaRecorder mRecorder;
    private String fileName = null;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private Context context;

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private int RECORDER_SAMPLERATE;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        context = getContext();
    }

    @OnClick(R.id.btn_record) void OnClick_btn_record(){
        if (status == false){
            RECORDER_SAMPLERATE = ((MenuUtamaActivity)getActivity()).getSample_rat();

            chron.setBase(SystemClock.elapsedRealtime());
            bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);

            btn_stop.setBackground(getResources().getDrawable(R.drawable.circular_rounded_corner_red));
            btn_record.setBackground(getResources().getDrawable(R.drawable.circular_rounded_corner_green_2));
            record();
            status = true;
        }
    }

    @OnClick(R.id.btn_stop) void OnClick_btn_stop(){
        if (status == true){
            status = false;
            btn_stop.setBackground(getResources().getDrawable(R.drawable.circular_rounded_corner_red_2));
            btn_record.setBackground(getResources().getDrawable(R.drawable.circular_rounded_corner_green));
            stop();
        }
    }

    private void record() {
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), Config.IMAGE_DIRECTORY_NAME);
//        if(!mediaStorageDir.exists()) {
//            mediaStorageDir.mkdirs();
//        }

//        File root = android.os.Environment.getExternalStorageDirectory();
//        File file = new File(root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios");
//        if (!file.exists()) {
//            file.mkdirs();
//        }

//        fileName =  root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios/" + String.valueOf(System.currentTimeMillis() + ".mp3");
//        fileName = mediaStorageDir.getPath() + File.separator + String.valueOf(System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);

//        Log.d("filename",fileName);
//        mRecorder.setOutputFile(fileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mRecorder.prepare();
//            mRecorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        lastProgress = 0;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        if(i==1){
            recorder.startRecording();
        }

        isRecording = true;
        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"AudioRecorder Thread");

        recordingThread.start();
        chron.setBase(SystemClock.elapsedRealtime());
        chron.start();
    }

    private String getFilename(){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), Config.IMAGE_DIRECTORY_NAME);
        if(!file.exists()){
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getTempFilename(){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), Config.IMAGE_DIRECTORY_NAME);
        if(!file.exists()){
            file.mkdirs();
        }
        File tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),AUDIO_RECORDER_TEMP_FILE);
        if(tempFile.exists()){
            tempFile.delete();
        }
        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void writeAudioDataToFile(){
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int read = 0;
        if(null != os){
            while(isRecording){
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stop(){
//        try{
//            mRecorder.stop();
//            mRecorder.release();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        mRecorder = null;
//        chron.stop();
//        chron.setBase(SystemClock.elapsedRealtime());
//        Toast.makeText(context, "Rekaman berhasil disimpan.", Toast.LENGTH_SHORT).show();

        if(null != recorder) {
            isRecording = false;

            int i = recorder.getState();
            if (i == 1) {
                recorder.stop();
                chron.stop();
                chron.setBase(SystemClock.elapsedRealtime());
            }
            recorder.release();

            recorder = null;
            recordingThread = null;
        }
        copyWaveFile(getTempFilename(),getFilename());
        deleteTempFile();
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            Log.i("AudioRecord","File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}
