/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.mutant.wordsmaster.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mutant.wordsmaster.data.source.WordsRepository;
import com.mutant.wordsmaster.data.source.local.WordsDatabase;
import com.mutant.wordsmaster.data.source.local.WordsLocalDataSource;

/**
 * Enables injection of mock implementations for
 * {@link com.mutant.wordsmaster.data.source.WordsDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static WordsRepository provideTasksRepository(@NonNull Context context) {
        WordsDatabase database = WordsDatabase.Companion.getInstance(context);
        return WordsRepository.Companion.getInstance(FakeTasksRemoteDataSource.Companion.getInstance(),
                WordsLocalDataSource.Companion.getInstance(new AppExecutors(),
                        database.wordDao()));
    }
}
