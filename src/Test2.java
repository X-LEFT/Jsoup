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
		// ���������ص�html����,������,�����һ����״�ṹ
		// Documentʵ���������ĸ��ڵ�
		Document doc =
		 Jsoup.connect("https://item.jd.com/100002795959.html").get();
		
		//��cssѡ����ѡ�� <div class="sku-name"> Ԫ��
		Element e = doc.selectFirst("div.sku-name");
		
		String title = e.text();//��Ԫ��ȡ���ڲ����ı�
		System.out.println(title);
	}
	
	@Test
	public void test4() throws Exception {
		//�����Ʒ����
		String title = getTitle("https://item.jd.com/100002795959.html");
		System.out.println(title);
	}

	///********************************************************************
	//��ָ������Ʒ��ַ,��ȡ��Ʒ����
	private String getTitle(String url) throws Exception {
		return Jsoup.connect(url).get().selectFirst("div.sku-name").text();
	}
	///********************************************************************
	
	
	@Test
	public void test5() throws Exception {
		//�����Ʒ�۸�
		double price = getPrice("100002795959");
		System.out.println(price);
	}

	///********************************************************************
	//��ָ������Ʒ��ַ,��ȡ��Ʒ�۸�
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
		
		//ignoreContentType(true) ��Jsoup������������,��Ҫ��html����������
		String p = Jsoup.connect(url).userAgent(userAgent).ignoreContentType(true).execute().body();
		//System.out.println("----"+p+"----");
		//��jackson api����json
		ObjectMapper m = new ObjectMapper();
		//TypeReference --- ���������ڲ����﷨,��ָ��json�����ʲô���͵Ķ���
		List<Map<String,String>> list = 
		 m.readValue(p, new TypeReference<List<Map<String,String>>>() {});
		
		//��ȡ�����еĵ�һ��map����, �ٴ�map��ȡp���Ե�ֵ
		String s = list.get(0).get("p");
		
		
		return Double.parseDouble(s);
	}
	///********************************************************************
	
	
	@Test
	public void test6() throws Exception {
		//�����Ʒ����
		String desc = getDesc("100002795959");
		System.out.println(desc);
	}

	///********************************************************************
	//��Ʒ����
	private String getDesc(String id) throws Exception {
		String url = "http://d.3.cn/desc/"+id;
		String userAgent = "Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12";
		
		//����user-agentЭ��ͷ����,��ƭ������,�Լ�������������������
		String s = Jsoup.connect(url).userAgent(userAgent).ignoreContentType(true).execute().body();
		// "showdesc(aaaaaa)" ------> "aaaaaa"
//		System.out.println(s);
//		if (s.length()-1 == -10) {
//			System.exit(0);
//		}
		s = s.substring(9, s.length()-1);
		
		// {"date":123,  "content":"��������"}
		// ��json��ȡ��������
		ObjectMapper m = new ObjectMapper();
		Map map = m.readValue(s, Map.class);
		return (String) map.get("content");
	}
	///********************************************************************
	
	@Test
	public void test7() throws Exception {
		//�����Ʒ����
		List<String> list = getFLList();
		for (String s : list) {
			System.out.println(s);
		}
		System.out.println(list.size());
	}

	///********************************************************************
	//��Ʒ����
	private List<String> getFLList() throws Exception {
		String url = "https://www.jd.com/allSort.aspx";
		Elements as = Jsoup.connect(url).get().select("dl.clearfix dd a");
		
		ArrayList<String> list = new ArrayList<String>();
		for (Element a : as) {
			// <a href="���ӵ�ַ">
			String link = "http:"+a.attr("href");
			//ֻ���������ķ���,����������ҳ��,Ƶ��ҳ����Ե�
			if (link.startsWith("http://list.jd.com")) {
				list.add(link);
			}
		}
		return list;
	}
	///********************************************************************
	
	@Test
	public void test8() throws Exception {
		//ĳһ����������ҳҳ��
		int maxPage = getMaxPage("http://list.jd.com/list.html?cat=6144,12042,12055");
		System.out.println(maxPage);
	}

	///********************************************************************
	//��ȡָ����������ҳ��
	private int getMaxPage(String url) throws Exception {
		String s = Jsoup.connect(url).get().selectFirst("div.f-pager i").text();
		return Integer.parseInt(s);
	}
	///********************************************************************
	
	@Test
	public void test9() throws Exception {
		//ĳһ����������ҳ�ĵ�ַ
		List<String> list = 
		 getAllPageLink("http://list.jd.com/list.html?cat=6144,12042,12055");
		
		for (String s : list) {
			System.out.println(s);
		}
	}

	///********************************************************************
	//���ָ����������ҳ�ĵ�ַ
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
	//���һҳ�е� 60 ����Ʒ�ĵ�ַ
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
		//���з���
		List<String> list = getFLList();
		for (String s : list) {			
			handleFL(s);//����һ������
		}
	}
	private void handleFL(String url) throws Exception {
		//�����������ҳ�ĵ�ַ
		List<String> list = getAllPageLink(url);
		for (String s : list) {			
			handlePage(s);//����һ��ҳ
		}
	}
	private void handlePage(String url) throws Exception {
		//�����һҳ��Ʒ�ĵ�ַ
		List<String> list = getItemLinkList(url);
		for (String s : list) {			
			handleItem(s);//������һ����Ʒ
		}
	}
	private void handleItem(String url) throws Exception {
		try {
			String title = getTitle(url);//��ȡ����
			
			//��url��ȡid
			//https://item.jd.com/55718657220.html ------> 55718657220
			String id = 
					url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));			
			
			double price = getPrice(id);//��ȡ�۸�
			String desc = getDesc(id);//��ȡ����
			
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




















