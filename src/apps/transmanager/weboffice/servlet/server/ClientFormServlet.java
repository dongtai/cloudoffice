package apps.transmanager.weboffice.servlet.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.databaseobject.flow.FlowFormValues;
import apps.transmanager.weboffice.databaseobject.flow.FormFields;
import apps.transmanager.weboffice.databaseobject.flow.FormInfo;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;


/**
 * 客户端表单处理
 * @author 李际财
 * 日期：2013-1-19
 */
public class ClientFormServlet extends HttpServlet {
	private String formname = null;
	private Map<String, String> fieldvalue = null;
	
	private JQLServices jqlService = (JQLServices) ApplicationContext.getInstance().getBean(JQLServices.NAME);
	
	protected void service(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		try {
			List<FormInfo> list = jqlService.findAllBySql("select a from FormInfo as a where a.formname = '" + formname + "'");
			FormInfo formInfo = list.get(0);
			
			Set<String> set = fieldvalue.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()) {
				List<FormFields> list1 = jqlService.findAllBySql("select a from FormFields as a where a.formInfo.id = ? and a.fieldname = ?", formInfo.getId(), it.next());
				FormFields formFields = list1.get(0);
				
				FlowFormValues flowFormValues = new FlowFormValues();
				//flowFormValues.setFlowTransForms(flowTransForms);
				flowFormValues.setFormFields(formFields);
				flowFormValues.setFieldvalues(fieldvalue.get(it.next()));
				jqlService.save(flowFormValues);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
}
