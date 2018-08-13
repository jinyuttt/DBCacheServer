/**    
 * 文件名：XMLRead.java    
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：XMLRead    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月7日 上午2:33:43    
 * 修改人：jinyu    
 * 修改时间：2018年8月7日 上午2:33:43    
 * 修改备注：    
 * @version     
 *     
 */
public class XMLRead {
    private String fileName="";
    public static String dir="SQLConfig";
    private  Document document = null;
    private  int idNum=0;
    public XMLRead(String file)
    {
        this.fileName=file;
    }
public HashMap<String,String> readALL()
{
    HashMap<String,String>  map=new HashMap<String,String>();
    try
    {
    SAXReader reader = new SAXReader();
    Document document = reader.read(new File(fileName));
    Element root = document.getRootElement();
    Iterator<?> it = root.elementIterator();
    //HashMap<String,String> map=new HashMap<String,String>();
    while (it.hasNext()) {
        Element element = (Element) it.next();
        StringBuffer parent=new StringBuffer();
        StringBuffer bufSql=new StringBuffer();
        parent.append(element.getName());
        getNodes(element,parent,bufSql,map);
        
       }
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
    return map;
}
private void getNodes(Element node,StringBuffer parent,StringBuffer bufSql,Map<String,String> map)
{
     List<Element> lst= node.elements();
     if(lst.isEmpty())
     {
         //说明没有子节点了，生成id
        //   String name=node.getName();
           String id= node.attributeValue("id");
           if(id==null)
           {
               id=String.valueOf(idNum++);
           }
           String sql=node.getText();
           parent.append("_");
          // parent.append(name);
           parent.append("_");
           parent.append("id-");
           parent.append(id);
          // bufSql.append(sql);
           map.put(parent.toString(), sql);
     }
     else
     {
         for(Element e:lst)
         {
             StringBuffer buf=new StringBuffer();
             StringBuffer bufSQL=new StringBuffer();
             buf.append(parent);
             buf.append("_");
             buf.append(e.getName());
             getNodes(e,buf,bufSQL,map);
         }
     }
   
}
public String read(String name,String child,String id)
{
    SAXReader reader = new SAXReader();
    if(document==null)
    {
    try {
        document = reader.read(new File(fileName));
    } catch (DocumentException e) {
        e.printStackTrace();
    }
    }
    Element root = document.getRootElement();
    Element e=root.element(name);
    Element childN= e.element(child);
    return childN.getText();
}

}
