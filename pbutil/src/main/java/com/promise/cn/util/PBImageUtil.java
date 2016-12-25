package com.promise.cn.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**  
 * 功能描述: 图片工具类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年11月9日 下午2:40:21  
 */
public class PBImageUtil {

    /**
     * 截图
     * @param srcFile 源图片
     * @param targetFile 截好后图片全名
     * @param startAcross 开始截取位置横坐标
     * @param startEndlong 截取的长
     * @param width 截取的长
     * @param hight 截取的高
     * @param imageFormat jpg,png
     * @throws Exception
     */
    public static String CutImage(String srcFile, String targetFile, int startAcross, int startEndlong, int width, int hight,String imageFormat) throws Exception {  
        //取得图片读入器  
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);  
        ImageReader reader = readers.next();  
        //取得图片读入流  
        InputStream source = new FileInputStream(srcFile);  
        ImageInputStream iis = ImageIO.createImageInputStream(source);  
        reader.setInput(iis, true);  
        //图片参数对象  
        ImageReadParam param = reader.getDefaultReadParam();  
        Rectangle rect = new Rectangle(startAcross, startEndlong, width, hight);  
        param.setSourceRegion(rect);  
        BufferedImage bi = reader.read(0, param);  
        ImageIO.write(bi, targetFile.split("\\.")[1], new File(targetFile)); 
        return "success";
    } 
    
    /**
     * 拼接图片 图片拼接顺序按照二维数组来拼接 目前支持图片大小一样的图片进行拼接
     * @param files 图片的二维数组 
     * @param targetFile 拼接之后的图片位置
     */
    public static String MergeImage(String[][] files,String targetFile) {  
        String result = "";
        int sum =0;//记录长度 
        int xNum = files.length;
        int yNum = 0;
        int xWidthP = 0;
        int yWidthP = 0;
        for(int a = 0 ;a<files.length;a++){//获取行的长度
            if(yNum==0){
                yNum = files[a].length;
            }
            for(int b = 0;b<files[a].length;b++){//获取列的长度  
                sum++;//长度+1  
            }  
        } 
        if (sum <= 1) {  
            result = "拼接的图片数量为1，不需要拼接！";
            return result;
        }  
        File[][] src = new File[xNum][yNum];
        BufferedImage[][] images = new BufferedImage[xNum][yNum];
        int[][][] imageArrays = new int[xNum][yNum][];  
        for (int i = 0; i < xNum; i++) {
            for(int j = 0; j < yNum; j++){
                try {  
                    src[i][j] = new File(files[i][j]);  
                    images[i][j] = ImageIO.read(src[i][j]);
                    int width = images[i][j].getWidth();  
                    int height = images[i][j].getHeight();
                    if(xWidthP==0 ||yWidthP==0){
                        xWidthP = width;
                        yWidthP = height;
                    }
                    
                    imageArrays[i][j] = new int[width * height];  
                    imageArrays[i][j] = images[i][j].getRGB(0, 0, width, height, imageArrays[i][j], 0, width);
                } catch (Exception e) {  
                    throw new RuntimeException(e);  
                }  
            }
        }  
        int newHeight = yWidthP*xNum;  
        int newWidth = xWidthP*yNum;  
        //生成新图片  
        try {  
            BufferedImage imageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);  
            for (int i = 0; i < xNum; i++) {
                for(int j = 0; j < yNum; j++){  
                    imageNew.setRGB(j*yWidthP,xWidthP*i, xWidthP, yWidthP, imageArrays[i][j], 0, images[i][j].getWidth());  
                }
            }  
            //输出想要的图片  
            ImageIO.write(imageNew, targetFile.split("\\.")[1], new File(targetFile));  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        } 
        return result;
    }
    
    /**
     * 覆盖图片
     * @param bigPath 底图
     * @param smallPath 上面图片
     * @param outFile
     * @param alpha 上面图片的透明度
     * @return
     */
    public static String OverlapImage(String bigPath, String smallPath, String outFile,float alpha) {  
        try {  
            BufferedImage big = ImageIO.read(new File(bigPath));  
            BufferedImage small = ImageIO.read(new File(smallPath));  
            Graphics2D g = big.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            int x = (big.getWidth() - small.getWidth()) / 2;  
            int y = (big.getHeight() - small.getHeight()) / 2;  
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);  
            g.dispose();  
            ImageIO.write(big, outFile.split("\\.")[1], new File(outFile));  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        } 
        return "success";
    } 
}
