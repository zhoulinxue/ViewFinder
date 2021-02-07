package org.zhx.common.apt.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.zhx.common.apt.annotation.FindView;
import org.zhx.common.apt.annotation.ViewFinder;

/**
 * @ProjectName: demo
 * @Package: org.zhx.common.apt.demo
 * @ClassName: FragmentTest
 * @Description:java类作用描述
 * @Author: zhouxue
 * @CreateDate: 2020/9/3 11:21
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/9/3 11:21
 * @UpdateRemark: 更新说明
 * @Version:1.0
 */
public class FragmentTest extends Fragment {
    @FindView(id = R.id.hello_tv)
    TextView hello;
    @FindView(id = R.id.hello_tv2,name="新字符")
    TextView setTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewFinder.init(view);
        hello.setText("测试内容");
    }
}
