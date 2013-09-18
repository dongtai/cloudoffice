package apps.moreoffice.ext.biokee.bioplat.demo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.biokee.bioplat.demo package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _Verify_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "verify");
	private final static QName _NVerify_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "nVerify");
	private final static QName _NVerifyResponse_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "nVerifyResponse");
	private final static QName _VerifyResponse_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "verifyResponse");
	private final static QName _Modify_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "modify");
	private final static QName _BindResponse_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "bindResponse");
	private final static QName _GetFingerInfoResponse_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "getFingerInfoResponse");
	private final static QName _Register_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "register");
	private final static QName _RegisterResponse_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "registerResponse");
	private final static QName _GetTokenResponse_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "getTokenResponse");
	private final static QName _Bind_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "bind");
	private final static QName _GetToken_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "getToken");
	private final static QName _GetFingerInfo_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "getFingerInfo");
	private final static QName _ModifyResponse_QNAME = new QName(
			"http://ws.bioplat.biokee.com/", "modifyResponse");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.biokee.bioplat.demo
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Bind }
	 * 
	 */
	public Bind createBind() {
		return new Bind();
	}

	/**
	 * Create an instance of {@link GetFingerInfoResponse }
	 * 
	 */
	public GetFingerInfoResponse createGetFingerInfoResponse() {
		return new GetFingerInfoResponse();
	}

	/**
	 * Create an instance of {@link VerifyResponse }
	 * 
	 */
	public VerifyResponse createVerifyResponse() {
		return new VerifyResponse();
	}

	/**
	 * Create an instance of {@link RegisterResponse }
	 * 
	 */
	public RegisterResponse createRegisterResponse() {
		return new RegisterResponse();
	}

	/**
	 * Create an instance of {@link NVerifyResponse }
	 * 
	 */
	public NVerifyResponse createNVerifyResponse() {
		return new NVerifyResponse();
	}

	/**
	 * Create an instance of {@link GetToken }
	 * 
	 */
	public GetToken createGetToken() {
		return new GetToken();
	}

	/**
	 * Create an instance of {@link GetTokenResponse }
	 * 
	 */
	public GetTokenResponse createGetTokenResponse() {
		return new GetTokenResponse();
	}

	/**
	 * Create an instance of {@link Modify }
	 * 
	 */
	public Modify createModify() {
		return new Modify();
	}

	/**
	 * Create an instance of {@link BindResponse }
	 * 
	 */
	public BindResponse createBindResponse() {
		return new BindResponse();
	}

	/**
	 * Create an instance of {@link Verify }
	 * 
	 */
	public Verify createVerify() {
		return new Verify();
	}

	/**
	 * Create an instance of {@link ModifyResponse }
	 * 
	 */
	public ModifyResponse createModifyResponse() {
		return new ModifyResponse();
	}

	/**
	 * Create an instance of {@link GetFingerInfo }
	 * 
	 */
	public GetFingerInfo createGetFingerInfo() {
		return new GetFingerInfo();
	}

	/**
	 * Create an instance of {@link Register }
	 * 
	 */
	public Register createRegister() {
		return new Register();
	}

	/**
	 * Create an instance of {@link NVerify }
	 * 
	 */
	public NVerify createNVerify() {
		return new NVerify();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Verify }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "verify")
	public JAXBElement<Verify> createVerify(Verify value) {
		return new JAXBElement<Verify>(_Verify_QNAME, Verify.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link NVerify }{@code
	 * >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "nVerify")
	public JAXBElement<NVerify> createNVerify(NVerify value) {
		return new JAXBElement<NVerify>(_NVerify_QNAME, NVerify.class, null,
				value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link NVerifyResponse }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "nVerifyResponse")
	public JAXBElement<NVerifyResponse> createNVerifyResponse(
			NVerifyResponse value) {
		return new JAXBElement<NVerifyResponse>(_NVerifyResponse_QNAME,
				NVerifyResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link VerifyResponse }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "verifyResponse")
	public JAXBElement<VerifyResponse> createVerifyResponse(VerifyResponse value) {
		return new JAXBElement<VerifyResponse>(_VerifyResponse_QNAME,
				VerifyResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Modify }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "modify")
	public JAXBElement<Modify> createModify(Modify value) {
		return new JAXBElement<Modify>(_Modify_QNAME, Modify.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link BindResponse }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "bindResponse")
	public JAXBElement<BindResponse> createBindResponse(BindResponse value) {
		return new JAXBElement<BindResponse>(_BindResponse_QNAME,
				BindResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link GetFingerInfoResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "getFingerInfoResponse")
	public JAXBElement<GetFingerInfoResponse> createGetFingerInfoResponse(
			GetFingerInfoResponse value) {
		return new JAXBElement<GetFingerInfoResponse>(
				_GetFingerInfoResponse_QNAME, GetFingerInfoResponse.class,
				null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Register }{@code
	 * >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "register")
	public JAXBElement<Register> createRegister(Register value) {
		return new JAXBElement<Register>(_Register_QNAME, Register.class, null,
				value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link RegisterResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "registerResponse")
	public JAXBElement<RegisterResponse> createRegisterResponse(
			RegisterResponse value) {
		return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME,
				RegisterResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link GetTokenResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "getTokenResponse")
	public JAXBElement<GetTokenResponse> createGetTokenResponse(
			GetTokenResponse value) {
		return new JAXBElement<GetTokenResponse>(_GetTokenResponse_QNAME,
				GetTokenResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Bind }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "bind")
	public JAXBElement<Bind> createBind(Bind value) {
		return new JAXBElement<Bind>(_Bind_QNAME, Bind.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link GetToken }{@code
	 * >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "getToken")
	public JAXBElement<GetToken> createGetToken(GetToken value) {
		return new JAXBElement<GetToken>(_GetToken_QNAME, GetToken.class, null,
				value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link GetFingerInfo }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "getFingerInfo")
	public JAXBElement<GetFingerInfo> createGetFingerInfo(GetFingerInfo value) {
		return new JAXBElement<GetFingerInfo>(_GetFingerInfo_QNAME,
				GetFingerInfo.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link ModifyResponse }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.bioplat.biokee.com/", name = "modifyResponse")
	public JAXBElement<ModifyResponse> createModifyResponse(ModifyResponse value) {
		return new JAXBElement<ModifyResponse>(_ModifyResponse_QNAME,
				ModifyResponse.class, null, value);
	}

}
