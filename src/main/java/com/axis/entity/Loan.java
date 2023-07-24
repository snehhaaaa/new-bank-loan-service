package com.axis.entity;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="Loan")
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int loanid;
	private String loantype;
	private double loanamount;
	private double monthlyemi;
	private LocalDate statedate;
	private LocalDate enddate;
	private int duration;
	private String status;	

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accid", referencedColumnName = "accid")
    public Account account;

	 @ManyToOne
	    @JoinColumn(name = "user_id")
	    private Users user;
	public Loan() {
		super();
	}

	public Loan(String loantype, double loanamount, double monthlyemi, LocalDate statedate, LocalDate enddate, int duration, String status, Account account) {
		super();
		this.loantype = loantype;
		this.loanamount = loanamount;
		this.monthlyemi = monthlyemi;
		this.statedate = statedate;
		this.enddate = enddate;
		this.duration = duration;
		this.status = status;
		this.account = account;
	}

	public int getLoanid() {
		return loanid;
	}

	public void setLoanid(int loanid) {
		this.loanid = loanid;
	}

	public String getLoantype() {
		return loantype;
	}

	public void setLoantype(String loantype) {
		this.loantype = loantype;
	}

	public double getLoanamount() {
		return loanamount;
	}

	public void setLoanamount(double loanamount) {
		this.loanamount = loanamount;
	}

	public double getMonthlyemi() {
		return monthlyemi;
	}

	public void setMonthlyemi(double monthlyemi) {
		this.monthlyemi = monthlyemi;
	}

	public LocalDate getStatedate() {
		return statedate;
	}

	public void setStatedate(LocalDate statedate) {
		this.statedate = statedate;
	}

	public LocalDate getEnddate() {
		return enddate;
	}

	public void setEnddate(LocalDate enddate) {
		this.enddate = enddate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "Loan [loanid=" + loanid + ", loantype=" + loantype + ", loanamount=" + loanamount + ", monthlyemi="
				+ monthlyemi + ", statedate=" + statedate + ", enddate=" + enddate + ", duration=" + duration
				+ ", status=" + status + ", account=" + account + "]";
	}
	
}
