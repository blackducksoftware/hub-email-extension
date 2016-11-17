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
package com.blackducksoftware.integration.email.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EmailTargetTest {

    private static final String TEMPLATE = "template.ftl";

    private static final String ADDRESS = "address@domain.com";

    private static final String MODEL_KEY = "model_key";

    private static final String MODEL_ITEM = "model_item";

    @Test
    public void testConstructor() {
        final Map<String, Object> map = new HashMap<>();
        map.put(MODEL_KEY, MODEL_ITEM);
        final EmailTarget target = new EmailTarget(ADDRESS, TEMPLATE, map);
        assertEquals(ADDRESS, target.getEmailAddress());
        assertEquals(TEMPLATE, target.getTemplateName());
        assertEquals(map, target.getModel());
        assertTrue(target.getModel().containsKey(MODEL_KEY));
        assertEquals(MODEL_ITEM, target.getModel().get(MODEL_KEY));
    }
}
