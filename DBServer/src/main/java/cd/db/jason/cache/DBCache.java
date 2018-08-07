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
 * 类描述：    
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
    private DBCache()
    {
        init();
    }
    public static DBCache getInstance()
    {
        return Sington.instance;
    }
    private LoadingCache<String, Object> cache=null;
public void init()
{
     cache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)// 给定时间内没有被读/写访问，则回收。
            .expireAfterAccess(1, TimeUnit.HOURS)// 缓存过期时间和redis缓存时长一样
            .maximumSize(100000).// 设置缓存个数
            build(new CacheLoader<String, Object>() {
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
}
