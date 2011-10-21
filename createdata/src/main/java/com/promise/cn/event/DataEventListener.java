/*@文件名: DataEventListener.java  @创建人: 邢健   @创建日期: 2011-10-21 下午01:16:14*/
package com.promise.cn.event;

import java.util.EventListener;

/**   
 * @类名: DataEventListener.java 
 * @包名: com.promise.cn.event 
 * @描述: DataEventListener 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-21 下午01:16:14 
 * @版本 V1.0   
 */
public interface DataEventListener extends EventListener {

	public void dataEvent(DataEvent dataEvent);
}
