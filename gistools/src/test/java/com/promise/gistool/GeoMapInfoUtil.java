package com.promise.gistool;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.geotools.data.MapInfoFileReader;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.opengis.geometry.DirectPosition;

/**  
 * 功能描述:mapinfo功能包
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年11月10日 下午3:07:17  
 */
public class GeoMapInfoUtil {

    public void readMapInfoFile(File tabfile){
        try {
            MapInfoFileReader mfr = new MapInfoFileReader(tabfile);
            List<MappedPosition> list = mfr.getControlPoints();
            for(MappedPosition mp:list){
                DirectPosition dp = mp.getSource();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
