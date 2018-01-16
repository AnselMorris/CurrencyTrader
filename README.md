# CurrencyTrader
This trading algorithm is targetted towards crypotcurrencies. The algorithm establishes a peak used when selling and a dip used when buying. If the price rises above the peak, the peak will become the new price. If the price falls below the dip, the dip will become the new price. The algorithm then establishes a certain threshold as a percentage of the peak and dip, and then determines, based on if it is buying or selling, if the current price is below the peak minus the threshold, or above the dip plus the threshold. If such an instance occurs, then the algorithm will buy/sell. There are a variety of factors which are used to modify the value of the threshold.
