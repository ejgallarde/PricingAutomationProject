package com.lexisnexis.web.pricing.model;

import java.io.Serializable;

/**
 * This class represents the CompanyPricingPlan table.
 *
 * @author Gallea01 - Earl John Gallarde
 */
public class CompanyPricingPlan implements Serializable {

	private static final long serialVersionUID = -6804970484664402889L;
	
	public static final String COMPANY_PRICE_PLAN = "company_pricingplan";
	public static final String PLAN_ID = "planid";
	public static final String DISCOUNT_PERCENT = "discount_percent";
	public static final String COMPANY_ID = "company_id";
	public static final String ACTIVE = "active";
	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	
	private int companyId;
	
	private double amount;
	private boolean active;
	
	private int companyPricingPlanId;
	
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getCompanyPricingPlanId() {
		return companyPricingPlanId;
	}
	public void setCompanyPricingPlanId(int companyPricingPlanId) {
		this.companyPricingPlanId = companyPricingPlanId;
	}
	
	
}
