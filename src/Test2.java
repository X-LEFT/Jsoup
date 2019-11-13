package day18;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test3 {
	@Test
	public void test1() throws Exception {
		//  http://www.tedu.cn
		String html = Jsoup.connect("http://www.tedu.cn").execute().body();
		System.out.println(html);
	}
	
	@Test
	public void test2() throws Exception {
		String html = Jsoup.connect("https://item.jd.com/100002795959.html").execute().body();
		System.out.println(html);
	}
	
	/*
	 *        <a k1="v1" k2="v2" k3="v3">
	 * 
	 * 
	 * 
	 *        / ----------------------------- Document
	 *         |- <html> -------------------- Element
	 *             |- <head> ---------------- Element
	 *             |- <body> ---------------- Element
	 *                 |- <div> ------------- Element
	 *                 |- <div class="sku-name"> ------------- Element
	 *                     |- ....
	 *                 |- <div>
	 *                      |- k1="v1" ------ Attribute
	 *                      |- k2="v2" ------ Attribute
	 */
	@Test
	public void test3() throws Exception {
		// 服务器返回的html代码,被解析,处理成一个树状结构
		// Document实例就是树的根节点
		Document doc =
		 Jsoup.connect("https://item.jd.com/100002795959.html").get();
		
		//用css选择器选择 <div class="sku-name"> 元素
		Element e = doc.selectFirst("div.sku-name");
		
		String title = e.text();//从元素取出内部的文本
		System.out.println(title);
	}
	
	@Test
	public void test4() throws Exception {
		//获得商品标题
		String title = getTitle("https://item.jd.com/100002795959.html");
		System.out.println(title);
	}

	///********************************************************************
	//从指定的商品地址,获取商品标题
	private String getTitle(String url) throws Exception {
		return Jsoup.connect(url).get().selectFirst("div.sku-name").text();
	}
	///********************************************************************
	
	
	@Test
	public void test5() throws Exception {
		//获得商品价格
		double price = getPrice("100002795959");
		System.out.println(price);
	}

	///********************************************************************
	//从指定的商品地址,获取商品价格
	/*
	 * [ 
	 *   {
	 *    "cbf":"0",
	 *    "id":"J_100002795959",
	 *    "m":"9999.00",
	 *    "op":"5488.00",
	 *    "p":"4988.00"
	 *   }
	 * ]
	 */
	private double getPrice(String id) throws Exception {
		String url = "https://p.3.cn/prices/mgets?skuIds="+id;
		String userAgent = "Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12";
		
		//ignoreContentType(true) 让Jsoup忽略内容类型,不要按html来处理数据
		String p = Jsoup.connect(url).userAgent(userAgent).ignoreContentType(true).execute().body();
		//System.out.println("----"+p+"----");
		//用jackson api处理json
		ObjectMapper m = new ObjectMapper();
		//TypeReference --- 利用匿名内部类语法,来指把json处理成什么类型的对象
		List<Map<String,String>> list = 
		 m.readValue(p, new TypeReference<List<Map<String,String>>>() {});
		
		//获取集合中的第一个map对象, 再从map获取p属性的值
		String s = list.get(0).get("p");
		
		
		return Double.parseDouble(s);
	}
	///********************************************************************
	
	
	@Test
	public void test6() throws Exception {
		//获得商品详情
		String desc = getDesc("100002795959");
		System.out.println(desc);
	}

	///********************************************************************
	//商品详情
	private String getDesc(String id) throws Exception {
		String url = "http://d.3.cn/desc/"+id;
		String userAgent = "Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12";
		
		//设置user-agent协议头属性,欺骗服务器,自己是浏览器在请求服务器
		String s = Jsoup.connect(url).userAgent(userAgent).ignoreContentType(true).execute().body();
		// "showdesc(aaaaaa)" ------> "aaaaaa"
//		System.out.println(s);
//		if (s.length()-1 == -10) {
//			System.exit(0);
//		}
		s = s.substring(9, s.length()-1);
		
		// {"date":123,  "content":"详情内容"}
		// 从json提取详情内容
		ObjectMapper m = new ObjectMapper();
		Map map = m.readValue(s, Map.class);
		return (String) map.get("content");
	}
	///********************************************************************
	
	@Test
	public void test7() throws Exception {
		//获得商品分类
		List<String> list = getFLList();
		for (String s : list) {
			System.out.println(s);
		}
		System.out.println(list.size());
	}

	///********************************************************************
	//商品分类
	private List<String> getFLList() throws Exception {
		String url = "https://www.jd.com/allSort.aspx";
		Elements as = Jsoup.connect(url).get().select("dl.clearfix dd a");
		
		ArrayList<String> list = new ArrayList<String>();
		for (Element a : as) {
			// <a href="链接地址">
			String link = "http:"+a.attr("href");
			//只处理正常的分类,其他的主题页面,频道页面忽略掉
			if (link.startsWith("http://list.jd.com")) {
				list.add(link);
			}
		}
		return list;
	}
	///********************************************************************
	
	@Test
	public void test8() throws Exception {
		//某一个分类的最大页页号
		int maxPage = getMaxPage("http://list.jd.com/list.html?cat=6144,12042,12055");
		System.out.println(maxPage);
	}

	///********************************************************************
	//获取指定分类的最大页号
	private int getMaxPage(String url) throws Exception {
		String s = Jsoup.connect(url).get().selectFirst("div.f-pager i").text();
		return Integer.parseInt(s);
	}
	///********************************************************************
	
	@Test
	public void test9() throws Exception {
		//某一个分类所有页的地址
		List<String> list = 
		 getAllPageLink("http://list.jd.com/list.html?cat=6144,12042,12055");
		
		for (String s : list) {
			System.out.println(s);
		}
	}

	///********************************************************************
	//获得指定分类所有页的地址
	private List<String> getAllPageLink(String url) throws Exception {
		int maxPage = getMaxPage(url);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= maxPage; i++) {
			list.add(url+"&page="+i);
		}
		return list;
	}
	///********************************************************************
	
	
	@Test
	public void test10() throws Exception {
		List<String> list = getItemLinkList("http://list.jd.com/list.html?cat=6144,12042,12055&page=177");
		for (String s : list) {
			System.out.println(s);
		}
	}

	///********************************************************************
	//获得一页中的 60 件商品的地址
	private List<String> getItemLinkList(String url) throws Exception {
		Elements as = 
		 Jsoup.connect(url).get().select("li.gl-item div.p-name a");
		ArrayList<String> list = new ArrayList<String>();
		for (Element a : as) {
			String link = "http:"+a.attr("href");
			list.add(link);
		}
		return list;
	}
	///********************************************************************


	@Test
	public void test11() throws Exception {
		//所有分类
		List<String> list = getFLList();
		for (String s : list) {			
			handleFL(s);//处理一个分类
		}
	}
	private void handleFL(String url) throws Exception {
		//这个分类所有页的地址
		List<String> list = getAllPageLink(url);
		for (String s : list) {			
			handlePage(s);//处理一个页
		}
	}
	private void handlePage(String url) throws Exception {
		//获得这一页商品的地址
		List<String> list = getItemLinkList(url);
		for (String s : list) {			
			handleItem(s);//处理这一个商品
		}
	}
	private void handleItem(String url) throws Exception {
		try {
			String title = getTitle(url);//获取标题
			
			//从url截取id
			//https://item.jd.com/55718657220.html ------> 55718657220
			String id = 
					url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));			
			
			double price = getPrice(id);//获取价格
			String desc = getDesc(id);//获取详情
			
			System.out.println(title);
			System.out.println(price);
			System.out.println(desc);
			System.out.println("-----------------------------------\n\n");
		} catch (Exception e) {
			//System.out.println(url);
			//System.exit(0);
		}
	}

}




















