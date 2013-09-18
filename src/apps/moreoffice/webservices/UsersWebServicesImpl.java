package apps.moreoffice.webservices;

import java.io.StringReader;
import java.util.Iterator;

import javax.jws.WebService;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

@WebService
public class UsersWebServicesImpl implements UsersWebServices{
	public UsersWebServicesImpl()
	{
		System.out.println("UsersWebServicesImpl===================");
	}
	public String synchroUser(String xmlstr)
	{
		parser4jXml(xmlstr);
		 return "<?xml version=\"1.0\" encoding=\"GBK\"?>"
				 +"<result>"
				 +"<code>0</code>"//结果码：0：成功1：失败
				 +"<message>用户同步成功</message>"//结果描述：成功或失败原因
				 +"</result>";
	}
	public void parser4jXml(String xmlstr) 
	 {
		 SAXReader saxReader = new SAXReader();
		 try {
			 Document document = saxReader.read(new StringReader(xmlstr));
			 Element message=document.getRootElement();
			 System.out.println("message==="+message.getName());
			 for(Iterator i = message.elementIterator(); i.hasNext();){
				 Element messageHoB = (Element) i.next();
				 System.out.println("messageHead==="+messageHoB.getName());
				 for(Iterator j = messageHoB.elementIterator(); j.hasNext();){
					 Element entry=(Element) j.next();
					 if ("OperateType".equals(entry.getName()))
					 {
						 System.out.println("OperateType==="+entry.getText());//操作类型,与entry平级
					 }
					 if (entry.hasContent())
					 {
						 for(Iterator n = entry.elementIterator(); n.hasNext();){
							 Element subnode=(Element) n.next();
							 if ("dlmm".equals(subnode.getName()))
							 {
								 System.out.println("登录密码======="+subnode.getText());
							 }
							 else if ("dzyj".equals(subnode.getName()))
							 {
								 System.out.println("电子邮件======="+subnode.getText());
							 }
							 else if ("bz".equals(subnode.getName()))
							 {
								 System.out.println("备注======="+subnode.getText());
							 }
							 else if ("xm".equals(subnode.getName()))
							 {
								 System.out.println("姓名======="+subnode.getText());
							 }
							 else if ("dlzh".equals(subnode.getName()))
							 {
								 System.out.println("登录账号======="+subnode.getText());
							 }
							 else if ("xb".equals(subnode.getName()))
							 {
								 System.out.println("性别======="+subnode.getText());
							 }
							 else if ("yhlx".equals(subnode.getName()))
							 {
								 System.out.println("用户类型======="+subnode.getText());
							 }
							 else if ("yhjb".equals(subnode.getName()))
							 {
								 System.out.println("所属单位编码======="+subnode.getText());
							 }
							 else if ("yhjb".equals(subnode.getName()))
							 {
								 System.out.println("用户级别======="+subnode.getText());
							 }
							 else if ("ykjdx".equals(subnode.getName()))
							 {
								 System.out.println("云空间大小======="+subnode.getText());
							 }
							 else if ("pxh".equals(subnode.getName()))
							 {
								 System.out.println("排序号======="+subnode.getText());
							 }
							 else if ("yhid".equals(subnode.getName()))
							 {
								 System.out.println("用户ID======="+subnode.getText());
							 }
							 else if ("lxdh".equals(subnode.getName()))
							 {
								 System.out.println("联系电话======="+subnode.getText());
							 }
							 else if ("jh".equals(subnode.getName()))
							 {
								 System.out.println("警号======="+subnode.getText());
							 }
							 else if ("yhzt".equals(subnode.getName()))
							 {
								 System.out.println("用户状态======="+subnode.getText());
							 }
							 else if ("tbsj".equals(subnode.getName()))
							 {
								 System.out.println("同步时间======="+subnode.getText());
							 }
						 }
					 }
//					 System.out.println(node.getName()+":"+node.getText());
				 }
			 }
		 } catch (DocumentException e) {
			 System.out.println(e.getMessage());
		 }
		 System.out.println("dom4j parserXml");
//		 +"<yhysqjs>用户有效期结束</yhysqjs>"
//			+"<dlmm>登录密码</dlmm>"
//			+"<dzyj>电子邮件</dzyj>"
//			+"<bz>备注</bz>"
//			+"<xm>姓名</xm>"
//			+"<dlzh>登录账号</dlzh>"
//			+"<sfzh>身份证号</sfzh>"
//			+"<xb>性别</xb>"
//			+"<yhlx>用户类型</yhlx>"//警员/新警/单位用户/非警员/兼职人员
//			+"<ssdw>所属单位编码</ssdw>"
//			+"<mmyxqks>密码有效期开始</mmyxqks>"
//			+"<xmjp>姓名简拼</xmjp>"
//			+"<rzrq>入职日期</rzrq>"
//			+"<yhjb>用户级别</yhjb>"
//			+"<xgr>修改人</xgr>"
//			+"<xgsj>修改时间</xgsj>"
//			+"<zhdlsj>最后登录时间</zhdlsj>"
//			+"<ztbgsj>状态变更时间</ztbgsj>"
//			+"<yhsj>用户上级与用户表关联</yhsj>"
//			+"<ykjdx>云空间大小</ykjdx>"
//			+"<rybh>人员编号</rybh>"
//			+"<yhyxqks>用户有效期开始</yhyxqks>"
//			+"<pxh>排序号</pxh>"
//			+"<mmyxqjs>密码有效期结束</mmyxqjs>"
//			+"<xsxlh>显示顺序号</xsxlh>"
//			+"<ipqx>IP地址权限验证</ipqx>"
//			+"<yhid>用户ID</yhid>"
//			+"<djsj>登记时间</djsj>"
//			+"<yhdlr>用户代理人与用户表关联</yhdlr>"
//			+"<lxdh>联系电话</lxdh>"
//			+"<djr>登记人</djr>"
//			+"<jh>警号</jh>"
//			+"<yhzt>用户状态</yhzt>"
//			+"<ssdwmc>所属单位名称</ssdwmc>"
//			+"<tbsj>同步时间</tbsj>"
	 }
}
