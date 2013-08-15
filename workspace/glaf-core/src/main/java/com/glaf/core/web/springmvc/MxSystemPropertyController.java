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

package com.glaf.core.web.springmvc;

import java.util.List;
 
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.glaf.core.config.MessageProperties;
import com.glaf.core.config.SystemConfig;
import com.glaf.core.config.SystemProperties;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.domain.SystemProperty;
import com.glaf.core.service.ISystemPropertyService;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;

@Controller("/sys/property")
@RequestMapping("/sys/property")
public class MxSystemPropertyController {

	protected static final Log logger = LogFactory
			.getLog(MxSystemPropertyController.class);

	 
	protected ISystemPropertyService systemPropertyService;

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String category = request.getParameter("category");
		if (StringUtils.isNotEmpty(category)) {
			List<SystemProperty> rows = systemPropertyService
					.getSystemProperties(category);
			request.setAttribute("rows", rows);
		}
		String jx_view = request.getParameter("jx_view");

		if (StringUtils.isNotEmpty(jx_view)) {
			return new ModelAndView(jx_view, modelMap);
		}

		String x_view = ViewProperties.getString("sys_property.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/modules/sys/property/edit");
	}

	@RequestMapping("/saveAll")
	public ModelAndView saveAll(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String category = request.getParameter("category");
		if (StringUtils.isNotEmpty(category)) {
			List<SystemProperty> rows = systemPropertyService
					.getSystemProperties(category);
			for (SystemProperty p : rows) {
				String key = p.getName();
				String value = request.getParameter(key);
				if (value != null) {
					p.setValue(value);
				}
			}
			systemPropertyService.saveAll(rows);
		}

		String jx_view = request.getParameter("jx_view");

		if (StringUtils.isNotEmpty(jx_view)) {
			return new ModelAndView(jx_view, modelMap);
		}

		String x_view = ViewProperties.getString("sys_property.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return this.edit(request, modelMap);
	}

	@ResponseBody
	@RequestMapping("/saveCfg")
	public byte[] saveCfg(HttpServletRequest request) {
		RequestUtils.setRequestParameterToAttribute(request);
		String category = request.getParameter("category");
		if (StringUtils.isNotEmpty(category)) {
			List<SystemProperty> rows = systemPropertyService
					.getSystemProperties(category);
			for (SystemProperty p : rows) {
				String key = p.getName();
				String value = request.getParameter(key);
				if (value != null) {
					p.setValue(value);
				}
			}
			systemPropertyService.saveAll(rows);
			
			SystemProperties.reload();
			SystemConfig.reload();
			MessageProperties.reload();
			return ResponseUtils.responseJsonResult(true, "����ɹ���");
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setSystemPropertyService(
			ISystemPropertyService systemPropertyService) {
		this.systemPropertyService = systemPropertyService;
	}

}