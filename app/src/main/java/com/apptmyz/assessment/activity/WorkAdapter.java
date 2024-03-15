package com.apptmyz.assessment.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.model.Work;
import com.apptmyz.assessment.utils.CalendarUtils;

import java.util.List;

public class WorkAdapter extends ArrayAdapter<Work> {
    public WorkAdapter(@NonNull Context context, List<Work> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Work work = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.work_cell, parent, false);

        TextView taskTv = convertView.findViewById(R.id.workTitleTv);
        TextView taskDescTv = convertView.findViewById(R.id.workDescTv);
        TextView fromTimeTv = convertView.findViewById(R.id.fromTimeTv);
        TextView toTimeTv = convertView.findViewById(R.id.toTimeTv);

        String fromTime = CalendarUtils.formattedTime(work.getFromTime());
        String toTime = CalendarUtils.formattedTime(work.getToTime());

        taskTv.setText(work.getName());
        taskDescTv.setText(work.getDesc());
        fromTimeTv.setText(fromTime);
        toTimeTv.setText(toTime);

        return convertView;
    }
}
