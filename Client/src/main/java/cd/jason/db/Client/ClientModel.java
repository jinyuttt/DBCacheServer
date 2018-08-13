/**    
 * 文件名：ClientModel.java    
 *    
 * 版本信息：    
 * 日期：2018年8月5日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.jason.db.Client;

import java.util.HashMap;

/**    
 *     
 * 项目名称：Client    
 * 类名称：ClientModel    
 * 类描述：  数据请求
 * 创建人：jinyu    
 * 创建时间：2018年8月5日 下午6:03:55    
 * 修改人：jinyu    
 * 修改时间：2018年8月5日 下午6:03:55    
 * 修改备注：    
 * @version     
 *     
 */
public class ClientModel {
/**
 * 客户端SQL
 */
public String strSQL="";//客户端SQL

/**
 * 配置ID,能够找到服务端SQL
 * 或返回Model时使用的名称
 */
public String configID="";//配置ID,能够找

/**
 * 用户名称
 */
public String userName="";

/**
 * 数据库类型
 */
public String dbType="";

/**
 * 数据转换，查询数据时转换数据
 * 字段名称中文文转换
 * 字典值转换
 */
public ZN_CN_Trans trans=ZN_CN_Trans.no;

/**
 * 结果返回时的序列化方式
 * JSON或者二进制
 */
public DataFormat resultFormat=DataFormat.datamodel;

/**
 * 0是默认服务端，-1是无线等待，其余是正常时间;
 * 单位ms
 */
public long timeout=0;

/**
 * 字段参数值
 * 如果没有数据则说明SQL已经拼接好
 * 如果有数据说明SQL是参数化方式，会按照字段替换参数
 */
private HashMap<String,ParamModel> mapData=new HashMap<String,ParamModel>();

/**
 * 
* @Title: addParam
* @Description: 添加参数化数据
* @param @param name
* @param @param obj    参数
* @return void    返回类型
 */
public  void addParam(String name,Object obj)
{
    ParamModel mode=new ParamModel();
    mode.clsName=obj.getClass().getName().replace("java.lang.", "");
    mode.value=String.valueOf(obj);
    mapData.put(name, mode);
}

/**
 * 
* @Title: getData
* @Description: 返回信息
* @param @return    参数
* @return HashMap<String,ParamModel>    返回类型
 */
public HashMap<String,ParamModel> getData()
{
    return mapData;
}
}
