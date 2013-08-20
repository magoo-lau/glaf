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

package com.glaf.base.modules.sys.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.glaf.base.dao.AbstractSpringDao;
import com.glaf.base.modules.sys.model.SysDepartment;
import com.glaf.base.modules.sys.model.SysDeptRole;
import com.glaf.base.modules.sys.model.SysRole;
import com.glaf.base.modules.sys.model.SysUser;
import com.glaf.base.modules.sys.model.SysUserRole;
import com.glaf.base.modules.sys.query.SysUserQuery;
import com.glaf.base.modules.sys.service.*;
import com.glaf.core.util.PageResult;
import com.glaf.core.util.StringTools;
 

public class SysUserServiceImpl implements SysUserService {
	private static final Log logger = LogFactory
			.getLog(SysUserServiceImpl.class);

	private AbstractSpringDao abstractDao;

	public void setAbstractDao(AbstractSpringDao abstractDao) {
		this.abstractDao = abstractDao;
		logger.info("setAbstractDao");
	}

	/**
	 * ����
	 * 
	 * @param bean
	 *            SysUser
	 * @return boolean
	 */
	public boolean create(SysUser bean) {
		return abstractDao.create(bean);
	}

	/**
	 * ����
	 * 
	 * @param bean
	 *            SysUser
	 * @return boolean
	 */
	public boolean update(SysUser bean) {
		return abstractDao.update(bean);
	}

	public boolean updateUser(SysUser bean) {

		String sql = " update sys_user set lastLoginTime = ? ,lastLoginIP = ? where account = ? ";
		List<Object> values = new ArrayList<Object>();
		values.add(bean.getLastLoginTime());
		values.add(bean.getLastLoginIP());
		values.add(bean.getAccount());

		return abstractDao.executeSQL(sql, values);
	}

	/**
	 * ɾ��
	 * 
	 * @param bean
	 *            SysUser
	 * @return boolean
	 */
	public boolean delete(SysUser bean) {
		return abstractDao.delete(bean);
	}

	/**
	 * ɾ��
	 * 
	 * @param id
	 *            int
	 * @return boolean
	 */
	public boolean delete(long id) {
		SysUser bean = findById(id);
		if (bean != null) {
			return delete(bean);
		} else {
			return false;
		}
	}

	/**
	 * ����ɾ��
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteAll(long[] ids) {
		List<SysUser> list = new ArrayList<SysUser>();
		for (int i = 0; i < ids.length; i++) {
			SysUser bean = findById(ids[i]);
			if (bean != null)
				list.add(bean);
		}
		return abstractDao.deleteAll(list);
	}

	/**
	 * ��ȡ����
	 * 
	 * @param id
	 * @return
	 */
	public SysUser findById(long id) {
		if (id == 0) {
			return null;
		}
		return (SysUser) abstractDao.find(SysUser.class, new Long(id));
	}

	/**
	 * �����Ʋ��Ҷ���
	 * 
	 * @param name
	 *            String
	 * @return SysUser
	 */
	public SysUser findByAccount(String account) {
		SysUser bean = null;
		Object[] values = new Object[] { account };
		String query = " from SysUser a where a.account=? order by a.id desc";
		List list = abstractDao.getList(query, values, null);
		if (list != null && list.size() > 0) {// �м�¼
			bean = (SysUser) list.get(0);
		}

		return bean;
	}

	/**
	 * ��ȡĳ���û����ϼ�
	 * 
	 * @param account
	 * @return
	 */
	public List<SysUser> getSuperiors(String account) {
		List<SysUser> superiors = new ArrayList<SysUser>();
		SysUser bean = this.findByAccount(account);
		if (bean != null && bean.getSuperiorIds() != null) {
			List<String> superiorIds = StringTools.split(bean.getSuperiorIds());
			if (superiorIds != null && !superiorIds.isEmpty()) {
				for (String superiorId : superiorIds) {
					SysUser user = this.findByAccount(superiorId);
					if (user != null && user.getBlocked() == 0) {
						superiors.add(user);
					}
				}
			}
		}
		return superiors;
	}

	/**
	 * �����Ʋ��Ҷ���
	 * 
	 * @param name
	 *            String
	 * @return SysUser
	 */
	public SysUser findByAccountWithAll(String account) {
		SysUser bean = null;
		Object[] values = new Object[] { account };
		String query = "from SysUser a where a.account=? order by a.id desc";
		List list = abstractDao.getList(query, values, null);
		if (list != null && list.size() > 0) {// �м�¼
			bean = (SysUser) list.get(0);
			if (bean != null) {
				Hibernate.initialize(bean.getApps());
				Hibernate.initialize(bean.getDepartment());
				Hibernate.initialize(bean.getRoles());
				Hibernate.initialize(bean.getUserRoles());
				Hibernate.initialize(bean.getFunctions());
			}
		}

		return bean;
	}

	/**
	 * ��ȡ�ض����ŵ�Ա�����ݼ� ��ҳ�б�
	 * 
	 * @param deptId
	 *            int
	 * @param pageNo
	 *            int
	 * @param pageSize
	 *            int
	 * @return
	 */
	public PageResult getSysUserList(int deptId, int pageNo, int pageSize) {
		if (deptId == 0) {// ��ʾ�����û�
			// ��������
			String query = "select count(*) from SysUser a where a.accountType=0";
			int count = ((Long) abstractDao.getList(query, null, null)
					.iterator().next()).intValue();
			if (count == 0) {// �����Ϊ��
				PageResult pager = new PageResult();
				pager.setPageSize(pageSize);
				return pager;
			}
			// ��ѯ�б�
			query = "select a from SysUser a where a.accountType=0 order by a.id desc";
			return abstractDao.getList(query, null, pageNo, pageSize, count);
		} else {// ��������ʾ�û�
			// ��������
			Object[] values = new Object[] { new Long(deptId) };
			String query = "select count(*) from SysUser a where a.department.id=? ";
			int count = ((Long) abstractDao.getList(query, values, null)
					.iterator().next()).intValue();
			if (count == 0) {// �����Ϊ��
				PageResult pager = new PageResult();
				pager.setPageSize(pageSize);
				return pager;
			}
			// ��ѯ�б�
			query = "select a from SysUser a where a.department.id=? "
					+ "order by a.id desc";
			return abstractDao.getList(query, values, pageNo, pageSize, count);
		}

	}

	/**
	 * ��ѯ��ȡsysUser�б�
	 * 
	 * @param deptId
	 * @param fullName
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public PageResult getSysUserList(int deptId, String fullName, int pageNo,
			int pageSize) {
		String query = "";
		List arrayList = new ArrayList();
		if (deptId == 0) {
			if (fullName == null || "".equals(fullName)) {
				query = "select count(*) from SysUser a where a.accountType=0";
				int count = ((Long) abstractDao.getList(query, null, null)
						.iterator().next()).intValue();
				if (count == 0) {// �����Ϊ��
					PageResult pager = new PageResult();
					pager.setPageSize(pageSize);
					return pager;
				}
				query = "select a from SysUser a where a.accountType=0 order by a.id desc";
				return abstractDao
						.getList(query, null, pageNo, pageSize, count);
			} else {
				Object[] values = new Object[] { new String("%" + fullName
						+ "%") };
				query = " select count(*) from SysUser a where a.name like ? and a.accountType=0";
				List list = abstractDao.getList(query, values, null);

				if (list == null) {// �����Ϊ��
					PageResult pager = new PageResult();
					pager.setPageSize(pageSize);
					return pager;
				}
				int count = list.size();
				query = "select a from SysUser a where a.name like ? and a.accountType=0";
				return abstractDao.getList(query, values, pageNo, pageSize,
						count);
			}
		} else {
			query = "select a from SysUser a where a.department.id=? and a.accountType=0";
			arrayList.add(new Long(deptId));

			if (fullName != null && !"".equals(fullName)) {
				query += "and a.name like ?";
				arrayList.add("%" + fullName + "%");
			}
			Object[] values = arrayList.toArray();
			List list = abstractDao.getList(query, values, null);
			if (list == null) {// �����Ϊ��
				PageResult pager = new PageResult();
				pager.setPageSize(pageSize);
				return pager;
			}
			int count = list.size();
			logger.info(query);
			logger.info("count = " + count);
			return abstractDao.getList(query, values, pageNo, pageSize, count);
		}
	}

	/**
	 * ��ȡ�б�
	 * 
	 * @param deptId
	 *            int
	 * @return List
	 */
	public List getSysUserList(int deptId) {
		// ��������
		Object[] values = new Object[] { new Long(deptId) };
		String query = "from SysUser a where a.department.id=? order by a.id desc";
		return abstractDao.getList(query, values, null);
	}

	/**
	 * ��ȡ�б�
	 * 
	 * @param deptId
	 *            int
	 * @return List
	 */
	public List getSysUserList() {
		// ��������
		String query = "from SysUser a order by a.id desc";
		return abstractDao.getList(query, null, null);
	}

	/**
	 * ��ȡ�б�
	 * 
	 * @param deptId
	 *            int
	 * @return List
	 */
	public List getSysUserWithDeptList() {
		// ��������
		String query = "from SysUser a order by a.id desc";
		List rows = abstractDao.getList(query, null, null);
		Iterator iterator = rows.iterator();
		while (iterator.hasNext()) {
			SysUser user = (SysUser) iterator.next();
			SysDepartment dept = user.getDepartment();
			if (dept == null) {
				Hibernate.initialize(user.getDepartment());
			}
		}
		return rows;
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public Set<SysDeptRole> getUserRoles(SysUser user) {
		Set<SysDeptRole> set = new HashSet<SysDeptRole>();
		Object[] values = new Object[] { user };
		String query = "from SysUserRole a where a.user=?";
		List<SysUserRole> rows = abstractDao.getList(query, values, null);
		Iterator<SysUserRole> iter = rows.iterator();
		while (iter.hasNext()) {
			SysUserRole bean = (SysUserRole) iter.next();
			if (bean.getDeptRole() != null) {
				// logger.debug("id=" + bean.getDeptRole().getId());
				set.add(bean.getDeptRole());
			}
		}

		// logger.debug("========================user roles:" + set);
		return set;
	}

	/**
	 * ���û�Ȩ��
	 * 
	 * @param user
	 * @return
	 */
	public SysUser getUserPrivileges(SysUser user) {
		SysUser bean = user;
		try {
			bean.setRoles(getUserRoles(bean));
			Iterator roles = bean.getRoles().iterator();
			while (roles.hasNext()) {
				SysDeptRole role = (SysDeptRole) roles.next();
				Set functions = role.getFunctions();
				Set apps = role.getApps();
				// logger.debug("========================apps:" + apps);
				bean.getFunctions().addAll(functions);
				bean.getApps().addAll(apps);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bean;
	}

	public SysUser getUserAndPrivileges(SysUser user) {
		SysUser bean = user;
		try {
			bean.setRoles(getUserRoles(bean));
			Iterator roles = bean.getRoles().iterator();
			while (roles.hasNext()) {
				SysDeptRole role = (SysDeptRole) roles.next();
				Set functions = role.getFunctions();
				Set apps = role.getApps();
				bean.getFunctions().addAll(functions);
				bean.getApps().addAll(apps);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bean;
	}

	/**
	 * ���ҹ�Ӧ���û� flag = true ��ʾ���û����ڣ�����Ϊ������
	 * 
	 * @param supplierNo
	 * @return
	 */
	public List getSupplierUser(String supplierNo) {
		Object[] values = new Object[] { supplierNo };
		String query = "from SysUser s where s.account=?";
		return abstractDao.getList(query, values, null);
	}

	/**
	 * �����û�Ȩ��
	 * 
	 * @param user
	 *            ϵͳ�û�
	 * @param delRoles
	 *            Ҫɾ�����û�Ȩ��
	 * @param newRoles
	 *            Ҫ���ӵ��û�Ȩ��
	 */
	public boolean updateRole(SysUser user, Set delRoles, Set newRoles) {
		boolean flag = false;
		// ɾ��Ҫɾ����Ȩ��
		user.getRoles().removeAll(delRoles);
		// ������Ȩ��
		if (newRoles != null && !newRoles.isEmpty()) {
			long ts = System.currentTimeMillis();
			Iterator<SysUserRole> iter = newRoles.iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				if (obj instanceof SysUserRole) {
					SysUserRole r = (SysUserRole) obj;
					if (r.getId() == 0) {
						r.setId(ts++);
					}
				}
			}
			user.getRoles().addAll(newRoles);
		}

		flag = update(user);
		return flag;
	}

	/**
	 * ��ȡ�б�
	 * 
	 */
	public PageResult getSysUserList(int deptId, String userName,
			String account, int pageNo, int pageSize) {
		String query = "from SysUser a where 1=1 ";
		if (deptId != 0) {
			query += " and a.department.id=" + deptId;
		}
		if (null != userName && userName.trim().length() > 0) {
			query += " and a.name like '%" + userName + "%'";
		}
		if (null != account && account.trim().length() > 0) {
			query += " and a.account like '%" + account + "%'";
		}
		int count = abstractDao.getResutlTotalByQuery("select count(*) "
				+ query, null, null);
		query += " order by a.id,a.department.id";
		PageResult pager = abstractDao.getList(query, null, pageNo, pageSize,
				count);
		return pager;
	}

	public boolean isThisPlayer(SysUser user, String code) {
		boolean flag = false;
		Set set = user.getRoles();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SysDeptRole s = (SysDeptRole) it.next();
			if (s.getRole() != null && code.equals(s.getRole().getCode())) {
				// ���ж��û��Ƿ�ӵ�д˽�ɫ
				flag = true;
				break;
			}
		}

		return flag;
	}

 
	public void deleteRoleUsers(SysDeptRole deptRole, long[] userIds) {
	 
		
	}

 
	public int getSysUserCountByQueryCriteria(SysUserQuery query) {
		 
		return 0;
	}

	 
	public List<SysUser> getSysUserList(long deptId) {
		 
		return null;
	}

 
	public List<SysRole> getUserRoles(List<String> actorIds) {
		 
		return null;
	}

	 
	public PageResult getSysUserList(long deptId, int pageNo, int pageSize) {
		 
		return null;
	}

	 
	public PageResult getSysUserList(long deptId, String fullName, int pageNo,
			int pageSize) {
		 
		return null;
	}

	 
	public PageResult getSysUserList(long deptId, String userName,
			String account, int pageNo, int pageSize) {
		 
		return null;
	}

	 
	public List<SysUser> getSysUsersByAppId(Long appId) {
		 
		return null;
	}

 
	public List<SysUser> getSysUsersByRoleCode(String roleCode) {
	 
		return null;
	}

 
	public List<SysUser> getSysUsersByQueryCriteria(int start, int pageSize,
			SysUserQuery query) {
		 
		return null;
	}

	 

}