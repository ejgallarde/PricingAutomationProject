package com.lexisnexis.web.pricing.dao;

public interface PricingTestDao {
	// PRICER TEST
	public static final String TEST_SQL_COMP_PP_UPDATE_PLAN_ID_DEV_FCRA = "UPDATE accurint..company_pricingplan SET planid = 1 "
			+ "WHERE companyid = 11093857 AND active = 'Y' AND GETDATE() BETWEEN start_date AND end_date";
	public static final String TEST_SQL_COMP_PP_UPDATE_PLAN_ID_DEV_NON_FCRA = "UPDATE accurint..company_pricingplan SET planid = 1 "
			+ "WHERE companyid = 1028469 AND active = 'Y' AND GETDATE() BETWEEN start_date AND end_date";
	public static final String TEST_SQL_COMP_PP_UPDATE_PLAN_ID_QA_NON_FCRA = "UPDATE accurint..company_pricingplan SET planid = 43 "
			+ "WHERE companyid = 1982478 AND active = 'Y' AND GETDATE() BETWEEN start_date AND end_date";

	public static final String TEST_SQL_ACCNT_LOG_UPDATE_DEV_FCRA = "UPDATE fcra_log..accounting_log SET price = -1, retail_price = -1, free = 0, pricing_error_code = 0, "
			+ "dateadded = dateadd(ss, 30, getdate()) WHERE transaction_id = '16869427R2'";
	public static final String TEST_SQL_ACCNT_LOG_UPDATE_DEV_NON_FCRA = "UPDATE accurint_al..accounting_log SET price = -1, retail_price = -1, free = 0, pricing_error_code = 0, "
			+ "dateadded = dateadd(ss, 30, getdate()) WHERE transaction_id = '16870547R633'";
	public static final String TEST_SQL_ACCNT_LOG_UPDATE_QA_NON_FCRA = "UPDATE accurint_al..accounting_log SET price = -1, retail_price = -1, free = 0, pricing_error_code = 0, "
			+ "dateadded = dateadd(ss, 30, getdate()) WHERE transaction_id = '04751572B0X7'";

	public static final String TEST_SQL_GET_PRICE_DEV_FCRA = "SELECT price FROM fcra_log..accounting_log WHERE transaction_id = '16869427R2'";
	public static final String TEST_SQL_GET_PRICE_DEV_NON_FCRA = "SELECT price FROM accurint_al..accounting_log WHERE transaction_id = '16870547R633'";
	public static final String TEST_SQL_GET_PRICE_QA_NON_FCRA = "SELECT price FROM accurint_al..accounting_log WHERE transaction_id = '04751572B0X7'";
}
