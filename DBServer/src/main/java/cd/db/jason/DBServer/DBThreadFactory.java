/**    
 * 文件名：DBThreadFactory.java    
 *    
 * 版本信息：    
 * 日期：2018年8月8日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DBThreadFactory    
 * 类描述：   处理线程
 * 创建人：jinyu    
 * 创建时间：2018年8月8日 上午12:03:12    
 * 修改人：jinyu    
 * 修改时间：2018年8月8日 上午12:03:12    
 * 修改备注：    
 * @version     
 *     
 */
public class DBThreadFactory implements  ThreadFactory {
    private static AtomicInteger threadid=new AtomicInteger(0);
    private String name="";
    public DBThreadFactory(String name)
    {
        this.name=name;
    }
    @Override
    public Thread newThread(Runnable thread) {
      Thread arg=new Thread(thread);
      arg.setDaemon(true);
      arg.setName(name+"_"+threadid.incrementAndGet());
      return arg;
    }

}
