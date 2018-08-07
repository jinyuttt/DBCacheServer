/**    
 * 文件名：DataTable.java    
 *    
 * 版本信息：    
 * 日期：2018年8月6日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.db.jason.DBServer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**    
 *     
 * 项目名称：DBServer    
 * 类名称：DataTable    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年8月6日 上午1:57:32    
 * 修改人：jinyu    
 * 修改时间：2018年8月6日 上午1:57:32    
 * 修改备注：    
 * @version     
 *     
 */
public class FilterTable {
    private static class Sington
    {
        private static FilterTable instance=new FilterTable();
    }
   private static CCJSqlParserManager parser=new CCJSqlParserManager();
   private static TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
    public static FilterTable getInstance()
    {
        return Sington.instance;
    }
    
    /**
     * 
     * @Title: processResult   
     * @Description:处理结果   
     * @param result    
     * void
     */
    private  static void processResult(FilterResult result)
    {
        List<FilterModel> lst=result.lst;
        //
        FilterModel insert=new FilterModel();
        insert.filter=TableType.insert;
        FilterModel delete=new FilterModel();
        delete.filter=TableType.delete;
        FilterModel update=new FilterModel();
        update.filter=TableType.update;
        FilterModel select=new FilterModel();
        select.filter=TableType.select;
        int size=lst.size();
        for(int i=0;i<size;i++)
        {
            FilterModel model= lst.get(i);
            if(model.filter==TableType.insert)
            {
                insert.tables.addAll(model.tables);
            }
            else if(model.filter==TableType.delete)
            {
                delete.tables.addAll(model.tables);
            }
            if(model.filter==TableType.update)
            {
                update.tables.addAll(model.tables);
            }
            if(model.filter==TableType.select)
            {
                select.tables.addAll(model.tables);
            }
        }
        result.lst.clear();
        result.lst.add(select);
        result.lst.add(insert);
        result.lst.add(delete);
        result.lst.add(update);
        
    }
    
    /**
     * 
     * @Title: getColumnName   
     * @Description: 处理参数   
     * @param lstfilter
     * @param expression
     * @param map
     * @param allColumnNames
     * @return    
     * StringBuffer
     */
    private static StringBuffer getColumnName(List<FilterModel> lstfilter,Expression expression,Map<String,Object> map,StringBuffer allColumnNames) {

        String columnName = null;
        if(expression instanceof BinaryExpression){
            //获得左边表达式
            BinaryExpression cur=(BinaryExpression) expression;
            Expression leftExpression = cur.getLeftExpression();
            //如果左边表达式为Column对象，则直接获得列名
            if(leftExpression  instanceof Column){
                //获得列名
                columnName = ((Column) leftExpression).getColumnName();
               // allColumnNames.append(columnName);
                //allColumnNames.append(":");
                //拼接操作符
               // cur.getStringExpression();
               // allColumnNames.append(cur.getStringExpression());
                //allColumnNames.append("-");
                map.put(columnName,  cur.getStringExpression());
            }
            //否则，进行迭代
            else if(leftExpression instanceof BinaryExpression){
                getColumnName(lstfilter,leftExpression,map,allColumnNames);
            }

            //获得右边表达式，并分解
            Expression rightExpression = ((BinaryExpression) expression).getRightExpression();
            if(rightExpression instanceof BinaryExpression){
                BinaryExpression rightE=(BinaryExpression) rightExpression;
                Expression leftExpression2 = rightE.getLeftExpression();
                if(leftExpression2 instanceof Column){
                    //获得列名
                    columnName = ((Column) leftExpression2).getColumnName();
                    map.put(columnName,  rightE.getStringExpression());
                   // allColumnNames.append("-");
                    //allColumnNames.append(columnName);
                   // allColumnNames.append(":");
                    //获得操作符
                    //allColumnNames.append(((BinaryExpression) rightExpression).getStringExpression());
                }
            }
        }
        else if(expression instanceof CaseExpression)
        {
            //获得左边表达式
            CaseExpression cur=(CaseExpression) expression;
            Expression leftExpression = cur.getElseExpression();
            //如果左边表达式为Column对象，则直接获得列名
            if(leftExpression  instanceof Column){
                //map.put(columnName,  cur.);
                //获得列名
//                columnName = ((Column) leftExpression).getColumnName();
//                allColumnNames.append(columnName);
//                allColumnNames.append(":");
//                //拼接操作符
//                allColumnNames.append(((BinaryExpression) expression).getStringExpression());
//                //allColumnNames.append("-");
            }
            //否则，进行迭代
            else if(leftExpression instanceof BinaryExpression){
                getColumnName(lstfilter,leftExpression,map,allColumnNames);
            }

            //获得右边表达式，并分解
            Expression rightExpression = ((BinaryExpression) expression).getRightExpression();
            if(rightExpression instanceof BinaryExpression){
                Expression leftExpression2 = ((BinaryExpression) rightExpression).getLeftExpression();
                if(leftExpression2 instanceof Column){
                 // map.put(columnName,  cur.);
                    //获得列名
//                    columnName = ((Column) leftExpression2).getColumnName();
//                    allColumnNames.append("-");
//                    allColumnNames.append(columnName);
//                    allColumnNames.append(":");
//                    //获得操作符
//                    allColumnNames.append(((BinaryExpression) rightExpression).getStringExpression());
                }
            }
        }
        else if(expression instanceof InExpression)
        {
            InExpression cur=(InExpression) expression;
            //获得左边表达式
            Expression leftExpression = cur.getLeftExpression();
//            //如果左边表达式为Column对象，则直接获得列名
//            if(leftExpression  instanceof Column){
//                //获得列名
//                columnName = ((Column) leftExpression).getColumnName();
//                allColumnNames.append(columnName);
//                allColumnNames.append(":");
//                //拼接操作符
//                allColumnNames.append(((BinaryExpression) expression).getStringExpression());
//                //allColumnNames.append("-");
//            }
//            //否则，进行迭代
//            else if(leftExpression instanceof BinaryExpression){
//                getColumnName((BinaryExpression)leftExpression,allColumnNames);
//            }

            //获得右边表达式，并分解
            ItemsList rightExpression = cur.getRightItemsList();
            if(rightExpression  instanceof SubSelect)
            {
                SubSelect select=(SubSelect)rightExpression;
                PlainSelect pselect=(PlainSelect) select.getSelectBody();
                if(pselect!=null)
                {
                    FilterModel filter=new FilterModel();
                    filter.filter=TableType.select;
                    //FromItem fitem = pselect.getFromItem();
                    filter.tables.add(pselect.getFromItem().toString());
                    //
                    getColumnName(lstfilter,pselect.getWhere(),map,allColumnNames);
                }
               
            }
            else if(rightExpression instanceof ExpressionList)
            {
                ExpressionList rightList=(ExpressionList) rightExpression;
                List<Expression> tmp = rightList.getExpressions();
                String ss=rightList.toString();
                if(ss.contains("?"))
                {
                    map.put(leftExpression.toString(), tmp);
                }
            }
        }
        return allColumnNames;
    }
    /**
     * 
     * @Title: getTableType   
     * @Description: 获取表操作
     * @param sql
     * @return    
     * List<FilterModel>
     */
public FilterResult getTableType(String sql)
{
    FilterResult result=new FilterResult();
    List<FilterModel> list=new ArrayList<FilterModel>();
    LinkedHashMap<String,Object> param=new LinkedHashMap<String,Object>();
    StringBuffer allColumnNames=new StringBuffer();
    Statement stmt = null;
    try {
        stmt = parser.parse(new StringReader(sql));
    } catch (JSQLParserException e) {
      
        e.printStackTrace();
        return result;
    }
  //
  
//    try
//    {
//     Select select = (Select) CCJSqlParserUtil.parse(sql);
//    StringBuilder buffer = new StringBuilder(); 
//    ExpressionDeParser expressionDeParser = new ExpressionDeParser(); 
//    SelectDeParser deparser = new SelectDeParser(expressionDeParser,buffer); 
//    expressionDeParser.setSelectVisitor(deparser); 
//    expressionDeParser.setBuffer(buffer); 
//    PlainSelect plainSelect =  (PlainSelect) select.getSelectBody();
//    expressionDeParser.setSelectVisitor(deparser);
//    plainSelect.getWhere().accept(expressionDeParser);
//    getColumnName(list,plainSelect.getWhere(),param,null);
//    //select.getSelect()
//    //select.accept(deparser);
//    }
//    catch(Exception ex)
//    {
//        ex.printStackTrace();
//    }
    //
if (stmt instanceof Select) {
     Select statement = (Select) stmt;
     FilterModel selectTable=new FilterModel();
     selectTable.filter=TableType.select;
    TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
    List<String> tableList = tablesNamesFinder.getTableList(statement);
    if(tableList!=null)
    {
        selectTable.tables.addAll(tableList);
    }
    list.add(selectTable);
    PlainSelect plainSelect =(PlainSelect) statement.getSelectBody();
    getColumnName(list,plainSelect.getWhere(),param,allColumnNames);
}
if (stmt instanceof Insert) {
    Insert statement = (Insert) stmt;
    FilterModel insertTable=new FilterModel();
    FilterModel selectTable=new FilterModel();
    selectTable.filter=TableType.select;
    insertTable.filter=TableType.insert;
    List<String> tableList = tablesNamesFinder.getTableList(statement);
    result.allTable.addAll(tableList);
   if(tableList!=null)
   {
       insertTable.tables.addAll(tableList);
   }
   Select selectStmt=statement.getSelect();
   List<String> selectTables= tablesNamesFinder.getTableList(selectStmt);
   if(selectTables!=null)
   {
       selectTable.tables.addAll(selectTables);
   }
   if(tableList!=null)
   {
       tableList.removeAll(selectTables);
       insertTable.tables.addAll(tableList);
   }
   list.add(insertTable);
   list.add(selectTable);
   PlainSelect plainSelect =(PlainSelect) selectStmt.getSelectBody();
   getColumnName(list,plainSelect.getWhere(),param,allColumnNames);
}
if (stmt instanceof Update) {
    Update statement = (Update) stmt;
    FilterModel selectTable=new FilterModel();
    selectTable.filter=TableType.select;
    FilterModel updateTable=new FilterModel();
    updateTable.filter=TableType.update;
   TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
   List<String> tableList = tablesNamesFinder.getTableList(statement);
   result.allTable.addAll(tableList);
   Select selectStmt=statement.getSelect();//获取select
   List<String> selectTables=  tablesNamesFinder.getTableList(selectStmt);
   if(selectTables!=null)
   {
       selectTable.tables.addAll(selectTables);
   }
   if(tableList!=null)
   {
       tableList.removeAll(selectTables);
       updateTable.tables.addAll(tableList);
   }
   list.add(updateTable);
   list.add(selectTable);
   PlainSelect plainSelect =(PlainSelect) selectStmt.getSelectBody();
   getColumnName(list,plainSelect.getWhere(),param,allColumnNames);
}
if (stmt instanceof Delete) {
    Delete statement = (Delete) stmt;
    FilterModel deletetTable=new FilterModel();
    deletetTable.filter=TableType.delete;
    List<String> tableList = tablesNamesFinder.getTableList(statement);
    result.allTable.addAll(tableList);
    list.add(deletetTable);
    deletetTable.tables.addAll(tableList);
    Expression where = statement.getWhere();
    getColumnName(list,where,param,allColumnNames);
}
if (stmt instanceof CreateTable) {
    CreateTable statement = (CreateTable) stmt;
    FilterModel createTable=new FilterModel();
    createTable.filter=TableType.create;
    List<String> tableList = tablesNamesFinder.getTableList(statement);
    result.allTable.addAll(tableList);
    createTable.tables.addAll(tableList);
    list.add(createTable);
}
if (stmt instanceof Drop) {
    Drop statement = (Drop) stmt;
    FilterModel dropTable=new FilterModel();
    dropTable.filter=TableType.drop;
    List<String> tableList = tablesNamesFinder.getTableList(statement);
    result.allTable.addAll(tableList);
    dropTable.tables.addAll(tableList);
    list.add(dropTable);
}
if (stmt instanceof Truncate) {
    Truncate statement = (Truncate) stmt;
    FilterModel truncTable=new FilterModel();
    truncTable.filter=TableType.truncate;
    List<String> tableList = tablesNamesFinder.getTableList(statement);
    result.allTable.addAll(tableList);
    truncTable.tables.addAll(tableList);
    list.add(truncTable);
}
result.lst.addAll(list);
result.param.putAll(param);
processResult(result);
return result;
}


}
