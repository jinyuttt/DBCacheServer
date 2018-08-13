/**    
 * 文件名：DBCache.java    
 *    
 * 版本信息：    
 * 日期：2018年8月8日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DBCache    
 * 类描述：   查询数据缓存
 * 创建人：jinyu    
 * 创建时间：2018年8月8日 上午4:19:35    
 * 修改人：jinyu    
 * 修改时间：2018年8月8日 上午4:19:35    
 * 修改备注：    
 * @version     
 *     
 */
public class DBCache {
    private static class Sington
    {
        private static DBCache instance=new DBCache();
    }
    
    /**
     * 
    * @Title: getInstance
    * @Description: 获取单例，内部类实现
    * @param @return    参数
    * @return DBCache    返回类型
     */
    public static DBCache getInstance()
    {
        return Sington.instance;
    }
    
    /**
         * 缓存对象
     */
    private LoadingCache<String, Object> cache=null;
    
    /**
         * 缓存大小，默认10000 
     */
    public  long maxCacheSize=10000;
    
    /**
       * 缓存时间（分钟），默认60分钟
     */
    public  int  maxCacheTime=60;
    
  /**
   * 
  * @Title: init
  * @Description:初始化缓存对象
  * @param     参数
  * @return void    返回类型
   */
   public void init()
    {
     cache = CacheBuilder.newBuilder().refreshAfterWrite(maxCacheTime*2, TimeUnit.MINUTES)// 给定时间内没有被读/写访问，则回收。
            .expireAfterAccess(maxCacheTime, TimeUnit.MINUTES)// 缓存过期时间和redis缓存时长一样
            .maximumSize(maxCacheSize)// 设置缓存个数
            .initialCapacity(100)//初始化大小
            .concurrencyLevel(10)//并发线程，同时写入的线程数
            .removalListener(new DataRemovalListener())//添加监视
            .build(new CacheLoader<String, Object>() {
                @Override
                /** 当本地缓存命没有中时，调用load方法获取结果并将结果缓存 **/
                public Object load(String appKey)  {
                    return getAppkeyInfo(appKey);
                }

                /** redis数据库进行查询 **/
                private Object getAppkeyInfo(String appKey)  {
                    //读取其它缓存
                   return null;
                }
            });
    }

/**
 * 
 * @Title: addCache   
 * @Description:添加  
 * @param key
 * @param data    
 * void
 */
public void addCache(String key,Object data)
{
    cache.put(key, data);
}

/**
 * 
 * @Title: getDataCache   
 * @Description: 获取数据   
 * @param key
 * @return    
 * Object
 */
public Object getDataCache(String key)
{
   return  cache.getIfPresent(key);
}

/**
 * 
* @Title: clear
* @Description: 清除所有缓存
* @param     参数
* @return void    返回类型
 */
public void clear()
{
    cache.invalidateAll();
}
}
