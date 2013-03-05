<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    String theme = com.glaf.core.util.RequestUtils.getTheme(request);
    request.setAttribute("theme", theme);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>出差申请</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/easyui/themes/${theme}/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/easyui/themes/icon.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">

   jQuery(function(){
		jQuery('#mydatagrid').datagrid({
				width:1000,
				height:480,
				fit:true,
				fitColumns:true,
				nowrap: false,
				striped: true,
				collapsible:true,
				url:'<%=request.getContextPath()%>/mx/apps/trip/json',
				sortName: 'id',
				sortOrder: 'desc',
				remoteSort: false,
				singleSelect:true,
				idField:'id',
				columns:[[
	                {title:'编号',field:'id',width:80,sortable:true},
					{title:'交通工具',field:'transType', width:120},
					{title:'开始日期',field:'startDate', width:120},
					{title:'结束日期',field:'endDate', width:120},
					{title:'天数',field:'days', width:120},
					{title:'费用',field:'money', width:120},
					{title:'创建日期',field:'createDate', width:120},
					{title:'创建人',field:'createBy', width:120},
					{title:'创建人',field:'createByName', width:120},
					{title:'修改日期',field:'updateDate', width:120},
					{title:'是否锁定',field:'locked', width:120},
					{title:'状态',field:'status', width:120},
					{title:'流程名称',field:'processName', width:120},
					{title:'流程实例编号',field:'processInstanceId', width:120},
					{field:'functionKey',title:'功能键',width:120}
				]],
				rownumbers:false,
				pagination:true,
				pageSize:15,
				pageList: [10,15,20,25,30,40,50,100],
				onDblClickRow: onRowClick 
			});

			var p = jQuery('#mydatagrid').datagrid('getPager');
			jQuery(p).pagination({
				onBeforeRefresh:function(){
					//alert('before refresh');
				}
		    });
	});

		 
	function addNew(){
		location.href="<%=request.getContextPath()%>/mx/apps/trip/edit";
	}

	function onRowClick(rowIndex, row){
            window.open('<%=request.getContextPath()%>/mx/apps/trip/edit?rowId='+row.id);
	}

	function searchWin(){
		jQuery('#dlg').dialog('open').dialog('setTitle','出差申请查询');
		//jQuery('#searchForm').form('clear');
	}

	function resize(){
		jQuery('#mydatagrid').datagrid('resize', {
			width:800,
			height:400
		});
	}

	function editSelected(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		if(rows == null || rows.length !=1){
			alert("请选择其中一条记录。");
			return;
		}
		var selected = jQuery('#mydatagrid').datagrid('getSelected');
		if (selected ){
			location.href="<%=request.getContextPath()%>/mx/apps/trip/edit?rowId="+selected.id;
		}
	}

	function viewSelected(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		if(rows == null || rows.length !=1){
			alert("请选择其中一条记录。");
			return;
		}
		var selected = jQuery('#mydatagrid').datagrid('getSelected');
		if (selected ){
		    location.href="<%=request.getContextPath()%>/mx/apps/trip/edit?readonly=true&rowId="+selected.id;
		}
	}

	function deleteSelections(){
		var ids = [];
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		for(var i=0;i<rows.length;i++){
			ids.push(rows[i].id);
		}
		if(ids.length > 0 && confirm("数据删除后不能恢复，确定删除吗？")){
		    var rowIds = ids.join(',');
			jQuery.ajax({
				   type: "POST",
				   url: '<%=request.getContextPath()%>/mx/apps/trip/delete?rowIds='+rowIds,
				   dataType:  'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
					   if(data.message != null){
						   alert(data.message);
					   } else {
						 alert('操作成功完成！');
					   }
					   jQuery('#mydatagrid').datagrid('reload');
				   }
			 });
		} else {
			alert("请选择至少一条记录。");
		}
	}

	function getSelected(){
		var selected = jQuery('#mydatagrid').datagrid('getSelected');
		if (selected){
			alert(selected.code+":"+selected.name+":"+selected.addr+":"+selected.col4);
		}
	}

	function getSelections(){
		var ids = [];
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		for(var i=0;i<rows.length;i++){
			ids.push(rows[i].code);
		}
		alert(ids.join(':'));
	}

	function clearSelections(){
		jQuery('#mydatagrid').datagrid('clearSelections');
	}

	function searchData(){
		var params = jQuery("#searchForm").formSerialize();
		var queryParams = jQuery('#mydatagrid').datagrid('options').queryParams;
		queryParams.transType = document.getElementById("query_transType").value;
		queryParams.applyDate = document.getElementById("query_applyDate").value;
		queryParams.startDate = document.getElementById("query_startDate").value;
		queryParams.endDate = document.getElementById("query_endDate").value;
		queryParams.days = document.getElementById("query_days").value;
		queryParams.money = document.getElementById("query_money").value;
		queryParams.cause = document.getElementById("query_cause").value;
		jQuery('#mydatagrid').datagrid('reload');	
		jQuery('#dlg').dialog('close');
	}
		 
</script>
</head>
<body style="margin:1px;">  
<div style="margin:0;"></div>  
<div class="easyui-layout" data-options="fit:true">  
   <div data-options="region:'north',split:true,border:true" style="height:40px"> 
    <div style="background:#fafafa;padding:2px;border:1px solid #ddd;font-size:12px"> 
	<img src="<%=request.getContextPath()%>/images/window.png">
	&nbsp;<span class="x_content_title">出差申请列表</span>
    <a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-add'" 
	   onclick="javascript:addNew();">新增</a>  
    <a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-edit'"
	   onclick="javascript:editSelected();">修改</a>  
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-remove'"
	   onclick="javascript:deleteSelections();">删除</a> 
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-search'"
	   onclick="javascript:searchWin();">查找</a>
   </div> 
  </div> 
  <div data-options="region:'center',border:true">
	 <table id="mydatagrid"></table>
  </div>  
</div>
<div id="dlg" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
	closed="true" buttons="#dlg-buttons">
 <form id="searchForm" name="searchForm" method="post">
	 <table class="easyui-form" >
		<tbody>
			<tr>
				 <td>交通工具</td>
				 <td>
                 <input id="query_transType" name="query_transType" class="easyui-validatebox" type="text"></input>
				</td>
			</tr>
			<tr>
				 <td>申请日期</td>
				 <td>
                 <input id="query_applyDate" name="query_applyDate" class="easyui-validatebox" type="text"></input>
				</td>
			</tr>
			<tr>
				 <td>开始日期</td>
				 <td>
                 <input id="query_startDate" name="query_startDate" class="easyui-validatebox" type="text"></input>
				</td>
			</tr>
			<tr>
				 <td>结束日期</td>
				 <td>
                 <input id="query_endDate" name="query_endDate" class="easyui-validatebox" type="text"></input>
				</td>
			</tr>
			<tr>
				 <td>天数</td>
				 <td>
                 <input id="query_days" name="query_days" class="easyui-validatebox" type="text"></input>
				</td>
			</tr>
			<tr>
				 <td>费用</td>
				 <td>
                 <input id="query_money" name="query_money" class="easyui-validatebox" type="text"></input>
				</td>
			</tr>
			<tr>
				 <td>事由</td>
				 <td>
                 <input id="query_cause" name="query_cause" class="easyui-validatebox" type="text"></input>
				</td>
			</tr>
	</tbody>
</table>
</div>
<div id="dlg-buttons">
	<a href="#" class="easyui-linkbutton" iconCls="icon-ok" onclick="javascript:searchData()">查询</a>
	<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:jQuery('#dlg').dialog('close')">取消</a>
</div>
</body>
</html>