package com.axis.entity;

import java.time.LocalDate;

public class LoanResponse {

	private int loanid;
	private String loantype;
	private double loanamount;
	private double monthlyemi;
	private LocalDate statedate;
	private LocalDate enddate;
	private int duration;
	private String status;
	
	public LoanResponse() {
		super();
	}

	public LoanResponse(int loanid, String loantype, double loanamount, double monthlyemi, LocalDate statedate, LocalDate enddate, int duration, String status) {
		super();
		this.loanid = loanid;
		this.loantype = loantype;
		this.loanamount = loanamount;
		this.monthlyemi = monthlyemi;
		this.statedate = statedate;
		this.enddate = enddate;
		this.duration = duration;
		this.status = status;
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

	@Override
	public String toString() {
		return "LoanResponse [loanid=" + loanid + ", loantype=" + loantype + ", loanamount=" + loanamount
				+ ", monthlyemi=" + monthlyemi + ", statedate=" + statedate + ", enddate=" + enddate + ", duration="
				+ duration + ", status=" + status + "]";
	}	
	
}
