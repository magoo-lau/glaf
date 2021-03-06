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

package com.glaf.base.modules.sys.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.base.modules.sys.util.SysApplicationJsonFactory;
import com.glaf.core.base.JSONable;

@Entity
@Table(name = "SYS_APPLICATION")
public class SysApplication implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	/**
	 * 编码
	 */
	@Column(name = "CODE", length = 50)
	protected String code;

	/**
	 * 创建人
	 */
	@Column(name = "CREATEBY", length = 50)
	protected String createBy;

	/**
	 * 创建日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATEDATE")
	protected Date createDate;

	/**
	 * 描述
	 */
	@Column(name = "APPDESC", length = 500)
	protected String desc;

	@javax.persistence.Transient
	private Set<SysFunction> functions = new HashSet<SysFunction>();

	@Id
	@Column(name = "ID", nullable = false)
	protected long id;

	/**
	 * 是否启用
	 */
	@Column(name = "locked")
	protected int locked;

	/**
	 * 名称
	 */
	@Column(name = "NAME", length = 250)
	protected String name;

	/**
	 * 类型
	 */
	@Column(name = "TYPE", length = 50)
	protected String type;

	@javax.persistence.Transient
	private SysTree node;

	/**
	 * 节点编号
	 */
	@Column(name = "NODEID")
	protected long nodeId;

	/**
	 * 显示菜单
	 */
	@Column(name = "SHOWMENU")
	protected int showMenu;

	/**
	 * 序号
	 */
	@Column(name = "SORT")
	protected int sort;

	/**
	 * 修改人
	 */
	@Column(name = "UPDATEBY", length = 50)
	protected String updateBy;

	/**
	 * 修改日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATEDATE")
	protected Date updateDate;

	/**
	 * URL
	 */
	@Column(name = "URL", length = 500)
	protected String url;

	/**
	 * 开放标记
	 */
	@Column(name = "OPENFLAG", length = 50)
	protected String openFlag;

	public SysApplication() {

	}

	public String getCode() {
		return code;
	}

	public String getCreateBy() {
		return createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getDesc() {
		return desc;
	}

	public Set<SysFunction> getFunctions() {
		if (functions == null) {
			functions = new HashSet<SysFunction>();
		}
		return functions;
	}

	public long getId() {
		return id;
	}

	public int getLocked() {
		return locked;
	}

	public String getName() {
		return name;
	}

	public SysTree getNode() {
		return node;
	}

	public long getNodeId() {
		return nodeId;
	}

	public String getOpenFlag() {
		return openFlag;
	}

	public int getShowMenu() {
		return showMenu;
	}

	public int getSort() {
		return sort;
	}

	public String getType() {
		return type;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public String getUrl() {
		return url;
	}

	public SysApplication jsonToObject(JSONObject jsonObject) {
		return SysApplicationJsonFactory.jsonToObject(jsonObject);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setFunctions(Set<SysFunction> functions) {
		this.functions = functions;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNode(SysTree node) {
		this.node = node;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}

	public void setShowMenu(int showMenu) {
		this.showMenu = showMenu;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject toJsonObject() {
		return SysApplicationJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return SysApplicationJsonFactory.toObjectNode(this);
	}

	public String toString() {
		return toJsonObject().toJSONString();
	}
}