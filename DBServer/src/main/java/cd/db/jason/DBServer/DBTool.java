/**    
 * 文件名：DBTool.java    
 *    
 * 版本信息：    
 * 日期：2018年8月7日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DBTool    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月7日 下午7:40:08    
 * 修改人：jinyu    
 * 修改时间：2018年8月7日 下午7:40:08    
 * 修改备注：    
 * @version     
 *     
 */
public class DBTool {
    
    /**
     * 
     * @Title: convertType   
     * @Description: 转为java类型   
     * @param name
     * @return    
     * String
     */
public static String convertType(String name)
{
    String javaName=name;
    if(name.startsWith("java.lang."))
    {
        name=name.replaceAll("java.lang.", "");
        javaName=name;
        switch(name)
        {
        case "Boolean":
            javaName="boolean";
            break;
        case "Integer":
            javaName="int";
            break;
        case "Long":
            javaName="long";
            break;
        case "Float":
            javaName="float";
            break;
        case "Double":
            javaName="double";
            break;
        case "Byte":
            javaName="byte";
            break;
        }
    }
    else if(name.startsWith("java.sql."))
    {
       name= name.replaceAll("java.sql.", "");
       javaName=name;
       if(name.contains("Time")||name.contains("Date"))
       {
           javaName="Date";
       }
    }
    else if(name.startsWith("java.math.BigDecimal"))
    {
        javaName="double";
    }
    return javaName;
}

/**
 * 
* @Title: convertJDBC
* @Description: 根据类型获取对应方法
* @param @param name
* @param @return    参数
* @return String    返回类型
 */
public static String convertJDBC(String name)
{
    
    String javaName="";
    switch(name)
    {
    case "boolean":
        javaName="getBoolean";
        break;
    case "int":
        javaName="getInt";
        break;
    case "long":
        javaName="getLong";
        break;
    case "float":
        javaName="getFloat";
        break;
    case "double":
        javaName="getDouble";
        break;
    case "byte":
        javaName="getByte";
    case "Date":
        javaName="getDate";
    case "String":
        javaName="getString";
        break;
        default:
            javaName="getObject";
            break;      
    }
    return javaName;
}

/**
 * 
* @Title: getVlaue
* @Description: 将字符串值转换成对应类型
* @param @param clsName
* @param @param value
* @param @return    参数
* @return Object    返回类型
 */
public static Object getVlaue(String clsName,String value)
{
    Object result=null;
    switch(clsName)
    {
    case "Boolean":
        result=Boolean.valueOf(value);
        break;
    case "Integer":
        result=Integer.valueOf(value);
        break;
    case "Long":
        result=Long.valueOf(value);
        break;
    case "Float":
        result=Float.valueOf(value);
        break;
    case "Double":
        result=Double.valueOf(value);
        break;
    case "Byte":
        result=Byte.valueOf(value);
        break;
    case "String":
        result=value;
        break;
    case "byte[]":
        result=value.getBytes();
        break;
    }
    return result;
}
}
