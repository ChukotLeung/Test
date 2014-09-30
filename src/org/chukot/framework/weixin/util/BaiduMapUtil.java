package org.chukot.framework.weixin.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.chukot.framework.weixin.model.baidumap.BaiduPlace;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 百度地图操作类
 * @author chukot
 *
 */
public class BaiduMapUtil {

	public static List<BaiduPlace> searchPlace(String query, String lng, String lat) {
		List<BaiduPlace> placeList = null;
		return placeList;
	}
	
	public static String httpRequest(String requestUrl) {
		StringBuffer buffer = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.connect();
			
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	@SuppressWarnings("unchecked")
	private static List<BaiduPlace> parsePlaceXml(String xml) {
		List<BaiduPlace> placeList = null;
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			Element resultsElement = root.element("results");
			List<Element> resultElementList = resultsElement.elements("result");
			if (resultElementList.size() > 0) {
				placeList = new ArrayList<BaiduPlace>();
				Element nameElement = null, addressElement = null, locationElement = null, telephoneElement = null, detailInfoElement = null, distanceElement = null;
				for (Element resultElement : resultElementList) {
					nameElement = resultElement.element("name");
					addressElement = resultElement.element("address");
					locationElement = resultElement.element("location");
					telephoneElement = resultElement.element("telephone");
					detailInfoElement = resultElement.element("detail_info");
					
					BaiduPlace place = new BaiduPlace();
					place.setName(nameElement.getText());
					place.setAddress(addressElement.getText());
					place.setLng(locationElement.elementText("lng"));
					place.setLat(locationElement.elementText("lat"));
					if (null != telephoneElement) {
						place.setTelephone(telephoneElement.getText());
					}
					if (null != detailInfoElement) {
						distanceElement = detailInfoElement.element("distance");
						if (null != distanceElement) {
							place.setDistance(Integer.parseInt(distanceElement.getText()));
						}
					}
					placeList.add(place);
				}
				Collections.sort(placeList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return placeList;
	}
	
}