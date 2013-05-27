package com.glaf.core.web.springmvc;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.base.ColumnModel;
import com.glaf.core.base.TableModel;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.domain.ColumnDefinition;
import com.glaf.core.domain.TableDefinition;
import com.glaf.core.entity.hibernate.HibernateBeanFactory;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.service.ITableDataService;
import com.glaf.core.service.ITableDefinitionService;
import com.glaf.core.service.ITablePageService;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.Dom4jUtils;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.JsonUtils;
import com.glaf.core.util.QueryUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.ZipUtils;
import com.glaf.core.xml.XmlWriter;

@Controller("/system/table")
@RequestMapping("/system/table")
public class MxSystemDbTableController {

	protected static final Log logger = LogFactory
			.getLog(MxSystemDbTableController.class);

	protected ITableDataService tableDataService;

	protected ITableDefinitionService tableDefinitionService;

	protected ITablePageService tablePageService;

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String tableName = request.getParameter("tableName_enc");
		if (StringUtils.isNotEmpty(tableName)) {
			tableName = RequestUtils.decodeString(tableName);
		} else {
			tableName = request.getParameter("tableName");
		}
		String businessKey = request.getParameter("businessKey");
		String primaryKey = null;
		ColumnDefinition idColumn = null;
		List<ColumnDefinition> columns = null;
		try {
			if (StringUtils.isNotEmpty(tableName)) {
				columns = DBUtils.getColumnDefinitions(tableName);
				modelMap.put("tableName", tableName);
				modelMap.put("tableName_enc",
						RequestUtils.encodeString(tableName));
				List<String> pks = DBUtils.getPrimaryKeys(tableName);
				if (pks != null && !pks.isEmpty()) {
					if (pks.size() == 1) {
						primaryKey = pks.get(0);
					}
				}
				if (primaryKey != null) {
					for (ColumnDefinition column : columns) {
						if (StringUtils.equalsIgnoreCase(primaryKey,
								column.getColumnName())) {
							idColumn = column;
							break;
						}
					}
				}
				if (idColumn != null) {
					TableModel tableModel = new TableModel();
					tableModel.setTableName(tableName);
					ColumnModel idCol = new ColumnModel();
					idCol.setColumnName(idColumn.getColumnName());
					idCol.setJavaType(idColumn.getJavaType());
					if ("Integer".equals(idColumn.getJavaType())) {
						idCol.setValue(Integer.parseInt(businessKey));
					} else if ("Long".equals(idColumn.getJavaType())) {
						idCol.setValue(Long.parseLong(businessKey));
					} else {
						idCol.setValue(businessKey);
					}
					tableModel.setIdColumn(idCol);
					Map<String, Object> dataMap = tableDataService
							.getTableDataByPrimaryKey(tableModel);
					Map<String, Object> rowMap = QueryUtils
							.lowerKeyMap(dataMap);
					for (ColumnDefinition column : columns) {
						Object value = rowMap.get(column.getColumnName()
								.toLowerCase());
						column.setValue(value);
					}
					modelMap.put("idColumn", idColumn);
					modelMap.put("columns", columns);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}

		String x_view = ViewProperties.getString("sys_table.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/modules/sys/table/edit", modelMap);
	}

	@ResponseBody
	@RequestMapping("/genCreateScripts")
	public byte[] genCreateScripts(HttpServletRequest request)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		String tables = request.getParameter("tables");
		String dbType = request.getParameter("dbType");
		if (StringUtils.isNotEmpty(dbType) && StringUtils.isNotEmpty(tables)) {
			List<String> list = StringTools.split(tables);
			for (String table : list) {
				List<ColumnDefinition> columns = DBUtils
						.getColumnDefinitions(table);
				TableDefinition tableDefinition = new TableDefinition();
				tableDefinition.setTableName(table);
				tableDefinition.setColumns(columns);
				for (ColumnDefinition column : columns) {
					if (column.isPrimaryKey()) {
						tableDefinition.setIdColumn(column);
						String sql = DBUtils.getCreateTableScript(dbType,
								tableDefinition);
						sb.append(FileUtils.newline).append(sql)
								.append(FileUtils.newline)
								.append(FileUtils.newline);
						break;
					}
				}
			}
		}

		return sb.toString().getBytes("UTF-8");
	}

	@ResponseBody
	@RequestMapping("/genMappings")
	public void genMappings(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String tables = request.getParameter("tables");
		String dbType = request.getParameter("dbType");
		if (StringUtils.isNotEmpty(dbType) && StringUtils.isNotEmpty(tables)) {
			XmlWriter xmlWriter = new XmlWriter();
			Map<String, byte[]> bytesMap = new HashMap<String, byte[]>();
			List<String> list = StringTools.split(tables);
			for (String table : list) {
				List<ColumnDefinition> columns = DBUtils
						.getColumnDefinitions(table);
				TableDefinition tableDefinition = new TableDefinition();
				tableDefinition.setPackageName("com.glaf.apps");
				tableDefinition.setModuleName("apps");
				tableDefinition.setTableName(table);
				tableDefinition.setClassName(StringTools.upper(StringTools
						.camelStyle(table)));
				tableDefinition.setTitle(tableDefinition.getClassName());
				tableDefinition.setEnglishTitle(tableDefinition.getClassName());
				tableDefinition.setEntityName(StringTools.upper(StringTools
						.camelStyle(table)));
				tableDefinition.setColumns(columns);
				for (ColumnDefinition column : columns) {
					column.setEditable(true);
					column.setUpdatable(true);
					column.setDisplayType(4);
					column.setName(StringTools.lower(StringTools
							.camelStyle(column.getColumnName())));
					column.setTitle(column.getName());
					column.setEnglishTitle(column.getName());
					if (column.isPrimaryKey()) {
						column.setUpdatable(false);
						tableDefinition.setIdColumn(column);
					}
				}
				Document doc = xmlWriter.write(tableDefinition);
				byte[] bytes = Dom4jUtils.getBytesFromPrettyDocument(doc);
				bytesMap.put(table + ".mapping.xml", bytes);
			}
			if (!bytesMap.isEmpty()) {
				byte[] bytes = ZipUtils.toZipBytes(bytesMap);
				try {
					ResponseUtils.download(request, response, bytes,
							"mappings.zip");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@ResponseBody
	@RequestMapping("/json")
	public byte[] json(HttpServletRequest request) throws IOException {
		JSONObject result = new JSONObject();
		JSONArray rowsJSON = new JSONArray();
		String[] types = { "TABLE" };
		Connection connection = null;
		try {
			connection = DBConnectionFactory.getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet rs = metaData.getTables(null, null, null, types);
			int startIndex = 1;
			while (rs.next()) {
				String tableName = rs.getObject("TABLE_NAME").toString();
				tableName = tableName.toLowerCase();
				if (tableName.startsWith("act_")) {
					continue;
				}
				if (tableName.startsWith("jbpm_")) {
					continue;
				}
				if (tableName.startsWith("tmp_")) {
					continue;
				}
				if (tableName.startsWith("temp_")) {
					continue;
				}
				if (tableName.startsWith("demo_")) {
					continue;
				}
				if (tableName.startsWith("wwv_")) {
					continue;
				}
				if (tableName.startsWith("aq_")) {
					continue;
				}
				if (tableName.startsWith("bsln_")) {
					continue;
				}
				if (tableName.startsWith("mgmt_")) {
					continue;
				}
				if (tableName.startsWith("ogis_")) {
					continue;
				}
				if (tableName.startsWith("ols_")) {
					continue;
				}
				if (tableName.startsWith("em_")) {
					continue;
				}
				if (tableName.startsWith("openls_")) {
					continue;
				}
				if (tableName.startsWith("mrac_")) {
					continue;
				}
				if (tableName.startsWith("orddcm_")) {
					continue;
				}
				if (tableName.startsWith("x_")) {
					continue;
				}
				if (tableName.startsWith("wlm_")) {
					continue;
				}
				if (tableName.startsWith("olap_")) {
					continue;
				}
				if (tableName.startsWith("ggs_")) {
					continue;
				}
				if (tableName.startsWith("jpage_")) {
					continue;
				}
				if (tableName.startsWith("ex_")) {
					continue;
				}
				if (tableName.startsWith("logmnrc_")) {
					continue;
				}
				if (tableName.startsWith("logmnrg_")) {
					continue;
				}
				if (tableName.startsWith("olap_")) {
					continue;
				}
				if (tableName.startsWith("sto_")) {
					continue;
				}
				if (tableName.startsWith("sdo_")) {
					continue;
				}
				if (tableName.startsWith("sys_iot_")) {
					continue;
				}
				if (tableName.indexOf("$") != -1) {
					continue;
				}
				if (tableName.indexOf("+") != -1) {
					continue;
				}
				if (tableName.indexOf("-") != -1) {
					continue;
				}
				if (tableName.indexOf("?") != -1) {
					continue;
				}
				if (tableName.indexOf("=") != -1) {
					continue;
				}
				JSONObject json = new JSONObject();
				json.put("startIndex", startIndex++);
				json.put("cat", rs.getObject("TABLE_CAT"));
				json.put("schem", rs.getObject("TABLE_SCHEM"));
				json.put("tablename", tableName);
				json.put("tableName_enc", RequestUtils.encodeString(tableName));
				rowsJSON.add(json);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(connection);
		}

		result.put("rows", rowsJSON);
		result.put("total", rowsJSON.size());
		return result.toJSONString().getBytes("UTF-8");
	}

	@RequestMapping
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String x_query = request.getParameter("x_query");
		if (StringUtils.equals(x_query, "true")) {
			Map<String, Object> paramMap = RequestUtils
					.getParameterMap(request);
			String x_complex_query = JsonUtils.encode(paramMap);
			x_complex_query = RequestUtils.encodeString(x_complex_query);
			request.setAttribute("x_complex_query", x_complex_query);
		} else {
			request.setAttribute("x_complex_query", "");
		}
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("system_table.list");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/modules/sys/table/list", modelMap);
	}

	@RequestMapping("/resultList")
	public ModelAndView resultList(HttpServletRequest request, ModelMap modelMap) {
		String jx_view = request.getParameter("jx_view");
		RequestUtils.setRequestParameterToAttribute(request);
		String tableName = request.getParameter("tableName_enc");
		if (StringUtils.isNotEmpty(tableName)) {
			tableName = RequestUtils.decodeString(tableName);
		} else {
			tableName = request.getParameter("tableName");
		}
		List<ColumnDefinition> columns = null;
		try {
			if (StringUtils.isNotEmpty(tableName)) {
				columns = DBUtils.getColumnDefinitions(tableName);
				modelMap.put("columns", columns);
				modelMap.put("tableName_enc",
						RequestUtils.encodeString(tableName));
				List<String> pks = DBUtils.getPrimaryKeys(tableName);
				if (pks != null && !pks.isEmpty()) {
					if (pks.size() == 1) {
						modelMap.put("primaryKey", pks.get(0));
						for (ColumnDefinition column : columns) {
							if (StringUtils.equalsIgnoreCase(pks.get(0),
									column.getColumnName())) {
								modelMap.put("idColumn", column);
								break;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}

		if (StringUtils.isNotEmpty(jx_view)) {
			return new ModelAndView(jx_view, modelMap);
		}

		String x_view = ViewProperties.getString("sys_table.resultList");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/modules/sys/table/resultList", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String tableName = request.getParameter("tableName_enc");
		if (StringUtils.isNotEmpty(tableName)) {
			tableName = RequestUtils.decodeString(tableName);
		} else {
			tableName = request.getParameter("tableName");
		}
		String businessKey = request.getParameter("businessKey");
		String primaryKey = null;
		ColumnDefinition idColumn = null;
		List<ColumnDefinition> columns = null;
		try {
			/**
			 * ��֤ϵͳ��ȫ�Բ����޸�ϵͳ����������������
			 */
			if (StringUtils.isNotEmpty(tableName)
					&& !StringUtils.equalsIgnoreCase(tableName,
							"cms_publicinfo")
					&& !StringUtils.startsWithIgnoreCase(tableName, "sys")
					&& !StringUtils.startsWithIgnoreCase(tableName, "jbpm")
					&& !StringUtils.startsWithIgnoreCase(tableName, "act")) {
				columns = DBUtils.getColumnDefinitions(tableName);
				modelMap.put("tableName", tableName);
				modelMap.put("tableName_enc",
						RequestUtils.encodeString(tableName));
				List<String> pks = DBUtils.getPrimaryKeys(tableName);
				if (pks != null && !pks.isEmpty()) {
					if (pks.size() == 1) {
						primaryKey = pks.get(0);
					}
				}
				if (primaryKey != null) {
					for (ColumnDefinition column : columns) {
						if (StringUtils.equalsIgnoreCase(primaryKey,
								column.getColumnName())) {
							idColumn = column;
							break;
						}
					}
				}
				if (idColumn != null) {
					TableModel tableModel = new TableModel();
					tableModel.setTableName(tableName);
					ColumnModel idCol = new ColumnModel();
					idCol.setColumnName(idColumn.getColumnName());
					idCol.setJavaType(idColumn.getJavaType());
					if ("Integer".equals(idColumn.getJavaType())) {
						idCol.setValue(Integer.parseInt(businessKey));
					} else if ("Long".equals(idColumn.getJavaType())) {
						idCol.setValue(Long.parseLong(businessKey));
					} else {
						idCol.setValue(businessKey);
					}
					tableModel.setIdColumn(idCol);

					for (ColumnDefinition column : columns) {
						String value = request.getParameter(column
								.getColumnName());
						ColumnModel col = new ColumnModel();
						col.setColumnName(column.getColumnName());
						col.setJavaType(column.getJavaType());
						if (value != null && value.trim().length() > 0
								&& !value.equals("null")) {
							if ("Integer".equals(column.getJavaType())) {
								col.setValue(Integer.parseInt(value));
							} else if ("Long".equals(column.getJavaType())) {
								col.setValue(Long.parseLong(value));
							} else if ("Date".equals(column.getJavaType())) {
								col.setValue(DateUtils.toDate(value));
							} else {
								col.setValue(value);
							}
							tableModel.addColumn(col);
						}
					}

					tableDataService.updateTableData(tableModel);

					return ResponseUtils.responseJsonResult(true);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setTableDataService(ITableDataService tableDataService) {
		this.tableDataService = tableDataService;
	}

	@javax.annotation.Resource
	public void setTableDefinitionService(
			ITableDefinitionService tableDefinitionService) {
		this.tableDefinitionService = tableDefinitionService;
	}

	@javax.annotation.Resource
	public void setTablePageService(ITablePageService tablePageService) {
		this.tablePageService = tablePageService;
	}

	@ResponseBody
	@RequestMapping("/updateHibernateDDL")
	public byte[] updateHibernateDDL(HttpServletRequest request)
			throws IOException {
		try {
			HibernateBeanFactory.reload();
			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ResponseUtils.responseJsonResult(false);
	}

}