package com.chelsea.thriftSpring.thrift;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.chelsea.thriftSpring.domain.Constants;
import com.chelsea.thriftSpring.domain.ConstantsCode;
import com.chelsea.thriftSpring.thrift.NormalService;

@Component
@SuppressWarnings("rawtypes")
public class NormalServiceHandler implements NormalService.Iface {
	private static Logger LOGGER = LoggerFactory.getLogger(NormalServiceHandler.class);
	private Map<String, Object> serviceList;
	
	public void setServiceList(Map<String, Object> serviceList) {
		this.serviceList = serviceList;
	}
	
	public Map<String, Object> getServiceList() {
		return serviceList;
	}
	
	/**
	 * 获取响应数据.
	 */
	@SuppressWarnings("unchecked")
	public String getResponse(String serviceName, String methodName, String params) throws TException {
		Method mt = null;
		
		try {
			LOGGER.info("recv rpc client msg, service: {}, method: {}, params: {}", serviceName, methodName, params);
			Class clazz = serviceList.get(serviceName).getClass();
			mt = clazz.getMethod(methodName, new Class[] { String.class });
			Object[] arguments = new Object[] { params};
			String result =  (String)mt.invoke(serviceList.get(serviceName), arguments);
			LOGGER.info("response:"+  result);
			return result;
		}catch (Exception e) {
			LOGGER.error("调用rpc异常, code:" + ConstantsCode.EXCPTIONE_CODE_100, e);
			return this.getExceptionResult(ConstantsCode.EXCPTIONE_CODE_100, e.getMessage());
		} 
	}
	
	/**
	 * 获取异常信息
	 * @param code 异常码
	 * @param desc 描述
	 * @return 封装异常结果信息
	 */
	public String getExceptionResult(int code, String desc) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constants.CODE, code);
		result.put(Constants.DESCRIBE, desc);
		return JSONObject.toJSONString(result);
	}
}
