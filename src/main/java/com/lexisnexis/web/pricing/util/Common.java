package com.lexisnexis.web.pricing.util;

public interface Common {
	public static final String SYSTEM_TEST_ENVIRONMENT = "QA";
	public static final String SYSTEM_DR_ENVIRONMENT = "DR";
	public static final String SYSTEM_PROD_ENVIRONMENT = "PROD";
	public static final String SYSTEM_OS_NAME_VER = System.getProperty("os.name") + " - " + System.getProperty("os.version");	
	public static final String SYSTEM_HOSTNAME = System.getenv("COMPUTERNAME");
    public static final String SYSTEM_USER_DIR = System.getProperty("user.dir");
    public static final String SYSTEM_USER_ID = System.getProperty("user.name");
    public static final String SYSTEM_DOWNLOAD_FOLDER = System.getProperty("user.home") + "\\Downloads";
    public static final String CONFIG_FILE_PATH = ("./src/test/resources/config/SBFEconfig.properties");
        
    public static final String TEST_RESULTS_SUMMARY = "./test-results/";    
    public static final String SCREENSHOTS_PATH = "./src/test/resources/screenshots/";
    public static final String EXTENT_CONFIG = "//extent-config.xml";
    public static final String SCREENSHOT_EXT = ".png";
    public static final String EXTENT_REPORT_EXT = ".html";
    public static final String EXTENT_CONF_XML = "extent-config.xml";
    public static final String PDF_EXT = ".pdf";
    
    public static final String PATH_DOWNLOAD_FIREFOX = "./src/test/resources/download/firefox";
    public static final String PATH_DOWNLOAD = "C:\\Downloads";    
    public static final String PATH_DOWNLOAD_CI = "/builds/SBFE/sbfecombinedmodules";
    public static final String PATH_DOWNLOAD_IE = "./src/test/resources/download/ie/";
    
    public static final String PATH_TESTDATA = "./src/test/resources/data/";
    public static final String FILE_TESTDATA = "SBFETests.xlsx";
    public static final String TESTDATA_FILEPATH = PATH_TESTDATA + FILE_TESTDATA;
    
    //ADR Data
    public static final String PATH_ADR_DATA = "./src/test/resources/data/";
    public static final String FILE_ADR_DATA = "ADRData.xlsx";
    public static final String ADR_DATA_FILEPATH = PATH_ADR_DATA + FILE_ADR_DATA;
    public static final String PATH_ADR_DATA_RESULTS = "./src/test/resources/testADRDataResults/";
    public static final String FILE_ADR_DATA_RESULTS = "ADRData";
    
    //Test Results spreadsheet (Excel) per Browser, eg. Firefox, Chrome, IE
    public static final String PATH_TEST_RESULTS_EXCEL = "./src/test/resources/testResultsExcel/";
    public static final String FILE_TEST_RESULTS_EXCEL_EXT = ".xlsx";

    public static final String PATH_PKG_CLASS = "./src/test/resources/data/";
    public static final String FILE_PKG_CLASS = "PkgsClasses.xlsx";
    public static final String PKG_CLASS_FILEPATH = PATH_PKG_CLASS + FILE_PKG_CLASS;

    public static final String Y = "Y";
    public static final String N = "N";
    public static final String YES = "YES";
    public static final String NO = "NO";   
    
    public static final String INTERNET_EXPLORER = "IE";
    
    public static final String US_EASTERN = "US/Eastern";    
    public static final String SIMPLE_DATE_FORMAT = "MMMddyyyy-HHmm";
    public static final String CAPTCHA = "1";

    public static final String TD_COLUMN_NAMES = "Test Case Number,Test Suite,Automation Test,Test Name,Test Description,Execute,Test Status,Date Tested,Tested By,Remarks";
    public static final String DELIMETER = ",";
    public static final String SYSLNASTER = "************************************************************************************************************";
    public static final String SYSLNEQUAL = "============================================================================================================";
}
