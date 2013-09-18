package test.weboffice.share;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;



public class TestWebServiceClient {
         public static void main(String[] args) {
        	 try
        	 {
                   JaxWsProxyFactoryBean svr = new JaxWsProxyFactoryBean();
                   svr.setServiceClass(TestWebService.class);
//                   svr.setAddress("http://demo.epoint.com.cn:9090/wxfgsp/EpointWebAudit/WebService/AuditOA.asmx?wsdl");
                   svr.setAddress("http://192.168.1.252/services/AuditOA?wsdl");
                   TestWebService auditOA = (TestWebService) svr.create();
//                   String hello=auditOA.HelloWorld();
//                   System.out.println("==============="+hello);
                   String back=auditOA.CheckUser("admin", "11111");
                   System.out.println("==============="+back);
//                   String back2=auditOA.getAllNewsWaitHandle("admin");
//                   System.out.println("==============="+back2);
        	 }
        	 catch (Exception e)
        	 {
        		 e.printStackTrace();
        	 }
        	 System.exit(0);
         }
}
