/**    
 * 文件名：MsgSerialize.java    
 *    
 * 版本信息：    
 * 日期：2018年8月5日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.io.IOException;

import org.msgpack.MessagePack;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：MsgSerialize    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月5日 下午4:20:01    
 * 修改人：jinyu    
 * 修改时间：2018年8月5日 下午4:20:01    
 * 修改备注：    
 * @version     
 *     
 */
public class MsgSerialize {
   private static MessagePack msgpack = new MessagePack();
   static 
   {
       JSONObject.DEFFAULT_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
       ParserConfig.getGlobalInstance().setAsmEnable(false);
   }
   /**
    * 
    * @Title: JSONSerialize   
    * @Description: JSON序列化
    * @param obj
    * @return    
    * String
    */
public static <T>  String JSONSerialize(T obj)
{
   return JSON.toJSONString(obj);
}


/**
 * 
 * @Title: JSONByteSerialize   
 * @Description: JSON序列化  
 * @param obj
 * @return    
 * byte[]
 */
public static <T>  byte[] JSONByteSerialize(T obj)
{
   return JSON.toJSONBytes(obj, SerializerFeature.WriteMapNullValue);
}

/**
 * 
 * @Title: JSONDeSerialize   
 * @Description: JSON反序列化
 * @param json
 * @param clazz
 * @return    
 * T
 */
public static <T> T   JSONDeSerialize(String json,Class<T> clazz)
{
    return JSON.parseObject(json, clazz);
}

/**
 * 
 * @Title: JSONByteDeSerialize   
 * @Description: JSON反序列化   
 * @param json
 * @param clazz
 * @return    
 * T
 */
public static <T> T   JSONByteDeSerialize(byte[] json,Class<T> clazz)
{
    return JSON.parseObject(json, clazz);
}

/**
 * 
 * @Title: Serialize   
 * @Description:二进制序列化
 * @param obj
 * @return    
 * byte[]
 */
public static <T>  byte[]  Serialize(T obj)
{
    try {
       return msgpack.write(obj);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
    
}

/**
 * 
 * @Title: DeSerialize   
 * @Description: 二进制反序列化
 * @param data
 * @param clazz
 * @return    
 * T
 */
public static <T>  T  DeSerialize(byte[]data,Class<T> clazz)
{
    try {
        return msgpack.read(data, clazz);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}


}
