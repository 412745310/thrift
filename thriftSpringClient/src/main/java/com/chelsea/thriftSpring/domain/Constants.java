package com.chelsea.thriftSpring.domain;

public interface Constants {
	final static Integer PAGE_SIZE = 10;
	// 冒号分隔符
	final static String SEPARATE_COLON = ";";
	
	// 逗号分隔符
	final static String SEPARATE_COMMA = ",";
	
	final static Integer SUCCESS_CODE = 200;
	
	final static String SUCCESS_DESC = "处理成功";
	
	final static Integer FAIL_CODE = 400;
	
	final static String EXCLUSIVE_GATEWAY = "ExclusiveGateway";
	
	final static String FLOW_TYPE_OUT = "out";

	//用户RPC调用返回结果封装：结果状态名字
	final static String STATUS_NAME = "status";
	//用户RPC调用返回结果封装：当前页名字
	final static String PAGE_NUM_NAME = "pageNum";
	//用户RPC调用返回结果封装：页面大小名字
	final static String PAGE_SIZE_NAME = "pageSize";
	//用户RPC调用返回结果封装：数据
	final static String DATA_NAME = "data";
	//用户RPC调用返回结果封装：数据总量
	final static String TOTAL_COUNT_NAME = "totalCount";
	//用户RPC调用返回结果编码
	final static String CODE = "code";
	//用户RPC调用返回结果封装：描述
	final static String DESCRIBE = "describe";
	//流程定义的key
	final static String PROCESS_KEY = "processKey";
	//任务节点定义的key
	final static String TASK_KEY = "TASKKEY";
	//任务节点定义的操作类型
	final static String TYPE = "TYPE";

	// 统计周期从星期一开始
	final static int WEEK_MONDAY = 0;
	// 统计周期从星期二开始
	final static int WEEK_SUNDAY = 1;



	// 周报附件单元格高度
	final static short EXCEL_CELL_HEIGHT = 270;
	// 周报附件单元格宽度
	final static int EXCLE_CELL_WIDTH = 5000;
	// 周报附件字体大小
	final static short EXCEL_FONT_HEIGHT = 220;
	
	//超过规定时间未处理任务流转KEY
	final static String PROCESS_TIMEOUNT_DAY="event_sub_company_process_timeout";
	//工单流转组织变量KEY
	final static String ORGANIZATION_ID="organizationId";
}
