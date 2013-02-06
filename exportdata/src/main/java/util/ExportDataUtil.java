/** @文件名: ExportDataUtil.java @创建人：邢健  @创建日期： 2013-2-5 下午1:29:53 */

package util;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

/**   
 * @类名: ExportDataUtil.java 
 * @包名: util 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-5 下午1:29:53 
 * @版本: V1.0   
 */
public class ExportDataUtil {

	public static Font getFont(String fontType,int fontStyle,int fontSize){
		return new Font(fontType,fontStyle,fontSize);
	}
	
	public static void setCenter(Component component){
		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		int x = (int)(toolkit.getScreenSize().getWidth()-component.getWidth())/2; 
		int y = (int)(toolkit.getScreenSize().getHeight()-component.getHeight())/2; 
		component.setLocation(x, y); 
	}
	
	public static Image getImage(String imageURL){
		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		return toolkit.createImage(ExportDataUtil.class.getResource(imageURL));
	}
}
