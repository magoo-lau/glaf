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

package com.glaf.core.resource.mongodb;

import java.util.Properties;

import com.glaf.core.resource.Resource;
import com.glaf.core.resource.ResourceProvider;

public class MongodbResourceProvider implements ResourceProvider {

	protected static MongodbResource cache;

	protected String servers = "127.0.0.1:27017";

	protected int cacheSize = 100000;

	protected int expireMinutes = 30;

	@Override
	public Resource buildResource(String regionName, boolean autoCreate) {
		if (cache == null) {
			cache = new MongodbResource(getServers(), getResourceSize(),
					getExpireMinutes());
		}
		return cache;
	}

	public int getResourceSize() {
		return cacheSize;
	}

	public int getExpireMinutes() {
		return expireMinutes;
	}

	protected int getProperty(Properties props, String key, int defaultValue) {
		try {
			return Integer.parseInt(props.getProperty(key,
					String.valueOf(defaultValue)).trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected String getProperty(Properties props, String key, String defaultValue) {
		return props.getProperty(key, defaultValue).trim();
	}

	public String getServers() {
		return servers;
	}

	@Override
	public String name() {
		return "mongodb";
	}

	public void setResourceSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public void setExpireMinutes(int expireMinutes) {
		this.expireMinutes = expireMinutes;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	@Override
	public void start(Properties props) {
		servers = getProperty(props, "servers", "127.0.0.1:27017");
		cacheSize = getProperty(props, "cacheSize", 100000);
		expireMinutes = getProperty(props, "expireMinutes", 30);
	}

	@Override
	public void stop() {
		cache.destroy();
	}

}
