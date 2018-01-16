import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class Algorithms {
	public static double peak; // Peak price
	public static double dip; // Low Price
	public static boolean buy = true; // Indicates whether or not to buy or sell
	public static double usd; // Current dollar amount
	public static double btc; // Current currency amount
	public static ArrayList<Double> arr; // Array of prices
	public static ArrayList<Double> volarr; // Array of trading volume
	public static boolean ret = false; // Indicates starting and ending values
	public static double start; // Start value stored in btc
	public static double end; // End value stored in btc
	public static double vol = 0; // Average trading volume
	public static int volnum = 0; // Used as a counter for trading volume
	public static double avg = 0; // Overall price average
	public static int avgnum = 0; // Used as a counter for price average
	public static ArrayList<Double> recent; // Array of recent averages
	public static ArrayList<Double> midrecent; // Array of less recent averages
	public static int recentnum = 0; // Determines how recent
	public static double counter = 0; // Used as a counter to prevent distortion of averages at beginning of data
	public static int outlier = 0; // Determines if there is an outlier
	public static double trend = 0; // Current trend Value
	public static ArrayList<Double> trends; // Array used to calculate trends
	public static int track = 0; // Used as a counter for establishing trends
	public static boolean isTrend = false; // Determines if there is a trend active
	public static ArrayList<Double> trendBroken; // Array used to calculate if trend is over
	public static int trendCounter = 0; // Used as a counter for breaking trends
	public static double recentTrend = 0; // Checks recent trends against overall trend to determine if trend is broken
	
	
	// Main method
	public static void main (String[] args) throws IOException {
		testTrade();
	}
	
	// Converts currency to dollars
	// @currentprice the price to sell at
	// @p determines whether to print transaction
	public static void sell(double currentprice, boolean p) {
		
		if (p == true) {
		}
		btc *= .9988;
		usd = (btc * currentprice);
		btc = 0;
		if (p == true) {
		System.out.println("USD : " + usd);
		}
		
	}
	// Converts dollars to currency
	// @currentprice the price to buy at
	// @p determines whether to print transaction
	public static void buy(double currentprice, boolean p) {
		
		if (p == true) {
		System.out.println("BUY");
		}
		usd *= .9988;
		btc = (usd/currentprice);
		usd = 0;
		if (ret == false) {
			ret = true;
			start = btc;
			if (p == true) {
			System.out.println("START BTC: " + btc);
			}
		}
		if (p == true) {

	//	System.out.println("BTC : " + btc);
		}

	}
	
	/* Primary trade algorithm. Basic concept is to establish a peak when you are selling and a dip when you are buying.
	 * The algorithm then determines if the current price is a certain percentage below a peak or above a dip. If this is
	 * true, the algorithm then buys or sells. There are many other modifiers that alter the percentage threshold above a
	 * dip or below a peak which are based on a variety of factors. Currently, to determine the best values for these modifiers
	 * (estimates), I have implemented a for loop to search for the best value to a given variable.
	 * @currentprice the current price
	 * @previousprice the previous price
	 * @varpercent the base percentage threshold required to perform transaction
	 * @p determines whether to print data
	 * @volume current trading volume
	 * @recency how recent averages should be calculated by
	 * @shortweight the base modifier for how much recent values should be considered, used for other averages
	 * @temp determines how different averages are weighted
	 * @midval how recent a mid average should be calculated
	 * @disty A value determining how averages valued as more data is available
	 * @trendweight valued for how much trend modifiers should be considered
	 * @trendsetter how long for a trend to be initiated
	 * @trendchange required threshold for a trend to be broken
	 */
	public static void trade(double currentprice, double previousprice, double varpercent, boolean p, double volume, double recency, double shortweight, double temp, double midval, int disty, double trendweight, double trendsetter, double trendchange) {
		if (isTrend == false) {
		track++;
		trends.add(currentprice);
		if (track > trendsetter) {
			for(int i = 0; i < trends.size() - 1; i++) {
				trend += (trends.get(i+1)/trends.get(i));
			}
			trend /= (trends.size() - 1);
			trend -= 1;
			trend *= 10000;
			isTrend = true;
		}
		}
		else {
		//	System.out.println("Trend: " + trend);
		trendCounter++;
		trendBroken.add(currentprice);
		if (trendCounter > trendsetter) {
			trendBroken.remove(0);
		for (int i = 0; i < trendBroken.size() - 1; i++) {
			recentTrend += (trendBroken.get(i+1) / trendBroken.get(i));
		}
		recentTrend /= (trendBroken.size()-1);
		if (Math.abs(recentTrend - trend) > trendchange) {
			isTrend = false;
			trendCounter = 0;
			track = 0;
			trendBroken = new ArrayList <Double>();
			trends = new ArrayList <Double>();
			trend = 0;
		}
		}
		recentTrend = 0;
		}
		
		int dist = disty;
		if (counter < dist) {
			counter++;
		}
		double longw = counter/(dist/5);
		double midw = (.59 * temp) - counter/(dist/5);
		double recentw = temp - counter/(dist/5);
		recent.add(currentprice);
		midrecent.add(currentprice);
		recentnum++;
		if (recentnum > recency) {
			recent.remove(0);
		}
		if (recentnum > recency * midval) {
			midrecent.remove(0);
		}
		double recentavg = 0;
		for (int i = 0; i < recent.size(); i++) {
			recentavg += recent.get(i);
		}
		double midrecentavg = 0;
		for (int i = 0; i < midrecent.size(); i++) {
			midrecentavg += midrecent.get(i);
		}
		recentavg /= recent.size();
		midrecentavg /= midrecent.size();
	//	varpercent *= (vol/volume);
		double totalavg = ((avg * longw) + (midrecentavg * midw) + (recentavg) * recentw)/(longw + midw + recentw);
	//	System.out.println("avgchange : " + (Math.pow((currentprice/totalavg), shortweight)));
		if (buy == true) {
			varpercent *= Math.pow((currentprice/totalavg), shortweight);
			if (isTrend == true) {
				
				varpercent /= (Math.pow((1 + trend), trendweight));
			}
			
	//	System.out.println("VARPERCENT : " + (double)Math.round(varpercent * 10000)/100 + "%" + " BUY");

//			System.out.println("AVG:" + avg);
//			System.out.println("RECENT AVG:" + recentavg);
			if (p == true) {
		//		System.out.println(Math.round(currentprice));
			}
			if(currentprice < dip) {
				if (p == true) {
					System.out.println("DIP : " + Math.round(currentprice));
				}
				dip = currentprice;
			}
			else {
				if (currentprice > (dip + (dip * varpercent))) {
				
					
					buy(currentprice, p);
					buy = false;
					peak = currentprice;
						
				}
			}
		}
		else {
			varpercent /= Math.pow((currentprice/totalavg), shortweight);
			if (isTrend == true) {
				varpercent *= (Math.pow((1 + trend), trendweight));
			}
		//	System.out.println("VARPERCENT : " + (double)Math.round(varpercent * 10000)/100 + "%" + " SELL");

//			System.out.println("AVG:" + avg);
//			System.out.println("RECENT AVG:" + recentavg);
			if (p == true) {
				System.out.println(Math.round(currentprice));
			}
			if (currentprice > peak) {
			if (p == true) {
				System.out.println("PEAK : " + Math.round(currentprice));
			}
				peak = currentprice;
			}
			else {
				if (currentprice < (peak - (peak * varpercent))) {
					
					
						
				
					sell(currentprice, p);
					buy = true;
					dip = currentprice;
					
					
					
				}
			}
			
			}
		avg = ((avg*avgnum) + currentprice)/(avgnum + 1);
		avgnum++;
		vol = ((vol*volnum) + volume)/(volnum + 1);
		volnum++;
		}
	
	
	public static void testTrade() throws IOException{
		arr = new ArrayList<Double>();
		volarr = new ArrayList<Double>();
		recent = new ArrayList<Double>();
		midrecent = new ArrayList<Double>();
		trends = new ArrayList<Double>();
		trendBroken = new ArrayList<Double>();
		
		
		
		
		BufferedReader br = null;
		
		try {
			
			br = new BufferedReader(new FileReader(new File("ETH_2_MONTH_PRICE.txt")));

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				arr.add(Double.parseDouble(sCurrentLine));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
try {
			
			br = new BufferedReader(new FileReader(new File("ETH_2_MONTH_VOLUME.txt")));

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
			
				volarr.add(Double.parseDouble(sCurrentLine));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	ret = true;


	double bestf = 0;
	double besta = 0;
	double bestd = 0;
	double bestusd = -100;
	

			for (double f = 0; f < 1; f += .01) {
		//		for (double a = 100; a < 10000; a+= 100) {
					
						track = 0;
						isTrend = false;
						trend = 0;
						trendCounter = 0;
						recentTrend = 0;
						recentnum = 0;
						recent = new ArrayList<Double>();
						midrecent = new ArrayList<Double>();
						trends = new ArrayList<Double>();
						trendBroken = new ArrayList<Double>();
						vol = volarr.get(0);
						avg = arr.get(0);
						avgnum = 1;
						volnum = 1;
						 usd = 100;
						    btc = 0;

						    double previousprice = arr.get(0);
						    peak = previousprice;
						    buy(previousprice, false);
						    buy = false;
						for (int i = 0; i < arr.size(); i++) {
							trade(arr.get(i), previousprice, .06, false, volarr.get(i), 85, 310, 99, 21, 5000, 15, 1000, f);
							previousprice = arr.get(i);
						}
						
						if (usd == 0) {
							sell(previousprice, true);
						}
						System.out.println("RETURN : " + (double)Math.round((((usd-100)/100) * 10000))/100 + "%  " + f);
						if (usd > bestusd) {

							bestf = f;
						//	besta = a;
						//	bestd = d;
							bestusd = usd;
						}
						
					
						
						
			//	}
				System.out.println("BEST RETURN : " + (double)Math.round((((bestusd-100)/100) * 10000))/100 + "%");
			}
			
			track = 0;
			isTrend = false;
			trend = 0;
			trendCounter = 0;
			recentTrend = 0;

				recentnum = 0;
				recent = new ArrayList<Double>();
				midrecent = new ArrayList<Double>();
				trends = new ArrayList<Double>();
				trendBroken = new ArrayList<Double>();
		ret = false;
		vol = volarr.get(0);
		avg = arr.get(0);
		avgnum = 1;
		volnum = 1;
		 usd = 100;
		    btc = 0;

		    double previousprice = arr.get(0);
		    peak = previousprice;
		    buy(previousprice, false);
		    buy = false;
		for (int i = 0; i < arr.size(); i++) {
			trade(arr.get(i), previousprice, .06, true, volarr.get(i), 85, 310, 99, 21, 5000, 15, 1000, bestf);
			previousprice = arr.get(i);
		}
		
		if (usd == 0) {
			sell(previousprice, false);
		}
		
		end = btc;
//		System.out.println("BESTD : " + bestd);
//		System.out.println("BESTF : " + bestf);
//		System.out.println("BESTA : " + besta);
		System.out.println("END USD : " + usd);
		System.out.println("END BTC : " + btc);
		System.out.println("RETURN : " + (double)Math.round((((usd-100)/100) * 10000))/100 + "%");
	//	System.out.println("RETURN : " + (double)Math.round((((usd-100)/100) * 10000))/100 + "%");

	}

	
	
}
