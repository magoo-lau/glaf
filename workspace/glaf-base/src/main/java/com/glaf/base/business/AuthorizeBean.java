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

package com.glaf.base.business;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.cache.CacheFactory;
import com.glaf.core.config.SystemConfig;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.util.ClassUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.web.callback.CallbackProperties;
import com.glaf.core.web.callback.LoginCallback;
import com.glaf.base.modules.sys.model.SysUser;
import com.glaf.base.modules.sys.service.AuthorizeService;
import com.glaf.base.modules.sys.service.SysApplicationService;
import com.glaf.base.modules.sys.service.SysUserService;
import com.glaf.base.modules.sys.util.SysUserJsonFactory;
import com.glaf.base.utils.ContextUtil;

public class AuthorizeBean {
	private static final Log logger = LogFactory.getLog(AuthorizeBean.class);
	protected static String configurationResource = "/conf/spring/spring-config.xml";

	protected static org.springframework.context.ApplicationContext ctx;

	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext(configurationResource);
		com.glaf.core.context.ContextFactory.setContext(ctx);
		AuthorizeBean bean = new AuthorizeBean();
		SysUser user = bean.getAuthorizeService().login("root");
		logger.debug(bean.getSysApplicationService().getMenu(3, user));
	}

	private SysApplicationService sysApplicationService;

	private AuthorizeService authorizeService;

	private SysUserService sysUserService;

	public AuthorizeBean() {

	}

	public AuthorizeService getAuthorizeService() {
		if (authorizeService == null) {
			authorizeService = ContextFactory.getBean("authorizeService");
		}
		return authorizeService;
	}

	public String getMenus(SysUser bean) {
		String menus = getSysApplicationService().getMenu(3, bean);
		return menus;
	}

	public SysApplicationService getSysApplicationService() {
		if (sysApplicationService == null) {
			sysApplicationService = ContextFactory
					.getBean("sysApplicationService");
		}
		return sysApplicationService;
	}

	public SysUserService getSysUserService() {
		if (sysUserService == null) {
			sysUserService = ContextFactory.getBean("sysUserService");
		}
		return sysUserService;
	}

	public SysUser getUser(String account) {
		logger.debug("#account=" + account);
		SysUser sysUser = null;
		if (account != null) {
			String cacheKey = "cache_user_" + account;
			if (SystemConfig.getBoolean("use_query_cache")) {
				String content = CacheFactory.getString(cacheKey);
				if (StringUtils.isNotEmpty(content)) {
					JSONObject jsonObject = JSON.parseObject(content);
					sysUser = SysUserJsonFactory.jsonToObject(jsonObject);
				}
			}
			if (sysUser == null) {
				sysUser = getSysUserService().findByAccountWithAll(account);
				if (SystemConfig.getBoolean("use_query_cache")
						&& sysUser != null) {
					CacheFactory.put(cacheKey, sysUser.toJsonObject()
							.toJSONString());
				}
			}
		}
		return sysUser;
	}

	/**
	 * 登录
	 * 
	 * @param request
	 */
	public SysUser login(String account, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug(account + " start login........................");
		// 用户登陆，返回系统用户对象
		SysUser bean = getSysUserService().findByAccount(account);
		if (bean != null) {
			// 登录成功，修改最近一次登录时间
			Properties props = CallbackProperties.getProperties();
			if (props != null && props.keys().hasMoreElements()) {
				Enumeration<?> e = props.keys();
				while (e.hasMoreElements()) {
					String className = (String) e.nextElement();
					try {
						Object obj = ClassUtils.instantiateObject(className);
						if (obj instanceof LoginCallback) {
							LoginCallback callback = (LoginCallback) obj;
							callback.afterLogin(bean.getAccount(), request,
									null);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						logger.error(ex);
					}
				}
			}

			ContextUtil.put(bean.getAccount(), bean);// 传入全局变量
			RequestUtils.setLoginUser(request, response, "default",
					bean.getAccount());

		}
		return bean;
	}

	public void setAuthorizeService(AuthorizeService authorizeService) {
		this.authorizeService = authorizeService;
	}

	public void setSysApplicationService(
			SysApplicationService sysApplicationService) {
		this.sysApplicationService = sysApplicationService;
	}

	public void setSysUserService(SysUserService sysUserService) {
		this.sysUserService = sysUserService;
	}

}