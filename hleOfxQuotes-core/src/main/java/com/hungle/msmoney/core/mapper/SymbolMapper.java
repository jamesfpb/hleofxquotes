package com.hungle.msmoney.core.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

// TODO: Auto-generated Javadoc
/**
 * The Class SymbolMapper.
 */
public class SymbolMapper {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(SymbolMapper.class);

    private static final String DEFAULT_MAPPER_FILE = "mapper.csv";

    /** The map by ms money symbol. */
    private final Map<String, List<SymbolMapperEntry>> mapByMsMoneySymbol = new HashMap<String, List<SymbolMapperEntry>>();

    /** The map by quotes source symbol. */
    private final Map<String, List<SymbolMapperEntry>> mapByQuotesSourceSymbol = new HashMap<String, List<SymbolMapperEntry>>();

    /** The entries. */
    private final List<SymbolMapperEntry> entries = new ArrayList<SymbolMapperEntry>();

    /**
     * Load.
     *
     * @param file
     *            the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void load(File file) throws IOException {
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            load(reader);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }

    }

    private void load(Reader reader) throws IOException {
        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(reader);
            csvReader.readHeaders();
            load(csvReader);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } finally {
                    csvReader = null;
                }
            }
        }
    }

    /**
     * Load.
     *
     * @param csvReader
     *            the csv reader
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void load(CsvReader csvReader) throws IOException {
        SymbolMapperEntry entry;
        while (csvReader.readRecord()) {
            entry = new SymbolMapperEntry();
            if (LOGGER.isDebugEnabled()) {
                String line = csvReader.getRawRecord();
                LOGGER.debug(line);
            }

            entry.load(csvReader);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("entry=" + entry);
            }
            add(entry);
        }
    }

    public void add(SymbolMapperEntry entry) {
        updateMapBys(entry);

        entries.add(entry);
    }

    private void merge(SymbolMapperEntry newEntry) {
        int count = 0;
        String newKey = newEntry.getQuotesSourceSymbol() + newEntry.getMsMoneySymbol();
        for (SymbolMapperEntry entry : entries) {
            String key = entry.getQuotesSourceSymbol() + entry.getMsMoneySymbol();
            if (key.compareToIgnoreCase(newKey) == 0) {
                count++;
                merge(newEntry, entry);
            }
        }

        if (count == 0) {
            add(newEntry);
        }
    }

    private void merge(SymbolMapperEntry newEntry, SymbolMapperEntry entry) {
        String value = null;

        value = newEntry.getQuotesSourceCurrency();
        if (StringUtils.isNotEmpty(value)) {
            entry.setQuotesSourceCurrency(value);
        }

        value = newEntry.getMsMoneyCurrency();
        if (StringUtils.isNotEmpty(value)) {
            entry.setMsMoneyCurrency(value);
        }

        value = newEntry.getType();
        if (StringUtils.isNotEmpty(value)) {
            entry.setType(value);
        }
    }

    /**
     * Update map bys.
     *
     * @param entry
     *            the entry
     */
    private void updateMapBys(SymbolMapperEntry entry) {
        String msMoneySymbol = entry.getMsMoneySymbol();
        String quotesSourceSymbol = entry.getQuotesSourceSymbol();

        List<SymbolMapperEntry> list = null;
        list = mapByMsMoneySymbol.get(msMoneySymbol);
        if (list == null) {
            list = new ArrayList<SymbolMapperEntry>();
            mapByMsMoneySymbol.put(msMoneySymbol, list);
        }
        list.add(entry);

        list = mapByQuotesSourceSymbol.get(quotesSourceSymbol);
        if (list == null) {
            list = new ArrayList<SymbolMapperEntry>();
            mapByQuotesSourceSymbol.put(quotesSourceSymbol, list);
        }
        list.add(entry);
    }

    /**
     * Entries by quote source.
     *
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @return the list
     */
    public List<SymbolMapperEntry> entriesByQuoteSource(String quoteSourceSymbol) {
        List<SymbolMapperEntry> list = mapByQuotesSourceSymbol.get(quoteSourceSymbol);
        if (list == null) {
            return null;
        }
        return list;
    }

    /**
     * Checks for entry.
     *
     * @param ticker
     *            the ticker
     * @return true, if successful
     */
    public boolean hasEntry(String ticker) {
        if (mapByQuotesSourceSymbol.get(ticker) != null) {
            return true;
        }
        if (mapByMsMoneySymbol.get(ticker) != null) {
            return true;
        }

        return false;
    }

    /**
     * Gets the checks if is mutual fund.
     *
     * @param ticker
     *            the ticker
     * @return the checks if is mutual fund
     */
    public boolean getIsMutualFund(String ticker) {
        List<SymbolMapperEntry> list = null;

        list = mapByQuotesSourceSymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isMutualFund()) {
                    return true;
                }
            }
        }

        list = mapByMsMoneySymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isMutualFund()) {
                    return true;
                }
            }
        }
        // log.warn("Cannot find SymbolMapperEntry for tickerName=" + ticker);
        return false;
    }

    /**
     * Gets the checks if is options.
     *
     * @param ticker
     *            the ticker
     * @return the checks if is options
     */
    public boolean getIsOptions(String ticker) {
        List<SymbolMapperEntry> list = null;

        list = mapByQuotesSourceSymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isOptions()) {
                    return true;
                }
            }
        }

        list = mapByMsMoneySymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isOptions()) {
                    return true;
                }
            }
        }

        // log.warn("Cannot find SymbolMapperEntry for tickerName=" + ticker);
        return false;
    }

    /**
     * Gets the checks if is bond.
     *
     * @param ticker
     *            the ticker
     * @return the checks if is bond
     */
    public boolean isBond(String ticker) {
        List<SymbolMapperEntry> list = null;

        list = mapByQuotesSourceSymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isBond()) {
                    return true;
                }
            }
        }

        list = mapByMsMoneySymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isBond()) {
                    return true;
                }
            }
        }

        // log.warn("Cannot find SymbolMapperEntry for tickerName=" + ticker);
        return false;
    }
    
    public SymbolMapperEntry getSymbolMapperEntry(String ticker) {
        List<SymbolMapperEntry> list = null;

        list = mapByQuotesSourceSymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isBond()) {
                    return entry;
                }
            }
        }

        list = mapByMsMoneySymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isBond()) {
                    return entry;
                }
            }
        }

        return null;
    }

    /**
     * Gets the entries.
     *
     * @return the entries
     */
    public List<SymbolMapperEntry> getEntries() {
        return entries;
    }

    /**
     * Creates the default symbol mapper.
     *
     * @return the symbol mapper
     */
    public static SymbolMapper loadMapperFile() {
        String fileName = DEFAULT_MAPPER_FILE;
        return loadMapperFile(fileName);
    }

    public static SymbolMapper loadMapperFile(String fileName) {
        SymbolMapper symbolMapper = new SymbolMapper();
        File symbolMapperFile = new File(fileName);
        LOGGER.info("Looking for mapper=" + symbolMapperFile.getAbsoluteFile().getAbsolutePath());
        if (symbolMapperFile.exists()) {
            try {
                symbolMapper.load(symbolMapperFile);
                LOGGER.info("Loaded symbolMapperFile=" + symbolMapperFile);
            } catch (IOException e) {
                LOGGER.warn("Cannot load symbolMapperFile=" + symbolMapperFile);
            }
        } else {
            LOGGER.info("No " + fileName + " file.");
        }
        return symbolMapper;
    }

    public void addAttributes(List<String> stockSymbols) {
        List<MetaStockSymbol> metaStockSymbols = MetaStockSymbol.parse(stockSymbols);
        for (MetaStockSymbol metaStockSymbol : metaStockSymbols) {
            SymbolMapperEntry symbolMapperEntry = new SymbolMapperEntry();
            populateEntry(metaStockSymbol, symbolMapperEntry);
            merge(symbolMapperEntry);

            // replace the current symbol with the quote source symbol
            ListIterator<String> iter = stockSymbols.listIterator();
            while (iter.hasNext()) {
                String s = iter.next();
                if (s.compareTo(metaStockSymbol.getOriginalSymbol()) == 0) {
                    iter.remove();
                }
            }
            // lookup using Quote Source symbol
            stockSymbols.add(metaStockSymbol.getQsSymbol());
        }
    }

    private void populateEntry(MetaStockSymbol metaStockSymbol, SymbolMapperEntry symbolMapperEntry) {
        symbolMapperEntry.setMsMoneySymbol(metaStockSymbol.getSymbol());
        symbolMapperEntry.setQuotesSourceSymbol(metaStockSymbol.getQsSymbol());
        symbolMapperEntry.setQuotesSourceCurrency(metaStockSymbol.getQsCurrency());
        symbolMapperEntry.setMsMoneyCurrency(metaStockSymbol.getCurrency());
    }

    public Map<String, List<SymbolMapperEntry>> getMapByMsMoneySymbol() {
        return mapByMsMoneySymbol;
    }

    public Map<String, List<SymbolMapperEntry>> getMapByQuotesSourceSymbol() {
        return mapByQuotesSourceSymbol;
    }

    public void dump() {
        LOGGER.debug("> BEGIN dump");
        for (SymbolMapperEntry entry : entries) {
            LOGGER.debug(entry);
        }
        LOGGER.debug("< END dump");
    }

    public static String getStockSymbol(String qsSymbol, SymbolMapper symbolMapper) {
        String symbol = qsSymbol;
        List<SymbolMapperEntry> entries = symbolMapper.getMapByQuotesSourceSymbol().get(qsSymbol);
        if (entries == null) {
            return symbol;
        }

        for (SymbolMapperEntry entry : entries) {
            String s = entry.getMsMoneySymbol();
            if (!StringUtils.isBlank(s)) {
                symbol = s;
                break;
            }
        }

        return symbol;
    }
}
