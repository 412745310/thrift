package com.chelsea.thriftSpring.thrift;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class NormalServiceClient {
	private static Logger LOGGER = LoggerFactory.getLogger(NormalServiceClient.class);

	@Autowired
	private ThriftClientProxyFactory thriftRpcClient;

	/**
	 * 请求服务端，获取响应结果信息.
	 * 
	 * @param serviceName
	 *            服务名称
	 * @param methodName
	 *            方法名称
	 * @param params
	 *            参数，为json格式
	 * @return 结果为json格式
	 * @throws Exception
	 */
	public String getResponse(String serviceName, String methodName, String params) {
		try {
			NormalService.Iface normalServiceClient = (NormalService.Iface) this.thriftRpcClient.getObject();
			return normalServiceClient.getResponse(serviceName, methodName, params);
		} catch (Exception e) {
			LOGGER.error("请求服务端，获取响应结果信息异常, server:{}, method:{}, params:{}", serviceName, methodName, params);
			return null;
		}
	}

	/**
	 * 请求服务端，获取响应结果信息.
	 * 
	 * @param serviceName
	 *            服务名称
	 * @param methodName
	 *            方法名称
	 * @param params
	 *            参数，为json格式
	 * @return 结果为json格式
	 * @throws TException
	 */
	public Object getResponseObject(String serviceName, String methodName, String params, Class<?> clazzType)
			throws TException {
		NormalService.Iface normalServiceClient = (NormalService.Iface) this.thriftRpcClient;
		String res = normalServiceClient.getResponse(serviceName, methodName, params);
		if (StringUtils.isBlank(res)) {
			return null;
		}

		try {
			return JSON.parseObject(res, clazzType);
		} catch (Exception e) {
			LOGGER.error("请求服务端，获取响应结果信息异常, server:{}, method:{}, params:{}", serviceName, methodName, params);
			return null;
		}
	}
}
