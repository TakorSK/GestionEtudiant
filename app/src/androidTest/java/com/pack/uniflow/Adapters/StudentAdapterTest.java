package com.pack.uniflow.Adapters;

import static org.junit.Assert.*;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.pack.uniflow.Models.Student; // Import Student

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Instrumented tests for StudentAdapter.
 */
@RunWith(AndroidJUnit4.class)
public class StudentAdapterTest {

    private Context context;
    private List<Student> testStudentList;
    private StudentAdapter adapter;
    private Student onlineStudent;
    private Student offlineStudent;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Create sample data (using 7-arg constructor)
        onlineStudent = new Student(101, "online@test.com", "Online Alice", 20, "111", 1, "pass");
        onlineStudent.isOnline = true;

        offlineStudent = new Student(102, "offline@test.com", "Offline Bob", 21, "222", 1, "pass");
        offlineStudent.isOnline = false;

        testStudentList = new ArrayList<>(Arrays.asList(onlineStudent, offlineStudent));
        adapter = new StudentAdapter(testStudentList);
    }

    @Test
    public void getItemCount_returnsCorrectSize() {
        // Explanation: Checks item count.
        assertEquals(testStudentList.size(), adapter.getItemCount());
    }

    @Test
    public void onCreateViewHolder_inflatesCorrectLayout() {
        // Explanation: Checks layout inflation and ViewHolder creation.
        FrameLayout parent = new FrameLayout(context);
        StudentAdapter.StudentViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        assertNotNull(viewHolder);
        assertNotNull(viewHolder.itemView);
    }

    @Test
    public void onBindViewHolder_forOnlineStudent_setsCorrectDataAndColor() {
        // Explanation: Verifies binding for an online student: name, status text "Online", green color.
        FrameLayout parent = new FrameLayout(context);
        StudentAdapter.StudentViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 0; // Online student is first

        adapter.onBindViewHolder(holder, position);

        assertEquals(onlineStudent.fullName, holder.studentName.getText().toString());
        assertEquals("Online", holder.studentStatus.getText().toString());
        // Compare color ints
        assertEquals(Color.parseColor("#4CAF50"), holder.studentStatus.getCurrentTextColor());
        // Check avatar (basic check if resource is set)
        assertNotNull(holder.studentAvatar.getDrawable());
    }

    @Test
    public void onBindViewHolder_forOfflineStudent_setsCorrectDataAndColor() {
        // Explanation: Verifies binding for an offline student: name, status text "Offline", red color.
        FrameLayout parent = new FrameLayout(context);
        StudentAdapter.StudentViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 1; // Offline student is second

        adapter.onBindViewHolder(holder, position);

        assertEquals(offlineStudent.fullName, holder.studentName.getText().toString());
        assertEquals("Offline", holder.studentStatus.getText().toString());
        // Compare color ints
        assertEquals(Color.parseColor("#F44336"), holder.studentStatus.getCurrentTextColor());
        // Check avatar
        assertNotNull(holder.studentAvatar.getDrawable());
    }
}