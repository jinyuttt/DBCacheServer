/**    
 * 文件名：RegisterServer.java    
 *    
 * 版本信息：    
 * 日期：2018年8月18日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.jason.BilSrv;

import java.io.File;

import cd.jason.clusterdiscovery.EtcdUtil;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：RegisterServer    
 * 类描述：    
 * 创建人：SYSTEM    
 * 创建时间：2018年8月18日 下午9:38:34    
 * 修改人：SYSTEM    
 * 修改时间：2018年8月18日 下午9:38:34    
 * 修改备注：    
 * @version     
 *     
 */
public class RegisterClusterServer {
    private Thread refresh=null;
    private int ttl=0;
    private String nodeID="XXXSrv";//标记服务节点唯一
    private String SYS_NAME="XXXSrv";//服务名称
    private String SYS_NODE="XXXSrvNode";//初始化测试使用的服务节点名称
    private String registerAddr="";
    private volatile boolean isStop=false;
    private String localSrvAddress="";
    private String[] config=null;
    private String confPath="config";
    
    /**
     * 
    * @Title: setServerName
    * @Description: 设置服务名称，标识服务集群
    * @param name    参数
    * @return void    返回类型
     */
    public void setServerName(String name)
    {
        this.SYS_NAME=name;
        this.SYS_NODE=name+"Node";
    }
    
    /**
     * 
    * @Title: setConfigUp
    * @Description: 设置需要上传的配置文件
    * @param config    参数
    * @return void    返回类型
     */
    public void setConfigUp(String config)
    {
        String[] confDir=config.split(";");
        if(confDir!=null&&confDir.length>0)
        {
            this.config=confDir;
        }
    }
    
    /**
     * 
    * @Title: registerNode
    * @Description: 注册服务
    * @param  noedeName 服务节点名称，标记唯一服务节点
    * @param  localSrvAddr 本节点提供服务的地址
    * @param  registerAddress 注册的etcd节点集群地址
    * @param  ttl    参数
    * @return void    返回类型
     */
public void registerNode(String noedeName,String localSrvAddr,String registerAddress,int ttl)
{
    this.nodeID=noedeName;
    this.ttl=ttl;
    this.localSrvAddress=localSrvAddr;
    this.registerAddr=registerAddress;
    start();
}

/**
 * 
* @Title: start
* @Description: 启动线程注册刷新
* @return void    返回类型
 */
private void start()
{
    refresh=new Thread(new Runnable() {

        @Override
        public void run() {
            EtcdUtil.setAddress(registerAddr);
            try {
                EtcdUtil.putEtcdValueByKeyTTL(SYS_NODE+"/"+nodeID, "", ttl);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //注册正常
            Long Leaseid=EtcdUtil.getEtcdLeaseid(SYS_NODE+"/"+nodeID);
            String key="";
            boolean isUp=true;
         while(!isStop)
         {
             if(Leaseid==null)
             {
                 try {
                    EtcdUtil.putEtcdValueByKeyTTL(SYS_NODE+"/"+nodeID, "", ttl);
                    Leaseid=EtcdUtil.getEtcdLeaseid(SYS_NODE+"/"+nodeID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
             }
             else if(key.isEmpty())
             {
                 key=SYS_NAME+"/"+Leaseid;
                 try {
                    EtcdUtil.putEtcdValueByKeyTTL(key, localSrvAddress, ttl);
                } catch (Exception e) {
                    key="";
                    e.printStackTrace();
                }
                //可以注册并且
             }
             else
             {
                 //更新
                 EtcdUtil.updatettl(key);
             }
             if(isUp)
             {
                 //更新配置项
                 if(config==null)
                 {
                     isUp=false;
                 }
                 else
                 {
                     //
                     RwUpFile rd=new RwUpFile();
                     for(int i=0;i<config.length;i++)
                     {
                         //直接读取所有byte
                        String f=config[i];
                        if(f==null||f.trim().isEmpty())
                        {
                            continue;
                        }
                        File fs=new File(f);
                        if(!fs.exists())
                        {
                            continue;
                        }
                        else if(fs.isDirectory())
                        {
                           File[] fis=fs.listFiles();
                           for(int j=0;j<fis.length;j++)
                           {
                               
                               byte[] bytes=rd.readToByte(fis[j].getAbsolutePath());
                               if(bytes!=null)
                               {
                                   //系统服务名称+配置根+目录+文件名称
                                   String cofKey=SYS_NAME+"/"+confPath+"/"+f+"/"+fis[j].getName();
                                   try {
                                    EtcdUtil.putEtcdByteByKey(cofKey, bytes);
                                } catch (Exception e) {
                                 
                                    e.printStackTrace();
                                }
                               }
                           }
                        }
                        else
                        {
                           
                            byte[] bytes=rd.readToByte(f);
                            if(bytes!=null)
                            {
                                //系统服务名称+配置根+目录+文件名称
                                String cofKey=SYS_NAME+"/"+confPath+"/"+f;
                                try {
                                 EtcdUtil.putEtcdByteByKey(cofKey, bytes);
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                            }
                        }
                       
                     }
                 }
                 
             }
             try {
                 //早1秒更新
                Thread.sleep((ttl-1)*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         }
            
        }
        
    });
    refresh.setDaemon(true);
    refresh.setName("reg");
    if(!refresh.isAlive())
    {
      refresh.start();
    }
}

}
