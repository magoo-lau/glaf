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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.id.*;

import com.glaf.core.domain.*;
import com.glaf.core.query.*;
import com.glaf.core.mapper.*;
import com.glaf.core.service.ITableDefinitionService;
import com.glaf.core.util.StringTools;

@Service("tableDefinitionService")
@Transactional(readOnly = true)
public class MxTableDefinitionServiceImpl implements ITableDefinitionService {
	protected final static Log logger = LogFactory
			.getLog(MxTableDefinitionServiceImpl.class);

	protected IdGenerator idGenerator;

	protected SqlSession sqlSession;

	protected QueryDefinitionMapper queryDefinitionMapper;

	protected TableDefinitionMapper tableDefinitionMapper;

	protected ColumnDefinitionMapper columnDefinitionMapper;

	public MxTableDefinitionServiceImpl() {

	}

	public int count(TableDefinitionQuery query) {
		query.ensureInitialized();
		if (StringUtils.isEmpty(query.getType())) {
			throw new RuntimeException(" type is null ");
		}
		return tableDefinitionMapper.getTableDefinitionCount(query);
	}

	@Transactional
	public void deleteColumn(String columnId) {
		columnDefinitionMapper.deleteColumnDefinitionById(columnId);
	}

	/**
	 * ɾ��������
	 * 
	 * @param tableName
	 */
	@Transactional
	public void deleteTable(String tableName) {
		columnDefinitionMapper
				.deleteColumnDefinitionByTableName(tableName);
		tableDefinitionMapper.deleteTableDefinitionById(tableName);
	}

	public ColumnDefinition getColumnDefinition(String columnId) {
		return columnDefinitionMapper.getColumnDefinitionById(columnId);
	}

	public List<ColumnDefinition> getColumnDefinitionsByTableName(
			String tableName) {
		TableDefinition tableDefinition = this.getTableDefinition(tableName);
		if (tableDefinition != null) {
			return tableDefinition.getColumns();
		}
		return null;
	}

	public TableDefinition getTableDefinition(String tableName) {
		if (tableName == null) {
			return null;
		}
		TableDefinition tableDefinition = tableDefinitionMapper
				.getTableDefinitionById(tableName);
		if (tableDefinition != null) {
			if (StringUtils.isNotEmpty(tableDefinition.getQueryIds())) {
				QueryDefinitionQuery query = new QueryDefinitionQuery();
				List<String> queryIds = StringTools.split(tableDefinition
						.getQueryIds());
				query.queryIds(queryIds);
				List<QueryDefinition> list = queryDefinitionMapper
						.getQueryDefinitions(query);
				if (list != null && !list.isEmpty()) {
					for (QueryDefinition q : list) {
						tableDefinition.addQuery(q);
					}
				}
			}

			List<ColumnDefinition> columns = columnDefinitionMapper
					.getColumnDefinitionsByTableName(tableName);
			if (columns != null && !columns.isEmpty()) {
				tableDefinition.setColumns(columns);
				for (ColumnDefinition column : columns) {
					if (column.isPrimaryKey()) {
						logger.debug("##PrimaryKey:"
								+ column.toJsonObject().toJSONString());
						tableDefinition.setIdColumn(column);
						columns.remove(column);
						break;
					}
				}
			}
		}

		return tableDefinition;
	}

	/**
	 * ���ݲ�ѯ������ȡ��¼����
	 * 
	 * @return
	 */
	public int getTableDefinitionCountByQueryCriteria(TableDefinitionQuery query) {
		if (StringUtils.isEmpty(query.getType())) {
			throw new RuntimeException(" type is null ");
		}
		return tableDefinitionMapper.getTableDefinitionCount(query);
	}

	/**
	 * ���ݲ�ѯ������ȡһҳ������
	 * 
	 * @return
	 */
	public List<TableDefinition> getTableDefinitionsByQueryCriteria(int start,
			int pageSize, TableDefinitionQuery query) {
		if (StringUtils.isEmpty(query.getType())) {
			throw new RuntimeException(" type is null ");
		}
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<TableDefinition> rows = sqlSession.selectList(
				"getTableDefinitions", query, rowBounds);
		return rows;
	}

	public List<TableDefinition> list(TableDefinitionQuery query) {
		query.ensureInitialized();
		if (StringUtils.isEmpty(query.getType())) {
			throw new RuntimeException(" type is null ");
		}
		List<TableDefinition> list = tableDefinitionMapper
				.getTableDefinitions(query);
		return list;
	}

	public List<TableDefinition> getTableColumnsCount(TableDefinitionQuery query) {
		List<TableDefinition> list = tableDefinitionMapper
				.getTableColumnsCount(query);
		return list;
	}

	@Transactional
	public void save(TableDefinition tableDefinition) {
		TableDefinition table = this.getTableDefinition(tableDefinition
				.getTableName());
		if (table == null) {
			tableDefinition.setCreateTime(new Date());
			tableDefinition.setRevision(1);
			if (tableDefinition.getColumns() != null
					&& !tableDefinition.getColumns().isEmpty()) {
				tableDefinition.setColumnQty(tableDefinition.getColumns()
						.size());
			}
			tableDefinitionMapper.insertTableDefinition(tableDefinition);
		} else {
			if (tableDefinition.getColumns() != null
					&& !tableDefinition.getColumns().isEmpty()) {
				tableDefinition.setColumnQty(tableDefinition.getColumns()
						.size());
			}
			tableDefinition.setRevision(tableDefinition.getRevision() + 1);
			tableDefinitionMapper.updateTableDefinition(tableDefinition);
		}

		if (tableDefinition.getColumns() != null
				&& !tableDefinition.getColumns().isEmpty()) {
			for (ColumnDefinition column : tableDefinition.getColumns()) {
				String id = (tableDefinition.getTableName().toLowerCase() + "_" + column
						.getColumnName()).toLowerCase();
				if (columnDefinitionMapper.getColumnDefinitionById(id) == null) {
					column.setId(id);
					column.setTableName(tableDefinition.getTableName()
							.toLowerCase());
					if ("SYS".equals(tableDefinition.getType())) {
						column.setSystemFlag("1");
					}
					columnDefinitionMapper.insertColumnDefinition(column);
				} else {
					columnDefinitionMapper.updateColumnDefinition(column);
				}
			}
		}

		if ("SYS".equals(tableDefinition.getType())) {
			String id = (tableDefinition.getTableName() + "_ID")
					.toLowerCase();
			if (columnDefinitionMapper.getColumnDefinitionById(id) == null) {
				ColumnDefinition column = new ColumnDefinition();
				column.setColumnName("ID");
				column.setId(id);
				column.setName("id");
				column.setTitle("ID");
				column.setPrimaryKey(true);
				column.setJavaType("String");
				column.setLength(50);
				column.setSystemFlag("1");
				column.setTableName(tableDefinition.getTableName()
						.toLowerCase());
				columnDefinitionMapper.insertColumnDefinition(column);
			}
		}
	}

	@Transactional
	public void saveColumn(String tableName, ColumnDefinition columnDefinition) {
		TableDefinition tableDefinition = this.getTableDefinition(tableName);
		if (tableDefinition != null) {
			List<ColumnDefinition> columns = tableDefinition.getColumns();
			boolean exists = false;
			if (columns != null && !columns.isEmpty()) {
				for (ColumnDefinition column : columns) {
					if (StringUtils.equalsIgnoreCase(column.getColumnName(),
							columnDefinition.getColumnName())) {
						column.setValueExpression(columnDefinition
								.getValueExpression());
						column.setFormula(columnDefinition.getFormula());
						column.setName(columnDefinition.getName());
						column.setTitle(columnDefinition.getTitle());
						column.setEnglishTitle(columnDefinition
								.getEnglishTitle());
						column.setHeight(columnDefinition.getHeight());
						column.setLength(columnDefinition.getLength());
						column.setTranslator(columnDefinition.getTranslator());
						column.setPrecision(columnDefinition.getPrecision());
						column.setScale(columnDefinition.getScale());
						column.setAlign(columnDefinition.getAlign());
						column.setCollection(columnDefinition.isCollection());
						column.setFrozen(columnDefinition.isFrozen());
						column.setNullable(columnDefinition.isNullable());
						column.setSortable(columnDefinition.isSortable());
						column.setDisplayType(columnDefinition.getDisplayType());
						column.setDiscriminator(columnDefinition
								.getDiscriminator());
						column.setFormatter(columnDefinition.getFormatter());
						column.setLink(columnDefinition.getLink());
						column.setOrdinal(columnDefinition.getOrdinal());
						column.setRegex(columnDefinition.getRegex());
						column.setSortType(columnDefinition.getSortType());
						column.setSummaryExpr(columnDefinition.getSummaryExpr());
						column.setSummaryType(columnDefinition.getSummaryType());

						columnDefinitionMapper
								.updateColumnDefinition(column);
						exists = true;
						break;
					}
				}
			}
			if (!exists) {
				String id = (tableDefinition.getTableName().toLowerCase() + "_" + columnDefinition
						.getColumnName()).toLowerCase();
				columnDefinition.setId(id);
				columnDefinition.setTableName(tableDefinition.getTableName()
						.toLowerCase());
				columnDefinitionMapper
						.insertColumnDefinition(columnDefinition);
			}
		}
	}

	@Transactional
	public void saveSystemTable(String tableName, List<ColumnDefinition> rows) {
		List<ColumnDefinition> list = columnDefinitionMapper
				.getColumnDefinitionsByTableName(tableName);
		TableDefinition table = this.getTableDefinition(tableName);
		if (table == null) {
			table = new TableDefinition();
			table.setType("SYS");
			table.setSystemFlag("1");
			table.setTableName(tableName.toLowerCase());
			table.setColumnQty(rows.size());
			table.setCreateBy("system");
			table.setCreateTime(new Date());
			table.setRevision(1);
			table.setDeleteFlag(0);
			table.setLocked(0);
			table.setEnglishTitle(tableName);
			tableDefinitionMapper.insertTableDefinition(table);
		} else {
			table.setColumnQty(rows.size());
			tableDefinitionMapper.updateTableDefinition(table);
		}
		if (list != null && !list.isEmpty()) {
			for (ColumnDefinition c : list) {
				columnDefinitionMapper.deleteColumnDefinitionById(c
						.getId());
			}
		}
		for (ColumnDefinition column : rows) {
			String id = (tableName.toLowerCase() + "_" + column.getColumnName())
					.toLowerCase();
			column.setId(id);
			column.setSystemFlag("1");
			column.setTableName(tableName.toLowerCase());
			columnDefinitionMapper.insertColumnDefinition(column);
		}

	}

	@Resource
	public void setColumnDefinitionMapper(
			ColumnDefinitionMapper columnDefinitionMapper) {
		this.columnDefinitionMapper = columnDefinitionMapper;
	}

	@Resource
	@Qualifier("myBatisDbIdGenerator")
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Resource
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Resource
	public void setQueryDefinitionMapper(
			QueryDefinitionMapper queryDefinitionMapper) {
		this.queryDefinitionMapper = queryDefinitionMapper;
	}

	@Resource
	public void setTableDefinitionMapper(
			TableDefinitionMapper tableDefinitionMapper) {
		this.tableDefinitionMapper = tableDefinitionMapper;
	}

}