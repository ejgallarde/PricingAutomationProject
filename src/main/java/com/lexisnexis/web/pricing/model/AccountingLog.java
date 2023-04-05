package com.lexisnexis.web.pricing.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class represents the AccountingLog model.
 *
 * @author Gallea01 - Earl John Gallarde
 */
public class AccountingLog implements Serializable {

	private static final long serialVersionUID = -6219905702218914829L;
	
	public static final String ACCTG_LOG_TBL = "accounting_log";
	public static final String COMPANY_ID = "company_id";
	public static final String PRICE = "price";
	public static final String TRANSACTION_ID = "transaction_id";
	public static final String TRANSACTION_TYPE = "transaction_type";
	public static final String FUNCTION_NAME = "function_name";
	public static final String LOGIN_ID = "loginid";
	public static final String RECORD_COUNT = "record_count";
	public static final String REPORT_OPTIONS = "report_options";
	public static final String RESULT_FORMAT = "result_format";
	public static final String DATE_ADDED = "dateadded";
	public static final String RETAIL_PRICE = "retail_price";
	public static final String FREE = "free";
	public static final String PRICING_ERROR_CODE = "pricing_error_code";
	
	private String transactionId;
	private String transactionType;
	private int companyId;
	private String referenceCode;
	private int recordCount;
	private double price;
	private int transactionCode;
	private String reportOptions;
	private double retailPrice;
	private String functionName;
	private String loginId;
	private String resultFormat;
	private int free;
	private int pricingErrorCode;
	private String description;
	private Timestamp dateAdded;
	
	//From company_pricingplan table
	private int planId;
	private double discountPercent;
	
	public int getPlanId() {
		return planId;
	}
	public void setPlanId(int planId) {
		this.planId = planId;
	}
	public double getDiscountPercent() {
		return discountPercent;
	}
	public void setDiscountPercent(double discountPercent) {
		this.discountPercent = discountPercent;
	}
	
	public int getFree() {
		return free;
	}
	public void setFree(int free) {
		this.free = free;
	}
	public int getPricingErrorCode() {
		return pricingErrorCode;
	}
	public void setPricingErrorCode(int pricingErrorCode) {
		this.pricingErrorCode = pricingErrorCode;
	}
	
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getResultFormat() {
		return resultFormat;
	}
	public void setResultFormat(String resultFormat) {
		this.resultFormat = resultFormat;
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(int transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getReportOptions() {
		return reportOptions;
	}
	public void setReportOptions(String reportOptions) {
		this.reportOptions = reportOptions;
	}
	public double getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(double retailPrice) {
		this.retailPrice = retailPrice;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Timestamp dateAdded) {
		this.dateAdded = dateAdded;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
