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
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/customer/loan")
//@CrossOrigin("http://localhost:3000/")
public class CustomerController {
	
	@Autowired
	private UserDetailsServiceImpl service;
	
	@GetMapping("/my-loans")
	public List<LoanResponse> myLoans(HttpServletRequest request) {
		String username = request.getAttribute("username").toString();
		Users user = service.findUser(username);
		int accid = service.findAccId(user.getId());
		List<Loan> lLoan = service.fetchAllMyLoans(accid);
		
		List<LoanResponse> lLoanResponse = new ArrayList<>();
		for(Loan l : lLoan) {
			LoanResponse loanResponse = new LoanResponse(l.getLoanid(),l.getLoantype(),l.getLoanamount(),l.getMonthlyemi(),l.getStatedate(),l.getEnddate(),l.getDuration(),l.getStatus());
			lLoanResponse.add(loanResponse);
		}
		return lLoanResponse;
	}
	
	@PostMapping("/apply-loan")
	public String applyLoan(HttpServletRequest request, @RequestBody Map<String, Object> requestData) {
	    String username = request.getAttribute("username").toString();
	    Users user = service.findUser(username);

	    String loantype = String.valueOf(requestData.get("loantype").toString());
	    double loanamount = Double.valueOf(requestData.get("loanamount").toString());
	    int duration = Integer.valueOf(requestData.get("duration").toString());
	    
	 
	    // Check if the user already has an active loan of the same type
	    if (service.hasActiveLoanOfType(loantype)) {
	        return "Loan application failed. You already have an active loan of type " + loantype + ".";
	    }
	  

	 

	    service.applyLoan(loantype, loanamount, duration, user.getId());

	    // Sending email
	    String senderEmail = "axisbank.confirmationmail@gmail.com";
	    String senderPassword = "cxhqkconrmkjetrr"; // Replace with the actual password
	    String recipientEmail = user.getEmail(); // Replace with the recipient's email address
	    String subject = "Loan Application Confirmation";
	    String messageBody = "Dear " + username + ",\n\n"
	                        + "Your loan application for " + loantype + " has been received and is under review.\n"
	                        + "Loan Amount: " + loanamount + "\n"
	                        + "Duration: " + duration + " months\n\n"
	                        + "We will get back to you soon with further details.\n\n"
	                        + "Thank you,\n"
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
	        return "You've successfully applied for " + loantype + ". Confirmation email has been sent.";
	    } catch (MessagingException e) {
	        return "You've successfully applied for " + loantype + ", but there was an error sending the confirmation email.";
	    }
	}

	
//	@PutMapping("/loan-payment")
//	public String loanPayment(HttpServletRequest request, @RequestBody Map<String, Object> requestData) {
//	    String username = request.getAttribute("username").toString();
//	    Users user = service.findUser(username);
//	    int accid = service.findAccId(user.getId());
//
//	    String loantype = String.valueOf(requestData.get("loantype").toString());
//	    double loanemi = Double.valueOf(requestData.get("loanemi").toString());
//
//	    service.loanPayment(loantype, loanemi, accid, user.getId());
//
//	    // Sending email
//	    String senderEmail = "axisbank.confirmationmail@gmail.com";
//	    String senderPassword = "cxhqkconrmkjetrr"; // Replace with the actual password
//	    String recipientEmail =user.getEmail(); // Replace with the recipient's email address
//	    String subject = "Loan Payment Confirmation";
//	    String messageBody = "Dear " + username + ",\n\n"
//	                        + "Your EMI payment for " + loantype + " has been successfully processed.\n"
//	                        + "Loan EMI: " + loanemi + "\n\n"
//	                        + "Thank you for your payment.\n\n"
//	                        + "Regards,\n"
//	                        + "Axis Bank";
//
//	    Properties properties = System.getProperties();
//	    properties.put("mail.smtp.auth", "true");
//	    properties.put("mail.smtp.ssl.enable", "true");
//	    properties.put("mail.smtp.host", "smtp.gmail.com");
//	    properties.put("mail.smtp.port", "465");
//
//	    Session session = Session.getInstance(properties, new Authenticator() {
//	        protected PasswordAuthentication getPasswordAuthentication() {
//	            return new PasswordAuthentication(senderEmail, senderPassword);
//	        }
//	    });
//
//	    session.setDebug(true);
//
//	    try {
//	        MimeMessage message = new MimeMessage(session);
//	        message.setFrom(new InternetAddress(senderEmail));
//	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
//	        message.setSubject(subject);
//	        message.setText(messageBody);
//
//	        Transport.send(message);
//	        return loantype + " EMI payment is done successfully. Confirmation email has been sent.";
//	    } catch (MessagingException e) {
//	        return loantype + " EMI payment is done successfully, but there was an error sending the confirmation email.";
//	    }
//	}
	@PutMapping("/loan-payment")
	public String loanPayment(HttpServletRequest request, @RequestBody Map<String, Object> requestData) {
	    String username = request.getAttribute("username").toString();
	    Users user = service.findUser(username);
	    int accid = service.findAccId(user.getId());

	    String loantype = String.valueOf(requestData.get("loantype").toString());
	    double loanemi = Double.valueOf(requestData.get("loanemi").toString());

	    // Call the service method to update the loan amount and get the outstanding amount
	    double outstandingAmount = service.updateLoanAmount(loantype, loanemi, accid, user.getId());

	    // Sending email
	    String senderEmail = "axisbank.confirmationmail@gmail.com";
	    String senderPassword = "cxhqkconrmkjetrr"; // Replace with the actual password
	    String recipientEmail = user.getEmail(); // Replace with the recipient's email address
	    String subject = "Loan Payment Confirmation";
	    String messageBody;

	    if (outstandingAmount <= 0) {
	        // Loan completed
	        service.markLoanAsCompleted(loantype,"active");

	        messageBody = "Dear " + username + ",\n\n"
	                + "Congratulations! Your EMI payment for " + loantype + " has been successfully processed.\n"
	                + "Loan EMI: " + loanemi + "\n\n"
	                + "Your loan for " + loantype + " has been fully paid. The loan is now completed.\n\n"
	                + "Thank you for your payment.\n\n"
	                + "Regards,\n"
	                + "Axis Bank";
	    } else {
	        messageBody = "Dear " + username + ",\n\n"
	                + "Your EMI payment for " + loantype + " has been successfully processed.\n"
	                + "Loan EMI: " + loanemi + "\n\n"
	                + "Outstanding Loan Amount: " + outstandingAmount + "\n\n"
	                + "Thank you for your payment.\n\n"
	                + "Regards,\n"
	                + "Axis Bank";
	    }

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

	        if (outstandingAmount <= 0) {
	            return loantype + " EMI payment is done successfully. Your loan is now completed. Confirmation email has been sent.";
	        } else {
	            return loantype + " EMI payment is done successfully. Outstanding loan amount: " + outstandingAmount + ". Confirmation email has been sent.";
	        }
	    } catch (MessagingException e) {
	        return "EMI payment status updated successfully, but there was an error sending the confirmation email.";
	    }
	}

}
