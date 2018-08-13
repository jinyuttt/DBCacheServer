/**    
 * 文件名：DataRemovalListener.java    
 *    
 * 版本信息：    
 * 日期：2018年8月13日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.cache;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import cd.strommq.log.LogFactory;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DataRemovalListener    
 * 类描述： 缓存移除监听   
 * 创建人：SYSTEM    
 * 创建时间：2018年8月13日 上午12:04:10    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月13日 上午12:04:10    
 * 修改备注：    
 * @version     
 *     
 */
public class DataRemovalListener  implements RemovalListener<String, Object>{

    @Override
    public void onRemoval(RemovalNotification<String, Object> notification) {
        LogFactory.getInstance().addDebug("移除缓存key:"+notification.getKey());
    }

}
