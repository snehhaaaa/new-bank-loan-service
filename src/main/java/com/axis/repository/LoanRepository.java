package com.axis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.axis.entity.Loan;
import com.axis.entity.Users;

public interface LoanRepository extends JpaRepository<Loan, Integer> {

	@Query("SELECT l FROM Loan l WHERE l.account.accid = :accid")
	public List<Loan> getAllMyLoansByAccountId(int accid);
	
	@Modifying
	@Transactional
    @Query("UPDATE Loan l SET l.status = 'ACTIVE' WHERE l.loanid = :loanid")
    public void updateLoanStatus(int loanid);
	
	@Query("SELECT l FROM Loan l WHERE l.status = 'PENDING'")
	public List<Loan> findByStatusPending();
	
	@Modifying
	@Transactional
    @Query("UPDATE Loan l SET l.status = 'COMPLETED' WHERE l.loanid = :loanid")
    public void foreCloseLoan(int loanid);
	
	@Query("SELECT l FROM Loan l WHERE l.loanid = :loanid")
	public Loan findById(int loanid);
	
	
	
	 Loan findByLoantypeAndStatus(String loantype, String status);
	 
	 boolean existsByLoantypeAndStatus(String loantype, String status);
	
}
