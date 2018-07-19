/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.leanback.widget.picker;


import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.leanback.test.R;
import androidx.leanback.testutils.PollingCheck;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class PinPickerTest {
    @Rule
    public final ActivityTestRule<PinPickerActivity> mActivityTestRule;

    public PinPickerTest() {
        mActivityTestRule = new ActivityTestRule<>(PinPickerActivity.class);
    }

    private PinPicker mPinPicker;

    @Before
    public void setUp() {
        mPinPicker = mActivityTestRule.getActivity().findViewById(R.id.test_picker);
    }

    private void waitStable() {
        PollingCheck.waitFor(new PollingCheck.PollingCheckCondition() {
            @Override
            public boolean canProceed() {
                for (int i = 0; i < 4; i++) {
                    if (mPinPicker.mColumnViews.get(i).getScrollState()
                            != RecyclerView.SCROLL_STATE_IDLE) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    @Test
    @LargeTest
    public void keyInputTest() throws Exception {
        final CompletableFuture<String> futurePin = new CompletableFuture<>();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mPinPicker.resetPin();
                mPinPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        futurePin.complete(mPinPicker.getPin());
                    }
                });
            }
        });
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_1));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_2));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_3));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_4));

        assertThat("keyboard input should set pin", futurePin.get(5, TimeUnit.SECONDS), is("1234"));

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mPinPicker.resetPin();
            }
        });
        assertThat("resetPin should reset pin", mPinPicker.getPin(), is("0000"));
    }

    @Test
    @LargeTest
    public void dpadInputTest() throws Exception {
        final CompletableFuture<String> futurePin = new CompletableFuture<>();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mPinPicker.resetPin();
                mPinPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        futurePin.complete(mPinPicker.getPin());
                    }
                });
            }
        });
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_CENTER));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_CENTER));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_CENTER));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
        onView(withId(R.id.test_picker)).perform(pressKey(KeyEvent.KEYCODE_DPAD_CENTER));
        waitStable();
        assertThat("dpad input should set pin", futurePin.get(5, TimeUnit.SECONDS), is("1234"));
    }

    private static class CompletableFuture<V> implements Future<V> {

        private volatile V mValue;
        private CountDownLatch mLatch = new CountDownLatch(1);

        public void complete(V value) {
            mValue = value;
            mLatch.countDown();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDone() {
            return mLatch.getCount() == 0;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            mLatch.await();
            return mValue;
        }

        @Override
        public V get(long timeout, @NonNull TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            mLatch.await(timeout, unit);
            return mValue;
        }
    }
}
