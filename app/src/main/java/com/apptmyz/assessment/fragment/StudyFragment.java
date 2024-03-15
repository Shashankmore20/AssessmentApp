package com.apptmyz.assessment.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.MainActivity2;
import com.apptmyz.assessment.activity.SubjectActivity;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.model.SubjectModel;
import com.apptmyz.assessment.utils.Util;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class StudyFragment extends Fragment {

    private DataSource dataSource;
    List<SubjectModel> subjectsList;

    GridView subjects_gv;
    ImageView iv_home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_study, container, false);

        dataSource = new DataSource(getContext());
        subjectsList = dataSource.subjects.getSubjects();
        iv_home = view.findViewById(R.id.iv_home);
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getContext(), MainActivity2.class);
                startActivity(intent);
            }
        });

        subjects_gv = view.findViewById(R.id.subjects_gv);
        SubjectsAdapter customAdapter = new SubjectsAdapter(getContext(), R.layout.subject_card, subjectsList);
        subjects_gv.setAdapter(customAdapter);

        subjects_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent;
                intent = new Intent(getContext(), SubjectActivity.class);
                intent.putExtra("subjectId", subjectsList.get(position).getId());
                intent.putExtra("subject", subjectsList.get(position).getSubjectName());
                startActivity(intent);

            }
        });

        return view;
    }


    public class SubjectsAdapter extends ArrayAdapter<SubjectModel> {

        List<SubjectModel> items_list = new ArrayList<>();
        int custom_layout_id;

        public SubjectsAdapter(@NonNull Context context, int resource, @NonNull List<SubjectModel> objects) {
            super(context, resource, objects);
            items_list = objects;
            custom_layout_id = resource;
        }

        @Override
        public int getCount() {
            return items_list.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(custom_layout_id, null);
            }

            CardView cardView = v.findViewById(R.id.cardView);
            if (position == 0) {
                cardView.getBackground().setTint(getResources().getColor(R.color.appcolor));
            } else if (position == 1) {
                cardView.getBackground().setTint(getResources().getColor(R.color.first));
            } else if (position == 2) {
                cardView.getBackground().setTint(getResources().getColor(R.color.second));
            } else if (position == 3) {
                cardView.getBackground().setTint(getResources().getColor(R.color.third));
            } else if (position == 4) {
                cardView.getBackground().setTint(getResources().getColor(R.color.appcolor));
            } else if (position == 5) {
                cardView.getBackground().setTint(getResources().getColor(R.color.first));
            } else if (position == 6) {
                cardView.getBackground().setTint(getResources().getColor(R.color.second));
            } else if (position == 7) {
                cardView.getBackground().setTint(getResources().getColor(R.color.third));
            } else if (position == 8) {
                cardView.getBackground().setTint(getResources().getColor(R.color.appcolor));
            } else if (position == 9) {
                cardView.getBackground().setTint(getResources().getColor(R.color.first));
            }

            ImageView imageView = v.findViewById(R.id.imageView);
            TextView textView = v.findViewById(R.id.subject);
            TextView topics = v.findViewById(R.id.topics);

            String item = items_list.get(position).getSubjectName();
            Glide.with(getContext()).load("http://104.237.10.23:8080/assessment-test/api/displayImage?image=" + items_list.get(position).getFileName()).into(imageView);
            Util.logD("http://104.237.10.23:8080/assessment-test/api/displayImage?image=" + items_list.get(position).getFileName());
            textView.setText(item);
            topics.setText(items_list.get(position).getTopics().size() + " Topics");
            return v;
        }
    }
}