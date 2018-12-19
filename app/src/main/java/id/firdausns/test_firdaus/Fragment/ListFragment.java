package id.firdausns.test_firdaus.Fragment;


import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.firdausns.test_firdaus.Config.Config;
import id.firdausns.test_firdaus.Config.ItemRecord;
import id.firdausns.test_firdaus.Config.MyAdapterListRecord;
import id.firdausns.test_firdaus.MenuUtamaActivity;
import id.firdausns.test_firdaus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    @BindView(R.id.recycler) RecyclerView recyclerView;
    @BindView(R.id.tv_no_data) TextView tv_no_data;
    @BindView(R.id.seekBar) SeekBar seekBar;
    @BindView(R.id.tv_nama) TextView tv_nama;
    @BindView(R.id.tv_sample) TextView tv_sample;
    public ArrayList<ItemRecord> itemRecords = new ArrayList<>();
    private MyAdapterListRecord myAdapterListRecord;
    private Context context;
    private MediaPlayer mPlayer;
    private Handler mHandler = new Handler();

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        context = getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            fetchData();
        }
    }

    private void fetchData(){
        itemRecords.clear();

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), Config.IMAGE_DIRECTORY_NAME);
        File[] files = directory.listFiles();
        if( files!=null ){
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = directory.getAbsolutePath() + File.separator + fileName;

                int sample_rat = ((MenuUtamaActivity)getActivity()).getSample_rat();
                if (sample_rat == Integer.valueOf(Config.getSample(recordingUri)+"000")){
                    itemRecords.add(new ItemRecord(recordingUri,fileName));
                }
            }
            myAdapterListRecord = new MyAdapterListRecord(itemRecords,getContext());
            recyclerView.setAdapter(myAdapterListRecord);

            tv_no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            myAdapterListRecord.SetOnItemClickListener(new MyAdapterListRecord.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, String uri_, String filename_) {
                    String uri = uri_;
                    String filename = filename_;
                    playing(uri,filename);
                }
            });

        }else{
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void playing(String uri, String filename){
        mPlayer = new MediaPlayer();
        seekBar.setProgress(0);
        try {
            mPlayer.setDataSource(uri);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }

        tv_nama.setText(filename);
        tv_sample.setText(Config.getSample(uri)+" kHz");

        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();
    }



    private void seekUpdation() {
        if(mPlayer != null){
            int mCurrentPosition = mPlayer.getCurrentPosition() ;
            seekBar.setProgress(mCurrentPosition);
        }
        mHandler.postDelayed(runnable, 100);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void refresh(){
        fetchData();
    }
}
