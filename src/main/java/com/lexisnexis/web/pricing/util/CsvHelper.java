package com.lexisnexis.web.pricing.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lexisnexis.web.pricing.model.PrsExcelData;

public class CsvHelper {

	public static final Logger logger = LogManager.getLogger(CsvHelper.class);

	private static final String CSV_REGRESSION_FILE = System.getProperty("user.dir")
			+ "\\src\\main\\resources\\PRS_Regression.csv";

	public static List<PrsExcelData> readData() throws IOException {
		List<PrsExcelData> regressionData = new ArrayList<PrsExcelData>();
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_REGRESSION_FILE))) {
			String line = StringUtils.EMPTY;
			String headerLine = br.readLine();
			String[] headercontent = headerLine.split(",");
			while ((line = br.readLine()) != null) {
				String[] content = line.split(",");
				for (int j = 6; j < content.length; j++) {
					PrsExcelData data = new PrsExcelData();
					data.setFunctionName(content[1].trim());
					data.setTransactionId(content[4].trim());
					data.setCompanyId(content[5].trim());
					data.setPlanId(StringUtils.remove(headercontent[j], "PP-"));
					data.setOnlinePrice(Double.parseDouble(StringUtils.remove(content[j], "$")));
					regressionData.add(data);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return regressionData;
	}

}
