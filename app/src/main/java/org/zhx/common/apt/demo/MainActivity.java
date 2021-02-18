package org.zhx.common.apt.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.zhx.common.apt.annotation.FindView;

public class MainActivity extends AppCompatActivity {
    @FindView(id = R.id.fragment_container)
    FrameLayout layout;
    @FindView(id = R.id.activity_tv, name = "activity_string")
    TextView activityTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewFinder.init(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentTest()).commit();
    }
}