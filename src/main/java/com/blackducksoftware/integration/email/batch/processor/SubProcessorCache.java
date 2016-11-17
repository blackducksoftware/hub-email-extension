/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.blackducksoftware.integration.email.batch.processor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;

public class SubProcessorCache<T extends NotificationEvent<?>> {
    private final Map<String, T> eventMap = new LinkedHashMap<>(500);

    public boolean hasEvent(String eventKey) {
        return eventMap.containsKey(eventKey);
    }

    public void addEvent(final T event) {
        final String key = event.getEventKey();
        if (!eventMap.containsKey(key)) {
            eventMap.put(key, event);
        } else {
            final T storedEvent = eventMap.get(key);
            final Set<ItemEntry> storedEventDataMap = storedEvent.getDataSet();
            final Set<ItemEntry> eventDataMap = event.getDataSet();
            storedEventDataMap.addAll(eventDataMap);
        }
    }

    public void removeEvent(final String eventKey) {
        if (eventMap.containsKey(eventKey)) {
            eventMap.remove(eventKey);
        }
    }

    public void removeEvent(final T event) {
        final String key = event.getEventKey();
        removeEvent(key);
    }

    public T getEvent(final String eventKey) {
        return eventMap.get(eventKey);
    }

    public Collection<T> getEvents() {
        return eventMap.values();
    }

    public Map<String, T> getEventMap() {
        return eventMap;
    }
}
