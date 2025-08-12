package com.pack.uniflow.Adapters;

import static org.junit.Assert.*;

import android.content.Context;
import android.widget.FrameLayout; // Use a simple ViewGroup for inflation context

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.pack.uniflow.Models.Club;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Instrumented tests for ClubAdapter.
 */
@RunWith(AndroidJUnit4.class)
public class ClubAdapterTest {

    private Context context;
    private List<Club> testClubList;
    private ClubAdapter adapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Create sample data
        Club club1 = new Club();
        club1.id = 1;
        club1.name = "Chess Club";
        club1.description = "Play chess";
        club1.uniId = 1;

        Club club2 = new Club();
        club2.id = 2;
        club2.name = "Coding Club";
        club2.description = "Learn to code";
        club2.uniId = 1;

        testClubList = new ArrayList<>(Arrays.asList(club1, club2));

        // Create adapter instance
        adapter = new ClubAdapter(testClubList);
    }

    @Test
    public void getItemCount_returnsCorrectSize() {
        // Explanation: Verifies that getItemCount() returns the number of items
        // passed to the adapter's constructor.
        assertEquals(testClubList.size(), adapter.getItemCount());
    }

    @Test
    public void onCreateViewHolder_inflatesCorrectLayout() {
        // Explanation: Verifies that onCreateViewHolder inflates the expected layout file
        // and returns the correct ViewHolder type without crashing.
        // Create a dummy parent ViewGroup for layout inflation context
        FrameLayout parent = new FrameLayout(context);
        ClubAdapter.ClubViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0); // viewType is not used

        assertNotNull("ViewHolder should not be null", viewHolder);
        // Check if the inflated view's root is what you expect (optional, depends on layout root)
        // Example: assertTrue(viewHolder.itemView instanceof ConstraintLayout);
        assertNotNull("ViewHolder itemView should not be null", viewHolder.itemView);
    }

    @Test
    public void onBindViewHolder_setsCorrectData() {
        // Explanation: Verifies that onBindViewHolder correctly sets the text
        // in the TextViews based on the Club object at the given position.

        // Arrange: Create a ViewHolder instance
        FrameLayout parent = new FrameLayout(context);
        ClubAdapter.ClubViewHolder holder = adapter.onCreateViewHolder(parent, 0);
        int position = 1; // Test binding for the second item ("Coding Club")
        Club expectedClub = testClubList.get(position);

        // Act: Bind data to the holder
        adapter.onBindViewHolder(holder, position);

        // Assert: Check if the TextViews in the holder display the correct data
        assertEquals(expectedClub.name, holder.name.getText().toString());
        assertEquals(expectedClub.description, holder.description.getText().toString());
    }
}