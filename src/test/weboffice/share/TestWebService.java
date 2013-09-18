package test.weboffice.share;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;


@WebService
public interface TestWebService {

    //@WebMethod(operationName = "CheckUser", action = "http://tempuri.org/CheckUser")
    public String CheckUser(
        String userGuid,
        String uassword
    );
}
