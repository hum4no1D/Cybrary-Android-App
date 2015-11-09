package com.cybrary.app.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cybrary.app.R;
import com.cybrary.app.pojo.Course;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class CourseAdapter extends ArrayAdapter<Course> implements StickyListHeadersAdapter {
    public CourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getHeaderView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_course_header, parent, false);
        }

        String category = getItem(i).category;
        ((TextView) convertView.findViewById(R.id.section_header)).setText(category);
        return convertView;
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).category.hashCode();
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
        messageName.setText(Html.fromHtml(course.name));

        // Return the completed view to render on screen
        return convertView;
    }
}