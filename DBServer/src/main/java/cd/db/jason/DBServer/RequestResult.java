/**    
 * 文件名：RequestResult.java    
 *    
 * 版本信息：    
 * 日期：2018年8月7日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.util.HashMap;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：RequestResult    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月7日 下午5:16:19    
 * 修改人：jinyu    
 * 修改时间：2018年8月7日 下午5:16:19    
 * 修改备注：    
 * @version     
 *     
 */
public class RequestResult {
public Object Result;
public boolean isSucess=false;
public String error;
public HashMap<String,String> mapTitle=new HashMap<String,String>();
public HashMap<String,HashMap<String,String>> mapDictionry=new HashMap<String,HashMap<String,String>>();
}
