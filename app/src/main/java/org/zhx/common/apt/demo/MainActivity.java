package org.zhx.common.apt.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import org.zhx.common.apt.annotation.FindView;
import org.zhx.common.apt.annotation.ViewFinder;

public class MainActivity extends AppCompatActivity {
    @FindView(id = R.id.fragment_container)
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewFinder.init(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentTest()).commit();
    }
}