/**    
 * 文件名：DynamicModel.java    
 *    
 * 版本信息：    
 * 日期：2018年8月7日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.model;

import java.sql.ResultSet;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DynamicModel    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月7日 下午6:09:17    
 * 修改人：jinyu    
 * 修改时间：2018年8月7日 下午6:09:17    
 * 修改备注：    
 * @version     
 *     
 */
public abstract class DynamicModel {
public abstract void setValues(ResultSet rs);
}
