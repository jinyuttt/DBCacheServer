/**    
 * 文件名：SQLConfig.java    
 *    
 * 版本信息：    
 * 日期：2018年8月7日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import cd.db.jason.model.DynamicModel;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：SQLConfig    
 * 类描述：    配置SQL读取
 * 创建人：jinyu    
 * 创建时间：2018年8月7日 上午2:17:12    
 * 修改人：jinyu    
 * 修改时间：2018年8月7日 上午2:17:12    
 * 修改备注：    
 * @version     
 *     
 */
public class SQLConfig {
    private static class Sington
    {
        private  static  SQLConfig instance=new SQLConfig();
    }
 private HashMap<String,DataResult> mapResult=new HashMap<String,DataResult>();
 private HashMap<String,DynamicModel> mapCache=new HashMap<String,DynamicModel>();
 public static SQLConfig getInstance()
 {
     return Sington.instance;
 }
 
 public DataResult getResult(String id)
 {
    return  mapResult.getOrDefault(id, null);
    //这里可以添加再次读取模式，需要自己设计读取的xml及节点
 }
 public DynamicModel getCache(String name)
 {
    return mapCache.getOrDefault(name, null); 
 }
 public void putCache(String name,DynamicModel model)
 {
     mapCache.put(name, model);
 }
  public void read()
  {
      File dir=new File(XMLRead.dir);
      if(dir.exists())
      {
          String[] sqlConfs=dir.list();
          for(String srvSql:sqlConfs)
          {
              XMLRead read=new XMLRead(XMLRead.dir+"/"+srvSql);
              HashMap<String, String> map = read.readALL();
              for(Entry<String, String> item:map.entrySet())
              {
                  DataResult sqlConf=new DataResult();
                  sqlConf.strSql=item.getValue();
                  mapResult.put(item.getKey(), sqlConf);
              }
          }
      }
  }
}
