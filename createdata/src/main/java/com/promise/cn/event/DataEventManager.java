/*@文件名: DateEventManager.java  @创建人: 邢健   @创建日期: 2011-10-21 下午01:21:30*/
package com.promise.cn.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**   
 * @类名: DateEventManager.java 
 * @包名: com.promise.cn.event 
 * @描述: DateEventManager
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-21 下午01:21:30 
 * @版本 V1.0   
 */
@SuppressWarnings("all")
public class DataEventManager {

	private Collection listeners;
	
	/**
	 * 增加事件
	 * @param listener
	 */
	public void addDataEventListener(DataEventListener listener){
		if(listeners == null){
			listeners = new HashSet();
		}
		listeners.add(listener);
	}
	
	/**
	 * 移除事件
	 * @param listener
	 */
	public void removeDataEventListener(DataEventListener listener) {
		if(listeners == null)
		return;
		listeners.remove(listener);
	}

	/**
	 * 触发事件
	 * @param data
	 */
	public void fireDataEvent(Object data) {
		if(listeners == null)
			return;
		DataEvent event = new DataEvent(this, data);
		notifyListeners(event);
	}

	/**
	 * 通知所有的DataEventListener
	 * @param event
	 */
	private void notifyListeners(DataEvent event) {
		Iterator iter = listeners.iterator();
		while (iter.hasNext()) {
			DataEventListener listener = (DataEventListener)iter.next();
			listener.dataEvent(event);
		}
	}

}
