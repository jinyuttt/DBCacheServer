/**    
 * 文件名：DBServer.java    
 *    
 * 版本信息：    
 * 日期：2018年8月7日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cd.db.jason.DBAcess.DBAcessResult;
import cd.db.jason.cache.DBCache;
import cd.db.jason.cglib.JavaStringCompiler;
import cd.db.jason.model.DynamicModel;
import cd.jason.db.Client.ClientModel;
import cd.jason.db.Client.DataFormat;
import cd.jason.db.Client.MsgSerialize;
import cd.jason.db.Client.ParamModel;
import cd.jason.db.Client.RequestResult;
import cd.jason.db.Client.ZN_CN_Trans;
import cd.strommq.channel.NettyRspData;
import cd.strommq.channel.NettyServer;
import cd.strommq.log.LogFactory;
import cd.strommq.nettyFactory.FactorySocket;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DBServer    
 * 类描述：    服务类
 * 创建人：jinyu    
 * 创建时间：2018年8月7日 上午3:35:20    
 * 修改人：jinyu    
 * 修改时间：2018年8月7日 上午3:35:20    
 * 修改备注：    
 * @version     
 *     
 */
public class DBServer {
   public String javaDir="javasrc";
   public String clsDir="clsSrc";
   public String dbType="psql";
   ExecutorService fixedThreadPool = null;
   ExecutorService cachePool = null;
   NettyServer netServer=null;
   public long timeOut=10*1000;//10s
   private volatile boolean isStop=false;
  private Class<ClientModel> clazz=ClientModel.class;
   public void  start(final int port)
   {
       int num=Runtime.getRuntime().availableProcessors()*2;
       fixedThreadPool = Executors.newFixedThreadPool(num,new DBThreadFactory("db_process"));
       cachePool=Executors.newCachedThreadPool(new DBThreadFactory("cache"));
       netServer = FactorySocket.createServer("tcp");
       Thread srvThread=new Thread(new Runnable() {
        @Override
        public void run() {
            netServer.start(port);
        }
           
       });
       srvThread.setDaemon(true);
       srvThread.setName("server");
       if(!srvThread.isAlive())
       {
           srvThread.start();
       }
       recvice();//开启数据接收
       LogFactory.getInstance().addInfo("开启服务");
       cachePool.execute(new Runnable() {
        @Override
        public void run() {
            init();
        }
       });
       //
       DBAcessResult db=new DBAcessResult();
       db.setDB(dbType);
       String sql="create table test(id int,name varchar(10))";
       db.executeDMLSql(sql);
       db.closeDB();
       try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
   }
   
   /**
      * 初始化
    * @Title: init   
    * @Description: 初始化信息    
    * void
    */
   public void init()
   {
       try
       {
       //用户信息
       String file=XMLRead.dir+"/dbbase.xml";
       XMLRead rd=new XMLRead(file);
       String sql=rd.read("suerinfo", "sql","");
       String userid=rd.read("suerinfo", "userid","");//userid字段名称
       String username=rd.read("suerinfo", "username","");//username字段名称
       readUser(sql,userid,username);
       //中英文转换
       sql=rd.read("ZNCN", "sql","");
       String zh=rd.read("ZNCN", "ZN", "");
       String en=rd.read("ZNCN", "CN", "");
       readZNEN(sql,zh,en);
       //字典值转换
       sql=rd.read("ZD", "sql","");
       String ename=rd.read("ZD", "key", "");
       String zid=rd.read("ZD", "value", "");
       String zname=rd.read("ZD", "name", "");
       readZDValue(sql,ename,zid,zname);
       //读取数据权限
       readPermissions();
       }
       catch(Exception ex)
       {
           LogFactory.getInstance().addError(ex.getMessage());
           ex.printStackTrace();
       }
   }
   
   /**
    * 
    * @Title: readUser   
    * @Description: 读取用户  
    * @param sql
    * @param userid
    * @param username    
    * void
    */
   private void readUser(String sql,String userid,String username)
   {
       DBAcessResult db=new DBAcessResult();
       db.setDB(dbType);
       HashMap<String, String> user=new  HashMap<String, String>();
       try
       {
       ResultSet rs = db.executeQuerySql(sql);
       while(rs.next())
       {
           String id=rs.getString(userid);
           String name=rs.getString(username);
           user.put(id, name);
       }
       }
       catch(Exception ex)
       {
           ex.printStackTrace();
       }
       db.closeDB();
      BusDictionary.getInstance().setUserInfo(user);
   }
   
   
   /**
    * 
    * @Title: readZNCN   
    * @Description: 读取中英文转换  
    * @param sql
    * @param zn
    * @param cn    
    * void
    */
   private  void readZNEN(String sql,String zn,String cn)
   {
       DBAcessResult db=new DBAcessResult();
       db.setDB(dbType);
       HashMap<String, String> map=new  HashMap<String, String>();
       try
       {
       ResultSet rs = db.executeQuerySql(sql);
       while(rs.next())
       {
           String zh=rs.getString(cn);
           String  ch=rs.getString(zn);
           map.put(ch, zh);
       }
       }
       catch(Exception ex)
       {
           ex.printStackTrace();
       }
       db.closeDB();
      BusDictionary.getInstance().setZNCN(map);
   }
   
   /**
    * 
    * @Title: readZDValue   
    * @Description: 多值字典  
    * @param sql
    * @param enname
    * @param zid
    * @param zname    
    * void
    */
   private  void readZDValue(String sql,String enname,String zid,String zname)
   {
       DBAcessResult db=new DBAcessResult();
       db.setDB(dbType);
       try
       {
       ResultSet rs = db.executeQuerySql(sql);
       while(rs.next())
       {
           String enames=rs.getString(enname);
           String  zids=rs.getString(zid);
           String  znames=rs.getString(zname);
           BusDictionary.getInstance().putValue(enames, zids, znames);
       }
       }
       catch(Exception ex)
       {
           ex.printStackTrace();
       }
       db.closeDB();
   }
   
   
   /**
    * 
    * @Title: readPermissions   
    * @Description: 读取权限      
    * void
    */
   private  void readPermissions()
   {
       DBAcessResult db=new DBAcessResult();
       db.setDB(dbType);
       String file=XMLRead.dir+"/dbbase.xml";
       XMLRead rd=new XMLRead(file);
       try
       {
       String id="";
       String sql=rd.read("Permissions","sql", id);
       String userid=rd.read("Permissions","userid", id);
       String table=rd.read("Permissions","table", id);
       String pinsert=rd.read("Permissions","insert", id);
       String pdelete=rd.read("Permissions","delete", id);
       String pupdate=rd.read("Permissions","update", id);
       String pselect=rd.read("Permissions","select", id);
       String pcreate=rd.read("Permissions","create", id);
       String pdrop=rd.read("Permissions","drop", id);
       String ptrunc=rd.read("Permissions","truncate", id);
       ResultSet rs = db.executeQuerySql(sql);
       while(rs.next())
       {
           String user=rs.getString(userid);
           String tname=rs.getString(table);
           boolean  zinsert=rs.getBoolean(pinsert);
           boolean  zdelete=rs.getBoolean(pdelete);
           boolean  zupdate=rs.getBoolean(pupdate);
           boolean  zselect=rs.getBoolean(pselect);
           boolean  zcreate=rs.getBoolean(pcreate);
           boolean  zdrop=rs.getBoolean(pdrop);
           boolean  ztrun=rs.getBoolean(ptrunc);
           if(zinsert)
           {
             BusDictionary.getInstance().putPermissions(user, TablePermissions.insert.name(), tname);
           }
           if(zdelete)
           {
             BusDictionary.getInstance().putPermissions(user, TablePermissions.delete.name(), tname);
           }
           if(zupdate)
           {
             BusDictionary.getInstance().putPermissions(user, TablePermissions.update.name(), tname);
           }
           if(zselect)
           {
             BusDictionary.getInstance().putPermissions(user, TablePermissions.select.name(), tname);
           }
           if(zcreate)
           {
             BusDictionary.getInstance().putPermissions(user, TablePermissions.create.name(), tname);
           }
           if(zdrop)
           {
             BusDictionary.getInstance().putPermissions(user, TablePermissions.drop.name(), tname);
           }
           if(ztrun)
           {
             BusDictionary.getInstance().putPermissions(user, TablePermissions.truncate.name(), tname);
           }
           
       }
       }
       catch(Exception ex)
       {
           ex.printStackTrace();
       }
       db.closeDB();
   }
   
   /**
    * 
    * @Title: recvice   
    * @Description: 接收数据       
    * void
    */
   private void recvice()
   {
       Thread srvThread=new Thread(new Runnable() {

           @Override
           public void run() {
             try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
              while(!isStop)
              {
                  NettyRspData rsp = netServer.recvice();
                  processThread(rsp);
              }
           }
              
          });
          srvThread.setDaemon(true);
          srvThread.setName("recvice");
          if(!srvThread.isAlive())
          {
              srvThread.start();
          }
   }
   
   /**
    * 采用多线程接收处理数据，固定线程执行的方式
    * 后果：1.数据请求大，执行线程固定个数，数据库查询压力正常，很服务压力；
    * 查询快就服务快，查询慢就服务满，压力在数据库上；
    * @Title: processThread   
    * @Description: 开启线程处理数据
    * @param rsp    
    * void
    */
   private void processThread(final NettyRspData rsp)
   {
       cachePool.execute(new Runnable() {
        @Override
        public void run() {
            final ClientModel client=MsgSerialize.DeSerialize((byte[])rsp.data, clazz);
            RequestResult result=new RequestResult();
            Future<RequestResult> future = fixedThreadPool.submit(new Callable<RequestResult>() {
              @Override
              public RequestResult call() throws Exception {
                  return startThread(client);
              }});
        try
        {
          if(client.timeout==0)
          {
              result=future.get(timeOut, TimeUnit.MILLISECONDS);
          }
          else if(client.timeout==-1)
          {
              result=future.get();
          }
          else
          {
              result=future.get(client.timeout, TimeUnit.MILLISECONDS);
          }
        }
        catch(TimeoutException ex)
        {
            result.isSucess=false;
            result.error="执行超时";
        }
        catch(Exception ex)
        {
            result.isSucess=false;
            result.error=ex.getMessage();
            LogFactory.getInstance().addError(ex.getMessage());
        }
        byte[] data;
        if(client.resultFormat==DataFormat.json)
        {
            data=MsgSerialize.JSONByteSerialize(result);
        }
        else
        {
            data= MsgSerialize.Serialize(result);
        }
          rsp.setRsp(data);  
        }
           
       });
   }
   
   /**
    * 
    * @Title: startThread   
    * @Description:启动处理
    * @param model
    * @return    
    * RequestResult
    */
public RequestResult startThread(ClientModel model)
{

    String strSQL="";
    DataResult configsql=null;
    HashMap<String,Object> map=null;//参数化结果
    String cacheid="";
    if(model.strSQL.isEmpty())
    {
        if(model.configID!=null)
        {
            model.configID=model.configID.replace("/", "_");
        }
        configsql = SQLConfig.getInstance().getResult(model.configID);
        strSQL=configsql.strSql;
    }
    else
    {
        strSQL=model.strSQL;
    }
    RequestResult reqResult=new RequestResult();
    DBAcessResult db=new DBAcessResult();
    db.setDB(dbType);
    //分析SQL
    FilterResult result = FilterTable.getInstance().getTableType(strSQL);
    //处理Sql 
   List<DataPermissions> lstPermission = do_Data_Permissions(result);
   //userName转userid
   boolean r= BusDictionary.getInstance().do_Data_Permissions(model.userName, lstPermission);
   if(!r)
   {
       reqResult.isSucess=false;
       reqResult.error="没有操作数据权限";
   }
   else
   {
       HashMap<String,ParamModel> mapData=model.getData();
       //处理一次参数
       cacheid=strSQL;
       if(!mapData.isEmpty())
       {
           map=convertData(mapData);
           String key=MsgSerialize.JSONSerialize(map);
           cacheid=cacheid+key;
       }
       reqResult=(RequestResult) DBCache.getInstance().getDataCache(cacheid);
       if(reqResult!=null)
       {
          return reqResult;
       }
       else
        {
              reqResult=new RequestResult();
        }
      if(map==null)
       {
        if(result.opt==TableType.select)
        {
            ResultSet rs = db.executeQuerySql(strSQL);
            queryData(model,reqResult,configsql,rs);
        }
        else
        {
            int num=db.executeDMLSql(strSQL);
            reqResult.Result=num;
        }
       }
    else
    {
       
        Object[] params =do_Params(result.param,map);
        if(result.opt==TableType.select)
        {
            ResultSet rs =  db.executeQueryRS(strSQL, params);
            queryData(model,reqResult,configsql,rs);
        }
        else
        {
            int num=db.executeUpdate(strSQL, params);
            reqResult.Result=num;
        }
    }
   }
   db.closeDB();
       //将参数转出json字符串组成key;
   if(!cacheid.isEmpty())
   DBCache.getInstance().addCache(cacheid, reqResult);
  return reqResult;
}

/**
 * 
* @Title: convertData
* @Description: 参数转换为数据
* @param @param mapData
* @param @return    参数
* @return HashMap<String,Object>    返回类型
 */
private  HashMap<String,Object> convertData(HashMap<String,ParamModel> mapData)
{
    HashMap<String,Object> map=new HashMap<String,Object>();
    Iterator<Entry<String, ParamModel>> iter = mapData.entrySet().iterator();
    while(iter.hasNext())
    {
        Entry<String, ParamModel> item = iter.next();
        ParamModel p=item.getValue();
        Object value =DBTool.getVlaue(p.clsName, p.value);
        String key=BusDictionary.getInstance().do_EN(item.getKey());
        map.put(key, value);
    }
    return map;
}
/**
 * 
 * @Title: do_Data_Permissions   
 * @Description:处理数据权限 
 * @param result
 * @return    
 * List<DataPermissions>
 */
private List<DataPermissions> do_Data_Permissions(FilterResult result )
{
    //处理Sql 
    int size=result.lst.size();
    List<DataPermissions> lst=new ArrayList<DataPermissions>(size);
    //转换成权限
    for(int i=0;i<size;i++)
    {
        FilterModel v = result.lst.get(i);
        if(!v.tables.isEmpty())
        {
          DataPermissions p=new DataPermissions();
          p.tables.addAll(v.tables);
          if(v.filter==TableType.insert)
          {
               p.permissions=TablePermissions.insert;
          }
          else if(v.filter==TableType.delete)
          {
              p.permissions=TablePermissions.delete;
          }
          else if(v.filter==TableType.update)
          {
              p.permissions=TablePermissions.update;
          }
          else if(v.filter==TableType.select)
          {
              p.permissions=TablePermissions.select;
          }
          else if(v.filter==TableType.create)
          {
              p.permissions=TablePermissions.create;
          }
          else if(v.filter==TableType.drop)
          {
              p.permissions=TablePermissions.drop;
          }
          else if(v.filter==TableType.truncate)
          {
              p.permissions=TablePermissions.truncate;
          }
          lst.add(p);
        }
    }
    return lst;
}


/**
 * 
 * @Title: do_Params   
 * @Description:处理
 * @param mapParam
 * @param mapData
 * @return    
 * Object[]
 */
private Object[]  do_Params(Map<String,Object> mapParam,Map<String,Object> mapData)
{
   
    List<Object> lst=new ArrayList<Object>();
    Iterator<Entry<String, Object>> iter = mapParam.entrySet().iterator();
    while(iter.hasNext())
    {
        //
       Entry<String, Object> item = iter.next();
       if(item.getValue() instanceof List)
       {
           lst.add(item.getValue());
       }
       else if(item.getValue().toString().contains("?"))
       {
           Object value=mapData.getOrDefault(item.getKey(), "");
           lst.add(value);
       }
    }
    Object[] params=new Object[lst.size()];
    lst.toArray(params);
    return params;
}


/**
 * 
 * @Title: executeSelectSQL   
 * @Description: 
 * @param rs
 * @return    
 * HashMap<String,String>
 */
private List<HashMap<String,String>>  executeSelectSQL(ZN_CN_Trans trans,ResultSet rs)
{
    List<HashMap<String,String>> lst=new ArrayList<HashMap<String,String>>(100000);
    try
    {
    ResultSetMetaData meta = rs.getMetaData();
    int colSize= meta.getColumnCount();
    HashMap<String,String> zd=new HashMap<String,String>();
    List<String> lstAll=new ArrayList<String>();
    String columnName="";
    Object value="";
    String strValue="";
   for(int i=0;i<colSize;i++)
   {
        columnName=meta.getColumnName(i);
       lstAll.add(columnName);
       if(BusDictionary.getInstance().do_dictionary(columnName))
       {
           zd.put(columnName,null);
       }
   }
   while(rs.next())
   {
       HashMap<String,String> map=new  HashMap<String,String>(100);
       for(int j=0;j<colSize;j++)
       {
           columnName= lstAll.get(j);
           value=rs.getObject(columnName);
           if(value!=null)
           {
               strValue=String.valueOf(value);
           }
          switch(trans)
          {
           case title:
               columnName=BusDictionary.getInstance().do_ZN(columnName);
               map.put(columnName, strValue);
               break;
           case value:
               if(zd.containsKey(columnName))
               {
                   strValue=BusDictionary.getInstance().do_ZN_Value(columnName, String.valueOf(value));
               }
               map.put(columnName, strValue);
               break;
           case all:
               if(zd.containsKey(columnName))
               {
                   strValue=BusDictionary.getInstance().do_ZN_Value(columnName, String.valueOf(value));
               }
               columnName=BusDictionary.getInstance().do_ZN(columnName);
               map.put(columnName, strValue);
               break;
           case no:
               map.put(columnName, strValue);
        default:
            break;
          }
       }
       lst.add(map);
   }
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
   //
   return lst;
}

/**
 * 
 * @Title: createClass   
 * @Description: 创建MODEL类   
 * @param id
 * @param rs
 * @return    
 * String
 */
private String createClass(String id,ResultSet rs)
{
    //创建类
    try
    {
    StringBuffer buf=new StringBuffer();
    StringBuffer bufData=new StringBuffer();
    buf.append("package cd.db.jason.model;\r\n" + 
            "import java.util.*;\r\nimport java.sql.ResultSet;\r\n");
    buf.append(" public class ");
    buf.append(id);//类名称
    buf.append("_cls ");
    buf.append("  extends DynamicModel {\r\n");
    ResultSetMetaData meta = rs.getMetaData();
   int colSize= meta.getColumnCount();
   String columnName="";
   String columnType="";
   for(int i=0;i<colSize;i++)
   {
       columnName=meta.getColumnName(i+1);
       columnType=meta.getColumnClassName(i+1);
       columnType= DBTool.convertType(columnType);
       buf.append("  ");
       buf.append(" public ");
       buf.append(columnType);
       buf.append("  ");
       buf.append(columnName);
       buf.append(";");
       buf.append("\r\n");
       bufData.append("this.");
       bufData.append(columnName);
       bufData.append("=");
       //取方法
       bufData.append("rs.");
       bufData.append(DBTool.convertJDBC(columnType));
       bufData.append("(\"");
       //另外一种方法利用强制转换
//       if(columnType.equals("String"))
//       {
//           bufData.append("rs.getString(\"");
//         
//       }
//       else if(columnType.equals("byte[]"))
//       {
//           bufData.append("rs.getBytes(\"");
//       }
//       else
//       {
  //     bufData.append("(");
 //      bufData.append(columnType);
    //   bufData.append(")");
//           bufData.append("rs.getObject(\"");
       
//       }
       //
       bufData.append(columnName);
       bufData.append("\")");
       bufData.append(";\r\n");
   }
   //处理方法
   buf.append("@Override\r\n" + 
           "    public void setValues(ResultSet rs) {\r\n");
   buf.append("try{");
   buf.append(bufData);
   buf.append("} catch(Exception ex)\r\n" + 
           "    {\r\n" + 
           "        ex.printStackTrace();\r\n" + 
           "    }");
   buf.append("\r\n}\r\n");
   buf.append("}");
   return buf.toString();
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
    return null;
}

/**
 * 
 * @Title: createModel   
 * @Description: 产生Model 
 * @param filePath
 * @param name
 * @param src
 * @return    
 * DynamicModel
 */
private DynamicModel createModel(String filePath,String name,String src)
{
    try
    {
    JavaStringCompiler compiler = new JavaStringCompiler();
    Map<String, byte[]> results = compiler.compile(filePath+"/"+name+".java", src);
    Class<?> clazz = compiler.loadClass("cd.db.jason.model."+name, results);
    // try instance:
    @SuppressWarnings("deprecation")
    DynamicModel model = (DynamicModel) clazz.newInstance();
    return model;
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
    return null;
}

/**
 * 
 * @Title: queryModel   
 * @Description: 创建model
 * @param clsName
 * @param rs
 * @return    
 * List<DynamicModel>
 */
@SuppressWarnings("deprecation")
private List<DynamicModel> queryModel(DynamicModel template,String clsName,ResultSet rs)
{
     clsName=clsName.replace("-", "_");
    List<DynamicModel> lst=new ArrayList<DynamicModel>(1000);
    String cls= createClass(clsName,rs);
    DynamicModel srvModel=null;
    Class<?> clazz=null;
    if(template==null)
    {
        srvModel=createModel(javaDir,clsName+"_cls",cls);
        System.out.println("创建类："+clsName+"_cls");
        clazz=srvModel.getClass();
        template=srvModel;
    }
    else
    {
        clazz=template.getClass();
    }
    try
    {
    while(rs.next())
    {
        srvModel=(DynamicModel) clazz.newInstance();
        srvModel.setValues(rs);
        lst.add(srvModel);
    }
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
    return lst;
}

/**
 * 
 * @Title: queryData   
 * @Description: 产生Model结果
 * @param model
 * @param reqResult
 * @param configsql
 * @param rs    
 * void
 */
private void queryData(ClientModel model,RequestResult reqResult,DataResult configsql,ResultSet rs)
{
    if(model.resultFormat==DataFormat.json)
    {
        //json格式
        List<HashMap<String, String>> lstR = executeSelectSQL(model.trans,rs);
        reqResult.Result=MsgSerialize.JSONSerialize(lstR);
    }
    else
    {
        if(configsql==null)
        {
            //客户端SQL
            if(model.configID==null||model.configID.isEmpty())
            {
                reqResult.Result=queryModel(null,"srv_Tmp",rs);
            }
            else
            {
                DynamicModel template = SQLConfig.getInstance().getCache(model.configID);
                reqResult.Result=queryModel(template,model.configID,rs);
                SQLConfig.getInstance().putCache(model.configID, template);
            }
        }
        else
        {
            //服务端SQL
            //如果是路径格式则进行替换
            String clsName=model.configID.replaceAll("/", "_");
            reqResult.Result=queryModel(configsql.model,clsName,rs);
        }
    }
}

/**
 * 
 * @Title: close   
 * @Description: 关闭  
 * void
 */
public void close()
{
    this.isStop=true;
    this.netServer.close();
    this.fixedThreadPool.shutdownNow();
    this.cachePool.shutdownNow();
}

}
