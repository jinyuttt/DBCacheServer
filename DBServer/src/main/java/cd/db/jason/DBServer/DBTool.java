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
}
