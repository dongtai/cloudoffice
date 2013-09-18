package apps.transmanager.weboffice.servlet.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.databaseobject.flow.FormFields;
import apps.transmanager.weboffice.databaseobject.flow.FormInfo;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;

/**
 * 用于处理eio所生成的表单的处理
 * @author 李际财
 * 日期：2013-1-29
 */
public class EioformServlet extends AbstractServlet {

	private Object[] formParams = null;// 表单信息：name、type、path；
	private ArrayList<Object[]> fieldParams = null;// 控件信息：name、id、type、valueType、ismust；
	
	private JQLServices jqlService = (JQLServices) ApplicationContext.getInstance().getBean(JQLServices.NAME);

	public EioformServlet() {
		
	}
	
	public EioformServlet(Object[] formParams, ArrayList<Object[]> fieldParams) {
		this.formParams = formParams;
		this.fieldParams = fieldParams;
	}
	
	@Override
	protected String handleService(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, Object> params)
			throws ServletException, Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 保存表单
	 */
	public void save() {
		try {
			// 保存forminfo信息
			int sign = 0; // 标记是否已存在该表单
			String sql = "select count(*) from FormInfo where formname=? and formtype=?";
			FormInfo formInfo = new FormInfo();
			Long hadnum = (Long)jqlService.getCount(sql, formParams[0].toString(), formParams[1].toString());
			if(hadnum==1) {
				List<FormInfo> list = jqlService.findAllBySql("select a from FormInfo as a where a.formname=? and a.formtype=?", formParams[0].toString(), formParams[1].toString());
				formInfo = list.get(0);
				sign = 1;
			}
			else {
				formInfo.setFormname(formParams[0].toString());
				formInfo.setFormtype(formParams[1].toString());
				//formInfo.setUserid();
				formInfo.setFormpath(formParams[2].toString());
				
				formInfo.setCreatetime(new Date());
				jqlService.save(formInfo);
			}
			
			// 保存formfields信息
			if(sign == 0) {
				for(int i=0; i<fieldParams.size(); i++) {
					FormFields formFields = new FormFields();
					Object[] obs = fieldParams.get(i);
					for(i=0; i<obs.length; i++) {
						formFields.setFormInfo(formInfo);
						formFields.setFieldname(obs[0].toString());
						formFields.setFieldid(obs[1].toString());
						formFields.setFieldtype(getFieldTypeId(obs[2].toString()));
						//formFields.setFieldvaluetype(obs[3]);
						formFields.setIsmust(getIsmustId(obs[4].toString()));
						
						jqlService.save(formFields);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取控件的类型id
	 */
	private Integer getFieldTypeId(String type) {
		if(type.equalsIgnoreCase("text"))
			return 0;
		else if(type.equalsIgnoreCase("textarea"))
			return 1;
		else if(type.equalsIgnoreCase("label"))
			return 2;
		else if(type.equalsIgnoreCase("button")||type.equalsIgnoreCase("submit")||type.equalsIgnoreCase("reset"))
			return 3;
		else if(type.equalsIgnoreCase("select-one"))
			return 4;
		else
			return null;
	}
	
	/**
	 * 获取是否必须填写
	 */
	private Integer getIsmustId(String ismust) {
		return ismust.equalsIgnoreCase("true") ? 1 : 0;
	}
	
	public Long getFormTypeId(String type) {
		return (long) 1;	
	}
	
	public Object[] getFormParams() {
		return formParams;
	}

	public void setFormParams(Object[] formParams) {
		this.formParams = formParams;
	}

	public ArrayList<Object[]> getFieldParams() {
		return fieldParams;
	}

	public void setFieldParams(ArrayList<Object[]> fieldParams) {
		this.fieldParams = fieldParams;
	}
	
	public JQLServices getJqlService() {
		return jqlService;
	}

	public void setJqlService(JQLServices jqlService) {
		this.jqlService = jqlService;
	}

}
