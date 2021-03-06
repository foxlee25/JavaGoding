package httpclient;

import java.net.Socket;
import java.util.Date;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

/**
 * @author springzero E-mail: 464150147@qq.com
 * @version 创建时间：2015年12月29日 下午9:58:30
 * 类说明  get方式请求
 */
public class HttpGet {
	
	public static void main(String[] agrs) throws Exception {
		//声明初始化一个http处理器，方式有些奇怪（第一次遇到这样）
		HttpProcessor httpProc = HttpProcessorBuilder.create()
				.add(new RequestContent())
				.add(new RequestTargetHost())
				.add(new RequestConnControl())
				.add(new RequestUserAgent("HTTP/1.1"))
				.add(new RequestExpectContinue(true)).build();
		
		//声明初始化一个http请求执行器
		HttpRequestExecutor httpExecutor= new HttpRequestExecutor();
		
		//一个又一个不同的声明初始化方式，如果不是官网的文档，我一定会怀疑其合理性。现在怀疑官网水平不够+ +
		//声明初始化一个http核心内容类（感觉解释的勉强）
		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost("localhost",9090);
		coreContext.setTargetHost(host);
		
		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		//翻译过来是重用策略。。。汗  大概意思应该懂了吧
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
		
		try {
			String[] targets = {
					"/", "/", "/", "/", "/",
					"/", "/", "/", "/", "/",
					"/", "/", "/", "/", "/",
					"/", "/", "/", "/", "/",
					"/", "/", "/", "/", "/",
					"/", "/", "/", "/", "/",
					};
			Long startTime = new Date().getTime();
			for(int i = 0; i < targets.length; i++) {
				if(!conn.isOpen()) {
					Socket socket  = new Socket(host.getHostName(), host.getPort());
					conn.bind(socket);
				}
				BasicHttpRequest request = new BasicHttpRequest("GET", targets[i]);
				System.out.println(">> Request URI: " + request.getRequestLine().getUri());
				httpExecutor.preProcess(request, httpProc, coreContext);
				HttpResponse response = httpExecutor.execute(request, conn, coreContext);
				httpExecutor.postProcess(response, httpProc, coreContext);
				
				System.out.println("<< Response: " + response.getStatusLine());
				System.out.println(EntityUtils.toString(response.getEntity()));
				System.out.println("========");
				if(!connStrategy.keepAlive(response, coreContext)) {
					conn.close();
				} else {
					System.out.println("Connection kept alive...");
				}
			}
			Long endTime = new Date().getTime();
			Long time = endTime-startTime;
			System.out.println("Time: " + time);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}
	
}






















