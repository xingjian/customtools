/**@Title: TableModel2Shape.java @author promisePB xingjian@yeah.net @date 2010-12-7 上午11:07:41 */
package com.promise.cn.postgis;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**   
 * @Title: TableModel2Shape.java 
 * @Package com.xingjian.cn.tools.gis 
 * @Description: 自定义tablemodel
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-7 上午11:07:41 
 * @version V1.0   
 */
@SuppressWarnings("all")
public class TableModel2Shape extends AbstractTableModel{

	private static final long serialVersionUID = -7495940408592595397L;
    private Vector content = null;
    private String[] title_name={"操作","Shape文件路径","生成表名","编码"};

    public TableModel2Shape() {
        content = new Vector();
    }

    public TableModel2Shape(int count) {
        content = new Vector(count);
    }

    public void addRow(String path, boolean selected, String code,String tableName) {
        Vector v = new Vector(4);
        v.add(0, new Boolean(selected));
        v.add(1, path);
        v.add(2, tableName);
        v.add(3, code);
        content.add(v);
    }

    public void removeRow(int row) {
        content.remove(row);
    }

    public void removeRows(int row, int count) {
        for (int i = 0; i < count; i++) {
            if (content.size() > row) {
                content.remove(row);
            }
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
    * 使修改的内容生效
    */
    public void setValueAt(Object value, int row, int col) {
        ((Vector) content.get(row)).remove(col);
        ((Vector) content.get(row)).add(col, value);
        this.fireTableCellUpdated(row, col);
    }

    public String getColumnName(int col) {
        return title_name[col];
    }

    public int getColumnCount() {
        return title_name.length;
    }

    public int getRowCount() {
        return content.size();
    }

    public Object getValueAt(int row, int col) {
        return ((Vector) content.get(row)).get(col);
    }

    /**
    * 返回数据类型
    */
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

}

