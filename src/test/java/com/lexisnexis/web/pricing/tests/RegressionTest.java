package com.lexisnexis.web.pricing.tests;

import java.io.IOException;
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
import com.lexisnexis.web.pricing.model.PrsExcelData;
import com.lexisnexis.web.pricing.util.Common;
import com.lexisnexis.web.pricing.util.CsvHelper;
import com.lexisnexis.web.pricing.util.ExcelHelper;

public class RegressionTest {

	public static final Logger logger = LogManager.getLogger(RegressionTest.class);

	private static List<PrsExcelData> prsDataList;
	private static List<AccountingLog> acctgLogList = null;
	private static String fileName;
	private static boolean isFcra = false;
	private static PricingAccurintDao dao;
	private static Instant startTime;
	private static Instant endTime;
	private static String env = "DEV";
	private static boolean usePipelineEmail = false;
	
	private static ExtentHtmlReporter htmlReporter;
	private static ExtentTest test;
	private static ExtentReports extent;
	
	@BeforeTest
	public void init() {
		logger.info("Beginning PRS Regression Test");
		startTime = Instant.now();
		logger.info("Start Run: " + startTime);

		String timeStamp = new SimpleDateFormat(Common.SIMPLE_DATE_FORMAT).format(Calendar.getInstance().getTime());
		fileName = "PRS_Regression_Results_" + timeStamp + ".xlsx";
		new DBConfig(env);
		dao = new PricingAccurintDao();
		initExtentReport();
	}
	
	private void initExtentReport() {
		// Initialize Extent Reports
		String timeStamp = new SimpleDateFormat(Common.SIMPLE_DATE_FORMAT).format(Calendar.getInstance().getTime());
		String strReportPath = Common.TEST_RESULTS_SUMMARY + "Test Results for PRS Regression - " + timeStamp + Common.EXTENT_REPORT_EXT;

		htmlReporter = new ExtentHtmlReporter(strReportPath);
		htmlReporter.config().setDocumentTitle("PRS Regression");
		htmlReporter.config().setReportName("PRS Regression - " + timeStamp);
		htmlReporter.config().setTheme(Theme.STANDARD);
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		
		extent.setSystemInfo("Environment", env);
		extent.setSystemInfo("User Name", Common.SYSTEM_USER_ID);
		extent.setSystemInfo("Host Name", Common.SYSTEM_HOSTNAME);
		extent.setSystemInfo("OS", Common.SYSTEM_OS_NAME_VER);
	}
	
	@Test(priority = 1)
	private void extractRegressionTestData() {
		// Retrieve test data from the Regression document
		test = extent.createTest("Regression Test Data Extraction");
		logger.info("Extracting data from PRS Excel file.");
		try {
			prsDataList = CsvHelper.readData();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			test.fail("Test Data Extraction encountered an Exception " + e.getMessage());
		}
		logger.info("PRS Regression Data has been extracted.");
		if(!prsDataList.isEmpty()) {
			test.pass("PRS Regression Data has been extracted");
		} else {
			test.fail("PRS extraction failed or did not yield any Rt data");
		}
	}
	
	@Test(priority = 2)
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
	
	@Test(priority = 3)
	private void updateCompanyPricingPlan() {
		// Update company pricing plan and get accounting log info
		logger.info("***** Update Company Pricing Plan Table based on Transaction ID. *****");
		test = extent.createTest("Update Company Pricing Plan");
		acctgLogList = dao.updatePricePlan(prsDataList, isFcra);
		
		if(acctgLogList.size() > 0) {
			test.pass("AccountingLog List has been populated");
		} else {
			test.fail("AccountingLog List is empty.");
		}
		
		logger.info("***** End of Test Step. ***** \n\n");
	}
	
	@Test(priority = 4)
	private void writeResultsToExcel() {
		logger.info("***** Write Test Results to Excel. *****");
		test = extent.createTest("Write Results to Excel");
		try {
			logger.info("Start writing to Excel.");
			ExcelHelper.writeResultsToExcel(acctgLogList, fileName);
			logger.info("Done writing to Excel.");
			test.pass("Completed writing to Excel");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			test.fail("Error writing to Excel");
		}
		logger.info("***** End of Test Step. ***** \n\n");
	}
	
	@Test(priority = 5)
	private void compareResults() {
		logger.info("***** Validate and Compare Results *****");
		String prevTransactionId = StringUtils.EMPTY;
		for(int i = 0; i < prsDataList.size(); i++) {
			
			PrsExcelData data = prsDataList.get(i);
			String transactionId = data.getTransactionId();
			
			if(!transactionId.equals(prevTransactionId)) {
				test = extent.createTest("Compare Test Results for " + transactionId);
			}
			prevTransactionId = transactionId;
			
			int planId = Integer.valueOf(data.getPlanId());
			
			double testDataPrice = data.getOnlinePrice();
			double testResultPrice = acctgLogList.get(i).getPrice();
			
			int compareResult = Double.compare(testDataPrice, testResultPrice);
			logCompareResults(compareResult, testDataPrice, testResultPrice, planId);
		}
		logger.info("***** End of Test Step. ***** \n\n");
	}

	@Test(priority = 6)
	private void sendMail() {
		logger.info("***** Send Email Report. *****");
		test = extent.createTest("Send Email Report");
		try {
			if(!usePipelineEmail) {
				logger.info("** Begin sending of email. **");
				MailHelper.sendMail("PRS", "Regression");
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
	private void endPrsRegressionTest() {
		endTime = Instant.now();
		logger.info("End Run: " + endTime);
		logger.info("Elapsed Time: " + Duration.between(startTime, endTime).toMillis() / (60 * 1000));
		extent.flush();
	}

}
