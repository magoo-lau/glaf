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
package com.glaf.oa.borrow.service;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;

import com.glaf.oa.borrow.model.*;
import com.glaf.oa.borrow.query.*;

@Transactional(readOnly = true)
public interface BorrowService {

	/**
	 * ��������ɾ����¼
	 * 
	 * @return
	 */
	@Transactional
	void deleteById(Long id);

	/**
	 * ��������ɾ��������¼
	 * 
	 * @return
	 */
	@Transactional
	void deleteByIds(List<Long> borrowids);

	/**
	 * ���ݲ�ѯ������ȡ��¼�б�
	 * 
	 * @return
	 */
	List<Borrow> list(BorrowQuery query);

	/**
	 * ���ݲ�ѯ������ȡ��¼����
	 * 
	 * @return
	 */
	int getBorrowCountByQueryCriteria(BorrowQuery query);

	/**
	 * ���ݲ�ѯ������ȡһҳ������
	 * 
	 * @return
	 */
	List<Borrow> getBorrowsByQueryCriteria(int start, int pageSize,
			BorrowQuery query);

	/**
	 * ����������ȡһ����¼
	 * 
	 * @return
	 */
	Borrow getBorrow(Long id);

	/**
	 * ����һ����¼
	 * 
	 * @return
	 */
	@Transactional
	void save(Borrow borrow);

	List<Borrow> getBorrowsApproveByQueryCriteria(int start, int limit,
			BorrowQuery query);

	int getBorrowApproveCountByQueryCriteria(BorrowQuery query);

}