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

package com.glaf.core.batch.domain;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.*;
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "SYS_JOB_INSTANCE")
public class JobInstance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "job_instance_id", nullable = false)
	protected int jobInstanceId;

	/**
	 * version
	 */
	@Column(name = "version")
	protected int version = 1;

	/**
	 * job_name
	 */
	@Column(name = "job_name", length = 100)
	protected String jobName;

	/**
	 * job_key
	 */
	@Column(name = "job_key", length = 32)
	protected String jobKey;

	@javax.persistence.Transient
	protected Collection<JobParam> params = new HashSet<JobParam>();

	@javax.persistence.Transient
	protected Collection<JobExecution> executions = new HashSet<JobExecution>();

	@javax.persistence.Transient
	protected Collection<StepExecution> steps = new CopyOnWriteArraySet<StepExecution>();

	public JobInstance() {

	}

	public void addJobExecution(JobExecution execution) {
		if (executions == null) {
			executions = new HashSet<JobExecution>();
		}
		executions.add(execution);
	}

	public void addJobParam(JobParam param) {
		if (params == null) {
			params = new HashSet<JobParam>();
		}
		params.add(param);
	}

	public void addStep(StepExecution step) {
		if (steps == null) {
			steps = new CopyOnWriteArraySet<StepExecution>();
		}
		steps.add(step);
	}

	public java.util.Date getDateValue(String keyName) {
		if (params != null && !params.isEmpty()) {
			for (JobParam param : params) {
				if (StringUtils.equals(keyName, param.getKeyName())) {
					return param.getDateVal();
				}
			}
		}
		return null;
	}

	public Double getDoubleValue(String keyName) {
		if (params != null && !params.isEmpty()) {
			for (JobParam param : params) {
				if (StringUtils.equals(keyName, param.getKeyName())) {
					return param.getDoubleVal();
				}
			}
		}
		return null;
	}

	public Collection<JobExecution> getExecutions() {
		return executions;
	}

	public Integer getIntValue(String keyName) {
		if (params != null && !params.isEmpty()) {
			for (JobParam param : params) {
				if (StringUtils.equals(keyName, param.getKeyName())) {
					return param.getIntVal();
				}
			}
		}
		return null;
	}

	public int getJobInstanceId() {
		return this.jobInstanceId;
	}

	public String getJobKey() {
		return this.jobKey;
	}

	public String getJobName() {
		return this.jobName;
	}

	public Long getLongValue(String keyName) {
		if (params != null && !params.isEmpty()) {
			for (JobParam param : params) {
				if (StringUtils.equals(keyName, param.getKeyName())) {
					return param.getLongVal();
				}
			}
		}
		return null;
	}

	public Collection<JobParam> getParams() {
		return params;
	}

	public Collection<StepExecution> getSteps() {
		return steps;
	}

	public String getStringValue(String keyName) {
		if (params != null && !params.isEmpty()) {
			for (JobParam param : params) {
				if (StringUtils.equals(keyName, param.getKeyName())) {
					return param.getStringVal();
				}
			}
		}
		return null;
	}

	public int getVersion() {
		return this.version;
	}

	public JobInstance jsonToObject(JSONObject jsonObject) {
		JobInstance model = new JobInstance();
		if (jsonObject.containsKey("version")) {
			model.setVersion(jsonObject.getInteger("version"));
		}
		if (jsonObject.containsKey("jobName")) {
			model.setJobName(jsonObject.getString("jobName"));
		}
		if (jsonObject.containsKey("jobKey")) {
			model.setJobKey(jsonObject.getString("jobKey"));
		}
		return model;
	}

	public void setExecutions(Collection<JobExecution> executions) {
		this.executions = executions;
	}

	public void setJobInstanceId(int jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setParams(Collection<JobParam> params) {
		this.params = params;
	}

	public void setSteps(Collection<StepExecution> steps) {
		this.steps = steps;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public JSONObject toJsonObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("jobInstanceId", jobInstanceId);
		jsonObject.put("version", version);
		if (jobName != null) {
			jsonObject.put("jobName", jobName);
		}
		if (jobKey != null) {
			jsonObject.put("jobKey", jobKey);
		}
		return jsonObject;
	}

	public ObjectNode toObjectNode() {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("jobInstanceId", jobInstanceId);
		jsonObject.put("version", version);
		if (jobName != null) {
			jsonObject.put("jobName", jobName);
		}
		if (jobKey != null) {
			jsonObject.put("jobKey", jobKey);
		}
		return jsonObject;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}