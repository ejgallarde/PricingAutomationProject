package com.lexisnexis.web.pricing.db.util;

import java.util.List;

public class DbHelper {

	public static String buildInQuery(String baseQuery, List<String> transactionIdList) {
		StringBuilder builder = new StringBuilder(baseQuery);
		
		if(transactionIdList.size() > 1) {
			builder.append(" IN (");
			for( int i = 0 ; i < transactionIdList.size(); i++ ) {
			    if(i == transactionIdList.size()-1) {
			    	builder.append("?");
			    } else {
			    	builder.append("?,");
			    }
			}
			builder.append(")");
		} else if (transactionIdList.size() == 1) {
			builder.append(" = ?");
		}
		return new String(builder);
	}
}
