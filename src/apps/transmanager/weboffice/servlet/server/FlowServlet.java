package apps.transmanager.weboffice.servlet.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.databaseobject.flow.FlowAllNode;
import apps.transmanager.weboffice.databaseobject.flow.FlowInfo;
import apps.transmanager.weboffice.databaseobject.flow.FlowState;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.UserService;

public class FlowServlet extends HttpServlet {

	/**
	 * 李际财 保存流程
	 * @param req
	 * @param resp
	 * @param jsonParams
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void saveFile(HttpServletRequest req,
			HttpServletResponse resp, HashMap<String, Object> jsonParams)
			throws ServletException, IOException {
		String name = "";
		try
		{
			HashMap<String, Object> param = (HashMap<String, Object>) jsonParams
					.get(ServletConst.PARAMS_KEY);
			String path = (String) param.get("path");
			name = (String) param.get("name");
			String fullPath = path + "/" + name; // user290 2012-04-11
			
			UserService userService = (UserService) ApplicationContext
					.getInstance().getBean(UserService.NAME);
			
			File file = new File(fullPath); // 用于读取文件
			File file1 = new File("temp-" + name); // 用于读出文件，默认目录/war/
	
			String suffix = name.substring(name.indexOf('.')+1);	// 获取文件类型
			if(!suffix.equalsIgnoreCase("xml")) {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				FileOutputStream fos = new FileOutputStream(file1);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				byte[] buffer = new byte[1024];
				while(bis.read(buffer)!=-1) {
					bos.write(buffer);
					buffer = new byte[1024];
				}
				bis.close();
				fis.close();
				bos.close();
				fos.close();
				return;
			}
			else {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				FileWriter fw = new FileWriter(file1);
				BufferedWriter bw = new BufferedWriter(fw);
				String tmp = null;
				while((tmp=br.readLine())!=null) {
					bw.write(tmp);
					bw.newLine();
				}
				br.close();
				fr.close();
				bw.close();
				fw.close();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			
			//先拿文件再解析
			SAXReader sReader = new SAXReader();
			//Document document = sReader.read(new File("D:/workspace/mobilecloud1.0/war/temp-flow.xml"));// 这个路径是项目的/war目录下的路径
			InputStream in = FileReader.class.getResourceAsStream("/war/temp-" + name);//获取相对路径文件
			Document document = sReader.read(in);
			Element root = document.getRootElement();
			
			// flowinfo的存储
			FlowInfo flowinfo = new FlowInfo();
			flowinfo.setFlowname(root.attributeValue("name")); // 流程名称
			Long orgid = (long) 1001;	// 单位编号
			flowinfo.setCompanyid(orgid);	
			jqlService.save(flowinfo);
			Long flowinfoid = flowinfo.getId(); // 获取流程编号id(主键)
			
			FlowAllNode[] flowAllNodes = new FlowAllNode[100];	// 初始化箭头组
			int nCount = 0; // 用于箭头计数
			
			FlowState[] flowStates = new FlowState[100];  // 初始化节点组
			int sCount = 0; // 用于节点计数
			
			Iterator iter = root.elementIterator();
			while(iter.hasNext()) {
				Element child = (Element) iter.next();
				if(child.getName().equals("node")) {
					flowStates[sCount] = new FlowState(); // 先初始化节点
					flowStates[sCount].setFlowinfoid(flowinfoid); // 设置流程编号
					flowStates[sCount].setCompanyid(orgid); // 设置单位编号
					Integer designId = Integer.parseInt(child.attributeValue("id")); // 获取设计时编号designid
					flowStates[sCount].setDisignid(designId.toString());
					String statename = child.attributeValue("name"); // 获取节点name
					flowStates[sCount].setStatename(statename);
					flowStates[sCount].setEndnode(Integer.parseInt(child.attributeValue("end"))); // 设置该节点的endnode为1，标志该节点是整个流程的起始端
					flowStates[sCount].setStartnode(Integer.parseInt(child.attributeValue("start"))); // 设置该节点的startnode为1，标志该节点是整个流程的终止端
					flowStates[sCount].setLeftdot(Integer.parseInt(child.attributeValue("x"))); // 节点的leftdot
					flowStates[sCount].setTopdot(Integer.parseInt(child.attributeValue("y"))); // 节点的topdot
					
					jqlService.save(flowStates[sCount]);// 保存该节点
					
					sCount++;	// 节点数量计数
				}
				else if(child.getName().equals("transition")) {
					flowAllNodes[nCount] = new FlowAllNode();
					flowAllNodes[nCount].setFlowinfoid(flowinfoid); // 设置流程编号
					flowAllNodes[nCount].setCompanyid(orgid); // 设置单位编号
					flowAllNodes[nCount].setDendid(child.attributeValue("target"));// 设置dendid
					flowAllNodes[nCount].setDstartid(child.attributeValue("source"));// 添加dstartid
					flowAllNodes[nCount].setArrowsites(child.attributeValue("path"));// 添加箭头信息x,y;x,y（绝对）
					
					nCount++; // 箭头计数
				}
				else
					continue;
			}
			
			for(int i=0; i<nCount; i++) {
				int sign = 0; // 用于计数，每个flowStates[j]累计通过2次if判断语句，则跳出下面的循环
				for(int j=0; j<sCount; j++) {
					if(flowStates[j].getDisignid().equals(flowAllNodes[i].getDstartid())) {// 如果节点是箭头的起始节点
						flowAllNodes[i].setStartstateid(flowStates[j].getId()); // 箭头的startstateid就是该节点的id
						sign++;
						continue;
					}
					if(flowStates[j].getDisignid().equals(flowAllNodes[i].getDendid())) { // 如果节点是箭头的终止节点
						flowAllNodes[i].setEndstateid(flowStates[j].getId()); // 箭头的endstateid就是该节点的id
						sign++;
						continue;
					}
					if(sign == 2)
						break;
				}
				jqlService.save(flowAllNodes[i]); // 保存箭头
			}
	
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
