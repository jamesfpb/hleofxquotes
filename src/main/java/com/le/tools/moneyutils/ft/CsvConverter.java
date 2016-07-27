package com.le.tools.moneyutils.ft;

import java.io.IOException;

import com.csvreader.CsvReader;
import com.le.tools.moneyutils.stockprice.StockPrice;

public interface CsvConverter {

    public abstract StockPrice convert(CsvReader csvReader) throws IOException;

    public abstract void setStockName(CsvReader csvReader, StockPrice stockPrice) throws IOException;

    public abstract void setStockSymbol(CsvReader csvReader, StockPrice stockPrice) throws IOException;

    public abstract void setLastPrice(CsvReader csvReader, StockPrice stockPrice) throws IOException;

    public abstract void setLastTrade(CsvReader csvReader, StockPrice stockPrice) throws IOException;

    public abstract void setCurrency(CsvReader csvReader, StockPrice stockPrice) throws IOException;

    public abstract void setUnits(CsvReader csvReader, StockPrice stockPrice) throws IOException;

}