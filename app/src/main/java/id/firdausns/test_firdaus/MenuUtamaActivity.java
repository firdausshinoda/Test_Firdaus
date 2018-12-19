package id.firdausns.test_firdaus;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.firdausns.test_firdaus.Config.Config;
import id.firdausns.test_firdaus.Config.TabAdapter;
import id.firdausns.test_firdaus.Fragment.ListFragment;
import id.firdausns.test_firdaus.Fragment.RecordFragment;

public class MenuUtamaActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private TabAdapter adapter;
    private int height_toolbar,height_toolbar_8,height_tabs,height_stt_bar,weight_view;
    private int RECORD_AUDIO_REQUEST_CODE =123 ;
    private int sample_rat = 8000;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.background_toolbar) View view_toolbar;
    @BindView(R.id.background_toolbar_reveal) View view_tabs;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.linear_toolbar) LinearLayout linear_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AndroidBKTest");
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_settings_black_24dp));

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new RecordFragment(), "Recorder");
        adapter.addFragment(new ListFragment(), "File");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getPermissionToRecordAudio();
        }
        setHightToolbar();
    }

    private void setHightToolbar() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            height_toolbar = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        height_stt_bar = Config.getStatusBarHeight(this);
        height_tabs = height_toolbar;
        height_toolbar_8 = (height_toolbar/8)*3;
        weight_view = toolbar.getWidth();

        height_tabs = height_toolbar;
        height_toolbar_8 = (height_toolbar/8)*3;
        weight_view = toolbar.getWidth();

        int size = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            size = height_toolbar+height_tabs+(height_toolbar_8/2);
        } else {
            size = height_toolbar+height_tabs+(height_toolbar_8*2);
        }
        view_toolbar.getLayoutParams().height = height_stt_bar+size;
        view_tabs.getLayoutParams().height = height_stt_bar+size;
//        view_toolbar.getLayoutParams().height = Config.getStatusBarHeight(this);
        view_toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        linear_toolbar.setPadding(10,Config.getStatusBarHeight(this)+10,10,0);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        reveal(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void reveal(final int position){
        final int FIRST_COLOR = getResources().getColor(R.color.colorPrimary);
        final int SECOND_COLOR = getResources().getColor(R.color.Colororange_500);
        float x = 0,y = 0;

        view_tabs.setVisibility(View.VISIBLE);
        int cx = view_tabs.getWidth();
        int cy = view_tabs.getHeight();

        int width_2 = cx/2;
        int height_2 = (cy/2)*3;
        if (position == 0){
            x = width_2/2;
            y = height_2/2;
        } else if (position == 1){
            x = width_2+(width_2/2);
            y = height_2/2;
        }

        float finalRadius = Math.max(cx, cy) * 1.2f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Animator reveal = ViewAnimationUtils.createCircularReveal(view_tabs, (int) x, (int) y, 0f, finalRadius);

            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (position == 0) {
                        view_toolbar.setBackgroundColor(FIRST_COLOR);
                    } else if (position == 1) {
                        view_toolbar.setBackgroundColor(SECOND_COLOR);
                    }
                    view_tabs.setVisibility(View.INVISIBLE);
                }
            });
            if (position == 0) {
                view_tabs.setBackgroundColor(FIRST_COLOR);
            } else if (position == 1){
                view_tabs.setBackgroundColor(SECOND_COLOR);
            }
            reveal.start();
        } else {
            if (position == 0) {
                view_tabs.setBackgroundColor(FIRST_COLOR);
            } else if (position == 1){
                view_tabs.setBackgroundColor(SECOND_COLOR);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_AUDIO_REQUEST_CODE);

        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){
            } else {
                Toast.makeText(this, "Silahkan berikan akses untuk dapat merekam.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }

    }

    public void setSample_rat(int sample){
        sample_rat = sample;

        int pos = viewPager.getCurrentItem();
        Fragment activeFragment = adapter.getItem(pos);
        if(pos == 1)
            ((ListFragment)activeFragment).refresh();
    }

    public int getSample_rat(){
        return sample_rat;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Popup.newInstance(sample_rat).show(getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class Popup extends DialogFragment {
        private View v;
        private Context context;
        private int sample_rat;
        @BindView(R.id.btn_8)
        Button btn_8;
        @BindView(R.id.btn_16)
        Button btn_16;


        public static Popup newInstance(int sample_rat) {
            Popup popup = new Popup();
            popup.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Dialog);

            Bundle args = new Bundle();
            args.putInt("sample_rat", sample_rat);
            popup.setArguments(args);
            return popup;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sample_rat = getArguments().getInt("sample_rat");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            v = inflater.inflate(R.layout.popup, container, false);
            ButterKnife.bind(this, v);
            context = getContext();

            return v;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            if (sample_rat == 8000){
                btn_8.setBackground(getResources().getDrawable(R.drawable.rounded_corner_btn_red_2_with_ripple));
                btn_16.setBackground(getResources().getDrawable(R.drawable.rounded_corner_btn_red_with_ripple));
            } else if (sample_rat == 16000){
                btn_8.setBackground(getResources().getDrawable(R.drawable.rounded_corner_btn_red_with_ripple));
                btn_16.setBackground(getResources().getDrawable(R.drawable.rounded_corner_btn_red_2_with_ripple));
            }
        }

        @OnClick(R.id.btn_8) void OnClick_btn_8(){
            ((MenuUtamaActivity)getActivity()).setSample_rat(8000);
            dismiss();
        }

        @OnClick(R.id.btn_16) void OnClick_btn_16(){
            ((MenuUtamaActivity)getActivity()).setSample_rat(16000);
            dismiss();
        }
    }

}
