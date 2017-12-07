package com.chelsea.thriftSpring.thrift;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.chelsea.thriftSpring.domain.Constants;
import com.chelsea.thriftSpring.zookeeper.ZookeeperUtil;

/**
 * 客户端代理类
 * 
 * @author haow
 *
 */
@SuppressWarnings("rawtypes")
public class ThriftClientProxyFactory implements FactoryBean {

	private Logger LOGGER = LoggerFactory.getLogger(ThriftClientProxyFactory.class);
	
	@Autowired
	private ZookeeperUtil zookeeperUtil;

	/**
	 * 服务名称
	 */
	private String normalService;

	/**
	 * 服务端地址列表，多个分号隔开.
	 */
	private String serverAddress;

	/**
	 * 服务端地址提供者
	 */
	private ThriftClientServerAddressProvider addressProvider;

	/**
	 * 客户端代理对象.
	 */
	private Object proxyClient;

	/**
	 * 对象类型.
	 */
	private Class objectClass;

	/**
	 * 存放存活的对象.
	 */
	private LinkedBlockingQueue<TServiceClient> linkedBlockingQueue;

	/**
	 * 已经存活的对象列表
	 */
	private Set<TServiceClient> lstTServiceClient;

	/**
	 * 客户端工厂类
	 */
	private ThriftClientFactory thriftClientFactory;

	/**
	 * 最大的激活数量
	 */
	private Integer maxActive;
	
	
	/**
	 * thrift服务器在zookeeper里的配置路径
	 */
	private String thriftServerPath;
	
	public void setNormalService(String normalService) {
		this.normalService = normalService;
	}

	public String getNormalService() {
		return normalService;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}
	
	public String getThriftServerPath() {
		return thriftServerPath;
	}

	public void setThriftServerPath(String thriftServerPath) {
		this.thriftServerPath = thriftServerPath;
	}

	public void setServerAddress() throws Exception{
		List<String> children = zookeeperUtil.getChildren(this.thriftServerPath, true);
		this.serverAddress = StringUtils.join(children.toArray(), Constants.SEPARATE_COLON);
	}

	/**
	 * 初始化方法
	 * 
	 * @throws Exception
	 *             异常
	 */
	public void init() throws Exception {
		zookeeperUtil.register(this, new String[]{"setServerAddress", "initProxyClient"});
		setServerAddress();
		initProxyClient();
	}

	@SuppressWarnings("unchecked")
	public void initProxyClient() throws Exception {
		this.lstTServiceClient = Collections.synchronizedSet(new HashSet<TServiceClient>());
		this.linkedBlockingQueue = new LinkedBlockingQueue<>();
		if (this.serverAddress != null) {
			this.addressProvider = new FixedAddressProvider(this.serverAddress);
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// 加载Iface接口
		this.objectClass = classLoader.loadClass(this.normalService + "$Iface");
		// 加载Client.Factory类
		Class<TServiceClientFactory<TServiceClient>> tscf = (Class<TServiceClientFactory<TServiceClient>>) classLoader
				.loadClass(this.normalService + "$Client$Factory");
		TServiceClientFactory<TServiceClient> clientFactory = tscf.newInstance();
		this.thriftClientFactory = new ThriftClientFactory(this.addressProvider, clientFactory);
		this.proxyClient = Proxy.newProxyInstance(classLoader, new Class[] { this.objectClass },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						LOGGER.debug("链接服务端的当前空闲数：" + linkedBlockingQueue.size() + ", 总数=" + lstTServiceClient.size());
						boolean isException = false;
						TServiceClient client = null;
						try {
							client = getClient();
							if (!thriftClientFactory.validate(client)) {
								isException = true;
								destroy(client);
								return null;
							}

							return method.invoke(client, args);
						} catch (Throwable e) {
							isException = true;
							LOGGER.error("请求服务异常", e);
							return null;
						} finally {
							if (isException && null != client) {
								destroy(client);
							} else if (null != client) {
								recycle(client);
							}
						}
					}
				});
	}

	/**
	 * 获取服务的代理对象.
	 */
	public Object getObject() throws Exception {
		return proxyClient;
	}

	/**
	 * 获取serviceClient
	 * 
	 * @return serviceClient
	 */
	public synchronized TServiceClient getClient() {
		try {
			if (this.maxActive <= this.lstTServiceClient.size()) {
				try {
					TServiceClient tc = null;
					while (!this.linkedBlockingQueue.isEmpty()) {
						tc = this.linkedBlockingQueue.poll(3000, TimeUnit.MILLISECONDS);
						if (thriftClientFactory.validate(tc)) {
							return tc;
						} else {
							this.destroy(tc);
							tc = null;
							continue;
						}
					}
					
					// 重新创建
					if (null == tc) {
						TServiceClient client = this.thriftClientFactory.makeObject();
						if (null == client) {
							return null;
						}
						this.lstTServiceClient.add(client);
						return client;
					}
				} catch (Exception e) {
					LOGGER.error("获取空闲队列异常,超时时间为3秒", e);
					return null;
				}
			} else if (this.lstTServiceClient.size() < this.maxActive) {
				TServiceClient client = this.thriftClientFactory.makeObject();
				if (null == client) {
					return null;
				}
				this.lstTServiceClient.add(client);
				return client;
			}
		} catch (Exception e) {
			LOGGER.error("获取链接异常", e);
		}
		return null;
	}

	/**
	 * 回收对象.
	 * 
	 * @param client
	 *            客户端对象.
	 */
	public void recycle(TServiceClient client) {
		if (null != client && this.thriftClientFactory.validate(client)) {
			this.linkedBlockingQueue.add(client);
		}
	}

	/**
	 * 销毁客户端.
	 * 
	 * @param client
	 *            客户端对象.
	 */
	public void destroy(TServiceClient client) {
		this.thriftClientFactory.destroy(client);
		this.lstTServiceClient.remove(client);
	}

	/**
	 * 关闭资源
	 */
	public void close() {
		if (this.addressProvider != null) {
			this.addressProvider.close();
		}
	}

	@Override
	public Class getObjectType() {
		return this.objectClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
