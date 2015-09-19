package com.example.cybrary02.cybrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cybrary02.cybrary.R;
import com.example.cybrary02.cybrary.pojo.Course;

import java.util.ArrayList;


public class CourseAdapter extends ArrayAdapter<Course> {
    public CourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_course, parent, false);
        }

        TextView messageName = (TextView) convertView.findViewById(R.id.course_name);
        messageName.setText(course.name);

        // Return the completed view to render on screen
        return convertView;
    }
}