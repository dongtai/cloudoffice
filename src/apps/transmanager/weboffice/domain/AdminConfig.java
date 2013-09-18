package apps.transmanager.weboffice.domain;


public class AdminConfig implements SerializableAdapter
{
	public String assistAddress;
	public String companyID;
	public String email;
	public String mailSerAddress;
	public String password;
	public String getAssistAddress() {
		return assistAddress;
	}
	public void setAssistAddress(String assistAddress) {
		this.assistAddress = assistAddress;
	}
	public String getCompanyID() {
		return companyID;
	}
	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMailSerAddress() {
		return mailSerAddress;
	}
	public void setMailSerAddress(String mailSerAddress) {
		this.mailSerAddress = mailSerAddress;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
