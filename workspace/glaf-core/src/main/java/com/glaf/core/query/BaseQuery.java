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

package com.glaf.core.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.glaf.core.identity.User;

import com.glaf.core.util.Paging;

@SuppressWarnings("rawtypes")
public class BaseQuery extends AbstractQuery<Object> {
	private static final long serialVersionUID = 1L;
	protected String actorId;
	protected String createBy;
	List rowIds = new ArrayList();
	protected List<String> actorIds = new ArrayList<String>();
	protected User loginContext;
	protected boolean isFilterPermission = true;
	protected boolean isInitialized = false;
	protected boolean isOwner = false;
	protected Integer locked;
	protected int pageNo;
	protected int pageSize;
	protected Object parameter;
	protected String serviceKey;
	protected String sortField;
	protected String sortOrder;
	protected String orderBy;
	protected QueryCondition currentQueryCondition;

	public BaseQuery() {

	}

	public BaseQuery actorId(String actorId) {
		if (actorId == null) {
			throw new RuntimeException("actorId is null");
		}
		this.actorId = actorId;
		return this;
	}

	public BaseQuery actorIds(List<String> actorIds) {
		if (actorIds == null) {
			throw new RuntimeException("actorIds is null");
		}
		this.actorIds = actorIds;
		return this;
	}

	public BaseQuery createBy(String createBy) {
		if (createBy == null) {
			throw new RuntimeException("createBy is null");
		}
		this.createBy = createBy;
		return this;
	}

	public void ensureInitialized() {
		if (isInitialized) {
			return;
		}
		if (loginContext != null) {
			if (StringUtils.equals(createBy, loginContext.getActorId())) {
				isFilterPermission = false;
			}

			if (StringUtils.isNotEmpty(createBy)) {
				isFilterPermission = false;
			}

			if (isFilterPermission) {
				/**
				 * �û����Է��ʵ�������ģ���������+������
				 */
				// ///////////////////////////////////////////////////////
				// ģ�����Ȩ��
				// ///////////////////////////////////////////////////////
				/**
				 * �����û�
				 */
				if (loginContext.getActorId() != null) {

					this.actorIds.add(loginContext.getActorId());
				}

				/**
				 * ���ʲ���
				 */
				if (loginContext.getDeptId() != 0) {

				}

			}
		}
		isInitialized = true;
	}

	public String getActorId() {
		return actorId;
	}

	public List<String> getActorIds() {
		return actorIds;
	}

	public int getBegin() {
		if (pageNo < 1) {
			pageNo = 1;
		}
		if (pageSize <= 0) {
			pageSize = Paging.DEFAULT_PAGE_SIZE;
		}

		int begin = (pageNo - 1) * pageSize;
		return begin;
	}

	public String getCreateBy() {
		return createBy;
	}

	public QueryCondition getCurrentQueryCondition() {
		return currentQueryCondition;
	}

	public Integer getLocked() {
		return locked;
	}

	public boolean getOnlyDataModels() {
		return true;
	}

	public String getOrderBy() {
		if (orderBy != null) {
			return orderBy;
		}

		if (this.getSortField() != null) {
			orderBy = " E." + this.getSortField() + "_";
			if (this.getSortOrder() != null) {
				orderBy += " " + this.getSortOrder();
			}
		}

		return orderBy;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		if (pageSize <= 0) {
			pageSize = Paging.DEFAULT_PAGE_SIZE;
		}
		return pageSize;
	}

	public Object getParameter() {
		return parameter;
	}

	public List getRowIds() {
		return rowIds;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public String getSortField() {
		return sortField;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public User getUser() {
		return loginContext;
	}

	public boolean isFilterPermission() {
		return isFilterPermission;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public BaseQuery locked(Integer locked) {
		this.locked = locked;
		return this;
	}

	public BaseQuery rowIds(List rowIds) {
		if (rowIds == null) {
			throw new RuntimeException("rowIds is null");
		}
		this.rowIds = rowIds;
		return this;
	}

	public BaseQuery serviceKey(String serviceKey) {
		if (serviceKey == null) {
			throw new RuntimeException("serviceKey  is null");
		}
		this.serviceKey = serviceKey;
		return this;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public void setActorIds(List<String> actorIds) {
		this.actorIds = actorIds;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCurrentQueryCondition(QueryCondition currentQueryCondition) {
		this.currentQueryCondition = currentQueryCondition;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

	public void setRowIds(List rowIds) {
		this.rowIds = rowIds;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setUser(User loginContext) {
		this.loginContext = loginContext;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}