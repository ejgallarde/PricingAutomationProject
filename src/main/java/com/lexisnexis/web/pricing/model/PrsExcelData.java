package com.lexisnexis.web.pricing.model;

public class PrsExcelData {
	public static final String PLAN_ID = "PlanID";
	public static final String FUNCTION_NAME = "FunctionName";
	public static final String PRICING_FACTOR = "PricingFactor";
	public static final String REPORT_OPTIONS = "ReportOptions";
	public static final String ONLINE_PRICE = "OnlinePrice";
	public static final String RO_ONLINE_PRICE = "ReportOptionsOnlinePrice";
	public static final String REPORT_OPTIONS_POSITION = "ReportOptionsPosition";
	public static final String DISCOUNTABLE = "Discountable";
	public static final String DISCOUNTABLE_RO = "DiscountableRO";
	public static final String TRANSACTION_TYPE = "TransactionType";
	
	private String pricingFactor;
	private String functionName;
	private String planId;
	private String reportOptions;
	private double onlinePrice;
	private String transactionId;
	private String companyId;
	private double updatedPrice;
	private String reportOptionsValue;
	private int reportOptionsPosition;
	private String discountable;
	private String transactionType;
	
	private int startPlanId;
	private int endPlanId;
	
	public String getPricingFactor() {
		return pricingFactor;
	}
	public void setPricingFactor(String pricingFactor) {
		this.pricingFactor = pricingFactor;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public String getReportOptions() {
		return reportOptions;
	}
	public void setReportOptions(String reportOptions) {
		this.reportOptions = reportOptions;
	}
	public double getOnlinePrice() {
		return onlinePrice;
	}
	public void setOnlinePrice(double onlinePrice) {
		this.onlinePrice = onlinePrice;
	}
	public int getStartPlanId() {
		return startPlanId;
	}
	public void setStartPlanId(int startPlanId) {
		this.startPlanId = startPlanId;
	}
	public int getEndPlanId() {
		return endPlanId;
	}
	public void setEndPlanId(int endPlanId) {
		this.endPlanId = endPlanId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public double getUpdatedPrice() {
		return updatedPrice;
	}
	public void setUpdatedPrice(double updatedPrice) {
		this.updatedPrice = updatedPrice;
	}
	public String getReportOptionsValue() {
		return reportOptionsValue;
	}
	public void setReportOptionsValue(String reportOptionsValue) {
		this.reportOptionsValue = reportOptionsValue;
	}
	public int getReportOptionsPosition() {
		return reportOptionsPosition;
	}
	public void setReportOptionsPosition(int reportOptionsPosition) {
		this.reportOptionsPosition = reportOptionsPosition;
	}
	public String getDiscountable() {
		return discountable;
	}
	public void setDiscountable(String discountable) {
		this.discountable = discountable;
	}
	public boolean isDiscountable() {
		return this.discountable.equalsIgnoreCase("Y");
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
}
