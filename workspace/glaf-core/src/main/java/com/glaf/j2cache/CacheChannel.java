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

package com.glaf.j2cache;

import java.io.InputStream;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.config.SystemProperties;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.SerializationUtils;

/**
 * ����ಥͨ��
 * 
 * @author oschina.net
 */
public class CacheChannel extends ReceiverAdapter implements
		CacheExpiredListener {

	private final static Logger log = LoggerFactory
			.getLogger(CacheChannel.class);

	private final static String CONFIG_XML = "/conf/j2cache/network.xml";

	private final static byte OPT_DELETE_KEY = 0x01;

	public final static byte LEVEL_1 = 1;

	public final static byte LEVEL_2 = 2;

	private String name;
	private JChannel channel;

	private static volatile CacheChannel instance;

	/**
	 * ��������
	 * 
	 * @return
	 */
	public final static CacheChannel getInstance() {
		if (instance == null) {
			synchronized (CacheChannel.class) {
				if (instance == null) {
					instance = new CacheChannel("default");
				}
			}
		}
		return instance;
	}

	/**
	 * ��ʼ������ͨ��������
	 * 
	 * @param name
	 * @throws CacheException
	 */
	private CacheChannel(String name) throws CacheException {
		this.name = name;
		InputStream configStream = null;
		try {
			CacheManager.initCacheProvider(this);

			long ct = System.currentTimeMillis();

			configStream = FileUtils.getInputStream(SystemProperties
					.getConfigRootPath() + CONFIG_XML);

			channel = new JChannel(configStream);
			channel.setReceiver(this);
			channel.connect(this.name);

			log.info("Connected to channel:" + this.name + ", time "
					+ (System.currentTimeMillis() - ct) + " ms.");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CacheException(ex);
		} finally {
			IOUtils.closeStream(configStream);
		}
	}

	/**
	 * ��ȡ�����е�����
	 * 
	 * @param <T>
	 * @param level
	 * @param resultClass
	 * @param region
	 * @param key
	 * @return
	 */
	public CacheObject get(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.get(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.get(LEVEL_2, region, key));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
					CacheManager.set(LEVEL_1, region, key, obj.getValue());
				}
			} else{
				obj.setLevel(LEVEL_1);
			}
		}
		return obj;
	}

	/**
	 * д�뻺��
	 * 
	 * @param level
	 * @param region
	 * @param key
	 * @param value
	 */
	public void set(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null)
				evict(region, key);
			else {
				// �ּ������
				// Object obj1 = CacheManager.get(LEVEL_1, region, key);
				// Object obj2 = CacheManager.get(LEVEL_2, region, key);
				// 1. L1 �� L2 ��û��
				// 2. L1 �� L2 û�У�������������ڣ�������д L2 ��ʱ��ʧ��
				// 3. L1 û�У�L2 ��
				// 4. L1 �� L2 ����
				_sendEvictCmd(region, key);// ���ԭ�е�һ�����������
				CacheManager.set(LEVEL_1, region, key, value);
				CacheManager.set(LEVEL_2, region, key, value);
			}
		}
		// log.info("write data to cache region="+region+",key="+key+",value="+value);
	}

	/**
	 * ɾ������
	 * 
	 * @param region
	 * @param key
	 */
	public void evict(String region, Object key) {
		CacheManager.evict(LEVEL_1, region, key); // ɾ��һ������
		CacheManager.evict(LEVEL_2, region, key); // ɾ����������
		_sendEvictCmd(region, key); // ���͹㲥
	}

	/**
	 * ����ɾ������
	 * 
	 * @param region
	 * @param keys
	 */
	@SuppressWarnings({ "rawtypes" })
	public void batchEvict(String region, List keys) {
		CacheManager.batchEvict(LEVEL_1, region, keys);
		CacheManager.batchEvict(LEVEL_2, region, keys);
		_sendEvictCmd(region, keys);
	}

	/**
	 * Clear the cache
	 */
	public void clear(String region) throws CacheException {
		CacheManager.clear(LEVEL_1, region);
		CacheManager.clear(LEVEL_2, region);
	}

	@SuppressWarnings("rawtypes")
	public List keys(String region) throws CacheException {
		return CacheManager.keys(LEVEL_1, region);
	}

	/**
	 * Ϊ�˱�֤ÿ���ڵ㻺���һ�£���ĳ�����������Ϊ��ʱ�����ʱ��Ӧ��֪ͨȺ��������Ա
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void notifyElementExpired(String region, Object key) {

		log.debug("Cache data expired, region=" + region + ",key=" + key);

		// ɾ����������
		if (key instanceof List) {
			CacheManager.batchEvict(LEVEL_2, region, (List) key);
		} else {
			CacheManager.evict(LEVEL_2, region, key);
		}

		// ���͹㲥
		_sendEvictCmd(region, key);
	}

	/**
	 * �����������Ĺ㲥����
	 * 
	 * @param region
	 * @param key
	 */
	private void _sendEvictCmd(String region, Object key) {
		// ���͹㲥
		Command cmd = new Command(OPT_DELETE_KEY, region, key);
		try {
			Message msg = new Message(null, null, cmd.toBuffers());
			channel.send(msg);
		} catch (Exception e) {
			log.error(
					"Unable to delete cache,region=" + region + ",key=" + key,
					e);
		}
	}

	/**
	 * ɾ��һ������ļ���Ӧ����
	 * 
	 * @param region
	 * @param key
	 */
	@SuppressWarnings("rawtypes")
	protected void onDeleteCacheKey(String region, Object key) {
		if (key instanceof List)
			CacheManager.batchEvict(LEVEL_1, region, (List) key);
		else
			CacheManager.evict(LEVEL_1, region, key);
		log.debug("Received cache evict message, region=" + region + ",key="
				+ key);
	}

	/**
	 * ��Ϣ����
	 */
	@Override
	public void receive(Message msg) {
		// ��Ч��Ϣ
		byte[] buffers = msg.getRawBuffer();
		if (buffers.length < 1) {
			log.warn("Message is empty.");
			return;
		}

		// ���������͸��Լ�����Ϣ
		if (msg.getSrc().equals(channel.getAddress())) {
			return;
		}

		try {
			Command cmd = Command.parse(buffers);

			if (cmd == null) {
				return;
			}

			switch (cmd.operator) {
			case OPT_DELETE_KEY:
				onDeleteCacheKey(cmd.region, cmd.key);
				break;
			default:
				log.warn("Unknown message type = " + cmd.operator);
			}
		} catch (Exception e) {
			log.error("Unable to handle received msg", e);
		}
	}

	/**
	 * ���г�Ա�仯ʱ
	 */
	public void viewAccepted(View view) {
		StringBuffer sb = new StringBuffer("Group Members Changed, LIST: ");
		List<Address> addrs = view.getMembers();
		for (int i = 0; i < addrs.size(); i++) {
			if (i > 0)
				sb.append(',');
			sb.append(addrs.get(i).toString());
		}
		log.info(sb.toString());
	}

	/**
	 * �رյ�ͨ��������
	 */
	public void close() {
		channel.close();
	}

	/**
	 * ������Ϣ��װ ��ʽ�� ��1���ֽ�Ϊ������룬����1 [OPT] ��2��3���ֽ�Ϊregion���ȣ�����2 [R_LEN] ��4��N Ϊ
	 * region ֵ������Ϊ [R_LEN] ��N+1��N+2 Ϊ key ���ȣ�����2 [K_LEN] ��N+3��MΪ keyֵ������Ϊ
	 * [K_LEN]
	 */
	private static class Command {

		private byte operator;
		private String region;
		private Object key;

		public Command(byte o, String r, Object k) {
			this.operator = o;
			this.region = r;
			this.key = k;
		}

		public byte[] toBuffers() {
			byte[] keyBuffers = SerializationUtils.serialize(key);
			int r_len = region.getBytes().length;
			int k_len = keyBuffers.length;

			byte[] buffers = new byte[5 + r_len + k_len];
			int idx = 0;
			buffers[idx] = operator;
			buffers[++idx] = (byte) (r_len >> 8);
			buffers[++idx] = (byte) (r_len & 0xFF);
			System.arraycopy(region.getBytes(), 0, buffers, ++idx, r_len);
			idx += r_len;
			buffers[idx++] = (byte) (k_len >> 8);
			buffers[idx++] = (byte) (k_len & 0xFF);
			System.arraycopy(keyBuffers, 0, buffers, idx, k_len);
			return buffers;
		}

		public static Command parse(byte[] buffers) {
			Command cmd = null;
			try {
				int idx = 0;
				byte opt = buffers[idx];
				int r_len = buffers[++idx] << 8;
				r_len += Math.abs(buffers[++idx]);
				if (r_len > 0) {
					String region = new String(buffers, ++idx, r_len);
					idx += r_len;
					int k_len = buffers[idx++] << 8;
					k_len += Math.abs(buffers[idx++]);
					if (k_len > 0) {
						byte[] keyBuffers = new byte[k_len];
						System.arraycopy(buffers, idx, keyBuffers, 0, k_len);
						Object key = SerializationUtils.unserialize(keyBuffers);
						cmd = new Command(opt, region, key);
					}
				}
			} catch (Exception e) {
				log.error("Unabled to parse received command.", e);
			}
			return cmd;
		}
	}

	public static void main(String[] args) {
		Command cmd = new Command(OPT_DELETE_KEY, "users", "ld");
		byte[] bufs = cmd.toBuffers();
		for (byte b : bufs) {
			System.out.printf("[%s]", Integer.toHexString(b));
		}
		System.out.println();
		Command cmd2 = Command.parse(bufs);
		System.out.printf("%d:%s:%s\n", cmd2.operator, cmd2.region, cmd2.key);
	}

}