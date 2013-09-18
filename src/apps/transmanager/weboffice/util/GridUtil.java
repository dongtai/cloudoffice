package apps.transmanager.weboffice.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;

public class GridUtil {


	/**
	 * 获取Grid请求参数(分页，排序参数要求必有其一)
	 * @param request 请求参数
	 * @return 参数
	 */
	public static Map<String, Object> getGridParamMap(HttpServletRequest request) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		Page page = new Page();
		int toPage = 1;
		int pageSize = page.getPageSize();
        if(null!=request.getParameter("page"))
		toPage = Integer.parseInt(request.getParameter("page"));
        if(null!=request.getParameter("rows"))
		pageSize =Integer.parseInt(request.getParameter("rows"));
		String sortName = request.getParameter("sidx");
		if(sortName==null || "".equals(sortName))
			sortName="id";
		String sort = request.getParameter("sord");
		if(sort==null || "".equals(sortName))
			sort= PageConstant.DESC;
		page.setPageSize(pageSize);
		page.setCurrentRecord((toPage-1)*pageSize);
		page.setCurrentPage(toPage);
		paramMap.put("page", page);
		paramMap.put("sortName", sortName);
		paramMap.put("sort", sort);
		return paramMap;
	}
	
	/**
	 * 获取分页辅助类
	 * @param goPage 要跳转的页面
	 * @param size 每页显示页数
	 * @return 分页辅助类
	 */
	public static Page getGridPage(Integer goPage,Integer size) {
		Page page = new Page();
		int toPage = 1;
		int pageSize = page.getPageSize();
		if(goPage!=null)
			toPage = goPage.intValue();
		if(size!=null)
			pageSize = size.intValue();
		page.setPageSize(pageSize);
		page.setCurrentRecord((toPage-1)*pageSize);
		page.setCurrentPage(toPage);
		return page;
	}
	
	
}
