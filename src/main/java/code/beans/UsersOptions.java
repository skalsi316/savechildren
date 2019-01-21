package code.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersOptions {

	private String msisdn;
	private String name;
	private String email;	
	private String pswd;

    public UsersOptions() {
    }

    public UsersOptions(String msisdn, String name, String email, String pswd) {
        this.msisdn = msisdn;
        this.name = name;
        this.email = email;
        this.pswd = pswd;
    }
 
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPswd() {
		return pswd;
	}
	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

    @Override
    public String toString() {
		return "[{" +
				"msisdn='" + msisdn + '\'' +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", pswd='" + pswd + '\'' + 
				"}]";
    }
}