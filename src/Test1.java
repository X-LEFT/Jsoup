package day18;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Test2 {

   public static void main(String[] args) throws Exception {
      String host = "www.tedu.cn";
      Socket s = new Socket(host, 80);
      System.out.println("已连接");
      OutputStream out = s.getOutputStream();
      String http = "GET / HTTP/1.1\n"+
             "Host: "+host+"\n"+
             "Connection: keep-alive\n"+
             "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36\n"+
             "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n"+
             "Accept-Language: zh-CN,zh;q=0.9\n\n";
      out.write(http.getBytes());
      out.flush();
      System.out.println("已发送");
      BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
      String line;
      while((line = in.readLine()) != null) {
          System.out.println(line);
      }
   }
}
