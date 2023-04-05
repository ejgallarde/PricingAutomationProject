package com.lexisnexis.web.pricing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lexisnexis.web.pricing.db.DatabaseManager;
import com.lexisnexis.web.pricing.model.AccountingLog;
import com.lexisnexis.web.pricing.model.CompanyPricingPlan;
import com.lexisnexis.web.pricing.model.MoxieFunctionDesc;
import com.lexisnexis.web.pricing.model.PrsExcelData;
import com.lexisnexis.web.pricing.util.PricingConstants;

/**
 * This is the DAO class for anything related to Pricing Accurint.
 *
 * @author Gallea01 - Earl John Gallarde
 */
public class PricingAccurintDao implements PricingTestDao {

	public static final Logger logger = LogManager.getLogger(PricingAccurintDao.class);

	// SELECT Queries for Accounting Log
	// FCRA (fcra_log)
	public static final String SQL_GET_TRANSID_BY_FUNCNAME_FCRA = "SELECT TOP 1 transaction_id, report_options FROM fcra_log..accounting_log WHERE function_name = ? AND transaction_type = ? AND free = 0";
	public static final String SQL_GET_COMPID_BY_TXN_ID_FCRA = "SELECT company_id FROM fcra_log..accounting_log WHERE transaction_id = ?";
	public static final String SQL_GET_ACCTG_LOG_INFO_FCRA = "SELECT a.transaction_id, a.loginid, a.company_id, a.transaction_type, a.function_name, "
			+ "m.description, a.record_count, a.report_options, a.result_format, a.dateadded, a.price, a.retail_price, a.free, a.pricing_error_code, "
			+ "c.planid, c.discount_percent "
			+ "FROM fcra_log..accounting_log a, accurint..company_pricingplan c, accurint..moxie_function_desc m "
			+ "WHERE a.company_id = c.companyid AND a.transaction_type = m.transaction_type AND a.function_name = m.function_name "
			+ "AND c.active = 'Y' AND GETDATE() BETWEEN c.start_date AND c.end_date AND a.transaction_id = ?";
	public static final String SQL_GET_ACCTG_LOG_INFO_FCRA_BACKUP = "SELECT a.transaction_id, a.loginid, a.company_id, a.transaction_type, a.function_name, "
			+ "a.record_count, a.report_options, a.result_format, a.dateadded, a.price, a.retail_price, a.free, a.pricing_error_code, "
			+ "c.planid, c.discount_percent "
			+ "FROM fcra_log..accounting_log a, accurint..company_pricingplan c "
			+ "WHERE a.company_id = c.companyid "
			+ "AND c.active = 'Y' AND GETDATE() BETWEEN c.start_date AND c.end_date AND a.transaction_id = ?";
	// NON-FCRA (accurint_al)
	public static final String SQL_GET_TRANSID_BY_FUNCNAME_NON_FCRA = "SELECT TOP 1 transaction_id, report_options FROM accurint_al..accounting_log WHERE function_name = ? AND transaction_type = ? AND free = 0";
	public static final String SQL_GET_COMPID_BY_TXN_ID_NON_FCRA = "SELECT company_id FROM accurint_al..accounting_log WHERE transaction_id = ?";
	public static final String SQL_GET_ACCTG_LOG_INFO_NON_FCRA = "SELECT a.transaction_id, a.loginid, a.company_id, a.transaction_type, a.function_name, "
			+ "m.description, a.record_count, a.report_options, a.result_format, a.dateadded, a.price, a.retail_price, a.free, a.pricing_error_code, "
			+ "c.planid, c.discount_percent "
			+ "FROM accurint_al..accounting_log a, accurint..company_pricingplan c, accurint..moxie_function_desc m "
			+ "WHERE a.company_id = c.companyid AND a.transaction_type = m.transaction_type AND a.function_name = m.function_name "
			+ "AND c.active = 'Y' AND GETDATE() BETWEEN c.start_date AND c.end_date AND a.transaction_id = ?";
	public static final String SQL_GET_ACCTG_LOG_INFO_NON_FCRA_BACKUP = "SELECT a.transaction_id, a.loginid, a.company_id, a.transaction_type, a.function_name, "
			+ "a.record_count, a.report_options, a.result_format, a.dateadded, a.price, a.retail_price, a.free, a.pricing_error_code, "
			+ "c.planid, c.discount_percent "
			+ "FROM accurint_al..accounting_log a, accurint..company_pricingplan c "
			+ "WHERE a.company_id = c.companyid "
			+ "AND c.active = 'Y' AND GETDATE() BETWEEN c.start_date AND c.end_date AND a.transaction_id = ?";

	// UPDATE Queries for Accounting Log
	// FCRA (fcra_log)
	public static final String SQL_ACCNT_LOG_UPDATE_FCRA = "UPDATE fcra_log..accounting_log SET price = -1, retail_price = -1, free = 0, pricing_error_code = 0, "
			+ "dateadded = dateadd(ss, 30, getdate()) WHERE transaction_id = ?";
	public static final String SQL_REPORT_OPTIONS_UPDATE_FCRA = "UPDATE fcra_log..accounting_log SET report_options = ?, dateadded = dateadd(ss, 30, getdate()) "
			+ "WHERE transaction_id = ?";
	// NON-FCRA (accurint_al)
	public static final String SQL_ACCNT_LOG_UPDATE_NON_FCRA = "UPDATE accurint_al..accounting_log SET price = -1, retail_price = -1, free = 0, pricing_error_code = 0, "
			+ "dateadded = dateadd(ss, 30, getdate()) WHERE transaction_id = ?";
	public static final String SQL_REPORT_OPTIONS_UPDATE_NON_FCRA = "UPDATE accurint_al..accounting_log SET report_options = ?, dateadded = dateadd(ss, 30, GETDATE()) "
			+ "WHERE transaction_id = ?";

	// SELECT Queries for Company Pricing Plan
	public static final String SQL_COMP_PP_GET_COMPID_PLANID = "SELECT companyid, planid FROM accurint..company_pricingplan "
			+ "WHERE companyid = ? AND active = ? AND GETDATE() BETWEEN ? AND ?";
	// UPDATE Queries for Company Pricing Plan
	public static final String SQL_COMP_PP_UPDATE_PLAN_ID = "UPDATE accurint..company_pricingplan SET planid = ?, discount_percent = ? "
			+ "WHERE companyid = ? AND active = 'Y' AND GETDATE() BETWEEN start_date AND end_date";
	public static final String SQL_COMP_PP_UPDATE_PLAN_ID_NO_DISCOUNT = "UPDATE accurint..company_pricingplan SET planid = ? "
			+ "WHERE companyid = ? AND active = 'Y' AND GETDATE() BETWEEN start_date AND end_date";
	// Queries to check Pricer
	// FCRA
	public static final String SQL_GET_DATE_DIFF_FCRA = "Select datediff(MINUTE, (select max(dateadded) from fcra_log..accounting_log) ,(select max(dateadded) from fcra_log..accounting_log where price > 0)) as date_diff";
	// NON-FCRA
	public static final String SQL_GET_DATE_DIFF_NON_FCRA = "Select datediff(MINUTE, (select max(dateadded) from accurint_al..accounting_log) ,(select max(dateadded) from accurint_al..accounting_log where price > 0)) as date_diff";

	public void appendTransactionIdsAndReportOptionsValue(PrsExcelData data, boolean isFcra) {
		String query = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try (Connection conn = DatabaseManager.getSybaseConnection()) {
			if (isFcra) {
				query = SQL_GET_TRANSID_BY_FUNCNAME_FCRA;
			} else {
				query = SQL_GET_TRANSID_BY_FUNCNAME_NON_FCRA;
			}
			logger.info("QUERY: " + query);
			if (data.getPricingFactor().equals(PricingConstants.SEARCH)) {
				ps = conn.prepareStatement(query);
				ps.setString(1, data.getFunctionName());
				ps.setString(2, data.getTransactionType());
			} else {
				StringBuilder builder = new StringBuilder(query);
				builder.append(" AND record_count > 0");
				query = builder.toString();
				ps = conn.prepareStatement(query);
				ps.setString(1, data.getFunctionName());
				ps.setString(2, data.getTransactionType());
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				String txnId = rs.getString(AccountingLog.TRANSACTION_ID);
				logger.info("Found Transaction ID: " + txnId);
				data.setTransactionId(txnId);
				if (StringUtils.isNotEmpty(data.getReportOptions()) && data.getReportOptions().equalsIgnoreCase("Y")) {
					String reportOptionsValue = rs.getString(AccountingLog.REPORT_OPTIONS);
					logger.info("Found Report Options Value: " + reportOptionsValue);
					data.setReportOptionsValue(reportOptionsValue);
				} else {
					data.setReportOptionsValue(null);
				}
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			logger.error("Failed to run query: " + query);
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * Method to a transaction ID to be used for each function name i
	 * 
	 * @param prsDataList - list of items to be tested in the filtered PRS Data List
	 * @param isFcra
	 */
	public void appendCompanyIds(List<PrsExcelData> prsDataList, boolean isFcra) {
		String query = null;
		ResultSet rs = null;
		try (Connection conn = DatabaseManager.getSybaseConnection()) {

			PreparedStatement ps;
			if (isFcra) {
				query = SQL_GET_COMPID_BY_TXN_ID_FCRA;
				ps = conn.prepareStatement(query);
			} else {
				query = SQL_GET_COMPID_BY_TXN_ID_NON_FCRA;
				ps = conn.prepareStatement(query);
			}
			logger.info("QUERY: " + query);
			for (PrsExcelData data : prsDataList) {
				logger.info("Using Transaction ID: " + data.getTransactionId());
				ps.setString(1, data.getTransactionId());
				rs = ps.executeQuery();
				while (rs.next()) {
					data.setCompanyId(rs.getString(AccountingLog.COMPANY_ID));
					logger.info("Found Company ID: " + data.getCompanyId());
				}
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error("ERROR running query: " + query);
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * Method to set plan_id in company_pricingplan based on transaction ID
	 * 
	 * @param prsDataList - list of data that holds the plan id and transaction id
	 * @param isFcra
	 * @return
	 */
	public List<AccountingLog> updatePricePlanByIterationOfPlanId(List<PrsExcelData> prsDataList, boolean isFcra) {
		List<AccountingLog> acctgLogList = new ArrayList<AccountingLog>();

		String query1 = SQL_COMP_PP_UPDATE_PLAN_ID;
		try (Connection conn = DatabaseManager.getSybaseConnection();
				PreparedStatement ps = conn.prepareStatement(query1)) {
			PreparedStatement ps2;
			PreparedStatement ps3;
			PreparedStatement ps4;
			ResultSet rs = null;
			String query2 = null;
			String query3 = null;
			String query4 = null;
			if (isFcra) {
				query2 = SQL_ACCNT_LOG_UPDATE_FCRA;
				ps2 = conn.prepareStatement(query2);
				query3 = SQL_GET_ACCTG_LOG_INFO_FCRA;
				ps3 = conn.prepareStatement(query3);
				query4 = SQL_GET_ACCTG_LOG_INFO_FCRA_BACKUP;
				ps4 = conn.prepareStatement(query4);
			} else {
				query2 = SQL_ACCNT_LOG_UPDATE_NON_FCRA;
				ps2 = conn.prepareStatement(query2);
				query3 = SQL_GET_ACCTG_LOG_INFO_NON_FCRA;
				ps3 = conn.prepareStatement(query3);
				query4 = SQL_GET_ACCTG_LOG_INFO_NON_FCRA_BACKUP;
				ps4 = conn.prepareStatement(query4);
			}
			logger.info("QUERY 1: " + query1);
			logger.info("QUERY 2: " + query2);
			logger.info("QUERY 3: " + query3);
			for (PrsExcelData data : prsDataList) {
				int startId = data.getStartPlanId();
				int endId = data.getEndPlanId();
				logger.info("Running queries for Plan ID Range: " + startId + " to " + endId);
				logger.info("Transaction ID: " + data.getTransactionId());
				for (int i = startId; i <= endId; i++) {
					logger.info("Processing Plan ID No. " + i);
					ps.setInt(1, i);
					if (i == 20 && data.isDiscountable()) {
						ps.setDouble(2, 20.0);
					} else {
						ps.setDouble(2, 0.0);
					}
					ps.setInt(3, Integer.parseInt(data.getCompanyId()));
					int updatedRows = ps.executeUpdate();
					if(updatedRows < 1) {
						System.out.println("Error updating QUERY 1.");
					}

					// for each plan id, update price in accounting log
					updatedRows = 0;
					ps2.setString(1, data.getTransactionId());
					updatedRows = ps2.executeUpdate();
					if(updatedRows < 1) {
						System.out.println("Error updating QUERY 2.");
					}

					Thread.sleep(1000);
					ps3.setString(1, data.getTransactionId());
					rs = ps3.executeQuery();
					
					if(rs.getRow() > 0) {
						while (rs.next()) {
							AccountingLog acctgLog = new AccountingLog();
							acctgLog.setTransactionId(rs.getString(AccountingLog.TRANSACTION_ID));
							acctgLog.setLoginId(rs.getString(AccountingLog.LOGIN_ID));
							acctgLog.setCompanyId(rs.getInt(AccountingLog.COMPANY_ID));
							acctgLog.setTransactionType(rs.getString(AccountingLog.TRANSACTION_TYPE));
							acctgLog.setFunctionName(rs.getString(AccountingLog.FUNCTION_NAME));
							acctgLog.setDescription(rs.getString(MoxieFunctionDesc.DESCRIPTION));
							acctgLog.setRecordCount(rs.getInt(AccountingLog.RECORD_COUNT));
							acctgLog.setReportOptions(rs.getString(AccountingLog.REPORT_OPTIONS));
							acctgLog.setResultFormat(rs.getString(AccountingLog.RESULT_FORMAT));
							acctgLog.setDateAdded(rs.getTimestamp(AccountingLog.DATE_ADDED));
							acctgLog.setPrice(rs.getDouble(AccountingLog.PRICE));
							acctgLog.setRetailPrice(rs.getDouble(AccountingLog.RETAIL_PRICE));
							acctgLog.setFree(rs.getInt(AccountingLog.FREE));
							acctgLog.setPricingErrorCode(rs.getInt(AccountingLog.PRICING_ERROR_CODE));
							acctgLog.setPlanId(i);
							acctgLog.setDiscountPercent(rs.getDouble(CompanyPricingPlan.DISCOUNT_PERCENT));

							acctgLogList.add(acctgLog);

							data.setUpdatedPrice(rs.getDouble(AccountingLog.PRICE));
						}
					} else {
						
						ps4.setString(1, data.getTransactionId());
						rs = ps4.executeQuery();
						
						while (rs.next()) {
							AccountingLog acctgLog = new AccountingLog();
							acctgLog.setTransactionId(rs.getString(AccountingLog.TRANSACTION_ID));
							acctgLog.setLoginId(rs.getString(AccountingLog.LOGIN_ID));
							acctgLog.setCompanyId(rs.getInt(AccountingLog.COMPANY_ID));
							acctgLog.setTransactionType(rs.getString(AccountingLog.TRANSACTION_TYPE));
							acctgLog.setFunctionName(rs.getString(AccountingLog.FUNCTION_NAME));
							acctgLog.setDescription("No description");
							acctgLog.setRecordCount(rs.getInt(AccountingLog.RECORD_COUNT));
							acctgLog.setReportOptions(rs.getString(AccountingLog.REPORT_OPTIONS));
							acctgLog.setResultFormat(rs.getString(AccountingLog.RESULT_FORMAT));
							acctgLog.setDateAdded(rs.getTimestamp(AccountingLog.DATE_ADDED));
							acctgLog.setPrice(rs.getDouble(AccountingLog.PRICE));
							acctgLog.setRetailPrice(rs.getDouble(AccountingLog.RETAIL_PRICE));
							acctgLog.setFree(rs.getInt(AccountingLog.FREE));
							acctgLog.setPricingErrorCode(rs.getInt(AccountingLog.PRICING_ERROR_CODE));
							acctgLog.setPlanId(i);
							acctgLog.setDiscountPercent(rs.getDouble(CompanyPricingPlan.DISCOUNT_PERCENT));

							acctgLogList.add(acctgLog);

							data.setUpdatedPrice(rs.getDouble(AccountingLog.PRICE));
						}
					}
					
					
				}
				Thread.sleep(5000);
			}
			rs.close();
			ps.close();
			ps2.close();
			ps3.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return acctgLogList;

	}

	public List<AccountingLog> updatePricePlan(List<PrsExcelData> prsDataList, boolean isFcra) {
		List<AccountingLog> acctgLogList = new ArrayList<AccountingLog>();

		String query1 = SQL_COMP_PP_UPDATE_PLAN_ID_NO_DISCOUNT;
		try (Connection conn = DatabaseManager.getSybaseConnection();
				PreparedStatement ps = conn.prepareStatement(query1)) {
			PreparedStatement ps2;
			PreparedStatement ps3;
			ResultSet rs = null;
			String query2 = null;
			String query3 = null;
			if (isFcra) {
				query2 = SQL_ACCNT_LOG_UPDATE_FCRA;
				ps2 = conn.prepareStatement(query2);
				query3 = SQL_GET_ACCTG_LOG_INFO_FCRA;
				ps3 = conn.prepareStatement(query3);
			} else {
				query2 = SQL_ACCNT_LOG_UPDATE_NON_FCRA;
				ps2 = conn.prepareStatement(query2);
				query3 = SQL_GET_ACCTG_LOG_INFO_NON_FCRA;
				ps3 = conn.prepareStatement(query3);
			}
			logger.info("QUERY 1: " + query1);
			logger.info("QUERY 2: " + query2);
			logger.info("QUERY 3: " + query3);
			for (PrsExcelData data : prsDataList) {
				int planId = Integer.parseInt(data.getPlanId());
				logger.info("Running queries for Plan ID : " + planId);
				logger.info("Transaction ID: " + data.getTransactionId());
				ps.setInt(1, planId);
				ps.setInt(2, Integer.parseInt(data.getCompanyId()));
				ps.executeUpdate();

				// for each plan id, update price in accounting log
				ps2.setString(1, data.getTransactionId());
				ps2.executeUpdate();

				Thread.sleep(1000);
				ps3.setString(1, data.getTransactionId());
				rs = ps3.executeQuery();
				while (rs.next()) {
					AccountingLog acctgLog = new AccountingLog();
					acctgLog.setTransactionId(rs.getString(AccountingLog.TRANSACTION_ID));
					acctgLog.setLoginId(rs.getString(AccountingLog.LOGIN_ID));
					acctgLog.setCompanyId(rs.getInt(AccountingLog.COMPANY_ID));
					acctgLog.setTransactionType(rs.getString(AccountingLog.TRANSACTION_TYPE));
					acctgLog.setFunctionName(rs.getString(AccountingLog.FUNCTION_NAME));
					acctgLog.setDescription(rs.getString(MoxieFunctionDesc.DESCRIPTION));
					acctgLog.setRecordCount(rs.getInt(AccountingLog.RECORD_COUNT));
					acctgLog.setReportOptions(rs.getString(AccountingLog.REPORT_OPTIONS));
					acctgLog.setResultFormat(rs.getString(AccountingLog.RESULT_FORMAT));
					acctgLog.setDateAdded(rs.getTimestamp(AccountingLog.DATE_ADDED));
					acctgLog.setPrice(rs.getDouble(AccountingLog.PRICE));
					acctgLog.setRetailPrice(rs.getDouble(AccountingLog.RETAIL_PRICE));
					acctgLog.setFree(rs.getInt(AccountingLog.FREE));
					acctgLog.setPricingErrorCode(rs.getInt(AccountingLog.PRICING_ERROR_CODE));
					acctgLog.setPlanId(planId);
					acctgLog.setDiscountPercent(rs.getDouble(CompanyPricingPlan.DISCOUNT_PERCENT));

					acctgLogList.add(acctgLog);

					data.setUpdatedPrice(rs.getDouble(AccountingLog.PRICE));
				}
			}
			rs.close();
			ps.close();
			ps2.close();
			ps3.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return acctgLogList;

	}

	public void updateReportOptionsByTxnId(PrsExcelData data, boolean isFcra) {

		try (Connection conn = DatabaseManager.getSybaseConnection()) {
			PreparedStatement ps;
			String query = null;
			if (isFcra) {
				query = SQL_REPORT_OPTIONS_UPDATE_FCRA;
			} else {
				query = SQL_REPORT_OPTIONS_UPDATE_NON_FCRA;
			}

			logger.info("QUERY: " + query);
			ps = conn.prepareStatement(query);

			logger.info("Transaction ID: " + data.getTransactionId());
			ps.setString(1, data.getReportOptionsValue());
			ps.setString(2, data.getTransactionId());
			ps.executeUpdate();

			ps.close();
		} catch (SQLException e) {
			logger.error("ERROR running SQL query.");
			logger.error(e.getMessage(), e);
		}
	}

	public boolean isPricerDown(boolean isFcra) {
		boolean isPricerDown = true;
		try (Connection conn = DatabaseManager.getSybaseConnection()) {

			String query = null;
			if (isFcra) {
				query = SQL_GET_DATE_DIFF_FCRA;
			} else {
				query = SQL_GET_DATE_DIFF_NON_FCRA;
			}
			logger.info("QUERY: " + query);
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			int dateDiff = 0;
			while (rs.next()) {
				int diff = rs.getInt("date_diff");
				if (dateDiff == diff) {
					isPricerDown = false;
				} else {
					isPricerDown = true;
				}
				logger.warn("Date difference is " + diff);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error("ERROR running SQL query.");
			logger.error(e.getMessage(), e);
		}
		return isPricerDown;
	}

	public boolean testPricerStatus(boolean isFcra, String env) {
		boolean isPricerDown = false;
				
		try (Connection conn = DatabaseManager.getSybaseConnection()) {
			PreparedStatement ps1, ps2, ps3;
			ResultSet rs = null;
			String query1 = null;
			String query2 = null;
			String query3 = null;
			if (isFcra) {
				query1 = TEST_SQL_COMP_PP_UPDATE_PLAN_ID_DEV_FCRA;
				ps1 = conn.prepareStatement(query1);
				query2 = TEST_SQL_ACCNT_LOG_UPDATE_DEV_FCRA;
				ps2 = conn.prepareStatement(query2);
				query3 = TEST_SQL_GET_PRICE_DEV_FCRA;
				ps3 = conn.prepareStatement(query3);
			} else {
				if(env.equals(PricingConstants.DEV)) {
					query1 = TEST_SQL_COMP_PP_UPDATE_PLAN_ID_DEV_NON_FCRA;
					ps1 = conn.prepareStatement(query1);
					query2 = TEST_SQL_ACCNT_LOG_UPDATE_DEV_NON_FCRA;
					ps2 = conn.prepareStatement(query2);
					query3 = TEST_SQL_GET_PRICE_DEV_NON_FCRA;
					ps3 = conn.prepareStatement(query3);
				} else {
					query1 = TEST_SQL_COMP_PP_UPDATE_PLAN_ID_QA_NON_FCRA;
					ps1 = conn.prepareStatement(query1);
					query2 = TEST_SQL_ACCNT_LOG_UPDATE_QA_NON_FCRA;
					ps2 = conn.prepareStatement(query2);
					query3 = TEST_SQL_GET_PRICE_QA_NON_FCRA;
					ps3 = conn.prepareStatement(query3);
				}
			}
			logger.info("QUERY 1: " + query1);
			logger.info("QUERY 2: " + query2);
			logger.info("QUERY 3: " + query3);
			logger.info("Running additional tests to check Pricer Status");
			ps1.executeUpdate();
			ps2.executeUpdate();
			Thread.sleep(1000);
			rs = ps3.executeQuery();
			while (rs.next()) {
				if (rs.getDouble(AccountingLog.PRICE) == -1.0) {
					isPricerDown = true;
				}
			}
			rs.close();
			ps1.close();
			ps2.close();
			ps3.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return isPricerDown;
	}

}
