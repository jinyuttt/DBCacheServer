/**    
 * 文件名：SYS_Name.java    
 *    
 * 版本信息：    
 * 日期：2018年8月18日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.jason.BilSrv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import com.coreos.jetcd.Client;
import com.coreos.jetcd.cluster.Member;
import com.coreos.jetcd.cluster.MemberListResponse;


import cd.jason.clusterdiscovery.EtcdClientListener;
import cd.jason.clusterdiscovery.EtcdEvent;
import cd.jason.clusterdiscovery.EtcdEventType;
import cd.jason.clusterdiscovery.EtcdUtil;

/**    
 *     
 * 项目名称：DBClient    
 * 类名称：ClientCluster    
 * 类描述：  
 * 1.根据传递的etcd集群节点地址，获取服务的服务地址
 *  2.使用前设置集群地址，集群客户端监测频率（s）以及强制客户端更新集群地址频率（day）
 * 如果强制更新是整周倍数，则迁移到周六；根据设置的监测频频，尽可能保持在夜间更新
 *   3.更新上传的配置文件
 * 创建人：SYSTEM    
 * 创建时间：2018年8月18日 下午10:15:58    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月18日 下午10:15:58    
 * 修改备注：    
 * @version     
 *     
 */
public class ClientCluster {
    private static String SYS_NAME="XXXSrv";
    
    /*
     * 更新文件指令
     */
    public static String updateCmd="updateconf";
    /**
       * 设置上传的配置根目录
     */
    public static String confPath="config";
    private static class Sington
    {
        private  static  ClientCluster instance=new ClientCluster();
    }
    private List<SrvAddress>  srvAddress=new ArrayList<SrvAddress>();
    private int index=0;
    private int srvSize=0;
    //控制节点地址更新
    private ReentrantReadWriteLock lock_obj=new ReentrantReadWriteLock();
    private EtcdClientListener clientListenter=null;
    private Thread checkEtcdCluster=null;
    private long waitTime=60*60*1000;//1小时检查一次etcd的集群信息
    private int forceDay=7;//强制客户端更新时间长度
    private int  minSize=1;//集群节点个数，小于该值则更新集群客户端
    private boolean isStop=false;
    public String clusterAddr=null;
    private String[] clusterLst=null;
    private boolean isWeekend=false;//表示周六或者周天强制更新
    private boolean ischeckCluster=true;//是否监测客户端
    private long hourLen=60*60*1000;//1小时毫秒
    //上次更新时间
    private int month=-1;
    //上次更新天
    private int day=-1;
    private boolean isUpdateFile=false;//客户端是否更新文件
    private List<String> lstFile=new ArrayList<String>();//更新的文件名称，包括路径
    private int maxFileNum=1000;
    //控制上传文件更新
    private ReentrantReadWriteLock conf_lock=new ReentrantReadWriteLock();
    
    /**
     * 
    * @Title: getInstance
    * @Description: 单例
    * @return    参数
    * @return ClientCluster    返回类型
     */
public static ClientCluster getInstance()
{
    return Sington.instance;
}

/**
 * 
* @Title: setEtcdNode
* @Description: 设置etcd注册节点地址
* @param nodeAddr    参数
* @return void    返回类型
 */
public void setEtcdNode(String nodeAddr)
{
    this.clusterAddr=nodeAddr;
}

/**
 * 
* @Title: setCheckCluster
* @Description: 设置是否需要动态监测etcd节点，默认是
* @param isAllow    参数
* @return void    返回类型
 */
public  void setCheckCluster(boolean isAllow)
{
    this.ischeckCluster=isAllow;
}
/**
 * 
* @Title: setEtcdCheck
* @Description: 设置检查机集群客户端的频率(s)及强制更新天数(day)
* @param waitTime
* @param checkDay    参数
* @return void    返回类型
 */
public void setEtcdCheck(long waitTime,int checkDay)
{
    this.waitTime=waitTime*1000;
    this.forceDay=checkDay;
    if(forceDay<1)
    {
        //视为不监测客户端
        forceDay=1;
    }
}

/**
 * 
* @Title: startCluster
* @Description: 启动etcd节点检查
* @return void    返回类型
 */
private void startCluster()
{
    if(checkEtcdCluster!=null)
    {
        return;
    }
    //如果发现设置的强制更新恰好是周数，则重新规划到周末强制更新
    if(forceDay%7==0)
    {
        isWeekend=true;
    }
    
        checkEtcdCluster=new Thread(new Runnable() {
            
            /**
             * 重新加载节点
            * @Title: reLoad
            * @Description: 更新客户端
            * @param list    参数
            * @return void    返回类型
             */
       private void reLoad(List<String> list)
       {
           StringBuffer buf=new StringBuffer();
           //
           int size=list.size();
          for(int i=0;i<size;i++)
          {
              SrvAddress net= convertAddress(list.get(i));
              if(net!=null)
              {
                buf.append(net.srvIP+":"+net.srvPort);
                buf.append(";");
              } 
          }
          if(buf.length()>0)
          {
              buf.deleteCharAt(buf.length()-1);
              clusterAddr=buf.toString();
              EtcdUtil.close();
              initClient();  
          }
       }
       
       /**
        * 
       * @Title: getNodeAddress
       * @Description:获取所有etcd节点地址
       * @param nodes
       * @return    参数
       * @return List<String>    返回类型
        */
       private List<String> getNodeAddress(List<Member> nodes)
       {
           int size=nodes.size();
           List<String> lstEtcdClient=new ArrayList<String>(size);
           for(int i=0;i<size;i++)
           {
               lstEtcdClient.addAll(nodes.get(i).getClientURLS());
           }
           //如果有127.0.0.1或者lcoalhost则移除，业务客户端不认为有该地址
           size=lstEtcdClient.size();
           for(int i=0;i<size;i++)
           {
               if(lstEtcdClient.get(i).contains("127.0.0.1")|| lstEtcdClient.get(i).contains("lcoalhost"))
               {
                   lstEtcdClient.remove(i);
                   size--;
                   i--;
               }
           }
           return lstEtcdClient;
       }
       
       /**
        * 
       * @Title: forceUpdateNode
       * @Description: 更新集群节点信息
       * @param nodes    参数
       * @return void    返回类型
        */
       private void forceUpdateNode( List<Member> nodes)
       {
         //已经有很多异常，用正常值更新，以免影响性能
           List<String> lstEtcdClient=getNodeAddress(nodes);
           //
           reLoad(lstEtcdClient);
       }
       
       /**
        * 
       * @Title: updateNoe
       * @Description: 此次是否更新
       * @return    参数
       * @return boolean    返回类型
        */
       private boolean updateNow()
       {
        boolean isUpdate=false;
        if(waitTime>24*hourLen)
           {
               isUpdate=true;//此次更新
           }
           else if(waitTime<hourLen)
           {
               //小于1小时
                Calendar cal = Calendar.getInstance();
                if(cal.get(Calendar.HOUR_OF_DAY)==1)
                 {
                     isUpdate=true;
                 }
               
           }
           else
           {
              //判断当前是否是1-3时间
               Calendar cal = Calendar.getInstance();
               if(cal.get(Calendar.HOUR_OF_DAY)>=1||cal.get(Calendar.HOUR_OF_DAY)<=3)
                {
                    isUpdate=true;
                }
              int curTime=cal.get(Calendar.DAY_OF_MONTH);
              int hour=(int) (waitTime/hourLen);
              cal.add(Calendar.HOUR_OF_DAY, hour);
              if(curTime!=cal.get(Calendar.DAY_OF_MONTH)||(cal.get(Calendar.HOUR_OF_DAY)>=1||cal.get(Calendar.HOUR_OF_DAY)<=3))
              {
                  //超过天或者没有1-3的机会就此次更新
                  isUpdate=true;
              }
           }
        //
        if(isUpdate)
        {
            //如果是则监测上次时间
            Calendar cal = Calendar.getInstance();
            if(day==-1)
            {
                month=cal.get(Calendar.MONTH);
                day=cal.get(Calendar.DAY_OF_MONTH);
            }
            else if(month==cal.get(Calendar.MONTH)&& day==cal.get(Calendar.DAY_OF_MONTH))
            {
                isUpdate=false;//当天已经更新
            }
            else
            {
                //此次更新
                month=cal.get(Calendar.MONTH);
                day=cal.get(Calendar.DAY_OF_MONTH);
            }
        }
        return isUpdate;
       }
       
       
       
       @Override
        public void run() {
           double num=0;
           boolean isUpdate=false;//是否可以更新
         while(!isStop)
         {
             try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
             
                e.printStackTrace();
            }
            if(!ischeckCluster)
            {
                //10分钟
                try {
                    Thread.sleep(10*60*1000);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
           Client client = EtcdUtil.getEtclClient();
           CompletableFuture<MemberListResponse> future = client.getClusterClient().listMember();
          List<Member> lst = null;
        try {
            lst = future.get().getMembers();
        } catch (Exception e) {
            e.printStackTrace();
        }
         if(lst!=null)
          {
              int size=lst.size();
              if(size<minSize)
              {
                  //如果集群个数小于最小值；检查更新配置值
                  if(size<clusterLst.length)
                  {
                      //已经有很多异常，用正常值更新，以免影响性能
                      forceUpdateNode(lst);
                  }
              }
              else if(size<clusterLst.length/2)
              {
                  //有一半异常清除，重新更新
                  //已经有很多异常，用正常值更新，以免影响性能
                  forceUpdateNode(lst);
                  
              }
          }
         //强制更新
      
          if(isWeekend)
          {
              //如果整周强制更新
              //计算
              Date bdate =new Date();
              Calendar cal = Calendar.getInstance();
              cal.setTime(bdate);
              //
              Calendar calNum = Calendar.getInstance();
              calNum.add(Calendar.SECOND,  (int) (waitTime/1000));
              if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                  //监测此次是否更新
                  isUpdate=updateNow();
                  if(isUpdate)
                  {
                     forceUpdateNode(lst);
                  }
              } 
          }
          else
          {
              num=num+(double)waitTime/1000;
          //按照秒统计；
           if(num/60/24>forceDay&&forceDay>0)
           {
            
              //强制更新
               isUpdate=updateNow();
               if(isUpdate)
               {
                  forceUpdateNode(lst);
               }
               num=0;
            }
          }
         }
        
        }
        
    });
    checkEtcdCluster.setDaemon(true);
    checkEtcdCluster.setName("");
    if(!checkEtcdCluster.isAlive())
    {
        checkEtcdCluster.start();
    }
}

/**
 * 
* @Title: startWatch
* @Description: 开启线程监听
* @return void    返回类型
 */
private void startWatch()
{
    if(clientListenter!=null)
    {
        clientListenter.close();//关闭之后线程会退出
    }
    Thread watch=new Thread(new Runnable() {

        @Override
        public void run() {
          
            clientListenter=new EtcdClientListener();
            clientListenter.init();
            while(true)
            {
            EtcdEvent event = clientListenter.getEvent();
            if(event.value==null)
            {
                continue;
            }
            if(event.eventType==EtcdEventType.delete)
            {
                //有服务异常了
                //
                SrvAddress find=null;
                StringBuffer buf=new StringBuffer();
                String value=event.value.replaceAll("\\s", "").toLowerCase();
                lock_obj.readLock().lock();
               for(int i=0;i<srvSize;i++)
               {
                    SrvAddress addr = srvAddress.get(i);
                    buf.append(addr.netType);
                    buf.append("://");
                    buf.append(addr.srvIP);
                    buf.append(":");
                    buf.append(addr.srvPort);
                    if(value.equals(buf.toString()))
                    {
                        find=addr;
                        break;
                    }
               }
               lock_obj.readLock().unlock();
               //
               if(find!=null)
               {
                   lock_obj.writeLock().lock();
                   srvAddress.remove(find);
                   lock_obj.writeLock().unlock();
               }
            }
            else if(event.eventType==EtcdEventType.add)
            {
                //
                String value=event.value.toLowerCase();
                if(value.trim().equals(updateCmd))
                {
                    //说明要求读取上传的文件
                    readConfig();
                }
                else
                {
                  SrvAddress addr =convertAddress(event.value.toLowerCase());
                  if(addr!=null)
                  {
                      srvAddress.add(addr);
                      //如果添加了服务查看上传文件是否更新
                       readConfig();
                  }
                  else
                  {
                      System.out.println("最新启动的服务地址异常，key:"+event.key);
                  }
                }
            }
            }
        }
        
    });
   watch.setDaemon(true);
   watch.setName("watchEtcdNode");
   watch.start();
    
}

/**
 * 
* @Title: setServerName
* @Description:设置系统服务名称
* @param name    参数
* @return void    返回类型
 */
public void setServerName(String name)
{
    SYS_NAME=name;
}


/**
 * 
* @Title: initClient
* @Description: 初始化地址
* @return void   返回类型
 */
public void initClient()
{
    EtcdUtil.setAddress(clusterAddr);
    clusterLst=clusterAddr.split(";");
     try {
         //取出地址
       List<String> lst= EtcdUtil.getEtcdValueByDir(SYS_NAME);
       List<SrvAddress> srvList=analyzeSrv(lst);
       if(srvList==null)
       {    
           return;
       }
       lock_obj.writeLock().lock();
       srvAddress.clear();
       srvAddress.addAll(srvList);
       srvSize=srvAddress.size();
       index=0;
       lock_obj.writeLock().unlock();
    } catch (Exception e) {
        e.printStackTrace();
    } 
     //初始化已经完成
     startCluster();
     startWatch();
}

/**
 * 
* @Title: isCompareFile
* @Description:比较内容
* @param f1
* @param f2
* @return    参数
* @return boolean    返回类型
 */
private boolean isCompareFile(String f1,String f2)
{
    //比较内容,去除\r\n以及空格，认为是无效的
    f1 = f1.replaceAll("\r|\n|\\s", "");
    f2 = f2.replaceAll("\r|\n|\\s", "");
    return f1.equals(f2);
}

/**
 * 
* @Title: setUpdateFile
* @Description: 是否需要更新文件
* @param isUp    参数
* @return void    返回类型
 */
public void setUpdateFile(boolean isUp)
{
    this.isUpdateFile=isUp;
}
/**
 * 
* @Title: isHaveUpdateFile
* @Description: 是否有上传的文件更新过
* @return    参数
* @return boolean    返回类型
 */
public boolean isHaveUpdateFile()
{
    return lstFile.isEmpty();
}

/**
 * 
* @Title: lastUpdateFiles
* @Description: 最近一次更新的文件名称，包括路径
* @return    参数
* @return List<String>    返回类型
 */
public List<String> lastUpdateFiles()
{
    List<String> lst=new ArrayList<String>();
    conf_lock.writeLock().lock();
    lst.addAll(lstFile);
    lstFile.clear();
    conf_lock.writeLock().unlock();
    return lst;
}
/**
 * 
* @Title: readConfig
* @Description: 读取文件比较
* @return void    返回类型
 */
public void readConfig()
{
    if(!isUpdateFile)
    {
        return;
    }
    String key=SYS_NAME+"/"+confPath;
    try {
       Map<String,String> map= EtcdUtil.getEtcdKVDir(key);
       if(map!=null)
       {
           RwUpFile rw=new RwUpFile();
          Iterator<Entry<String, String>> iter = map.entrySet().iterator();
          while(iter.hasNext())
          {
              Entry<String, String> item = iter.next();
              String fileName=item.getKey();
              String content=item.getValue();
              if(fileName.startsWith(key))
              {
                  //删除
                  fileName=fileName.replace(key, "");
              }
              //
              String rdContent=rw.readToString(fileName);
              if(rdContent!=null)
              {
                   boolean r=isCompareFile(content,rdContent);
                   if(r)
                   {
                       rw.write(fileName, content.getBytes("utf-8"));
                       conf_lock.writeLock().lock();
                       this.lstFile.add(fileName);
                       conf_lock.writeLock().unlock();
                   }
              }
              
          }
          map.clear();
       }
    } catch (Exception e) {
        e.printStackTrace();
    }
    if(this.lstFile.size()>this.maxFileNum)
    {
        //认为是不需要文件名称
        this.lstFile.clear();
    }
}
/**
 * 
* @Title: getSrvAddress
* @Description: 返回服务地址
* @return    参数
* @return SrvAddress    返回类型
 */
public  SrvAddress getSrvAddress()
{
    //这里不是完全意义的轮训负载均衡，所以取到相同的地址也没有关系；
    //如果是完全意义的轮训负载均衡，客户端是没有办法的，需要专门做一个代理
    //但是对于查询数据库是没有必须要的，这样需要添加一个中间服务，每次向服务请求地址，则没有意义
    //后期我会添加一个，防止有洁癖需要
    SrvAddress address=null;
    lock_obj.readLock().lock();
    address= srvAddress.get(index++%srvSize);
    lock_obj.readLock().unlock();
    return address;
}

/**
 * 
* @Title: analyzeSrv
* @Description: 转换地址格式
* @param srvAddress
* @return    参数
* @return List<SrvAddress>    返回类型
 */
private List<SrvAddress> analyzeSrv(List<String> srvAddress)
{
    if(srvAddress==null||srvAddress.isEmpty())
    {
        return null;
    }
    //
    int size=srvAddress.size();
        List<SrvAddress> lst=new ArrayList<SrvAddress>(size);
        for(int i=0;i<size;i++)
        {
            SrvAddress net=convertAddress(srvAddress.get(i));
            if(net!=null)
            {
                lst.add(net);
            }
        }
    return lst;
}

/**
 * 
* @Title: convertAddress
* @Description: 解析地址
* @param address
* @return    参数
* @return SrvAddress    返回类型
 */
private SrvAddress convertAddress(String address)
{
    //tcp://localhost:12789;
    try
    {
    address=address.replaceAll("\\s", "");//去除空格
    StringBuffer buf=new StringBuffer();
    buf.append(address);
    //
   int index= buf.indexOf("://");
   String netType=buf.substring(0, index);
   buf.delete(0, index+2);
   index=buf.indexOf(":");
   String ip=buf.substring(0, index);
   buf.delete(0, index+1);
   String port=buf.toString();
   SrvAddress netAddr=new SrvAddress();
   
   netAddr.netType=netType.toLowerCase();
   netAddr.srvIP=ip;
   netAddr.srvPort=Integer.valueOf(port);
   return netAddr;
    }
    catch(Exception ex)
    {
        //LogFactory.
    }
    return null;
}

}
