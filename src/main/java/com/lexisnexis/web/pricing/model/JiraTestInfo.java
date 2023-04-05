package com.lexisnexis.web.pricing.model;

public class JiraTestInfo {
	private String jiraTicket;
	private String jiraDetails;
	private boolean fcra;
	private String environment;
	private int indexPosition;
	private boolean samePrice;
	
	public String getJiraTicket() {
		return jiraTicket;
	}
	public void setJiraTicket(String jiraTicket) {
		this.jiraTicket = jiraTicket;
	}
	public String getJiraDetails() {
		return jiraDetails;
	}
	public void setJiraDetails(String jiraDetails) {
		this.jiraDetails = jiraDetails;
	}
	public boolean isFcra() {
		return fcra;
	}
	public void setFcra(boolean fcra) {
		this.fcra = fcra;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public int getIndexPosition() {
		return indexPosition;
	}
	public void setIndexPosition(int indexPosition) {
		this.indexPosition = indexPosition;
	}
	public boolean isSamePrice() {
		return samePrice;
	}
	public void setSamePrice(boolean samePrice) {
		this.samePrice = samePrice;
	}

}
