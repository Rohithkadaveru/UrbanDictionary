package com.rohith.urbandictionary;

import android.app.Instrumentation;

import androidx.test.espresso.Espresso;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);
    private MainActivity mainActivity = null;

    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        assertNotNull(mainActivity.findViewById(R.id.et_define_term));
        assertNotNull(mainActivity.findViewById(R.id.list_definitions));
    }

    @Test
    public void testPerformSearch() throws InterruptedException {

        //Perform type action on the view with search term as acne
        Espresso.onView(withId(R.id.et_define_term)).perform(typeText("acne"));

        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.et_define_term)).perform(pressImeActionButton());

        Thread.sleep(2000);

        // Assert 2 definitions are visible
        Espresso.onView(withId(R.id.list_definitions)).check(matches(hasChildCount(2)));
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}
