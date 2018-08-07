/**    
 * 文件名：FilterResult.java    
 *    
 * 版本信息：    
 * 日期：2018年8月6日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：FilterResult    
 * 类描述：    统计结果
 * 创建人：jinyu    
 * 创建时间：2018年8月6日 下午10:49:30    
 * 修改人：jinyu    
 * 修改时间：2018年8月6日 下午10:49:30    
 * 修改备注：    
 * @version     
 *     
 */
public class FilterResult {
    List<FilterModel> lst=new ArrayList<FilterModel>();
    List<String> allTable=new ArrayList<String>();
    LinkedHashMap<String,Object> param=new  LinkedHashMap<String,Object>();
    TableType opt;
}
