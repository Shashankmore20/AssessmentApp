package com.apptmyz.assessment.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.apptmyz.assessment.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import java.util.ArrayList;

public class ReviewTestActivity extends Activity {

    float arr[] = new float[]{1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_answers);
        SparkView sparkView = (SparkView) findViewById(R.id.sparkview);

        sparkView.setAdapter(new MyAdapter(arr));

    }

    public class MyAdapter extends SparkAdapter {
        private float[] yData;

        public MyAdapter(float[] yData) {
            this.yData = yData;
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }

        @Override
        public boolean hasBaseLine() {
            return true;
        }
    }

}
