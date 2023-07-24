package com.axis.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.axis.entity.Account;
import com.axis.entity.Loan;
import com.axis.entity.Transaction;
import com.axis.entity.Users;
import com.axis.repository.AccountRepository;
import com.axis.repository.CustomerTransactionRepository;
import com.axis.repository.LoanRepository;
import com.axis.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository ur;
	
	@Autowired
	private AccountRepository ar;
	
	@Autowired
	private LoanRepository lr;
	
	@Autowired
	private CustomerTransactionRepository ctr;
 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("Validating user...................."+username);
		Users result = ur.findByUsername(username);
		System.out.println("User validation successful........."+result.getUsername());
		return new UserDetailsImpl(result);
	}
	
	public List<Loan> fetchAllMyLoans(int accid) {
		return lr.getAllMyLoansByAccountId(accid);
	}
	
	public void applyLoan(String loantype,double loanamount,int duration,int userid) {
		LocalDate startdate = LocalDate.now();
		LocalDate enddate = startdate.plusMonths(60);
		Loan loan = new Loan(loantype,loanamount,calculateEMI(loanamount, duration),startdate,enddate,duration,"PENDING",ar.findAccountByUserId(userid));
		lr.save(loan);
	}
	
	public List<Loan> fetchAllLoans() {
		return lr.findAll();
	}
	
	public void activeLoanStatus(int loanid) {
		lr.updateLoanStatus(loanid);
	}
	
	public List<Loan> fetchAllPendingLoans() {
		return lr.findByStatusPending();
	}
	
	public void closeLoan(int loanid) {
		lr.foreCloseLoan(loanid);
	}
	
	public Loan findLoanByLoanId(int loanid) {
		return lr.findById(loanid);
	}
	
	public void updateAmountByAccId(double amount,int accid) {
		ar.updateAmount(amount, accid);
	}
	
	public void loanDeposit(double amount,String loantype,Account account) {
		Transaction t=new Transaction(generateTransactionId(), LocalDateTime.now(), amount, loantype + " Deposit", "CREDIT", account);
		ctr.save(t);
	}
	
	public void loanPayment(String loantype,double loanemi,int accid,int userid) {
		ar.loanPayment(loanemi,accid);
		Transaction t=new Transaction(generateTransactionId(), LocalDateTime.now(), loanemi, loantype + " EMI Withdrawal", "DEBIT", ar.findAccountByUserId(userid));
		ctr.save(t);
	}
	
	public static double calculateEMI(double loanAmount, int loanDurationInMonths) {
        double interestRate = 13;
        double monthlyInterestRate = (interestRate / 100) / 12;
        double emi = (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanDurationInMonths)) / (Math.pow(1 + monthlyInterestRate, loanDurationInMonths) - 1);
        return Math.round(emi * 100.0) / 100.0;
    }
	
	public static String generateTransactionId() {
		String ALPHANUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	    int ID_LENGTH = 15;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int randomIndex = random.nextInt(ALPHANUMERIC_CHARS.length());
            char randomChar = ALPHANUMERIC_CHARS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

	public Users findUser(String username) {
		return ur.findByUsername(username);
	}
	
	 public Users findUserById(int userid) {
	        return ur.findUserById(userid);
	    }
	
	public int findAccId(int userid) {
		return ar.findAccountIdByUserId(userid);
	}

	
	 
	 public double updateLoanAmount(String loantype, double loanemi, int accid, int userid) {
	        // Fetch the loan details from the database based on the loan type and user's account ID
	        Loan loan =  fetchActiveLoanByType(loantype);

	        if (loan == null) {
	            // Loan not found, return some error value or throw an exception
	            throw new RuntimeException("Loan not found");
	        }

	        // Calculate the outstanding amount after deducting the EMI payment
	       
	        double outstandingAmount = loan.getLoanamount() - loanemi;
            loanPayment(loantype,loanemi,accid,userid);
	        if (outstandingAmount <= 0) {
	            // If outstanding amount is less than or equal to zero, update it to zero
	            outstandingAmount = 0;
	        }

	        // Update the loan amount in the database
	        updateLoanAmountInDatabase(loan.getLoanid(), outstandingAmount);

	        return outstandingAmount;
	    }

	 private Loan fetchActiveLoanByType(String loantype) {
		    // Hypothetical implementation to fetch the active loan from the database
		    return lr.findByLoantypeAndStatus(loantype, "active");
		}

	    // Method to update the loan amount in the database
	    private void updateLoanAmountInDatabase(int loanId, double outstandingAmount) {
	        // Hypothetical implementation to update the loan amount in the database
	        Loan loan = lr.findById(loanId);

	        if (loan != null) {
	            loan.setLoanamount(outstandingAmount);
	            lr.save(loan);
	        } else {
	            // Handle the case where the loan is not found in the database
	            throw new RuntimeException("Loan not found in the database");
	        }
	    }
	    
	    public void markLoanAsCompleted(String loantype, String status) {
	        // Fetch the active loan based on the loan type
	        Loan loan = fetchActiveLoanByType(loantype);

	        if (loan == null) {
	            throw new RuntimeException("Active loan with the specified loan type not found.");
	        }

	        // Mark the loan as completed by updating the status to "completed"
	        loan.setStatus("COMPLETED");
	        lr.save(loan);
	    }
	    
	    public boolean hasActiveLoanOfType(String loantype) {
	        return lr.existsByLoantypeAndStatus(loantype, "active");
	    }
	}


