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

package com.glaf.core.domain;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.base.JSONable;
import com.glaf.core.domain.util.TableDefinitionJsonFactory;

/**
 * 数据表定义
 * 
 * @author joy
 * 
 */
@Entity
@Table(name = "SYS_TABLE")
public class TableDefinition implements java.io.Serializable,
		java.lang.Comparable<TableDefinition>, JSONable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表名
	 */
	@Id
	@Column(name = "TABLENAME_", length = 50)
	protected String tableName;

	@Column(name = "PARENTTABLENAME_", length = 50)
	protected String parentTableName;

	@Column(name = "PACKAGENAME_", length = 200)
	protected String packageName;

	@Column(name = "ENTITYNAME_", length = 50)
	protected String entityName;

	@Column(name = "CLASSNAME_", length = 250)
	protected String className;

	/**
	 * 标题
	 */
	@Column(name = "TITLE_")
	protected String title;

	/**
	 * 标题
	 */
	@Column(name = "ENGLISHTITLE_")
	protected String englishTitle;

	@Column(name = "COLUMNQTY_")
	protected int columnQty;

	@Column(name = "ADDTYPE_")
	protected int addType;

	@Column(name = "SYSNUM_", length = 100)
	protected String sysnum;

	@Column(name = "ISSUBTABLE_", length = 2)
	protected String isSubTable;

	@Column(name = "TOPID_", length = 50)
	protected String topId;

	/**
	 * 聚合主键列集
	 */
	@Column(name = "AGGREGATIONKEYS_", length = 500)
	protected String aggregationKeys;

	/**
	 * 组合查询的查询编号
	 */
	@Column(name = "QUERYIDS_", length = 500)
	protected String queryIds;

	/**
	 * 是否临时表
	 */
	@Column(name = "TEMPORARYFLAG_", length = 1)
	protected String temporaryFlag;

	/**
	 * 是否删除抓取数据
	 */
	@Column(name = "DELETEFETCH_", length = 1)
	protected String deleteFetch;

	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATETIME_")
	protected Date createTime;

	/**
	 * 创建人
	 */
	@Column(name = "CREATEBY_", length = 50)
	protected String createBy;

	/**
	 * 描述
	 */
	@Column(name = "DESCRIPTION_", length = 500)
	protected String description;

	/**
	 * 表类型
	 */
	@Column(name = "TYPE_", length = 50)
	protected String type;

	/**
	 * 显示类型 form,grid,tree,treegrid
	 */
	@Column(name = "DISPLAYTYPE_", length = 50)
	protected String displayType;

	/**
	 * 级联插入
	 */
	@Column(name = "INSERTCASCADE_")
	protected int insertCascade;

	/**
	 * 级联更新
	 */
	@Column(name = "UPDATECASCADE_")
	protected int updateCascade;

	/**
	 * 级联删除
	 */
	@Column(name = "DELETECASCADE_")
	protected int deleteCascade;

	/**
	 * 是否锁定
	 */
	@Column(name = "LOCKED_")
	protected int locked;

	@Column(name = "DELETEFLAG_")
	protected int deleteFlag;

	@Column(name = "SYSTEMFLAG_", length = 2)
	protected String systemFlag;

	/**
	 * 修订版本
	 */
	@Column(name = "REVISION_")
	protected int revision;

	@Column(name = "SORTNO_")
	private int sortNo;

	@Transient
	protected ColumnDefinition idColumn;

	@Transient
	protected List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();

	@Transient
	protected List<QueryDefinition> queries = new ArrayList<QueryDefinition>();

	public TableDefinition() {

	}

	public void addColumn(ColumnDefinition column) {
		if (columns == null) {
			columns = new ArrayList<ColumnDefinition>();
		}
		columns.add(column);
	}

	public void addQuery(QueryDefinition query) {
		if (queries == null) {
			queries = new ArrayList<QueryDefinition>();
		}
		queries.add(query);
	}

	public int compareTo(TableDefinition o) {
		if (o == null) {
			return -1;
		}

		TableDefinition field = o;

		int l = this.sortNo - field.getSortNo();

		int ret = 0;

		if (l > 0) {
			ret = 1;
		} else if (l < 0) {
			ret = -1;
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableDefinition other = (TableDefinition) obj;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

	public int getAddType() {
		return addType;
	}

	public String getAggregationKeys() {
		return aggregationKeys;
	}

	public String getClassName() {
		if (className != null) {
			return className;
		}
		if (getPackageName() != null && getEntityName() != null) {
			return getPackageName() + "." + getEntityName();
		}
		return this.getEntityName();
	}

	public int getColumnQty() {
		return columnQty;
	}

	public List<ColumnDefinition> getColumns() {
		return columns;
	}

	public String getCreateBy() {
		return createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public int getDeleteCascade() {
		return deleteCascade;
	}

	public String getDeleteFetch() {
		return deleteFetch;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayType() {
		return displayType;
	}

	public String getEnglishTitle() {
		return englishTitle;
	}

	public String getEntityName() {
		return entityName;
	}

	public ColumnDefinition getIdColumn() {
		return idColumn;
	}

	public int getInsertCascade() {
		return insertCascade;
	}

	public String getIsSubTable() {
		return isSubTable;
	}

	public int getLocked() {
		return locked;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getParentTableName() {
		return parentTableName;
	}

	public List<QueryDefinition> getQueries() {
		return queries;
	}

	public String getQueryIds() {
		return queryIds;
	}

	public int getRevision() {
		return revision;
	}

	public int getSortNo() {
		return sortNo;
	}

	public String getSysnum() {
		return sysnum;
	}

	public String getSystemFlag() {
		return systemFlag;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTemporaryFlag() {
		return temporaryFlag;
	}

	public String getTitle() {
		return title;
	}

	public String getTopId() {
		return topId;
	}

	public String getType() {
		return type;
	}

	public int getUpdateCascade() {
		return updateCascade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}

	public TableDefinition jsonToObject(JSONObject jsonObject) {
		return TableDefinitionJsonFactory.jsonToObject(jsonObject);
	}

	public void setAddType(int addType) {
		this.addType = addType;
	}

	public void setAggregationKeys(String aggregationKeys) {
		this.aggregationKeys = aggregationKeys;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setColumnQty(int columnQty) {
		this.columnQty = columnQty;
	}

	public void setColumns(List<ColumnDefinition> columns) {
		this.columns = columns;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setDeleteCascade(int deleteCascade) {
		this.deleteCascade = deleteCascade;
	}

	public void setDeleteFetch(String deleteFetch) {
		this.deleteFetch = deleteFetch;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public void setEnglishTitle(String englishTitle) {
		this.englishTitle = englishTitle;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public void setIdColumn(ColumnDefinition idColumn) {
		idColumn.setPrimaryKey(true);
		this.idColumn = idColumn;
	}

	public void setInsertCascade(int insertCascade) {
		this.insertCascade = insertCascade;
	}

	public void setIsSubTable(String isSubTable) {
		this.isSubTable = isSubTable;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setParentTableName(String parentTableName) {
		this.parentTableName = parentTableName;
	}

	public void setQueries(List<QueryDefinition> queries) {
		this.queries = queries;
	}

	public void setQueryIds(String queryIds) {
		this.queryIds = queryIds;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public void setSysnum(String sysnum) {
		this.sysnum = sysnum;
	}

	public void setSystemFlag(String systemFlag) {
		this.systemFlag = systemFlag;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTemporaryFlag(String temporaryFlag) {
		this.temporaryFlag = temporaryFlag;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTopId(String topId) {
		this.topId = topId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUpdateCascade(int updateCascade) {
		this.updateCascade = updateCascade;
	}

	public JSONObject toJsonObject() {
		return TableDefinitionJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return TableDefinitionJsonFactory.toObjectNode(this);
	}

}