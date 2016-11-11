/*
 * PerfomanceMetricsPlugin - see how IntellijIdea improving your performance!
 * Copyright 2016 Svitkov Sergey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.svitkov.PerfomanceMetricsPlugin.stuff;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serialization state
 */
@State(name = "Counter",
        storages = {
                @Storage(id = "dir"
		                , file = "counter.xml"
		                , scheme = StorageScheme.DIRECTORY_BASED)
        })
public class Counter implements ApplicationComponent, PersistentStateComponent<Counter.CounterState> {

	/**
	 * Nested class for saving serialization state.
	 * Map is used for state saving for each class, existing in project.
	 * Key is filename in form: package_name.classname (for Java) and object is just count of typed symbols
	 */
	public static class CounterState {
		public CounterState() {
			stateMap = new ConcurrentHashMap<>();
		}

		public Map<String, Integer> stateMap;
	}

    @Nullable
    @Override
    public CounterState getState() {
        return counterState;
    }

    @Override
    public void loadState(CounterState state) {
        this.counterState = state;
    }

    public static Counter getInstance() {
	    return ServiceManager.getService(Counter.class);
    }

	/**
	 * Incrementing related to filename count of typed symbols
	 * @param fileName
	 */
	public void increment (@NotNull String fileName) {
		if (counterState.stateMap != null && counterState.stateMap.get(fileName) != null)
        	counterState.stateMap.put(fileName, counterState.stateMap.get(fileName) + 1);
	    else if (counterState.stateMap != null)
	    	counterState.stateMap.put(fileName, 1);
	    else
	    	counterState.stateMap = new ConcurrentHashMap<>();
    }

	/**
	 * Decrementing related to filename count of typed symbols
	 * @param fileName
	 */
	public void decrement(String fileName) {
		Map<String, Integer> stateMap = counterState.stateMap;
		if (counterState.stateMap != null && stateMap.get(fileName) != null)
		    counterState.stateMap.put(fileName, counterState.stateMap.get(fileName) - 1);
	    else if (counterState.stateMap != null)
	    	counterState.stateMap.put(fileName, 0);
	    else
			counterState.stateMap = new ConcurrentHashMap<>();
	}

	/**
	 * Returns typed symbols count related to specified file name
	 * @param fileName
	 * @return
	 */
	public int getTypedSymbolsCount(@NotNull String fileName) {
	    if (counterState.stateMap != null && counterState.stateMap.get(fileName) != null)
		    return counterState.stateMap.get(fileName);
	    else
	    	return -1;
    }

	@Override
	public void initComponent() {
	}

	@Override
	public void disposeComponent() {
	}

	@NotNull
	@Override
	public String getComponentName() {
		return String.valueOf(Counter.class);
	}

	public CounterState counterState = new CounterState();
}