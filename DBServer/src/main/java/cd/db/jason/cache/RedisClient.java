/**    
 * 文件名：RedisClient.java    
 *    
 * 版本信息：    
 * 日期：2018年8月14日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.cache;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：RedisClient    
 * 类描述：    redis客户端
 * 创建人：jinyu    
 * 创建时间：2018年8月14日 上午3:25:18    
 * 修改人：jinyu    
 * 修改时间：2018年8月14日 上午3:25:18    
 * 修改备注：    
 * @version     
 *     
 */
public class RedisClient {
   
   public static boolean isUse=false;
    private static Config config = new Config();
    private static RedissonClient redisson = null;
    private static final String RAtomicName = "genId_";  
  public static void setRedidAddress(String address)
  {
      //
      String[]addrs=address.split(";");
      ClusterServersConfig srvConfig=null;
      try {
          srvConfig=  config.useClusterServers() //这是用的集群server
                  .setScanInterval(2000) //设置集群状态扫描时间
                  .setMasterConnectionPoolSize(10000) //设置连接数
                  .setSlaveConnectionPoolSize(10000);
          //清空自增的ID数字
          RAtomicLong atomicLong = redisson.getAtomicLong(RAtomicName);
          atomicLong.set(1);
      }catch (Exception e){
          e.printStackTrace();
      }
      for(String addr:addrs)
      {
          srvConfig.addNodeAddress("redis://"+addr); 
      }

      config.setCodec(new org.redisson.codec.MsgPackJacksonCodec());
      redisson = Redisson.create(config);

  }

  /**
   * 
  * @Title: getData
  * @Description: 获取数据
  * @param @param key
  * @param @return    参数
  * @return Object    返回类型
   */
  public static Object getData(String key)
  {
      RLock mylock = redisson.getLock(key);
      RBucket<Object> bucket = redisson.getBucket(key);
      mylock.unlock();
      return bucket.get();
  }
  
  /**
   * 
  * @Title: addData
  * @Description: 添加数据
  * @param @param key
  * @param @param obj    参数
  * @return void    返回类型
   */
  public static void addData(String key,Object obj)
  {
      RLock mylock = redisson.getLock(key);
      RBucket<Object> bucket = redisson.getBucket(key);
      bucket.setAsync(obj);
      mylock.unlock();
  }

    // 关闭服务器时 关闭缓冲池  
    public static void shutDown() {  
       // client.shutdown(); 
        redisson.shutdown();
    }  
}
