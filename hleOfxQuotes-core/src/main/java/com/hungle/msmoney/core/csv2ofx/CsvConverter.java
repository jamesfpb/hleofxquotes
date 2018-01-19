package com.hungle.msmoney.core.csv2ofx;

import java.io.IOException;

import com.csvreader.CsvReader;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Interface CsvConverter.
 */
public interface CsvConverter {

    /**
     * Convert.
     *
     * @param csvReader the csv reader
     * @return the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract AbstractStockPrice convert(CsvReader csvReader) throws IOException;

    /**
     * Sets the stock name.
     *
     * @param csvReader the csv reader
     * @param AbstractStockPrice the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void setStockName(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    /**
     * Sets the stock symbol.
     *
     * @param csvReader the csv reader
     * @param AbstractStockPrice the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void setStockSymbol(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    /**
     * Sets the last price.
     *
     * @param csvReader the csv reader
     * @param AbstractStockPrice the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void setLastPrice(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    /**
     * Sets the last trade.
     *
     * @param csvReader the csv reader
     * @param AbstractStockPrice the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void setLastTrade(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    /**
     * Sets the currency.
     *
     * @param csvReader the csv reader
     * @param AbstractStockPrice the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void setCurrency(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    /**
     * Sets the units.
     *
     * @param csvReader the csv reader
     * @param AbstractStockPrice the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void setUnits(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

}