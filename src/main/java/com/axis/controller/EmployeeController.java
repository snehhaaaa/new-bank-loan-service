package com.axis.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axis.entity.Loan;
import com.axis.entity.LoanResponse;
import com.axis.entity.Users;
import com.axis.service.UserDetailsServiceImpl;

@RestController
@CrossOrigin(origins = "http://localhost:3000",allowCredentials="true")
@RequestMapping("/employee/loan")
public class EmployeeController {
	
	@Autowired
	private UserDetailsServiceImpl service;
	
	@GetMapping("/pending-loans")
	public List<LoanResponse> pendingLoanRequests() {		
		List<Loan> lLoan = service.fetchAllPendingLoans();
		List<LoanResponse> lLoanResponse = new ArrayList<>();
		for(Loan l : lLoan) {
			LoanResponse loanResponse = new LoanResponse(l.getLoanid(),l.getLoantype(),l.getLoanamount(),l.getMonthlyemi(),l.getStatedate(),l.getEnddate(),l.getDuration(),l.getStatus());
			lLoanResponse.add(loanResponse);
		}
		return lLoanResponse;
	}
	
	@PutMapping("/activate-loan")
	public String activateLoan(@RequestBody Map<String, Object> requestData) {
	    int loanid = Integer.valueOf(requestData.get("loanid").toString());
	    Loan loan = service.findLoanByLoanId(loanid);

	    service.activeLoanStatus(loanid);
	    service.updateAmountByAccId(loan.getLoanamount(), loan.getAccount().getAccid());
	    service.loanDeposit(loan.getLoanamount(), loan.getLoantype(), loan.getAccount());
	    
	    Users user = service.findUserById(loan.account.getUserid());
	    // Check if the user exists
	 	    if (user == null) {
	 	        return "Locker activation failed. User not found.";
	 	    }

	    // Sending email
	    String senderEmail = "axisbank.confirmationmail@gmail.com";
	    String senderPassword = "cxhqkconrmkjetrr"; // Replace with the actual password
	    String recipientEmail = user.getEmail(); // Replace with the recipient's email address
	    String subject = "Loan Activation Confirmation";
	    String messageBody = "Dear Customer,\n\n"
	                        + "Congratulations! Your " + loan.getLoantype() + " loan has been activated successfully.\n\n"
	                        + "Loan Amount: " + loan.getLoanamount() + "\n\n"
	                        + "Thank you for choosing Axis Bank.\n\n"
	                        + "Best Regards,\n"
	                        + "Axis Bank";

	    Properties properties = System.getProperties();
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.smtp.ssl.enable", "true");
	    properties.put("mail.smtp.host", "smtp.gmail.com");
	    properties.put("mail.smtp.port", "465");

	    Session session = Session.getInstance(properties, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(senderEmail, senderPassword);
	        }
	    });

	    session.setDebug(true);

	    try {
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(senderEmail));
	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
	        message.setSubject(subject);
	        message.setText(messageBody);

	        Transport.send(message);
	        return loan.getLoantype() + " is activated successfully. Confirmation email has been sent.";
	    } catch (MessagingException e) {
	        return loan.getLoantype() + " is activated successfully, but there was an error sending the confirmation email.";
	    }
	}

	
	@PutMapping("/close-loan")
	public String closeLoan(@RequestBody Map<String, Object> requestData) {
	    int loanid = Integer.valueOf(requestData.get("loanid").toString());
	    Loan loan = service.findLoanByLoanId(loanid);

	    service.closeLoan(loanid);
	    
	    Users user = service.findUserById(loan.account.getUserid());
	    // Check if the user exists
	 	    if (user == null) {
	 	        return "Locker activation failed. User not found.";
	 	    }

	    // Sending email
	    String senderEmail = "axisbank.confirmationmail@gmail.com";
	    String senderPassword = "cxhqkconrmkjetrr"; // Replace with the actual password
	    String recipientEmail = user.getEmail(); // Replace with the recipient's email address
	    String subject = "Loan Closure Confirmation";
	    String messageBody = "Dear Customer,\n\n"
	                        + "We would like to inform you that your " + loan.getLoantype() + " loan has been successfully closed.\n\n"
	                        + "Loan Amount: " + loan.getLoanamount() + "\n\n"
	                        + "Thank you for choosing Axis Bank.\n\n"
	                        + "Best Regards,\n"
	                        + "Axis Bank";

	    Properties properties = System.getProperties();
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.smtp.ssl.enable", "true");
	    properties.put("mail.smtp.host", "smtp.gmail.com");
	    properties.put("mail.smtp.port", "465");

	    Session session = Session.getInstance(properties, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(senderEmail, senderPassword);
	        }
	    });

	    session.setDebug(true);

	    try {
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(senderEmail));
	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
	        message.setSubject(subject);
	        message.setText(messageBody);

	        Transport.send(message);
	        return "Loan is foreclosed successfully. Confirmation email has been sent.";
	    } catch (MessagingException e) {
	        return "Loan is foreclosed successfully, but there was an error sending the confirmation email.";
	    }
	}

	
	@GetMapping("/all-loans")
	public List<LoanResponse> allLoans() {		
		List<Loan> lLoan = service.fetchAllLoans();
		List<LoanResponse> lLoanResponse = new ArrayList<>();
		for(Loan l : lLoan) {
			LoanResponse loanResponse = new LoanResponse(l.getLoanid(),l.getLoantype(),l.getLoanamount(),l.getMonthlyemi(),l.getStatedate(),l.getEnddate(),l.getDuration(),l.getStatus());
			lLoanResponse.add(loanResponse);
		}
		return lLoanResponse;
	}
	
}
