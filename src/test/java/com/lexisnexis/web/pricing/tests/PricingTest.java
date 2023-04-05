package com.lexisnexis.web.pricing.tests;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.lexisnexis.web.pricing.dao.PricingAccurintDao;
import com.lexisnexis.web.pricing.db.DBConfig;
import com.lexisnexis.web.pricing.mail.MailHelper;
import com.lexisnexis.web.pricing.mail.PricingMail;
import com.lexisnexis.web.pricing.model.AccountingLog;
import com.lexisnexis.web.pricing.model.JiraTestInfo;
import com.lexisnexis.web.pricing.model.PrsExcelData;
import com.lexisnexis.web.pricing.util.Common;
import com.lexisnexis.web.pricing.util.ExcelHelper;
import com.lexisnexis.web.pricing.util.PricingConstants;
import com.lexisnexis.web.pricing.util.UserInputPopup;

public class PricingTest {

	public static final Logger logger = LogManager.getLogger(PricingTest.class);

	private static List<PrsExcelData> prsDataList; //this is the filtered raw data
	private static List<AccountingLog> acctgLogList;
	private static String fileName;
	private static boolean isFcra;
	private static PricingAccurintDao dao;
	private static JiraTestInfo jiraTestInfo;
	private static List<PrsExcelData> rawPrsExcelData;
	private static Instant startTime;
	private static Instant endTime;
	private static int indexPos;
	private static String env;
	private static String jiraId;
	private static String jiraDesc;
	private static boolean usePipelineEmail;
	
	private static ExtentHtmlReporter htmlReporter;
	private static ExtentTest test;
	private static ExtentReports extent;
	
	@Parameters({"jiraNumber", "jiraInfo", "indexPosition", "environment", "fcraValue", "usePipeline"})
	@BeforeTest
	public void init(String jiraNumber, String jiraInfo, String indexPosition, String environment, String fcraValue, String usePipeline) {

		if(usePipeline.equalsIgnoreCase(PricingConstants.YES)) {
			startTime = Instant.now();
			logger.info("Begin Run: " + startTime + "\n");
			
			//Read parameters from testsuite.xml
			jiraId = jiraNumber;
			jiraDesc = jiraInfo;
			indexPos = Integer.valueOf(indexPosition);
			env = environment;
			if (PricingConstants.FCRA.equalsIgnoreCase(fcraValue)) {
				isFcra = true;
			} else {
				isFcra = false;
			}
			usePipelineEmail = true;
		} else {
			// Prompt user for test details (Jira ticket, Jira info, environment, etc.)
			logger.info("Prompting user for test input.");
			jiraTestInfo = UserInputPopup.promptPricingInfo();
			
			// Set jira parameters based on user input
			jiraId = jiraTestInfo.getJiraTicket();
			jiraDesc = jiraTestInfo.getJiraDetails();
			indexPos = jiraTestInfo.getIndexPosition();
			isFcra = jiraTestInfo.isFcra();
			env = jiraTestInfo.getEnvironment();
			usePipelineEmail = false;
			
			startTime = Instant.now();
			logger.info("Begin Run: " + startTime + "\n");
		}
		
		fileName = (jiraId + " " + jiraDesc).replaceAll("[^a-zA-Z0-9 _-]", "") + ".xlsx";
		
		// Set environment and whether the test should be run on the FCRA database
		logger.info("************ USER INPUTS ************");
		logger.info("Environment: " + env);
		logger.info("Use FCRA Table: " + isFcra);
		logger.info("Beginning test for " + jiraId + ": " + jiraDesc);
		logger.info("Report Options Index Position: " + indexPos);
		logger.info("************ USER INPUTS ************\n");

		new DBConfig(env);
		dao = new PricingAccurintDao();
		acctgLogList = null;
		initExtentReport();
	}
	
	private void initExtentReport() {
		// Initialize Extent Reports
		String timeStamp = new SimpleDateFormat(Common.SIMPLE_DATE_FORMAT).format(Calendar.getInstance().getTime());
		String strReportPath = Common.TEST_RESULTS_SUMMARY + "Test Results for " + jiraId  + "-" + timeStamp + Common.EXTENT_REPORT_EXT;

		htmlReporter = new ExtentHtmlReporter(strReportPath);
		htmlReporter.config().setDocumentTitle("Automation Report: " + jiraId);
		htmlReporter.config().setReportName(jiraId + " - " + jiraDesc);
		htmlReporter.config().setTheme(Theme.STANDARD);
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		
		extent.setSystemInfo("Environment", env);
		extent.setSystemInfo("User Name", Common.SYSTEM_USER_ID);
		extent.setSystemInfo("Host Name", Common.SYSTEM_HOSTNAME);
		extent.setSystemInfo("OS", Common.SYSTEM_OS_NAME_VER);
	}
	
	@Test(priority = 1)
	private void extractPrsData() {
		// Retrieve raw test data from the PRS Excel file
		// Get details from the Pricing tab (Plan Id, Transaction Type, etc.)
		test = extent.createTest("PRS Data Extraction");
		logger.info("Extracting data from PRS Excel file.");
		rawPrsExcelData = ExcelHelper.getPrsData();
		logger.info("PRS Excel Data has been extracted.");
		if(rawPrsExcelData.size() > 0) {
			test.pass("PRS Excel Data has been extracted");
		} else {
			test.fail("PRS extraction failed or did not yield any test data");
		}
	}
	
	@Test(priority = 2)
	private void filterPrsData() {
		// Filter raw PRS Data: Get distinct FunctionNames and its equivalent
		test = extent.createTest("Filter PRS Data");
		logger.info("Filtering data from PRS Excel file.");
		prsDataList = ExcelHelper.filterRawData(rawPrsExcelData);
		logger.info("PRS Excel Data has been filtered. \n\n");
		if(prsDataList.size() > 0) {
			test.pass("PRS Excel Data has been filtered");
		} else {
			test.fail("PRS filtration failed or did not yield any test data");
		}
	}

	@Test(priority = 3)
	private void checkPricerStatus() {
		logger.info("Checking if Pricer is up... \n");
		test = extent.createTest("Check if Pricer is Running");
		if (dao.isPricerDown(isFcra)) {
			logger.warn("Pricer may be DOWN. Running additional tests.");
			test.warning("Pricer may be DOWN. Running additional tests.");
			if(dao.testPricerStatus(isFcra, env)) {
				logger.warn("Pricer is DOWN. Sending an email notification.");
				PricingMail.sendMail(PricingMail.PRICER_IS_DOWN, null);
				logger.warn("Exiting automation.");
				test.fail("Pricer is down");
				System.exit(1);
			} else {
				logger.info("Pricer is up. Continue with program execution. \n");
				test.pass("Pricer is running");
			}

		} else {
			logger.info("Pricer is up. Continue with program execution. \n");
			test.pass("Pricer is running");
		}
	}
	
	@Test(priority = 4)
	private void appendTransactionIds() {

		logger.info("***** Retrieve transaction IDs and Report Options. *****");
		for (PrsExcelData prsData : prsDataList) {
			String functionName = prsData.getFunctionName();
			String transactionType = prsData.getTransactionType();
			test = extent.createTest("Get Transaction IDs and Report Options for: " + functionName);

			logger.info("Retrieving transaction ID for FUNCTION NAME = " + functionName);
			logger.info("TRANSACTION TYPE = " + transactionType);
			dao.appendTransactionIdsAndReportOptionsValue(prsData, isFcra);
			
			String transactionId = prsData.getTransactionId();
			if (StringUtils.isEmpty(transactionId)) {
				logger.warn("No transaction ID found for FUNCTION NAME = " + functionName);
				PricingMail.sendMail(PricingMail.NO_TXN_ID_FOUND, prsData);
				test.fail("No transaction ID found.");
			} else if (isReportOptionsValueEmpty(prsData)) {
				logger.warn("No report options found for FUNCTION NAME = " + functionName);
				PricingMail.sendMail(PricingMail.NO_REPORT_OPTIONS_VALUE_FOUND, prsData);
				test.fail("No report options found.");
			} else {
				logger.info("Transaction ID found: " + transactionId + "\n");
				if (prsData.getReportOptions().equalsIgnoreCase("Y")) {
					updateReportOptionsValue(prsData);
					dao.updateReportOptionsByTxnId(prsData, isFcra);
				} else {
					logger.info("Report options is set to 'N'. No need to update report options.");
				}
				test.pass("Transaction ID found: " + transactionId);
			}

		}
		logger.info("***** End of Test Step ***** \n\n");
	}

	private boolean isReportOptionsValueEmpty(PrsExcelData prsData) {
		return StringUtils.isNotEmpty(prsData.getReportOptions()) && prsData.getReportOptions().equalsIgnoreCase("Y")
				&& StringUtils.isEmpty(prsData.getReportOptionsValue());
	}

	private void updateReportOptionsValue(PrsExcelData data) {
		logger.info("Updating Report Options value.");
		String oldReportOptionsVal = data.getReportOptionsValue();
		logger.info("BEFORE: " + oldReportOptionsVal);
		int startIndex = indexPos;
		int reportOptionsPosition = data.getReportOptionsPosition();

		StringBuilder reportOptionsBuilder = new StringBuilder(oldReportOptionsVal.replaceAll("1", "0"));
		if(reportOptionsPosition > oldReportOptionsVal.length()) {
			int difference = reportOptionsPosition - oldReportOptionsVal.length();
			String addedZero = StringUtils.EMPTY;
			for(int i = 0; i <= difference; i++) {
				addedZero = addedZero + "0";
			}
			reportOptionsBuilder.append(addedZero);
		}
		reportOptionsBuilder.setCharAt(reportOptionsPosition - startIndex, '1');
		String newReportOptionsVal = reportOptionsBuilder.toString();

		logger.info("AFTER: " + newReportOptionsVal + "\n");
		data.setReportOptionsValue(newReportOptionsVal);
		logger.info("Report Options value has been updated.");
	}

	@Test(priority = 5)
	private void appendCompanyIds() {
		logger.info("***** Retrieve Company ID based on Transaction ID. *****");
		dao.appendCompanyIds(prsDataList, isFcra);
		
		for (PrsExcelData prsData : prsDataList) {
			String companyId = prsData.getCompanyId();
			String transactionId = prsData.getTransactionId();
			test = extent.createTest("Get Company ID for Transaction ID: " + transactionId);
			if(StringUtils.isNotEmpty(companyId)) {
				test.pass("Company ID found: " + companyId);
			} else {
				test.fail("Company ID is null or empty");
			}
		}
		logger.info("***** End of Test Step. ***** \n\n");
	}

	@Test(priority = 6)
	private void updateCompanyPricingPlan() {
		// Update company pricing plan and get accounting log info
		logger.info("***** Update Company Pricing Plan Table based on Transaction ID. *****");
		test = extent.createTest("Update Company Pricing Plan");
		acctgLogList = dao.updatePricePlanByIterationOfPlanId(prsDataList, isFcra);
		
		if(acctgLogList.size() > 0) {
			test.pass("AccountingLog List has been populated");
		} else {
			test.fail("AccountingLog List is empty.");
		}
		
		logger.info("***** End of Test Step. ***** \n\n");
	}
	
	@Test(priority = 7)
	private void writeResultsToExcel() {
		logger.info("***** Write Test Results to Excel. *****");
		test = extent.createTest("Write Results to Excel");
		
		if(acctgLogList.isEmpty()) {
			test.fail("Nothing to write. Empty list.");
		} else {
			try {
				logger.info("Start writing to Excel.");
				ExcelHelper.writeResultsToExcel(acctgLogList, fileName);
				logger.info("Done writing to Excel.");
				test.pass("Completed writing to Excel");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				test.fail("Error writing to Excel");
			}
		}
		logger.info("***** End of Test Step. ***** \n\n");
	}
	
	
//	private void compareResults() {
//		logger.info("***** Validate and Compare Results *****");
//		int index = 1; //initialized to 1 to match the common start plan ID
//		int jump = 0;
//		for (PrsExcelData data : prsDataList) {
//			test = extent.createTest("Compare Test Results for " + data.getFunctionName());
//			int startPlanId = data.getStartPlanId();
//			int endPlanId = data.getEndPlanId();
//			int testDataPlanId = 0;
//			double testResultPrice = 0.0;
//			double testDataOnlinePrice = 0.0;
//			int compareResult = 0;
//			try {
//				do {
//					testDataPlanId = Integer.valueOf(rawPrsExcelData.get(index-1-jump).getPlanId());
//					testResultPrice = acctgLogList.get(index-1).getPrice();
//					if (startPlanId == testDataPlanId) {
//						testDataOnlinePrice = rawPrsExcelData.get(index-1-jump).getOnlinePrice();
//					} else {
//						logger.info("Plan ID: " + startPlanId + " is not in the Test Data.");
//						testDataOnlinePrice = 0.00;
//						jump++;
//					}
//					compareResult = Double.compare(testDataOnlinePrice, testResultPrice);
//					logCompareResults(compareResult, testDataOnlinePrice, testResultPrice, startPlanId);
//					startPlanId++;
//					index++;
//				} while (startPlanId <= endPlanId);
//			} catch (Exception e) {
//				logger.info("Plan ID is a range. \n");
//				int countPlanId = endPlanId-startPlanId+1;
//				for(int i = index; i <= countPlanId; i++) {
//					testResultPrice = acctgLogList.get(i-1).getPrice();
//					testDataOnlinePrice = data.getOnlinePrice();
//					compareResult = Double.compare(testDataOnlinePrice, testResultPrice);
//					logCompareResults(compareResult, testDataOnlinePrice, testResultPrice, startPlanId);
//					startPlanId++;
//					index++;
//				}
//			}
//
//		}
//
//		logger.info("***** End of Test Step. ***** \n\n");
//	}
	
//	@Test(priority = 8)
//	private void compareResults() {
//		logger.info("***** Validate and Compare Results *****");
//		int resultsIndex = 0;
//		int testDataIndex = 0;
//		
//		for (int i = testDataIndex; i < prsDataList.size(); i++) {
//			PrsExcelData data = prsDataList.get(i);
//			int dataRange = data.getEndPlanId() - data.getStartPlanId();
//			double testDataOnlinePrice = data.getOnlinePrice();
//			test = extent.createTest("Compare Test Results for " + data.getFunctionName());
//			int iteration = 0;
//			for (int j = resultsIndex; j < acctgLogList.size(); j++) {
//				
//				
//				AccountingLog acctgLog = acctgLogList.get(j);
//				double testResultPrice = acctgLog.getPrice();
//				int planId = acctgLog.getPlanId();
//				
//				int compareResult = Double.compare(testDataOnlinePrice, testResultPrice);
//				logCompareResults(compareResult, testDataOnlinePrice, testResultPrice, planId);
//				resultsIndex++;
//				
//				if(iteration >= dataRange) {
//					break;
//				}
//				iteration++;
//			}
//			
//		}
//
//
//		logger.info("***** End of Test Step. ***** \n\n");
//	}
	
	@Test(priority = 8)
	private void compareResults() {
		logger.info("***** Validate and Compare Results *****");
		
		for (int i = 0; i < rawPrsExcelData.size(); i++) {
			PrsExcelData data = rawPrsExcelData.get(i);
			double testDataOnlinePrice = data.getOnlinePrice();
			test = extent.createTest("Compare Test Results for " + data.getFunctionName());

			AccountingLog acctgLog = acctgLogList.get(i);
			double testResultPrice = acctgLog.getPrice();
			int planId = acctgLog.getPlanId();

			int compareResult = Double.compare(testDataOnlinePrice, testResultPrice);
			logCompareResults(compareResult, testDataOnlinePrice, testResultPrice, planId);

		}

		logger.info("***** End of Test Step. ***** \n\n");
	}

	@Test(priority = 9)
	private void sendMail() {
		logger.info("***** Send Email Report. *****");
		test = extent.createTest("Send Email Report");
		try {
			if(!usePipelineEmail) {
				logger.info("** Begin sending of email. **");
				MailHelper.sendMail(jiraId, jiraDesc);
				logger.info("***** Email sent. *****");
				test.pass("Email sent successfully");
			} else {
				test.skip("Pipeline Email will be used");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			test.fail("Failed to send the email: \n" + e.getMessage());
		}
		logger.info("***** End of TEST No. 6. ***** \n\n");
	}

	private void logCompareResults(int compareResult, double testDataOnlinePrice, double testResultOnlinePrice,
			int planId) {
		if (compareResult == 0) {
			logger.info("Test data Online Price: " + testDataOnlinePrice + " MATCHES the Test Results Price: "
					+ testResultOnlinePrice + " for Plan ID: " + planId + "\n");
			test.pass("Test data Online Price: " + testDataOnlinePrice + " MATCHES the Test Results Price: "
					+ testResultOnlinePrice + " for Plan ID: " + planId);
		} else {
			logger.info("Test data Online Price: " + testDataOnlinePrice + " DOES NOT MATCH the Test Results Price: "
					+ testResultOnlinePrice + " for Plan ID: " + planId + "\n");
			test.fail("Test data Online Price: " + testDataOnlinePrice + " DOES NOT MATCH the Test Results Price: "
					+ testResultOnlinePrice + " for Plan ID: " + planId);
		}
	}
	
	@AfterTest
	private void endPricingTest() {
		endTime = Instant.now();
		logger.info("End Run: " + endTime);
		logger.info("Elapsed Time: " + Duration.between(startTime, endTime).toMillis() / (60 * 1000));
		extent.flush();
	}

}
