package com.apptmyz.assessment.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apptmyz.assessment.R;

import com.apptmyz.assessment.activity.RefreshableFragment;
import com.apptmyz.assessment.database.ScheduleHelper;
import com.apptmyz.assessment.model.ScheduleModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends Fragment/* implements CalendarAdapter.OnItemListener*/ {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ScheduleHelper scheduleHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        scheduleHelper = new ScheduleHelper(getContext());

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Today"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("Calendar"), 1);
        viewPager = view.findViewById(R.id.vPager);

        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(ScheduleFragment.this);
        viewPager.setAdapter(scheduleAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0)
                    tab.setText("Today");
//                Intent intent = new Intent(getContext(), TodayScheduleFragment.class);
//                startActivity(intent);
                if (position == 1)
                    tab.setText("Calendar");
//                Intent intent1 = new Intent(getContext(), WeekScheduleFragment.class);
//                startActivity(intent1);
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                RefreshableFragment selectedFragment = scheduleAdapter.getFragment(tab.getPosition());
                if (selectedFragment != null) {
                    selectedFragment.refresh();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.setCurrentItem(0);

        return view;
    }

    class ScheduleAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        public ScheduleAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            if (position == 0) {
                fragment = new TodayScheduleFragment();
            } else {
                fragment = new WeekScheduleFragment();
            }
            fragments.add(fragment);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
        public RefreshableFragment getFragment(int position) {
            if (position < fragments.size()) {
                return (RefreshableFragment) fragments.get(position);
            } else {
                Log.e("ScheduleAdapter", "Index out of bounds: " + position);
                return null;
            }
        }
    }
}