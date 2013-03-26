/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glaf.test;

import java.util.List;

import org.junit.Test;

import com.glaf.core.service.EntityService;
import com.glaf.core.todo.query.TodoQuery;
import com.glaf.core.util.ThreadFactory;

public class MyBatisTest extends AbstractTest {

	protected EntityService entityService;

	@Test
	public void testList() {
		entityService = super.getBean("entityService");
		for (int i = 0; i <= 10; i++) {
			List<Object> todoList = entityService.getList("getTodoList",
					new TodoQuery());
			System.out.println(todoList);
		}
	}

	@Test
	public void testNextDbidBlock() {
		entityService = super.getBean("entityService");
		Thread thread = new TestThread(entityService);
		for (int i = 0; i <= 1000000; i++) {
			ThreadFactory.run(thread);
		}
	}

}