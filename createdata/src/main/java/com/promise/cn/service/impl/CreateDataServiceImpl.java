/*@文件名: CreateDataServiceImpl.java  @创建人: 邢健   @创建日期: 2011-10-20 下午01:23:12*/
package com.promise.cn.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.promise.cn.service.CreateDataService;
import com.promise.cn.util.CreateDataUtil;
import com.promise.cn.vo.DataConnectConfigVO;
import com.promise.cn.vo.PolicyVO;
import com.promise.cn.vo.TableConfigVO;

/**   
 * @类名: CreateDataServiceImpl.java 
 * @包名: com.promise.cn.service.impl 
 * @描述: 构造数据服务对象接口实现类 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-10-20 下午01:23:12 
 * @版本 V1.0   
 */
@SuppressWarnings("all")
public class CreateDataServiceImpl implements CreateDataService {

	/**
	 * 保存数据配置对象到data_connect.xml
	 */
	@Override
	public boolean saveDataConnectVO(DataConnectConfigVO dccVO) {
		try {
			String filePath = CreateDataServiceImpl.class.getResource("/data_connect.xml").getPath();
			XMLWriter writer = new XMLWriter(new FileWriter(filePath));
			Document document = null;
			document = DocumentHelper.parseText(dccVO.toString());
			writer.write(document);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}

	/**
	 * 验证数据配置对象正确性
	 */
	@Override
	public boolean checkDataConnectVO(DataConnectConfigVO dccVO) {
		Connection connect = null;
		try {
			Class.forName(dccVO.getDriverClassName());
			connect = DriverManager.getConnection(dccVO.getUrl(),dccVO.getUserName(),dccVO.getPassword());
			if(connect!=null){
				if(!connect.isClosed()){
					connect.close();
				}
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除数据配置对象
	 */
	@Override
	public boolean deleteDataConnectVO(DataConnectConfigVO dccVO) {
		return false;
	}

	/**
	 * 编辑数据配置对象
	 */
	@Override
	public boolean editDataConnectVO(DataConnectConfigVO dccVO) {
		return false;
	}

	@Override
	public boolean saveDataConnectVOList(List<DataConnectConfigVO> dccVOList) {
		StringBuffer result = new StringBuffer();
		result.append("<datasources>"+"\n");
		for(int i=0;i<dccVOList.size();i++){
			result.append(dccVOList.get(i).toString());
		}
		result.append("</datasources>");
		String filePath = CreateDataServiceImpl.class.getResource("/data_connect.xml").getPath();
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(filePath));
			Document document = null;
			document = DocumentHelper.parseText(result.toString());
			writer.write(document);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 解析data_connnect.xml,转换成DataConnectConfigVO集合
	 */
	@SuppressWarnings({"all"})
	@Override
	public List<DataConnectConfigVO> getAllDataConnectConfigVO() {
		List<DataConnectConfigVO> dccList = new ArrayList<DataConnectConfigVO>();
		 SAXReader reader = new SAXReader();
		 String filePath = CreateDataServiceImpl.class.getResource("/data_connect.xml").getPath();
         try {
			Document  document = reader.read(new File(filePath));
			Element rootElm = document.getRootElement();
			List nodes = rootElm.elements("datasource");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				   Element elm = (Element) it.next();
				   String name = elm.elementText("name");
				   String userName = elm.elementText("userName");
				   String password = elm.elementText("password");
				   String driverClassName = elm.elementText("driverClassName");
				   String url = elm.elementText("url");
				   DataConnectConfigVO dccTemp = new DataConnectConfigVO();
				   dccTemp.setName(name);
				   dccTemp.setUrl(url);
				   dccTemp.setUserName(userName);
				   dccTemp.setPassword(password);
				   dccTemp.setDriverClassName(driverClassName);
				   dccList.add(dccTemp);
				}

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return dccList;
	}
	
	/**
	 * 通过
	 */
	@Override
	public boolean createInsertSqlByList(List<TableConfigVO> list, String path,String tableName,int count) {
		boolean retBoolean = true;
		List<PolicyVO> listPolicy = getAllPolicyVO();
		File file  = null;
		FileOutputStream fos = null;
		FileChannel outputChannel = null;
		if(null!=path&&!path.equals("")){
			file = new File(path);
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}   
			outputChannel = fos.getChannel();   
		}
		/**insert into tableName values('a','b','c')**/
		for(int i=0;i<count;i++){
			String insertSql = "insert into "+tableName+" values(";
			for(Iterator<TableConfigVO> iterator = list.iterator();iterator.hasNext();){
				TableConfigVO tcvTemp = iterator.next();
				PolicyVO pTemp= getPolicyByName(tcvTemp.getPolicyName(),listPolicy);
				if(pTemp.getType().equals(CreateDataUtil.RANDOMDOUBLE_AB)){
					insertSql = insertSql+"'"+CreateDataUtil.getRandomDouble(Double.parseDouble(pTemp.getInitValue()), Double.parseDouble(pTemp.getEndValue()), Integer.parseInt(pTemp.getNumberDecimal()))+"',";
				}else if(pTemp.getType().equals(CreateDataUtil.RANDOMINT_AB)){
					insertSql = insertSql+"'"+CreateDataUtil.getRandomInt(Integer.parseInt(pTemp.getInitValue()), Integer.parseInt(pTemp.getEndValue()))+"',";
				}else if(pTemp.getType().equals(CreateDataUtil.RANDOMSTRING)){
					insertSql = insertSql+"'"+CreateDataUtil.getRandomString(pTemp.getValue(), Integer.parseInt(pTemp.getStrLength()), pTemp.getSiteStr())+"',";
				}else if(pTemp.getType().equals(CreateDataUtil.CONSTANTVALUE)){
					String value = pTemp.getValue();
					String[] values = value.trim().split(",");
					String tempValue = null;
					if(values.length>1){
						tempValue = values[CreateDataUtil.getRemainder(i, values.length)];
					}else{
						tempValue = value;
					}
					insertSql = insertSql+"'"+tempValue+"',";
				}else if(pTemp.getType().equals(CreateDataUtil.DESCEND_INT)){
					int temp = 0;
					if(pTemp.isDesc()){
						temp = Integer.parseInt(pTemp.getValue())+i*(Integer.parseInt(pTemp.getStepValue()));
					}else{
						temp = Integer.parseInt(pTemp.getValue())-i*(Integer.parseInt(pTemp.getStepValue()));
					}
					insertSql = insertSql+"'"+temp+"',";
				}else if(pTemp.getType().equals(CreateDataUtil.DESCEND_DOUBLE)){
					double temp = 0;
					if(pTemp.isDesc()){
						temp = Double.parseDouble(pTemp.getValue())+i*(Double.parseDouble(pTemp.getStepValue()));
					}else{
						temp = Double.parseDouble(pTemp.getValue())-i*(Double.parseDouble(pTemp.getStepValue()));
					}
					DecimalFormat dcmFmt = new DecimalFormat(CreateDataUtil.getDecimalPointFormat(Integer.parseInt(pTemp.getNumberDecimal())));
					temp = Double.parseDouble(dcmFmt.format(temp));
					insertSql = insertSql+"'"+temp+"',";
				}else if(pTemp.getType().equals(CreateDataUtil.DESCEND_DATE)){
					String temp = "";
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					try {
						Date startDate = sdf.parse(pTemp.getValue());
						GregorianCalendar gc = new GregorianCalendar();
						gc.setTime(startDate);
						int stepValueDouble = Integer.parseInt(pTemp.getStepValue());
						if(pTemp.isDesc()){
							if(pTemp.getStepValueUnit().equals(CreateDataUtil.DATA_HOUR)){
								gc.add(Calendar.HOUR, i*stepValueDouble);
							}else if(pTemp.getStepValueUnit().equals(CreateDataUtil.DATA_MINUTE)){
								gc.add(Calendar.MINUTE, i*stepValueDouble);
							}else if(pTemp.getStepValueUnit().equals(CreateDataUtil.DATA_SECOND)){
								gc.add(Calendar.SECOND, i*stepValueDouble);
							}
						}else{
							if(pTemp.getStepValueUnit().equals(CreateDataUtil.UNIT_HOUR)){
								gc.add(Calendar.HOUR, -i*stepValueDouble);
							}else if(pTemp.getStepValueUnit().equals(CreateDataUtil.UNIT_MINUTE)){
								gc.add(Calendar.MINUTE, -i*stepValueDouble);
							}else if(pTemp.getStepValueUnit().equals(CreateDataUtil.UNIT_SECOND)){
								gc.add(Calendar.SECOND, -i*stepValueDouble);
							}
						}
						temp = sdf.format(gc.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					insertSql = insertSql+"'"+temp+"',";
				}
			}
			insertSql = insertSql.substring(0, insertSql.length()-1);
			insertSql = insertSql+");"+"\r\n";
			ByteBuffer bb = ByteBuffer.wrap(insertSql.getBytes());   
			try {
				outputChannel.write(bb);
			} catch (IOException e) {
				retBoolean = false;
				e.printStackTrace();
			}
		}
		return retBoolean;
	}

	/**
	 * 批量保存策略
	 */
	@Override
	public boolean savePolicyVOList(List<PolicyVO> policyVOList) {
		StringBuffer result = new StringBuffer();
		result.append("<policys>"+"\n");
		for(int i=0;i<policyVOList.size();i++){
			result.append(policyVOList.get(i).toString());
		}
		result.append("</policys>");
		String filePath = CreateDataServiceImpl.class.getResource("/policy.xml").getPath();
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(filePath));
			Document document = null;
			document = DocumentHelper.parseText(result.toString());
			writer.write(document);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取所有的策略文件
	 */
	@Override
	public List<PolicyVO> getAllPolicyVO() {
		List<PolicyVO> policyList = new ArrayList<PolicyVO>();
		 SAXReader reader = new SAXReader();
		 String filePath = CreateDataServiceImpl.class.getResource("/policy.xml").getPath();
        try {
			Document  document = reader.read(new File(filePath));
			Element rootElm = document.getRootElement();
			List nodes = rootElm.elements("policy");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				   Element elm = (Element) it.next();
				   String name = elm.elementText("name");
				   String type = elm.elementText("type");
				   PolicyVO policyTemp = new PolicyVO();
				   policyTemp.setName(name);
				   policyTemp.setType(type);
				   if(type.equals(CreateDataUtil.RANDOMDOUBLE_AB)){
						String initValue = elm.elementText("initValue");
						String endValue = elm.elementText("endValue");
						String numberDecimal = elm.elementText("numberDecimal");
						policyTemp.setInitValue(initValue);
						policyTemp.setEndValue(endValue);
						policyTemp.setNumberDecimal(numberDecimal);
				   }else if(type.equals(CreateDataUtil.RANDOMINT_AB)){
						String initValue = elm.elementText("initValue");
						String endValue = elm.elementText("endValue");
						policyTemp.setInitValue(initValue);
						policyTemp.setEndValue(endValue);
					}else if(type.equals(CreateDataUtil.RANDOMSTRING)){
						String value = elm.elementText("value");
						String siteStr = elm.elementText("siteStr");
						String strLength = elm.elementText("strLength");
						policyTemp.setValue(value);
						policyTemp.setSiteStr(siteStr);
						policyTemp.setStrLength(strLength);
					}else if(type.equals(CreateDataUtil.CONSTANTVALUE)){
						String value = elm.elementText("value");
						policyTemp.setValue(value);
					}else if(type.equals(CreateDataUtil.DESCEND_INT)){
						String value = elm.elementText("value");
						String stepValue = elm.elementText("stepValue");
						String desc = elm.elementText("desc");
						policyTemp.setValue(value);
						policyTemp.setStepValue(stepValue);
						policyTemp.setDesc(Boolean.parseBoolean(desc));
					}else if(type.equals(CreateDataUtil.DESCEND_DOUBLE)){
						String value = elm.elementText("value");
						String stepValue = elm.elementText("stepValue");
						String desc = elm.elementText("desc");
						String numberDecimal = elm.elementText("numberDecimal");
						policyTemp.setValue(value);
						policyTemp.setStepValue(stepValue);
						policyTemp.setDesc(Boolean.parseBoolean(desc));
						policyTemp.setNumberDecimal(numberDecimal);
					}else if(type.equals(CreateDataUtil.DESCEND_DATE)){
						String value = elm.elementText("value");
						String stepValue = elm.elementText("stepValue");
						String desc = elm.elementText("desc");
						String stepValueUnit = elm.elementText("stepValueUnit");
						policyTemp.setValue(value);
						policyTemp.setStepValue(stepValue);
						policyTemp.setDesc(Boolean.parseBoolean(desc));
						policyTemp.setStepValueUnit(stepValueUnit);
					}
				   policyList.add(policyTemp);
				}

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return policyList;
	}

	/**
	 * 通过name获取policyvo
	 */
	public PolicyVO getPolicyByName(String name,List<PolicyVO> list){
		PolicyVO policy = null;
		for(Iterator<PolicyVO> iter = list.iterator();iter.hasNext();){
			PolicyVO p = iter.next();
			if(p.getName().equals(name)){
				policy = p;
				break;
			}
		}
		return policy;
	}

	@Override
	public List<String> getTablesByDataSource(DataConnectConfigVO dccv) {
		
		return null;
	}
}
