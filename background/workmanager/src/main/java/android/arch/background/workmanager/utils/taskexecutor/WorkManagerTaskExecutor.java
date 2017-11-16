/*
 * Copyright 2017 The Android Open Source Project
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

package android.arch.background.workmanager.utils.taskexecutor;

import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

/**
 * A static class that serves as a central point to execute common tasks in WorkManager.
 * This is used for business logic internal to WorkManager and NOT for worker processing.
 * Adapted from {@link android.arch.core.executor.ArchTaskExecutor}
 * @hide
 */

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class WorkManagerTaskExecutor implements TaskExecutor {
    private static WorkManagerTaskExecutor sInstance;
    private final TaskExecutor mDefaultTaskExecutor = new DefaultTaskExecutor();
    private TaskExecutor mTaskExecutor = mDefaultTaskExecutor;

    /**
     * Returns an instance of the task executor.
     * @return The singleton WorkManagerTaskExecutor.
     */
    public static synchronized WorkManagerTaskExecutor getInstance() {
        if (sInstance == null) {
            sInstance = new WorkManagerTaskExecutor();
        }
        return sInstance;
    }

    private WorkManagerTaskExecutor() {
    }

    void setTaskExecutor(@Nullable TaskExecutor taskExecutor) {
        mTaskExecutor = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    @Override
    public void postToMainThread(Runnable r) {
        mTaskExecutor.postToMainThread(r);
    }

    @Override
    public void executeOnBackgroundThread(Runnable r) {
        mTaskExecutor.executeOnBackgroundThread(r);
    }
}
