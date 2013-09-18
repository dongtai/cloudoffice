package apps.moreoffice.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * 网络office为外界提供同步用户及组织结构的webservice接口。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@WebService
public interface OrgWebServices
{
		
	@WebMethod
	int CA_UpdateForSame (String xmlString, String strTrustId);
		
	
}
