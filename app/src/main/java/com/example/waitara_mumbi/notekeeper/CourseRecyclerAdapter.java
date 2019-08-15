package com.example.waitara_mumbi.notekeeper;

import android.content.Context;

import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>{ //1 //3

    private final Context mContext; //4
    private final List<CourseInfo> mCourses;//8
    private final LayoutInflater mLayoutInflater;//5

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mCourses = courses;
        mLayoutInflater = LayoutInflater.from(mContext); //to create views  from layout resource //it inflates the layout resources into the actual view hierarchy
    }

    @NonNull
    @Override //create our view holder instances and views themselves.To create the views, we need a context - something that we use for factoring things like creating things
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list,viewGroup, false);//6 //takes the layout resource to inflate, viewgoup it is being inflated within,false meaning no attachment to parent
        return new ViewHolder(itemView);
    }

    @Override //responsible for associating data with our views
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {//i is the position of the note
        CourseInfo course = mCourses.get(position);//9
        viewHolder.mTextCourse.setText(course.getTitle());


        viewHolder.mCurrentPosition = position;//11

    }

    @Override//indicate the number of data items we have
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //the references to the views
        public final TextView mTextCourse;//7

        public int mCurrentPosition;//10

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, mCourses.get(mCurrentPosition).getTitle(), Snackbar.LENGTH_LONG).show();
                }
            });

        }
    }
}
