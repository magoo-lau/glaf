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

package com.glaf.shiro.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;

public class ZooKeeperCacheManager implements CacheManager, Initializable,
		Destroyable {

	private volatile CuratorFramework zkClient;

	protected String servers = "localhost:2181";

	public void destroy() throws Exception {
		zkClient.close();
	}

	public <K, V> Cache<K, V> getCache(String key) throws CacheException {
		return new ZooKeeperCache<K, V>(zkClient, "/SHIRO_CACHE");
	}

	public String getServers() {
		return servers;
	}

	public void init() throws ShiroException {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000,
				Integer.MAX_VALUE);
		zkClient = CuratorFrameworkFactory.newClient(getServers(), retryPolicy);
		zkClient.start();
		try {
			zkClient.create().forPath("/SHIRO_CACHE");
		} catch (Exception ex) {
		}
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

}