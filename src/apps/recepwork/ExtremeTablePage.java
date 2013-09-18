package apps.recepwork;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.context.HttpServletRequestContext;
import org.extremecomponents.table.limit.Filter;
import org.extremecomponents.table.limit.FilterSet;
import org.extremecomponents.table.limit.Limit;
import org.extremecomponents.table.limit.LimitFactory;
import org.extremecomponents.table.limit.Sort;
import org.extremecomponents.table.limit.TableLimit;
import org.extremecomponents.table.limit.TableLimitFactory;

/**
 * 辅助ExtremeTable获取分页信息的Util类
 *
 * @author calvin
 */
public class ExtremeTablePage
{
	HashMap filterMap = new HashMap();
	HashMap sortMap = new HashMap();
	/**
	 * 从request构造Limit对象实例. Limit的构造流程比较不合理，为了照顾export Excel时忽略信息分页，导出全部数据
	 * 因此流程为程序先获得total count, 再使用total count 构造Limit，再使用limit中的分页数据查询分页数据
	 * 而SS的page函数是在同一步的，无法拆分，再考虑到首先获得的totalCount
	 */
	public Limit getLimit(HttpServletRequest request)
	{
		Context context = new HttpServletRequestContext(request);
		LimitFactory limitFactory = new TableLimitFactory(context);
		TableLimit limit = new TableLimit(limitFactory);
		return limit;
	}

	/**
	 * 将Limit中的排序信息转化为Map{columnName,升序/降序}
	 */
    public String getSort(Limit limit)
	{
    	String temp ="";
		if (limit != null)
		{
			Sort sort = limit.getSort();
			if (sort != null && sort.isSorted())
			{
				temp=" order by "+sort.getProperty()+" "+sort.getSortOrder();
			}
		}
		return temp;
	}

	/**
	 * 将Limit中的过滤信息转化为Map{字段，值}
	 */
    public String getFilter(Limit limit)
	{
    	String temp="";
		if (limit != null)
		{
			FilterSet filterSet = limit.getFilterSet();
			if (!filterSet.isFiltered() || filterSet.isCleared())
			{
				return temp;
			}
			Filter[] filters = filterSet.getFilters();
			for (int i = 0; i < filters.length; i++)
			{
				if(i==0)
				{
					temp=temp+" where ";
				}
				if (filters[i] != null && filters[i].equals(" "))
				{
					temp=temp+filters[i].getProperty()+"="+filters[i].getValue()+" ";
					System.out.println(filters[i].getProperty());
				}
			}
		}
		return temp="";
	}
}