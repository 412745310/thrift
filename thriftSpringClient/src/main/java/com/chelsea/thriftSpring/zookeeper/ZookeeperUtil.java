package com.chelsea.thriftSpring.zookeeper;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperUtil {

	private static ZooKeeper zk = null;
	
	private String zooKeeperIpAddress;
	
	public String getZooKeeperIpAddress() {
		return zooKeeperIpAddress;
	}

	public void setZooKeeperIpAddress(String zooKeeperIpAddress) {
		this.zooKeeperIpAddress = zooKeeperIpAddress;
	}

	public void init() throws Exception {
		zk = new ZooKeeper(this.zooKeeperIpAddress, 10000, null);
	}
	
	public void register(final Object obj, final String... methodName) throws Exception{
		Watcher watcher = new Watcher() {
			public void process(WatchedEvent event) {
				try {
					if (event.getType() == EventType.NodeChildrenChanged) {
						for(int i=0;i<methodName.length;i++){
							Method method = obj.getClass().getMethod(methodName[i]);
							method.invoke(obj);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		zk.register(watcher);
	}
	
	public List<String> getChildren(String path, boolean watch) throws Exception{
		List<String> children = zk.getChildren(path, watch);
		return children;
	}

}
