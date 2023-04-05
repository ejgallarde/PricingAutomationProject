package com.lexisnexis.web.pricing.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.lexisnexis.web.pricing.model.AccountingLog;
import com.lexisnexis.web.pricing.model.PrsExcelData;

public class ExcelHelper {
	
	private static Sheet sheet;
	private static CellStyle yellowCellStyle;
	private static CellStyle boldCellStyle;
	private static CellStyle dateCellStyle;
	private static CellStyle currencyCellStyle;
	
	public static final Logger logger = LogManager.getLogger(ExcelHelper.class);
	
	public static final String PRS_TEST_DATA = System.getProperty("user.dir")
	+ "\\src\\main\\resources\\PRS_TestData.xlsx";
	public static final String RESULTS_DATA = System.getProperty("user.dir") + "\\test-results\\";
	
	public static final String QUERY_PRS_DATA = "SELECT PlanID, FunctionName, PricingFactor, TransactionType, "
			+ "ReportOptions, OnlinePrice, ReportOptionsOnlinePrice, ReportOptionsPosition, Discountable, DiscountableRO FROM Pricing";
	
	public static List<PrsExcelData> getPrsData() {
		List<PrsExcelData> prsList = new ArrayList<PrsExcelData>();
		
		Connection connection = null;
		Recordset rs = null;
		try {
			Fillo fillo=new Fillo();
			connection = fillo.getConnection(PRS_TEST_DATA);
			rs = connection.executeQuery(QUERY_PRS_DATA);
			
			while(rs.next()) {
				PrsExcelData data = new PrsExcelData();
				data.setPlanId((rs.getField(PrsExcelData.PLAN_ID)));
				logger.info("Plan ID: " + data.getPlanId());
				data.setFunctionName(rs.getField(PrsExcelData.FUNCTION_NAME));
				data.setPricingFactor(rs.getField(PrsExcelData.PRICING_FACTOR));
				data.setReportOptions(rs.getField(PrsExcelData.REPORT_OPTIONS));
				data.setTransactionType(rs.getField(PrsExcelData.TRANSACTION_TYPE));
				
				String onlinePrice = null;
				String onlinePriceReportOption = null;
				if(rs.getField(PrsExcelData.REPORT_OPTIONS).equalsIgnoreCase("Y")) {
					data.setDiscountable(rs.getField(PrsExcelData.DISCOUNTABLE_RO));
					try {
						onlinePrice = rs.getField(PrsExcelData.ONLINE_PRICE);
						onlinePriceReportOption = rs.getField(PrsExcelData.RO_ONLINE_PRICE);
						
						double onlinePriceValue = Double.parseDouble(StringUtils.remove(onlinePrice, "$"));
						double onlinePriceReportOptionValue = Double.parseDouble(StringUtils.remove(onlinePriceReportOption, "$"));
						
						data.setOnlinePrice(onlinePriceReportOptionValue + onlinePriceValue);
						data.setReportOptionsPosition(Integer.parseInt(rs.getField(PrsExcelData.REPORT_OPTIONS_POSITION)));
					} catch (NumberFormatException e) {
						logger.info(e.getMessage(), e);
						data.setOnlinePrice(0.0);
					}
				} else {
					data.setDiscountable(rs.getField(PrsExcelData.DISCOUNTABLE));
					try {
						onlinePrice = rs.getField(PrsExcelData.ONLINE_PRICE);
						data.setOnlinePrice(Double.parseDouble(StringUtils.remove(onlinePrice, "$")));
					} catch (NumberFormatException e) {
						logger.info(e.getMessage(), e);
						data.setOnlinePrice(0.0);
					}
				}
				prsList.add(data);
			}
			
		} catch (FilloException e) {
			logger.error("Failed to extract data from excel file.");
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Error running getPrsData.");
			logger.error(e.getMessage(), e);
		} finally {
			rs.close();
			connection.close();
		}

		return prsList;
	}

	public static List<PrsExcelData> filterRawData(List<PrsExcelData> rawPrsData) {
		
		int startId = 0;
		int endId = 0;
		boolean isSameFuncName = false;
		String prevFuncName = null;
		String currentFuncName = null;
		double prevOnlinePrice = 0.0;
		String prevReportOptions = null;
		String prevDiscountable = null;
		String prevPricingFactor = null;
		int prevReportOptionsPosition = 0;
		String prevTransactionType = null;
		boolean isPrevPlanIdAnInteger = false;
		List<PrsExcelData> filteredList = new ArrayList<PrsExcelData>();
		for(int i=0; i < rawPrsData.size(); i++) {
			PrsExcelData data = rawPrsData.get(i);
			String planId = data.getPlanId();
			boolean isPlanIdAnInteger = !planId.contains("-") && !planId.contains(",");
			currentFuncName = data.getFunctionName();
			
			isSameFuncName = currentFuncName.equals(prevFuncName);
			if(!isSameFuncName && i != 0 && isPrevPlanIdAnInteger) {
				PrsExcelData newData = new PrsExcelData();
				newData.setFunctionName(prevFuncName);
				newData.setEndPlanId(endId);
				newData.setStartPlanId(startId);
				newData.setOnlinePrice(prevOnlinePrice);
				newData.setReportOptions(prevReportOptions);
				newData.setDiscountable(prevDiscountable);
				newData.setPricingFactor(prevPricingFactor);
				newData.setReportOptionsPosition(prevReportOptionsPosition);
				newData.setTransactionType(prevTransactionType);
				filteredList.add(newData);
			}
			
			if(isSameFuncName) {
				endId = Integer.parseInt(data.getPlanId());
			} else {
				
				if(!isPlanIdAnInteger) {
					String[] startIdRanges = planId.split(",");
					for (String range : startIdRanges) {
						range = StringUtils.deleteWhitespace(range);
						String[] numbers = range.split("-");
						startId = Integer.parseInt(numbers[0]);
						if (numbers.length > 1) {
							endId = Integer.parseInt(numbers[1]);
						} else {
							endId = Integer.parseInt(numbers[0]);
						}
						
						PrsExcelData newData = new PrsExcelData();
						newData.setFunctionName(currentFuncName);
						newData.setEndPlanId(endId);
						newData.setStartPlanId(startId);
						newData.setOnlinePrice(data.getOnlinePrice());
						newData.setReportOptions(data.getReportOptions());
						newData.setDiscountable(data.getDiscountable());
						newData.setReportOptionsPosition(data.getReportOptionsPosition());
						newData.setPricingFactor(data.getPricingFactor());
						newData.setTransactionType(data.getTransactionType());
						filteredList.add(newData);
					}
					prevFuncName = currentFuncName;
					isPrevPlanIdAnInteger = isPlanIdAnInteger;
					prevOnlinePrice = data.getOnlinePrice();
					prevPricingFactor = data.getPricingFactor();
					prevReportOptions = data.getReportOptions();
					prevDiscountable = data.getDiscountable();
					prevTransactionType = data.getTransactionType();
					continue;
				} else {
					startId = Integer.parseInt(planId);
					endId = Integer.parseInt(planId);
					isPrevPlanIdAnInteger = isPlanIdAnInteger;
				}
			}
			
			prevFuncName = currentFuncName;
			prevOnlinePrice = data.getOnlinePrice();
			prevPricingFactor = data.getPricingFactor();
			prevReportOptions = data.getReportOptions();
			prevDiscountable = data.getDiscountable();
			prevReportOptionsPosition = data.getReportOptionsPosition();
			prevTransactionType = data.getTransactionType();
			
			if(i == rawPrsData.size()-1) {
				PrsExcelData newData = new PrsExcelData();
				newData.setFunctionName(currentFuncName);
				newData.setEndPlanId(endId);
				newData.setStartPlanId(startId);
				newData.setOnlinePrice(data.getOnlinePrice());
				newData.setReportOptions(data.getReportOptions());
				newData.setDiscountable(data.getDiscountable());
				newData.setPricingFactor(data.getPricingFactor());
				newData.setReportOptionsPosition(data.getReportOptionsPosition());
				newData.setTransactionType(data.getTransactionType());
				filteredList.add(newData);
			}
		}
		
		return filteredList;
	}
	
	
	public static void writeResultsToExcel(List<AccountingLog> acctgLogList, String fileName) throws IOException {
	    Workbook workbook = getWorkbook(fileName);
	    sheet = workbook.createSheet();
	    createHeaderRow();
	    createStyles();
	    
	    int rowCount = 0;
	 
	    for (AccountingLog acctgLog  : acctgLogList) {
	        Row row = sheet.createRow(++rowCount);
	        writeBook(acctgLog, row);
	    }
	 
	    try (FileOutputStream outputStream = new FileOutputStream(RESULTS_DATA + fileName)) {
	        workbook.write(outputStream);
	    }
	    workbook.close();
	}
	
	private static void createStyles() {
		yellowCellStyle = sheet.getWorkbook().createCellStyle();
		yellowCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		yellowCellStyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
		yellowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		boldCellStyle = sheet.getWorkbook().createCellStyle();
		Font font = sheet.getWorkbook().createFont();
	    font.setBold(true);
		boldCellStyle.setFont(font);
		
		CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
		dateCellStyle = sheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMMM dd, yyyy HH:mm:ss"));
		
		currencyCellStyle = sheet.getWorkbook().createCellStyle();
		currencyCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00\\ _$"));
		
		yellowCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00\\ _$"));
	}


	private static Workbook getWorkbook(String excelFilePath)
	        throws IOException {
	    Workbook workbook = null;
	 
	    if (excelFilePath.endsWith("xlsx")) {
	        workbook = new XSSFWorkbook();
	    } else if (excelFilePath.endsWith("xls")) {
	        workbook = new HSSFWorkbook();
	    } else {
	        throw new IllegalArgumentException("The specified file is not an Excel file");
	    }
	 
	    return workbook;
	}
		
	private static void writeBook(AccountingLog acctgLog, Row row) {
	    Cell cell = row.createCell(0);
	    cell.setCellValue(acctgLog.getTransactionId());
	    cell.setCellStyle(boldCellStyle);
	 
	    cell = row.createCell(1);
	    cell.setCellValue(acctgLog.getLoginId());
	 
	    cell = row.createCell(2);
	    cell.setCellValue(acctgLog.getCompanyId());
		
	    cell = row.createCell(3);
	    cell.setCellValue(acctgLog.getTransactionType());

	    cell = row.createCell(4);
	    cell.setCellValue(acctgLog.getFunctionName());
	    
	    cell = row.createCell(5);
	    cell.setCellValue(acctgLog.getDescription());

	    cell = row.createCell(6);
	    cell.setCellValue(acctgLog.getRecordCount());
		
	    cell = row.createCell(7);
	    cell.setCellValue(acctgLog.getReportOptions());

	    cell = row.createCell(8);
	    cell.setCellValue(acctgLog.getResultFormat());
	    
	    cell = row.createCell(9);
		cell.setCellValue(acctgLog.getDateAdded());
		cell.setCellStyle(dateCellStyle);

	    cell = row.createCell(10);
	    cell.setCellValue(acctgLog.getPrice());
	    cell.setCellStyle(yellowCellStyle);
	    
	    cell = row.createCell(11);
	    cell.setCellValue(acctgLog.getRetailPrice());
	    cell.setCellStyle(currencyCellStyle);

	    cell = row.createCell(12);
	    cell.setCellValue(acctgLog.getFree());
		
	    cell = row.createCell(13);
	    cell.setCellValue(acctgLog.getPricingErrorCode());

	    cell = row.createCell(14);
	    cell.setCellValue(acctgLog.getPlanId());
	    
	    cell = row.createCell(15);
	    cell.setCellValue(acctgLog.getDiscountPercent());
	}
	
	private static void createHeaderRow() {
		 
	    CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
	    Font font = sheet.getWorkbook().createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 14);
	    cellStyle.setFont(font);
	 
	    Row row = sheet.createRow(0);

	    Cell cellTxnId = row.createCell(0);
	    cellTxnId.setCellStyle(cellStyle);
	    cellTxnId.setCellValue("Transaction ID");
	 
	    Cell cellLoginId = row.createCell(1);
	    cellLoginId.setCellStyle(cellStyle);
	    cellLoginId.setCellValue("Login ID");
	 
	    Cell cellCompId = row.createCell(2);
	    cellCompId.setCellStyle(cellStyle);
	    cellCompId.setCellValue("Company ID");
	    
	    Cell cellTxnType = row.createCell(3);
	    cellTxnType.setCellStyle(cellStyle);
	    cellTxnType.setCellValue("Transaction Type");
	 
	    Cell cellFuncName = row.createCell(4);
	    cellFuncName.setCellStyle(cellStyle);
	    cellFuncName.setCellValue("Function Name");
	   
	    Cell cellDescription = row.createCell(5);
	    cellDescription.setCellStyle(cellStyle);
	    cellDescription.setCellValue("Description");
	 
	    Cell cellRecordCount = row.createCell(6);
	    cellRecordCount.setCellStyle(cellStyle);
	    cellRecordCount.setCellValue("Record Count");
	 
	    Cell cellReportOptions = row.createCell(7);
	    cellReportOptions.setCellStyle(cellStyle);
	    cellReportOptions.setCellValue("Report Options");
	    
	    Cell cellResultFormat = row.createCell(8);
	    cellResultFormat.setCellStyle(cellStyle);
	    cellResultFormat.setCellValue("Result Format");
	 
	    Cell cellDateAdded = row.createCell(9);
	    cellDateAdded.setCellStyle(cellStyle);
	    cellDateAdded.setCellValue("Date Added");
	 
	    Cell cellPrice = row.createCell(10);
	    cellPrice.setCellStyle(cellStyle);
	    cellPrice.setCellValue("Price");
	    
	    Cell cellRetailPrice = row.createCell(11);
	    cellRetailPrice.setCellStyle(cellStyle);
	    cellRetailPrice.setCellValue("Retail Price");
	 
	    Cell cellFree = row.createCell(12);
	    cellFree.setCellStyle(cellStyle);
	    cellFree.setCellValue("Free");
	 
	    Cell cellPricingErrorCode = row.createCell(13);
	    cellPricingErrorCode.setCellStyle(cellStyle);
	    cellPricingErrorCode.setCellValue("Pricing Error Code");
	    
	    Cell cellPlanId = row.createCell(14);
	    cellPlanId.setCellStyle(cellStyle);
	    cellPlanId.setCellValue("Plan Id");
	 
	    Cell cellDiscountPercent = row.createCell(15);
	    cellDiscountPercent.setCellStyle(cellStyle);
	    cellDiscountPercent.setCellValue("Discount Percent");
	}
}
