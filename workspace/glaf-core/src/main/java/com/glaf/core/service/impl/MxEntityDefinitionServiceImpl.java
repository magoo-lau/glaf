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
package com.glaf.core.service.impl;

import java.util.*;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.id.*;
import com.glaf.core.base.BlobItem;
import com.glaf.core.dao.*;
import com.glaf.core.mapper.*;
import com.glaf.core.domain.*;
import com.glaf.core.query.*;
import com.glaf.core.service.EntityDefinitionService;
import com.glaf.core.service.IBlobService;

@Service("entityDefinitionService")
@Transactional(readOnly = true)
public class MxEntityDefinitionServiceImpl implements EntityDefinitionService {
	protected IBlobService blobService;

	protected EntityDAO entityDAO;

	protected EntityDefinitionMapper entityDefinitionMapper;

	protected IdGenerator idGenerator;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected SqlSessionTemplate sqlSessionTemplate;

	public MxEntityDefinitionServiceImpl() {

	}

	public int count(EntityDefinitionQuery query) {
		query.ensureInitialized();
		return entityDefinitionMapper.getEntityDefinitionCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			entityDefinitionMapper.deleteEntityDefinitionById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> rowIds) {
		if (rowIds != null && !rowIds.isEmpty()) {
			EntityDefinitionQuery query = new EntityDefinitionQuery();
			query.rowIds(rowIds);
			entityDefinitionMapper.deleteEntityDefinitions(query);
		}
	}

	public EntityDefinition getEntityDefinition(String id) {
		if (id == null) {
			return null;
		}
		EntityDefinition entityDefinition = entityDefinitionMapper
				.getEntityDefinitionById(id);
		return entityDefinition;
	}

	public int getEntityDefinitionCountByQueryCriteria(
			EntityDefinitionQuery query) {
		return entityDefinitionMapper.getEntityDefinitionCount(query);
	}

	public List<EntityDefinition> getEntityDefinitionsByQueryCriteria(
			int start, int pageSize, EntityDefinitionQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<EntityDefinition> rows = sqlSessionTemplate.selectList(
				"getEntityDefinitions", query, rowBounds);
		return rows;
	}

	public List<EntityDefinition> list(EntityDefinitionQuery query) {
		query.ensureInitialized();
		List<EntityDefinition> list = entityDefinitionMapper
				.getEntityDefinitions(query);
		return list;
	}

	@Transactional
	public void save(EntityDefinition entityDefinition) {
		if (StringUtils.isEmpty(entityDefinition.getId())) {
			entityDefinition.setId(entityDefinition.getTablename());
			if (entityDefinition.getType() != null) {
				entityDefinition.setId(entityDefinition.getTablename() + "_"
						+ entityDefinition.getType());
			}
			entityDefinition.setCreateDate(new Date());
			entityDefinitionMapper.insertEntityDefinition(entityDefinition);
		} else {
			EntityDefinition model = this.getEntityDefinition(entityDefinition
					.getId());
			if (model == null) {
				entityDefinition.setCreateDate(new Date());
				entityDefinitionMapper.insertEntityDefinition(entityDefinition);
			} else {
				entityDefinition.setUpdateDate(new Date());
				entityDefinitionMapper.updateEntityDefinition(entityDefinition);
			}
		}

		if (entityDefinition.getData() != null) {
			BlobItem blob = new BlobItemEntity();
			blob.setData(entityDefinition.getData());
			blob.setFileId(entityDefinition.getId());
			blob.setBusinessKey(entityDefinition.getId());
			blob.setLastModified(System.currentTimeMillis());
			blob.setFilename(entityDefinition.getName() + ".mapping.xml");
			blob.setStatus(1);
			blob.setCreateBy(entityDefinition.getCreateBy());
			blob.setName(entityDefinition.getName());
			blob.setType(entityDefinition.getType());

			blobService.insertBlob(blob);
		}
	}

	@Resource
	public void setBlobService(IBlobService blobService) {
		this.blobService = blobService;
	}

	@Resource(name = "myBatisEntityDAO")
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Resource
	public void setEntityDefinitionMapper(
			EntityDefinitionMapper entityDefinitionMapper) {
		this.entityDefinitionMapper = entityDefinitionMapper;
	}

	@Resource(name = "myBatisDbIdGenerator")
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Resource
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

}
