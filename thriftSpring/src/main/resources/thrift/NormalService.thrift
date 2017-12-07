namespace  java com.chelsea.thriftSpring.thrift

service NormalService {
	string getResponse(1:string serviceName, 2:string methodName, 3:string params);
}