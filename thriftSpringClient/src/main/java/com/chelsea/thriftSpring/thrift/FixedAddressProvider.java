package com.chelsea.thriftSpring.thrift;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

/**
 * 固定地址列表提供者
 * @author haow
 *
 */
public class FixedAddressProvider implements ThriftClientServerAddressProvider {

	/**
	 * 存放地址列表
	 */
	private final List<InetSocketAddress> container;

	/**
	 * 地址队列
	 */
	private final Queue<InetSocketAddress> queue;

	/**
	 * 构造方法.
	 */
	public FixedAddressProvider() {
		this.container = new CopyOnWriteArrayList<InetSocketAddress>();
		this.queue = new LinkedList<InetSocketAddress>();
	}

	/**
	 * 构造方法.
	 * @param serverAddress 服务端地址列表
	 */
	public FixedAddressProvider(String serverAddress) {
		this.container = new CopyOnWriteArrayList<InetSocketAddress>();
		this.queue = new LinkedList<InetSocketAddress>();
		String[] hostnames = serverAddress.split(";");// "ip:port:priority;ip:port"
		for (String hostname : hostnames) {
			if(StringUtils.isBlank(hostname)){
				continue;
			}
			String[] address = hostname.split(":");
			InetSocketAddress sa = new InetSocketAddress(address[0], Integer.valueOf(address[1]));
			// 根据权重值
			Integer priority = 1;// 权重值
			if (address.length == 3) {
				priority = Integer.valueOf(address[2]);
			}
			// 权重越高,list中占有的数据条目越多
			for (int i = 0; i < priority; i++) {
				this.container.add(sa);
			}
		}
		// 随机
		Collections.shuffle(this.container);
		this.queue.addAll(this.container);
	}

	@Override
	public List<InetSocketAddress> getAll() {
		return Collections.unmodifiableList(this.container);
	}

	@Override
	public synchronized InetSocketAddress selector() {
		if (this.queue.isEmpty()) {
			this.queue.addAll(this.container);
		}

		return this.queue.poll();
	}

	@Override
	public void close() {
		this.queue.clear();
		this.container.clear();
	}
}
