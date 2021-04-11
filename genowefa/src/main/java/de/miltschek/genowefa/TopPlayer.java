package de.miltschek.genowefa;

public class TopPlayer {
	private String companyName;
	private long topIncome;
	private long maxLoan;
	private long topCash;
	private long topCompanyValue;
	private int topPerformance;
	private long gameStartTs;
	private long gameFinishTs;
	
	public TopPlayer(String companyName,
			long topIncome,
			long maxLoan,
			long topCash,
			long topCompanyValue,
			int topPerformance,
			long gameStartTs,
			long gameFinishTs) {
		super();
		this.companyName = companyName;
		this.topIncome = topIncome;
		this.maxLoan = maxLoan;
		this.topCash = topCash;
		this.topCompanyValue = topCompanyValue;
		this.topPerformance = topPerformance;
		this.gameStartTs = gameStartTs;
		this.gameFinishTs = gameFinishTs;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public long getTopIncome() {
		return topIncome;
	}
	
	public long getMaxLoan() {
		return maxLoan;
	}
	
	public long getTopCash() {
		return topCash;
	}
	
	public long getTopCompanyValue() {
		return topCompanyValue;
	}
	
	public int getTopPerformance() {
		return topPerformance;
	}
	
	public long getGameStartTs() {
		return gameStartTs;
	}
	
	public long getGameFinishTs() {
		return gameFinishTs;
	}
}
