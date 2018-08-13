/**    
 * 文件名：BusDictionary.java    
 *    
 * 版本信息：    
 * 日期：2018年8月6日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：BusDictionary    
 * 类描述：  字典  
 * 创建人：jinyu    
 * 创建时间：2018年8月6日 上午1:27:40    
 * 修改人：jinyu    
 * 修改时间：2018年8月6日 上午1:27:40    
 * 修改备注：    
 * @version     
 *     
 */
public class BusDictionary {
private static class Sington
{
    private static BusDictionary instance=new BusDictionary();
}
private  HashMap<String,String> mapZN=new HashMap<String,String>(1000);
private  HashMap<String,String> mapCN=new HashMap<String,String>(1000);
private  HashMap<String,HashMap<String,String>> mapENZD=new  HashMap<String,HashMap<String,String>>(1000);
private  HashMap<String,HashMap<String,String>> mapZNZD=new  HashMap<String,HashMap<String,String>>(1000);
public  String defaultDB="psql";
/**
 * 用户信息
 * userid-username
 */
private  HashMap<String,String> mapUser=new  HashMap<String,String>();

/**
 * 用户信息
 * username-userid
 */
private  HashMap<String,String> mapUserInfo=new  HashMap<String,String>();

/**
 * 用户表权限
 * key-userid
 * value-权限名称-所有表
 */
private  HashMap<String,HashMap<String,List<String>>> mapPermissions=new  HashMap<String,HashMap<String,List<String>>>();

/**
 * 配置的模块数据库
 */
private HashMap<String,String> mapDB=new HashMap<String,String>();

public static BusDictionary getInstance()
{
   return Sington.instance;
}

/**
 * 
 * @Title: setUserInfo   
 * @Description: 添加用户信息
 * @param user    
 * void
 */
public void setUserInfo(HashMap<String,String> user)
{
    if(user==null)
    {
        return;
    }
    mapUser.putAll(user);
    //
    for( Entry<String, String> kv:mapUser.entrySet())
    {
        mapUserInfo.put(kv.getValue(), kv.getKey());
    }
}

/**
 * 
* @Title: getUserid
* @Description: 返回用户ID
* @param @param username 用户名称
* @param @return    参数
* @return String    返回类型
 */
public String getUserid(String username)
{
   return mapUserInfo.getOrDefault(username, username);
}
/**
 * 
 * @Title: setZNCN   
 * @Description: 设置中英文转换
 * @param map    
 * void
 */
public void setZNCN(HashMap<String,String> map)
{
    if(map==null)
    {
        return;
    }
    mapZN.putAll(map);
    //
    Iterator<Entry<String, String>> iter = map.entrySet().iterator();
    while(iter.hasNext())
    {
        Entry<String, String> item = iter.next();
        mapCN.put(item.getValue(), item.getKey());
    }
}

/**
 * 
 * @Title: putValue   
 * @Description:数据值  
 * @param pkey
 * @param value
 * @param name    
 * void
 */
public void putValue(String pkey,String value,String name)
{
    HashMap<String, String> map = mapENZD.getOrDefault(pkey, null);
    if(map==null)
    {
        map=new  HashMap<String, String>();
        mapENZD.put(pkey, map);   
    }
    map.put(name, value);
    map = mapZNZD.getOrDefault(pkey, null);
    if(map==null)
    {
        map=new  HashMap<String, String>();
        mapENZD.put(pkey, map);   
    }
    map.put(value, name);
}

/**
 * 
 * @Title: putPermissions   
 * @Description: 添加表权限  
 * @param userid
 * @param permissionsName
 * @param table    
 * void
 */
public void putPermissions(String userid,String permissionsName,String table)
{
    HashMap<String, List<String>> map = mapPermissions.getOrDefault(userid, null);
    if(map==null)
    {
        map=new  HashMap<String, List<String>>();
        mapPermissions.put(userid, map);
    }
   List<String>  tables=map.getOrDefault(permissionsName, null);
   if(tables==null)
   {
       tables=new ArrayList<String>();
       map.put(permissionsName, tables);
   }
   if(!tables.contains(table))
   {
       tables.add(table);
   }
    
}

/**
 * 
* @Title: setDBType
* @Description: 设置数据库配置信息
* @param @param map    参数
* @return void    返回类型
 */
public void setDBType(HashMap<String,String> map)
{
    mapDB.putAll(map);
}
/**
 * 
 * @Title: do_ZN   
 * @Description:转中文   
 * @param name
 * @return    
 * String
 */
public String do_ZN(String name)
{
    return mapZN.getOrDefault(name, name);
}

/**
 * 
 * @Title: do_EN   
 * @Description: 转英文   
 * @param name 中文名称
 * @return    
 * String
 */
public String do_EN(String name)
{
    return mapCN.getOrDefault(name, name);
}

/**
 * 
 * @Title: do_EN_Value   
 * @Description: 字典值类转换（DW-01-XX单位）
 * @param pk
 * @param value
 * @return    
 * String
 */
public String do_EN_Value(String pk,String value)
{
    HashMap<String,String> map= mapENZD.getOrDefault(pk, null);
    if(map==null)
    {
        return pk;
    }
    else
    {
      return  map.getOrDefault(value, value);
    }
}

/**
 * 
 * @Title: do_ZN_Value   
 * @Description:  字典值类转换（单位-XX单位-01）
 * @param pk
 * @param value
 * @return    
 * String
 */
public String do_ZN_Value(String pk,String value)
{
    HashMap<String,String> map= mapZNZD.getOrDefault(pk, null);
    if(map==null)
    {
        return pk;
    }
    else
    {
      return  map.getOrDefault(value, value);
    }
}

/**
 * 
 * @Title: do_dictionary   
 * @Description: 包括字典
 * @param cloumn
 * @return    
 * boolean
 */
public boolean do_dictionary(String cloumn)
{
    
    return mapENZD.containsKey(do_ZN(cloumn));
}

/**
 * 
 * @Title: do_Data_Permissions   
 * @Description: 返回权限  
 * @param userName
 * @param lst
 * @return    
 * boolean
 */
public boolean do_Data_Permissions(String userName,List<DataPermissions> lst)
{
    if(lst==null||lst.isEmpty())
    {
        return true;//不需要任何权限
    }
    else
    {
       String userid=mapUserInfo.getOrDefault(userName, userName);
       if(userid==null)
       {
           return false;
       }
       else
       {
            HashMap<String, List<String>> map = mapPermissions.getOrDefault(userid, null);
             if(map==null)
             {
                 return false;
             }
             else
             {
                 int size=lst.size();
                 for(int i=0;i<size;i++)
                 {
                     //遍历需要的权限
                     DataPermissions pm=lst.get(i);
                     List<String> list= map.getOrDefault(pm.permissions.name(), null);
                     if(list==null)
                     {
                         return false;
                     }
                     else
                     {
                       //遍历查找条件
                        if(!pm.tables.isEmpty())
                        {
                            if(!list.containsAll(pm.tables))
                            {
                                return false;//只有存在没有权限的情况就返回
                            }
                        }
                     }
                 }
             }
       }
    }
    return true;
}

public String getModulDB(String name)
{
    return mapDB.getOrDefault(name, defaultDB);
}
}
