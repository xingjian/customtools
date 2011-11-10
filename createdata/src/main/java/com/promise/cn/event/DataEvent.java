/*@文件名: DataEvent.java  @创建人: 邢健   @创建日期: 2011-10-21 下午12:03:03*/
package com.promise.cn.event;

import java.util.EventObject;

/**   
 * @类名: DataEvent.java 
 * @包名: com.promise.cn.event 
 * @描述: 带事件传递数据
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-21 下午12:03:03 
 * @版本 V1.0   
 */
@SuppressWarnings("all")
public class DataEvent extends EventObject {

	public Object data;
	public DataEvent(Object source,Object data) {
		super(source);
		this.data = data;
	}

}
