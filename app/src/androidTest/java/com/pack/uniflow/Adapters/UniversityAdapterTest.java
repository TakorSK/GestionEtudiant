package com.pack.uniflow.Adapters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView; // <-- IMPORT ImageView

import androidx.core.content.ContextCompat; // <-- IMPORT ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager; // Import needed for LayoutManager check
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.pack.uniflow.Fragments.AdminFragment; // Import the inner class
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Instrumented tests for UniversityAdapter.
 */
@RunWith(AndroidJUnit4.class)
public class UniversityAdapterTest {

    private Context context;
    private List<AdminFragment.UniversityWithStudents> testUniList;
    private UniversityAdapter adapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Create sample data
        Uni uni1 = new Uni(); uni1.id = 1; uni1.name = "University A";
        Student s1a = new Student(101, "s1a@uni.com", "Student A1", 20, "111", 1, "p");
        Student s1b = new Student(102, "s1b@uni.com", "Student A2", 21, "112", 1, "p");
        // *** CORRECTED Variable Declaration ***
        AdminFragment.UniversityWithStudents uws1 = new AdminFragment.UniversityWithStudents(uni1, Arrays.asList(s1a, s1b));

        Uni uni2 = new Uni(); uni2.id = 2; uni2.name = "University B";
        Student s2a = new Student(201, "s2a@uni.com", "Student B1", 22, "211", 2, "p");
        // *** CORRECTED Variable Declaration ***
        AdminFragment.UniversityWithStudents uws2 = new AdminFragment.UniversityWithStudents(uni2, Arrays.asList(s2a));

        // *** CORRECTED Variable Usage ***
        testUniList = new ArrayList<>(Arrays.asList(uws1, uws2));
        adapter = new UniversityAdapter(testUniList, context);
    }

    @Test
    public void getItemCount_returnsCorrectSize() {
        assertEquals(testUniList.size(), adapter.getItemCount());
    }

    @Test
    public void onCreateViewHolder_inflatesCorrectLayout() {
        FrameLayout parent = new FrameLayout(context);
        UniversityAdapter.UniversityViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        assertNotNull(viewHolder);
        assertNotNull(viewHolder.itemView);
    }

    @Test
    public void initialState_isCorrect() {
        FrameLayout parent = new FrameLayout(context);
        UniversityAdapter.UniversityViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 0;
        adapter.onBindViewHolder(holder, position);

        assertEquals(testUniList.get(position).university.name, holder.tvUniversityName.getText().toString());
        assertEquals(View.GONE, holder.expandableLayout.getVisibility());
        // Use the custom matcher to check the drawable resource ID
        onView(withId(R.id.ivToggle)).check(matches(withDrawable(R.drawable.ic_arrow_drop_down)));
        // Alternative simple check: assertNotNull(holder.ivToggle.getDrawable());
    }

    @Test
    public void clickHeader_expandsAndUpdatesIcon() {
        FrameLayout parent = new FrameLayout(context);
        UniversityAdapter.UniversityViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 0;
        adapter.onBindViewHolder(holder, position);

        // Use Espresso to perform click on the item view's header
        // We need a way to target the specific item view. If testing within a RecyclerView,
        // we'd use RecyclerViewActions. Here, we simulate by clicking the holder's item view directly.
        // For a more robust test, consider RecyclerView testing patterns.
        holder.itemView.findViewById(R.id.universityHeader).performClick();


        assertEquals(View.VISIBLE, holder.expandableLayout.getVisibility());
        // Use the custom matcher to check the drawable resource ID
        onView(withId(R.id.ivToggle)).check(matches(withDrawable(R.drawable.ic_arrow_drop_up)));
        // Alternative simple check: assertNotNull(holder.ivToggle.getDrawable());
        assertNotNull("Nested RecyclerView should have LayoutManager after expand", holder.studentRecyclerView.getLayoutManager());
        assertNotNull("Nested RecyclerView should have Adapter after expand", holder.studentRecyclerView.getAdapter());
        assertEquals("Nested adapter should have correct student count",
                testUniList.get(position).students.size(),
                holder.studentRecyclerView.getAdapter().getItemCount());
    }

    @Test
    public void clickHeaderTwice_collapsesAndUpdatesIcon() {
        FrameLayout parent = new FrameLayout(context);
        UniversityAdapter.UniversityViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 0;
        adapter.onBindViewHolder(holder, position);
        holder.itemView.findViewById(R.id.universityHeader).performClick(); // Expand

        // Act: Click a second time
        holder.itemView.findViewById(R.id.universityHeader).performClick(); // Collapse

        // Assert: Check state *after* second click
        assertEquals(View.GONE, holder.expandableLayout.getVisibility());
        // Use the custom matcher to check the drawable resource ID
        onView(withId(R.id.ivToggle)).check(matches(withDrawable(R.drawable.ic_arrow_drop_down)));
        // Alternative simple check: assertNotNull(holder.ivToggle.getDrawable());
    }

    // --- Custom Matcher for Drawable Resource ---
    // (Requires ImageView and ContextCompat imports)
    public static Matcher<View> withDrawable(final int resourceId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) { // Use imported ImageView
            @Override
            public void describeTo(Description description) {
                description.appendText("has drawable resource " + resourceId);
            }

            @Override
            public boolean matchesSafely(ImageView imageView) { // Use imported ImageView
                // Compare constant state of drawables
                if (imageView.getDrawable() == null || imageView.getDrawable().getConstantState() == null) {
                    return false; // No drawable set
                }
                // Use imported ContextCompat
                if (ContextCompat.getDrawable(imageView.getContext(), resourceId) == null) {
                    return false; // Resource ID invalid
                }
                // Use imported ContextCompat
                return imageView.getDrawable().getConstantState().equals(
                        ContextCompat.getDrawable(imageView.getContext(), resourceId).getConstantState()
                );
            }
        };
    }
}