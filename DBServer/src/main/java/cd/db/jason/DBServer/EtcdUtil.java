/**    
 * 文件名：EtcdUtil.java    
 *    
 * 版本信息：    
 * 日期：2018年8月14日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.net.URI;
import java.net.URISyntaxException;

import mousio.client.retry.RetryOnce;
import mousio.etcd4j.EtcdClient;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：EtcdUtil    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月14日 上午5:45:15    
 * 修改人：jinyu    
 * 修改时间：2018年8月14日 上午5:45:15    
 * 修改备注：    
 * @version     
 *     
 */
public class EtcdUtil {
    //etcl客户端链接
    private static EtcdClient client = null;
    //链接初始化
    public static synchronized EtcdClient getEtclClient(){
        if(null == client){
             client = new EtcdClient(URI.create("http://127.0.0.1:4001"));
            client.setRetryHandler(new RetryOnce(20));
        }
            return client;
    }
   public static void setAddress(String address)
   {
       String[] addrs=address.split(";");
       URI[] urls=new URI[addrs.length];
       for(int i=0;i<addrs.length;i++)
       {
           try {
            urls[i]=new URI(addrs[i]);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
       }
       client = new EtcdClient(urls);
       
   }
    /**
     * 根据指定的配置名称获取对应的value
     * @param key 配置项
     * @return
     * @throws Exception
     */
    public static String getEtcdValueByKey(String key) throws Exception {
      client.get(key);
       return key;
    }

    /**
     * 新增或者修改指定的配置
     * @param key
     * @param value
     * @return
     */
    public static void putEtcdValueByKey(String key,String value) throws Exception{
       client.put(key, value);

    }

    /**
     * 删除指定的配置
     * @param key
     * @return
     */
    public static void deleteEtcdValueByKey(String key){
        client.delete(key);
    }

//V3 api配置初始化和监听

}
