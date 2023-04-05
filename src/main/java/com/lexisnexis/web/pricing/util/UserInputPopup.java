package com.lexisnexis.web.pricing.util;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.lexisnexis.web.pricing.model.JiraTestInfo;

public class UserInputPopup {

	public static int promptInfo(String title, Object[] message) {
		JFrame frame = new JFrame();
		return JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.OK_CANCEL_OPTION);
	}

	public static JiraTestInfo promptPricingInfo() {
		JTextField jiraTicket = new JTextField();
		JTextField jiraTitle = new JTextField();
		
		final String[] fcraList = { PricingConstants.NON_FCRA, PricingConstants.FCRA };
		JComboBox<String> fcra = new JComboBox<>(fcraList);
		
		final String[] environmentList = { PricingConstants.DEV, PricingConstants.QA };
		JComboBox<String> env = new JComboBox<>(environmentList);
		
		final Integer[] indexPositionList = { PricingConstants.ZERO, PricingConstants.ONE };
		JComboBox<Integer> indexPosition = new JComboBox<>(indexPositionList);
		
		Object[] message = { "Jira Ticket No.:", jiraTicket, 
				"Details:", jiraTitle, 
				"Environment:", env, 
				"Is FCRA?", fcra,
				"Report Options Index Position:", indexPosition
				};

		promptInfo("Enter Test Parameters", message);
		
		JiraTestInfo jiraTestInfo = new JiraTestInfo();
		jiraTestInfo.setJiraTicket(jiraTicket.getText());
		jiraTestInfo.setJiraDetails(jiraTitle.getText());
		jiraTestInfo.setEnvironment((String) env.getSelectedItem());
		jiraTestInfo.setIndexPosition((Integer) indexPosition.getSelectedItem());
		
		if (PricingConstants.FCRA.equals((String) fcra.getSelectedItem())) {
			jiraTestInfo.setFcra(true);
		} else {
			jiraTestInfo.setFcra(false);
		}
		
		return jiraTestInfo;
	}

}
