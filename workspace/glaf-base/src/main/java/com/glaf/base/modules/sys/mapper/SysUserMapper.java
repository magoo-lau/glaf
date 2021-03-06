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

package com.glaf.base.modules.sys.mapper;

import java.util.*;
import org.springframework.stereotype.Component;
import com.glaf.base.modules.sys.model.*;
import com.glaf.base.modules.sys.query.*;

@Component
public interface SysUserMapper {

	void deleteSysUserById(Long id);

	void deleteSysUsers(SysUserQuery query);

	List<SysUser> getAuthorizedUsers(SysUserQuery query);

	List<SysUser> getAuthorizedUsersByUserId(Long authorizeFrom);

	int getCountAuthorizedUsers(SysUserQuery query);

	int getCountDeptUsers(SysUserQuery query);

	List<SysUser> getDeptUsers(SysUserQuery query);

	List<SysUser> getSysDeptRoleUsers(SysDeptRoleQuery query);
	
	List<SysUser> getSysUsersByRoleId(Long roleId);
	
	List<UserRole> getRoleUserViews(UserRoleQuery query);

	SysUser getSysUserByAccount(String account);

	String getSysUserPasswordByAccount(String account);

	SysUser getSysUserByLowerCaseAccount(String account);

	SysUser getSysUserById(Long id);

	SysUser getSysUserByMail(String mail);

	SysUser getSysUserByMobile(String mobile);

	int getSysUserCount(SysUserQuery query);

	List<SysUser> getSysUsers(SysUserQuery query);

	List<SysUser> getSysUsersByAppId(Long appId);

	void insertSysUser(SysUser model);

	void updateSysUser(SysUser model);

}
