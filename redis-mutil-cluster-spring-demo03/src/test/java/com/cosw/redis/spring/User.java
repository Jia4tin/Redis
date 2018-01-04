package com.cosw.redis.spring;

import java.io.Serializable;

public class User  implements Serializable {  
  
    private static final long serialVersionUID = -1267719235225203410L;  
  
    private String uid;  
  
    private String address;

    private String mobile;  
    
    private String postCode;  
    
    
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	@Override
	public String toString() {
		return "User [uid=" + uid + ", address=" + address + ", mobile="
				+ mobile + ", postCode=" + postCode + "]";
	}
}
