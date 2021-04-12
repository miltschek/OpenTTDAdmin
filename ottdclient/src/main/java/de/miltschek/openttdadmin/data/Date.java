/*
 * The majority of the file is part of OpenTTD.
 * OpenTTD is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2.
 * OpenTTD is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with OpenTTD. If not, see <http://www.gnu.org/licenses/>. 
 */
package de.miltschek.openttdadmin.data;

/**
 * Date conversion utility.
 */
public final class Date {
	private int rawValue;
	private int day;
	private int month;
	private int year;
	
	/**
	 * Creates an instance of the date.
	 * @param date date value as reported by an OTTD server
	 * 
	 * The code of the function comes from the original code of the game, licensed under GNU GPL v2,
	 * specifically the function void ConvertDateToYMD(Date date, YearMonthDay *ymd) from date.cpp-file.
	 */
	public Date(int date) {
		this.rawValue = date;
		/* Year determination in multiple steps to account for leap
		 * years. First do the large steps, then the smaller ones.
		 */
		
		final int DAYS_IN_YEAR      = 365;
		final int DAYS_IN_LEAP_YEAR = 366;
		final int ACCUM_JAN = 0;
		final int ACCUM_FEB = ACCUM_JAN + 31;
		final int ACCUM_MAR = ACCUM_FEB + 29;

		/* There are 97 leap years in 400 years */
		int yr = 400 * (date / (DAYS_IN_YEAR * 400 + 97));
		int rem = date % (DAYS_IN_YEAR * 400 + 97);

		if (rem >= DAYS_IN_YEAR * 100 + 25) {
			/* There are 25 leap years in the first 100 years after
			 * every 400th year, as every 400th year is a leap year */
			yr  += 100;
			rem -= DAYS_IN_YEAR * 100 + 25;

			/* There are 24 leap years in the next couple of 100 years */
			yr += 100 * (rem / (DAYS_IN_YEAR * 100 + 24));
			rem = (rem % (DAYS_IN_YEAR * 100 + 24));
		}

		if (!isLeapYear(yr) && rem >= DAYS_IN_YEAR * 4) {
			/* The first 4 year of the century are not always a leap year */
			yr  += 4;
			rem -= DAYS_IN_YEAR * 4;
		}

		/* There is 1 leap year every 4 years */
		yr += 4 * (rem / (DAYS_IN_YEAR * 4 + 1));
		rem = rem % (DAYS_IN_YEAR * 4 + 1);

		/* The last (max 3) years to account for; the first one
		 * can be, but is not necessarily a leap year */
		while (rem >= (isLeapYear(yr) ? DAYS_IN_LEAP_YEAR : DAYS_IN_YEAR)) {
			rem -= isLeapYear(yr) ? DAYS_IN_LEAP_YEAR : DAYS_IN_YEAR;
			yr++;
		}

		/* Skip the 29th of February in non-leap years */
		if (!isLeapYear(yr) && rem >= ACCUM_MAR - 1) rem++;

		//int x = _month_date_from_year_day[rem];
		//return String.format("%02d-%02d-%04d", yr, x >> 5, x & 0x1F);
		
		int m = 1;
		int d = 31;
		if ((rem -= d) > 0) { // jan
			m++;
			d = 29;
			
			if ((rem -= d) > 0) { // feb
				m++;
				d = 31;
				
				if ((rem -= d) > 0) { // mar
					m++;
					d = 30;
					
					if ((rem -= d) > 0) { // apr
						m++;
						d = 31;
						
						if ((rem -= d) > 0) { // may
							m++;
							d = 30;
							
							if ((rem -= d) > 0) { // jun
								m++;
								d = 31;
								
								if ((rem -= d) > 0) { // jul
									m++;
									d = 31;
									
									if ((rem -= d) > 0) { // aug
										m++;
										d = 30;
										
										if ((rem -= d) > 0) { // sep
											m++;
											d = 31;
											
											if ((rem -= d) > 0) { // oct
												m++;
												d = 30;
												
												if ((rem -= d) > 0) { // nov
													m++;
													d = 31;
													
													if ((rem -= d) > 0) { // dec
														// error!
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		d += rem;
		
		this.day = d;
		this.month = m;
		this.year = yr;
	}
	
	/**
	 * Gets the day number of the date.
	 * @return day number (1..31)
	 */
	public int getDay() {
		return day;
	}
	
	/**
	 * Gets the month number of the date.
	 * @return month number (1..12)
	 */
	public int getMonth() {
		return month;
	}
	
	/**
	 * Gets the year of the date.
	 * @return year value
	 */
	public int getYear() {
		return year;
	}
	
	/**
	 * Gets the raw value of the date as reported by the game server.
	 * @return the raw value of the date as reported by the game server
	 */
	public int getRawValue() {
		return rawValue;
	}
	
	/**
	 * Formats the date as DD.MM.YYYY.
	 */
	@Override
	public String toString() {
		return String.format("%02d.%02d.%04d", this.day, this.month, this.year);
	}
	
	/**
	 * Checks wheter a given year is a leap year.
	 * @param yr year to be verified.
	 * @return true for a leap year, false otherwise
	 * 
	 * The code of the function comes from the original code of the game, licensed under GNU GPL v2,
	 * specifically the function static inline bool IsLeapYear(Year yr)  from date-func.h-file.
	 */
	private static boolean isLeapYear(int yr)
	{
		return yr % 4 == 0 && (yr % 100 != 0 || yr % 400 == 0);
	} 
}
