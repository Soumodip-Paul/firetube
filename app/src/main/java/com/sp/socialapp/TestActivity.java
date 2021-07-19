package com.sp.socialapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sp.socialapp.adapters.TabAdapter;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_demo);

//        DemoCollectionAdapter demoCollectionAdapter;
//        ViewPager2 viewPager;
//        demoCollectionAdapter = new DemoCollectionAdapter(this);
//        viewPager = findViewById(R.id.pager);
//        viewPager.setAdapter(demoCollectionAdapter);
        ViewPager2 viewPager2 = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        TabAdapter adapter = new TabAdapter();
        viewPager2.setAdapter(adapter);

        //must be implemented after attaching an adapter
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText("OBJECT " + (position + 1))
        ).attach();

        findViewById(R.id.fab).setOnClickListener(v -> {
            startActivity(new Intent(this,TabbedActivity.class));
        });
    }
}