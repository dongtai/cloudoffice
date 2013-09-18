package apps.moreoffice.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface UsersWebServices {

	@WebMethod
	public String synchroUser(String xmlstr);
}
