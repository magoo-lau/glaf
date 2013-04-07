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
package com.glaf.form.core.graph.def;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

@Entity
@Table(name = "FORM_APPLICATION")
public class FormApplication implements Serializable {
	private static final long serialVersionUID = 1L;

	@Basic
	@Column(name = "ARCHIVESNODE_")
	protected String archivesNode = null;

	@Basic
	@Column(name = "AUDITUPLOADFLAG_")
	protected String auditUploadFlag = null;

	@Basic
	@Column(name = "AUTOARCHIVESFLAG_")
	protected String autoArchivesFlag = null;

	@Basic
	@Column(name = "CREATEBY_", updatable = false)
	protected String createBy = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATEDATE_", updatable = false)
	protected Date createDate = null;

	@Basic
	@Column(name = "DESCRIPTION_")
	protected String description = null;

	@Basic
	@Column(name = "DOCREQUIREDFLAG_")
	protected String docRequiredFlag = null;

	@Basic
	@Column(name = "FORMNAME_")
	protected String formName = null;

	@Basic
	@Column(name = "FORMRENDERERTYPE_")
	protected String formRendererType = null;

	@Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id = null;

	@Basic
	@Column(name = "LINKCONTROLLERNAME_")
	protected String linkControllerName = null;

	@Basic
	@Column(name = "LINKTEMPLATEID_")
	protected String linkTemplateId = null;

	@Basic
	@Column(name = "LISTCONTROLLERNAME_")
	protected String listControllerName = null;

	@Basic
	@Column(name = "LISTTEMPLATEID_")
	protected String listTemplateId = null;

	@Basic
	@Column(name = "MANUALROUTEFLAG_")
	protected String manualRouteFlag = null;

	@Basic
	@Column(name = "NAME_", updatable = false)
	protected String name = null;

	@Basic
	@Column(name = "NODEID_")
	protected Long nodeId;

	@Basic
	@Column(name = "OBJECTID_")
	protected String objectId = null;

	@Basic
	@Column(name = "OBJECTVALUE_")
	protected String objectValue = null;

	@Basic
	@Column(name = "PROCESSDEFINITIONID_")
	protected String processDefinitionId = null;

	@Basic
	@Column(name = "PROCESSNAME_")
	protected String processName = null;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "formApplication")
	@MapKey(name = "name")
	protected Map<String, FormApplicationProperty> properties = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RELEASEDATE_")
	protected Date releaseDate = null;

	@Basic
	@Column(name = "RELEASEFLAG_")
	protected String releaseFlag = null;

	@Basic
	@Column(name = "TABLENAME_")
	protected String tableName = null;

	@Basic
	@Column(name = "TITLE_")
	protected String title = null;

	@Basic
	@Column(name = "UPLOADFLAG_")
	protected String uploadFlag = null;

	public FormApplication() {

	}

	@Transient
	public void addProperty(FormApplicationProperty property) {
		if (property != null && StringUtils.isNotEmpty(property.getName())
				&& StringUtils.isNotEmpty(property.getValue())) {
			property.setFormApplication(this);
			if (properties == null) {
				properties = new HashMap<String, FormApplicationProperty>();
			}
			FormApplicationProperty p = properties.get(property.getName());
			if (p == null) {
				p = new FormApplicationProperty();
			}
			p.setName(property.getName());
			p.setValue(property.getValue());
			p.setFormApplication(this);
			properties.put(name, p);
		}
	}

	@Transient
	public void addProperty(String name, String value) {
		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
			if (properties == null) {
				properties = new HashMap<String, FormApplicationProperty>();
			}
			FormApplicationProperty p = properties.get(name);
			if (p == null) {
				p = new FormApplicationProperty();
			}
			p.setName(name);
			p.setValue(value);
			p.setFormApplication(this);
			properties.put(name, p);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormApplication other = (FormApplication) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getArchivesNode() {
		return archivesNode;
	}

	public String getAuditUploadFlag() {
		return auditUploadFlag;
	}

	public String getAutoArchivesFlag() {
		return autoArchivesFlag;
	}

	public String getCreateBy() {
		return createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getDescription() {
		return description;
	}

	public String getDocRequiredFlag() {
		return docRequiredFlag;
	}

	public String getFormName() {
		return formName;
	}

	public String getFormRendererType() {
		return formRendererType;
	}

	public String getId() {
		return id;
	}

	public String getLinkControllerName() {
		return linkControllerName;
	}

	public String getLinkTemplateId() {
		return linkTemplateId;
	}

	public String getListControllerName() {
		return listControllerName;
	}

	public String getListTemplateId() {
		return listTemplateId;
	}

	public String getManualRouteFlag() {
		return manualRouteFlag;
	}

	public String getName() {
		return name;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getObjectValue() {
		return objectValue;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public String getProcessName() {
		return processName;
	}

	public Map<String, FormApplicationProperty> getProperties() {
		return properties;
	}

	@Transient
	public String getProperty(String name) {
		if (properties != null) {
			FormApplicationProperty p = properties.get(name);
			if (p != null) {
				return p.getValue();
			}
		}
		return null;
	}

	@Transient
	public String getProperty(String name, String defaultValue) {
		if (properties != null) {
			FormApplicationProperty p = properties.get(name);
			if (p != null && StringUtils.isNotEmpty(p.getValue())) {
				return p.getValue();
			}
		}
		return defaultValue;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public String getReleaseFlag() {
		return releaseFlag;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTitle() {
		return title;
	}

	public String getUploadFlag() {
		return uploadFlag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setArchivesNode(String archivesNode) {
		this.archivesNode = archivesNode;
	}

	public void setAuditUploadFlag(String auditUploadFlag) {
		this.auditUploadFlag = auditUploadFlag;
	}

	public void setAutoArchivesFlag(String autoArchivesFlag) {
		this.autoArchivesFlag = autoArchivesFlag;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDocRequiredFlag(String docRequiredFlag) {
		this.docRequiredFlag = docRequiredFlag;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setFormRendererType(String formRendererType) {
		this.formRendererType = formRendererType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLinkControllerName(String linkControllerName) {
		this.linkControllerName = linkControllerName;
	}

	public void setLinkTemplateId(String linkTemplateId) {
		this.linkTemplateId = linkTemplateId;
	}

	public void setListControllerName(String listControllerName) {
		this.listControllerName = listControllerName;
	}

	public void setListTemplateId(String listTemplateId) {
		this.listTemplateId = listTemplateId;
	}

	public void setManualRouteFlag(String manualRouteFlag) {
		this.manualRouteFlag = manualRouteFlag;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public void setObjectValue(String objectValue) {
		this.objectValue = objectValue;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public void setProperties(Map<String, FormApplicationProperty> properties) {
		this.properties = properties;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setReleaseFlag(String releaseFlag) {
		this.releaseFlag = releaseFlag;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

}