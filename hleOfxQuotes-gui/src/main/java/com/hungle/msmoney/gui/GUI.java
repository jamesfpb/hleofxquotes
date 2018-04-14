package com.hungle.msmoney.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.gui.PopupListener;
import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.msmoney.core.jna.ImportDialogAutoClickService;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.mapper.SymbolMapperEntry;
import com.hungle.msmoney.core.misc.BuildNumber;
import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.core.misc.ResourceUtils;
import com.hungle.msmoney.core.ofx.ImportUtils;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxSaveParameter;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.FxSymbol;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.core.stockprice.StockPrice;
import com.hungle.msmoney.core.stockprice.StockPriceCsvUtils;
import com.hungle.msmoney.core.template.TemplateUtils;
import com.hungle.msmoney.gui.about.AboutAction;
import com.hungle.msmoney.gui.action.CreateNewFiAction;
import com.hungle.msmoney.gui.action.EditCurrencyAction;
import com.hungle.msmoney.gui.action.EditOFXAccountIdAction;
import com.hungle.msmoney.gui.action.EditRandomizeShareCountAction;
import com.hungle.msmoney.gui.action.EditWarnSuspiciousPriceAction;
import com.hungle.msmoney.gui.action.ExitAction;
import com.hungle.msmoney.gui.action.ImportAction;
import com.hungle.msmoney.gui.action.ProfileSelectedAction;
import com.hungle.msmoney.gui.action.SaveAndOpenQIFAction;
import com.hungle.msmoney.gui.action.SaveMDCSVAction;
import com.hungle.msmoney.gui.action.SaveOfxAction;
import com.hungle.msmoney.gui.action.SaveQIFAction;
import com.hungle.msmoney.gui.dnd.FileDropHandler;
import com.hungle.msmoney.gui.qs.BloombergQuoteSourcePanel;
import com.hungle.msmoney.gui.qs.FtCsvQuoteSourcePanel;
import com.hungle.msmoney.gui.qs.FtEquitiesSourcePanel;
import com.hungle.msmoney.gui.qs.FtEtfsSourcePanel;
import com.hungle.msmoney.gui.qs.FtFundsSourcePanel;
import com.hungle.msmoney.gui.qs.MultiSourcePanel;
import com.hungle.msmoney.gui.qs.QuoteSourcePanel;
import com.hungle.msmoney.gui.qs.TIAACREFQuoteSourcePanel;
import com.hungle.msmoney.gui.qs.YahooApiQuoteSourcePanel;
import com.hungle.msmoney.gui.qs.YahooHistSourcePanel;
import com.hungle.msmoney.gui.qs.YahooQuoteSourcePanel;
import com.hungle.msmoney.gui.qs.YahooSS2SourcePanel;
import com.hungle.msmoney.gui.task.StockPricesReceivedTask;
import com.hungle.msmoney.qs.DefaultQuoteSource;
import com.hungle.msmoney.qs.QuoteSource;
import com.hungle.msmoney.qs.QuoteSourceListener;
import com.hungle.msmoney.qs.QuotesResult;
import com.hungle.msmoney.qs.yahoo.YahooQuotesGetter;
import com.hungle.msmoney.stmt.StatementPanel;
import com.hungle.msmoney.stmt.fi.props.FIBean;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;

// TODO: Auto-generated Javadoc
/**
 * The Class GUI.
 */
public class GUI extends JFrame {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(GUI.class);

    /** The Constant VERSION_PREFIX. */
    private static final String VERSION_PREFIX = "Build";
    // private static final String VERSION_PREFIX = "SNAPSHOT";

    private static final String VERSION_DATE = "20171110";

    /** The Constant VERSION_SUFFIX. */
    private static final String VERSION_SUFFIX = "129";

    /** The version. */
    // 20171104_122
    public static String VERSION = VERSION_PREFIX + "_" + VERSION_DATE + "_" + VERSION_SUFFIX;

    /** The Constant PREF_DEFAULT_CURRENCY. */
    private static final String PREF_DEFAULT_CURRENCY = "defaultCurrency";

    /** The Constant PREF_LAST_KNOWN_IMPORT_STRING. */
    public static final String PREF_LAST_KNOWN_IMPORT_STRING = "lastKnownImportString";

    /** The Constant PREF_RANDOMIZE_SHARE_COUNT. */
    public static final String PREF_RANDOMIZE_SHARE_COUNT = "randomizeShareCount";

    /** The Constant PREF_FORCE_GENERATING_INVTRANLIST. */
    private static final String PREF_FORCE_GENERATING_INVTRANLIST = "forceGeneratingINVTRANLIST";

    private static final String PREF_TEMPLATE_DECIMAL_SEPARATOR = "template.decimalSeparator";

    /** The Constant HOME_PAGE. */
    public static final String HOME_PAGE = "http://code.google.com/p/hle-ofx-quotes/";

    /** The Constant PREF_DATE_OFFSET. */
    private static final String PREF_DATE_OFFSET = "dateOffset";
    
    /** The Constant PREF_SUSPICIOUS_PRICE. */
    public static final String PREF_SUSPICIOUS_PRICE = "suspiciousPrice";

    /** The Constant PREF_INCREMENTALLY_INCREASED_SHARE_COUNT. */
    private static final String PREF_INCREMENTALLY_INCREASED_SHARE_COUNT = "incrementallyIncreasedShareCount";

    /** The Constant PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE. */
    private static final String PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE = "incrementallyIncreasedShareCountValue";

    /** The Constant PREF_ACCOUNT_ID. */
    private static final String PREF_ACCOUNT_ID = "accountId";

    /** The Constant PREF_IMPORT_DIALOG_AUTO_CLICK. */
    private static final String PREF_IMPORT_DIALOG_AUTO_CLICK = "importDialogAutoClick";

    private static final String PREF_SHOW_STATEMENT_PROGRESS = "showStatementProgress";

    private static final String PREF_SELECTED_QUOTE_SOURCE = "selectedQuoteSource";

    private static final String PRE_IMPORT_QFX_DIR = "importQFXDir";

    /** The thread pool. */
    final ExecutorService threadPool = Executors.newCachedThreadPool();

    /** The output files. */
    private List<File> outputFiles;

    /** The result view. */
    private JTextPane resultView;

    /** The price list. */
    private EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
    private EventList<AbstractStockPrice> convertedPriceList = new BasicEventList<AbstractStockPrice>();
    private EventList<AbstractStockPrice> notFoundPriceList = new BasicEventList<AbstractStockPrice>();

    /** The exchange rates. */
    // private EventList<AbstractStockPrice> exchangeRates = new
    // BasicEventList<AbstractStockPrice>();

    /** The mapper. */
    // private EventList<SymbolMapperEntry> mapper = new
    // BasicEventList<SymbolMapperEntry>();

    /** The price filter edit. */
    // private JTextField priceFilterEdit;

    /** The defaul currency label. */
    private JLabel defaulCurrencyLabel;

    /** The clock label. */
    private JLabel clockLabel;

    /** The clock formatters. */
    private SimpleDateFormat[] clockFormatters;

    /** The import to money button. */
    private JButton importToMoneyButton;

    /** The last known import. */
    private JLabel lastKnownImport;

    /** The last known import string. */
    private String lastKnownImportString;

    /** The update exchange rate button. */
    private JButton updateExchangeRateButton;

    /** The save ofx button. */
    private JButton saveOfxButton;

    /** The default currency. */
    private String defaultCurrency = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_DEFAULT_CURRENCY,
            com.hungle.msmoney.core.ofx.xmlbeans.CurrencyUtils.getDefaultCurrency());

    /** The randomize share count. */
    private Boolean randomizeShareCount = Boolean.valueOf(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_RANDOMIZE_SHARE_COUNT, "False"));

    /** The random. */
    private Random random = new Random();

    /** The incrementally increased share count. */
    private Boolean incrementallyIncreasedShareCount = Boolean
            .valueOf(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_INCREMENTALLY_INCREASED_SHARE_COUNT, "False"));

    /** The bottom tabs. */
    private JTabbedPane bottomTabs;

    /** The yahoo quote source view. */
    private YahooQuoteSourcePanel yahooQuoteSourceView;

    /** The yahoo api quote source panel. */
    private YahooApiQuoteSourcePanel yahooApiQuoteSourcePanel;

    /** The ft dot com quote source panel. */
    private FtCsvQuoteSourcePanel ftDotComQuoteSourcePanel;

    /** The yahoo historical quote source view. */
    private QuoteSourcePanel yahooHistoricalQuoteSourceView;

    /** The quote source listener. */
    private QuoteSourceListener quoteSourceListener;

    /** The force generating INVTRANLIST. */
    private Boolean forceGeneratingINVTRANLIST = Boolean.valueOf(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_FORCE_GENERATING_INVTRANLIST, "False"));

    /** The date offset. */
    private Integer dateOffset = Integer.valueOf(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_DATE_OFFSET, "0"));

    private Integer minimumFractionDigits = Integer.valueOf(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(le.com.tools.moneyutils.ofx.quotes.GUI.PREF_MINIMUM_FRACTION_DIGITS, 
            "" + le.com.tools.moneyutils.ofx.quotes.GUI.PREF_MINIMUM_FRACTION_DIGITS_DEFAULT));
    private Integer maximumFractionDigits = Integer.valueOf(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(le.com.tools.moneyutils.ofx.quotes.GUI.PREF_MAXIMUM_FRACTION_DIGITS, 
            "" + le.com.tools.moneyutils.ofx.quotes.GUI.PREF_MAXIMUM_FRACTION_DIGITS_DEFAULT));

    /** The suspicious price. */
    private Integer suspiciousPrice = Integer.valueOf(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_SUSPICIOUS_PRICE, "10000"));

    /** The account id. */
    private String accountId = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_ACCOUNT_ID, OfxPriceInfo.DEFAULT_ACCOUNT_ID);

    private boolean showStatementProgress = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.getBoolean(PREF_SHOW_STATEMENT_PROGRESS, Boolean.FALSE);

    /** The account id label. */
    private JLabel accountIdLabel;

    /** The download view. */
    private StatementPanel downloadView;

    /** The main tabbed. */
    private JTabbedPane mainTabbed;

    /** The selected quote source. */
    private int selectedQuoteSourceIndex;

    /** The import dialog auto click service. */
    private ImportDialogAutoClickService importDialogAutoClickService;

    /** The price formatter. */
    private NumberFormat priceFormatter;

    /** The bloomberg quote source panel. */
    private YahooApiQuoteSourcePanel bloombergQuoteSourcePanel;

    /** The t IAACREF quote source panel. */
    private TIAACREFQuoteSourcePanel tIAACREFQuoteSourcePanel;

    /** The backup view. */
    private BackupPanel backupView;

    /** The fi dir. */
    // TODO_FI
    private File fiDir = new File(System.getProperty("fi.dir", FIBean.getDefaultFiDir()));

//    private YahooSS2SourcePanel yahooScreenScrapper2SourcePanel;

    private SymbolMapper symbolMapper = SymbolMapper.loadMapperFile();

    private FxTable fxTable = FxTableUtils.loadFxFile();

    private FtEquitiesSourcePanel ftEquitiesSourcePanel;

    private FtFundsSourcePanel ftFundsSourcePanel;

    private FtEtfsSourcePanel ftEtfsSourcePanel;

    private QuoteSource quoteSource;
    
//    private List<QuoteSource> quoteSources = new ArrayList<QuoteSource>();

    public static final class TemplateEntry {

        private String src;
        private String dest;

        public TemplateEntry(String src) {
            this(src, src);
        }

        public TemplateEntry(String src, String dest) {
            this.src = src;
            this.dest = dest;
        }

    }

    /**
     * Clear price table.
     */
    private void clearPriceTable() {
        getPriceList().clear();
        getConvertedPriceList().clear();
        getNotFoundPriceList().clear();
    }

    /**
     * Clear mapper table.
     */
    private void clearMapperTable() {
        // MapperTableUtils.clearMapperTable(getMapper());
    }

    private void clearFxTable() {
        // FxTableUtils.clearFxTable(getExchangeRates());
    }

    /**
     * Save to OFX.
     *
     * @param stockPrices
     *            the stock prices
     * @param symbolMapper
     *            the symbol mapper
     * @param fxTable
     *            the fx table
     * @param onePerFile
     *            the one per file
     * @return the list
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private List<File> saveToOFX(final List<AbstractStockPrice> stockPrices, SymbolMapper symbolMapper, FxTable fxTable,
            boolean onePerFile) throws IOException {
        // cleanup
        deleteOutputFiles();

        ArrayList<File> files = new ArrayList<File>();
        setOutputFiles(files);

        if (onePerFile) {
            for (AbstractStockPrice stockPrice : stockPrices) {
                List<AbstractStockPrice> list = new ArrayList<AbstractStockPrice>();
                list.add(stockPrice);
                File outputFile = saveToOFX(list, symbolMapper, fxTable);
                files.add(outputFile);
            }
        } else {
            File outputFile = saveToOFX(stockPrices, symbolMapper, fxTable);
            files.add(outputFile);
        }

        return files;
    }

    private void deleteOutputFiles() {
        List<File> files = getOutputFiles();
        OfxFileIo.deleteFiles(files);
    }

    public void saveToOFX() throws IOException {
        EventList<AbstractStockPrice> list1 = getConvertedPriceList();
        EventList<AbstractStockPrice> list2 = getNotFoundPriceList();
        List<AbstractStockPrice> prices = OfxFileIo.concatPriceList(list1, list2);
        saveToOFX(prices);
    }

    public void saveToOFX(List<AbstractStockPrice> prices) throws IOException {
        boolean onePerFile = getQuoteSource().isHistoricalQuotes();
        List<File> ofxFiles = saveToOFX(prices, symbolMapper, fxTable, onePerFile);
    }

    /**
     * Save to OFX.
     *
     * @param stockPrices
     *            the stock prices
     * @param symbolMapper
     *            the symbol mapper
     * @param fxTable
     *            the fx table
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private File saveToOFX(final List<AbstractStockPrice> stockPrices, SymbolMapper symbolMapper, FxTable fxTable)
            throws IOException {
        File outputFile = File.createTempFile("quotes", ".ofx");
        outputFile.deleteOnExit();

        LOGGER.info("forceGeneratingINVTRANLIST=" + forceGeneratingINVTRANLIST);

        OfxSaveParameter params = new OfxSaveParameter();
        params.setDefaultCurrency(getDefaultCurrency());
        params.setAccountId(getAccountId());
        params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);
        params.setDateOffset(dateOffset);

        OfxPriceInfo.save(stockPrices, outputFile, params, symbolMapper, fxTable);
        LOGGER.info("FILE SAVED -> ofxFile=" + outputFile.getAbsolutePath());

        return outputFile;
    }

    /**
     * Save to csv.
     *
     * @param stockPrices
     *            the beans
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public File saveToCsv(List<AbstractStockPrice> stockPrices) throws IOException {
        SimpleDateFormat tradeDateFormatter = new SimpleDateFormat(AbstractStockPrice.DEFAULT_LAST_TRADE_DATE_PATTERN);
        SimpleDateFormat tradeTimeFormatter = new SimpleDateFormat(AbstractStockPrice.DEFAULT_LAST_TRADE_TIME_PATTERN);

        File outFile = null;
        outFile = new File("quotes.csv");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            // "YHOO","Yahoo! Inc.",12.76,"8/2/2011","4:00pm",12.75,13.18
            writer.println("#symbol,name,price,date,time,priceDayLow,priceDayHigh");
            writer.println("#\"YHOO\",\"Yahoo! Inc.\",12.76,\"8/2/2011\",\"4:00pm\",12.75,13.18");
            writer.println("");
            for (AbstractStockPrice stockPrice : stockPrices) {
                // symbol
                writer.print(stockPrice.getStockSymbol());

                // name
                String delimiter = ",";
                writer.print(delimiter);
                writer.print(stockPrice.getStockName().replace(delimiter, ""));

                // price
                writer.print(delimiter);
                Double price = stockPrice.getLastPrice().getPrice();
                // TODO: assume that portfolio currency is going to be
                // GBP
                String currency = stockPrice.getCurrency();
                if (currency != null) {
                    if (currency.compareToIgnoreCase("GBX") == 0) {
                        price = price / 100.0;
                    }
                }
                writer.print(priceFormatter.format(price));

                Date lastTrade = stockPrice.getLastTrade();

                // date
                writer.print(delimiter);
                if (lastTrade == null) {
                    writer.print("");
                } else {
                    writer.print(tradeDateFormatter.format(lastTrade));
                }

                // time
                writer.print(delimiter);
                if (lastTrade == null) {
                    writer.print("");
                } else {
                    writer.print(tradeTimeFormatter.format(lastTrade));
                }

                // priceDayLow
                writer.print(delimiter);
                writer.print("");

                // priceDayHigh
                writer.print(delimiter);
                writer.print("");

                writer.println();
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
        return outFile;
    }

    /**
     * Gets the mapper currency.
     *
     * @param symbol
     *            the symbol
     * @param mapper
     *            the mapper
     * @param defaultValue
     *            the default value
     * @return the mapper currency
     */
    private String getMapperCurrency(String symbol, SymbolMapper mapper, String defaultValue) {
        String quoteSourceSymbol = null;
        String currency = defaultValue;
        for (SymbolMapperEntry entry : mapper.getEntries()) {
            quoteSourceSymbol = entry.getQuotesSourceSymbol();
            if (CheckNullUtils.isEmpty(quoteSourceSymbol)) {
                continue;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("s=" + quoteSourceSymbol + ", symbol=" + symbol);
            }
            if (quoteSourceSymbol.compareToIgnoreCase(symbol) != 0) {
                continue;
            }
            currency = entry.getQuotesSourceCurrency();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getMapperCurrency: s=" + quoteSourceSymbol + ", currency=" + currency);
            }
            if (!CheckNullUtils.isEmpty(currency)) {
                return currency;
            }
        }
        return currency;
    }

    /**
     * Update last price currency.
     *
     * @param stockPrices
     *            the stock prices
     * @param defaultCurrency
     *            the default currency
     * @param symbolMapper
     *            the symbol mapper
     */
    private void updateLastPriceCurrency(List<AbstractStockPrice> stockPrices, String defaultCurrency,
            SymbolMapper symbolMapper) {
        for (AbstractStockPrice stockPrice : stockPrices) {
            Price price = stockPrice.getLastPrice();
            if ((defaultCurrency != null) && (price.getCurrency() == null)) {
                price.setCurrency(defaultCurrency);
            }
            String currency = stockPrice.getCurrency();
            if (CheckNullUtils.isEmpty(currency)) {
                String symbol = stockPrice.getStockSymbol();
                String overridingCurrency = null;
                overridingCurrency = getMapperCurrency(symbol, symbolMapper, overridingCurrency);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.info("symbol: " + symbol + ", overridingCurrency=" + overridingCurrency);
                }
                if (!CheckNullUtils.isEmpty(overridingCurrency)) {
                    stockPrice.setCurrency(overridingCurrency);
                    stockPrice.updateLastPriceCurrency();
                }
            }
        }
    }

    /**
     * Gets the save ofx button.
     *
     * @return the save ofx button
     */
    public JButton getSaveOfxButton() {
        return saveOfxButton;
    }

    /**
     * Instantiates a new gui.
     *
     * @param title
     *            the title
     */
    public GUI(String title) {
        super(title);

        this.priceFormatter = NumberFormat.getNumberInstance();
        this.priceFormatter.setGroupingUsed(false);
        this.priceFormatter.setMinimumFractionDigits(2);
        this.priceFormatter.setMaximumFractionDigits(10);

        quoteSourceListener = new QuoteSourceListener() {
            @Override
            public void stockSymbolsStringReceived(QuoteSource quoteSource, String lines) {
                GUI.this.stockSymbolsStringReceived(quoteSource, lines);
            }

            @Override
            public void stockPricesLookupStarted(QuoteSource quoteSource, List<String> stockSymbols) {
                GUI.this.stockPricesLookupStarted(quoteSource, stockSymbols);
            }

            @Override
            public void stockPricesReceived(QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
                GUI.this.stockPricesReceived(quoteSource, stockPrices);
            }

            @Override
            public void notFoundSymbolsReceived(List<String> symbols) {
                LOGGER.info("TODO");
            }
        };

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        updateMainMenu();

        getContentPane().add(createMainView());

        if (getImportDialogAutoClickService() != null) {
            getImportDialogAutoClickService().schedule();
        }

        WindowListener windowListener = new ClosingWindowListener(this);
        addWindowListener(windowListener);
    }

    /**
     * Stock symbols string received.
     *
     * @param quoteSource
     *            the quote source
     * @param stockSymbolsString
     *            the stock symbols string
     */
    public void stockSymbolsStringReceived(final QuoteSource quoteSource, String stockSymbolsString) {
        this.setQuoteSource(quoteSource);

        clearAllTables();

        this.saveOfxButton.setEnabled(false);

        this.importToMoneyButton.setEnabled(false);

        if (this.updateExchangeRateButton != null) {
            this.updateExchangeRateButton.setEnabled(false);
        }
    }

    /**
     * Stock prices lookup started.
     *
     * @param quoteSource
     *            the quote source
     * @param stockSymbols
     */
    private void stockPricesLookupStarted(final QuoteSource quoteSource, final List<String> stockSymbols) {
        this.setQuoteSource(quoteSource);

        setSymbolMapper(SymbolMapper.loadMapperFile());
        setFxTable(FxTableUtils.loadFxFile());

        // these symbols could have mapper attributes
        getSymbolMapper().addAttributes(stockSymbols);
        unique(stockSymbols);

        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                LOGGER.info("quoteSource=" + quoteSource);
                
                // HACK - quoteSource is null if we switch tab
                if (quoteSource == null) {
                    clearAllTables();
                } else {
                    clearAllTables();
                    
                    // repopulate with result if available
//                    restorePreviousResult(quoteSource);
                }
            }

        };
        SwingUtilities.invokeLater(doRun);
    }

    private void unique(final List<String> stockSymbols) {
        Set<String> unique = new TreeSet<String>();
        unique.addAll(stockSymbols);
        stockSymbols.clear();
        stockSymbols.addAll(unique);
    }

    /**
     * Stock prices received.
     *
     * @param quoteSource
     *            the quote source
     * @param stockPrices
     *            the stock prices
     */
    private void stockPricesReceived(final QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
        this.setQuoteSource(quoteSource);

        getSymbolMapper().dump();

        List<AbstractStockPrice> exchangeRates = quoteSource.getExchangeRates();
        if (exchangeRates == null) {
            exchangeRates = new ArrayList<AbstractStockPrice>();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> BEGIN exchangeRates");
            for (AbstractStockPrice exchangeRate : exchangeRates) {
                FxSymbol fxSymbol = exchangeRate.getFxSymbol();
                LOGGER.debug(fxSymbol);
            }
            LOGGER.debug("< END exchangeRates");
        }
        FxTableUtils.addExchangeRates(exchangeRates, getFxTable());
        getFxTable().dump();

        uiUpdateNotFoundPriceList(quoteSource);

        final List<AbstractStockPrice> prices = (stockPrices != null) ? stockPrices
                : new ArrayList<AbstractStockPrice>();
        updateLastPriceCurrency(prices, getDefaultCurrency(), getSymbolMapper());

        List<List<AbstractStockPrice>> listOfPriceList = new ArrayList<List<AbstractStockPrice>>();
        listOfPriceList.add(prices);
        listOfPriceList.add(getNotFoundPriceList());

        if (getRandomizeShareCount()) {
            randomizeShareCount(listOfPriceList);
        }

        boolean hasWrappedShareCount = false;
        if (getIncrementallyIncreasedShareCount()) {
            hasWrappedShareCount = incrementallyIncreasedShareCount(listOfPriceList);
        }

        Double badPrice = null;
        if (getSuspiciousPrice() > -1L) {
            badPrice = findBadPrice(listOfPriceList);
        }

        Runnable stockPricesReceivedTask = new StockPricesReceivedTask(this, prices, quoteSource, getSymbolMapper(),
                getFxTable(), badPrice, hasWrappedShareCount);
        SwingUtilities.invokeLater(stockPricesReceivedTask);
    }

    private Double findBadPrice(List<List<AbstractStockPrice>> listOfPriceList) {
        Double badPrice = null;

        Double d = new Double(getSuspiciousPrice());
        for (List<AbstractStockPrice> prices : listOfPriceList) {
            for (AbstractStockPrice bean : prices) {
                String stockSymbol = bean.getStockSymbol();
                if ((stockSymbol != null) && (stockSymbol.startsWith("^"))) {
                    // index
                    continue;
                }
                Price price = bean.getLastPrice();
                if (price == null) {
                    continue;
                }
                if (price.getPrice().compareTo(d) > 0) {
                    badPrice = price.getPrice();
                    break;
                }
            }
        }
        return badPrice;
    }

    private boolean incrementallyIncreasedShareCount(List<List<AbstractStockPrice>> listOfPriceList) {
        boolean hasWrappedShareCount = false;
        String key = PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE;
        double value = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.getDouble(key, 0.000);
        if (value > 0.999) {
            value = 0.000;
            // TODO
            LOGGER.warn("incrementallyIncreasedShareCount, is wrapping back to " + value);
            hasWrappedShareCount = true;
        }
        value = value + 0.001;
        for (List<AbstractStockPrice> prices : listOfPriceList) {
            for (AbstractStockPrice price : prices) {
                price.setUnits(value);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "incrementallyIncreasedShareCount=" + getIncrementallyIncreasedShareCount() + ", value=" + value);
        }
        le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.putDouble(key, value);
        return hasWrappedShareCount;
    }

    private void randomizeShareCount(List<List<AbstractStockPrice>> listOfPriceList) {
        int randomInt = random.nextInt(998);
        randomInt = randomInt + 1;
        double value = randomInt / 1000.00;
        LOGGER.info("randomizeShareCount=" + getRandomizeShareCount() + ", value=" + value);
        for (List<AbstractStockPrice> prices : listOfPriceList) {
            for (AbstractStockPrice price : prices) {
                price.setUnits(value);
            }
        }
    }

    private void uiUpdateNotFoundPriceList(final QuoteSource quoteSource) {
        List<String> symbols = quoteSource.getNotFoundSymbols();
        if ((symbols != null) && (symbols.size() > 0)) {
            ArrayList<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
            for (String symbol : symbols) {
                AbstractStockPrice stockPrice = new StockPrice(symbol, new Date(), 0.00);
                stockPrices.add(stockPrice);
            }

            EventList<AbstractStockPrice> priceList = getNotFoundPriceList();
            priceList.getReadWriteLock().writeLock().lock();
            try {
                priceList.addAll(stockPrices);
            } finally {
                priceList.getReadWriteLock().writeLock().unlock();
            }
        }
    }

    /**
     * Update main menu.
     */
    private void updateMainMenu() {
        JMenuBar menubar = new JMenuBar();

        addFileMenu(menubar);

        addEditMenu(menubar);

        addToolsMenu(menubar);

        addHelpMenu(menubar);

        setJMenuBar(menubar);
    }

    /**
     * Adds the tools menu.
     *
     * @param menubar
     *            the menubar
     */
    private void addToolsMenu(JMenuBar menubar) {
        JMenu menu = new JMenu("Tools");

        // addCopyTemplates(menu);
        //
        // menu.addSeparator();

        addImportQFXFile(menu);

        addAutoClickToolMenuItem(menu);

        menubar.add(menu);
    }

    private void addCopyTemplates(final JMenu menu) {
        File topDir = new File(GUI.getTopDirectory());
        final File templateDir = new File(topDir, "templates");
        AbstractAction action = new AbstractAction("Copy Templates") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Runnable command = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            copyTemplates(templateDir);
                        } finally {
                            Runnable doRun = new Runnable() {

                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(menu,
                                            "Done copying templates to\n" + templateDir.getAbsolutePath(),
                                            "Copying templates ...", JOptionPane.INFORMATION_MESSAGE);
                                }
                            };
                            SwingUtilities.invokeLater(doRun);
                        }
                    }

                };
                GUI.this.getThreadPool().execute(command);
            }
        };

        JMenuItem menuItem = new JMenuItem(action);
        menu.add(menuItem);
    }

    protected void copyTemplates(File templateDir) {
        TemplateEntry[] entries = { new TemplateEntry("qif.vm"), new TemplateEntry("mdcsv.vm"), };

        // File topDir = new File(GUI.getTopDirectory());
        // File templateDir = new File(topDir, "templates");
        if (!templateDir.exists()) {
            templateDir.mkdirs();
        }
        for (TemplateEntry entry : entries) {
            try {
                copyTemplate(entry, templateDir);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

    }

    private void copyTemplate(TemplateEntry entry, File templateDir) throws IOException {
        final String resourceName = "/templates/" + entry.src;
        URL url = ResourceUtils.getResource(resourceName);
        if (url == null) {
            LOGGER.warn("Cannot find resource=" + resourceName);
            return;
        }
        InputStream stream = null;
        try {
            stream = url.openStream();
            stream = new BufferedInputStream(stream);
            LOGGER.info("> Coping from resourceName=" + resourceName);
            File outFile = copyTemplate(stream, templateDir, entry.dest);
            LOGGER.info("<   to file=" + outFile.getAbsolutePath());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    stream = null;
                }
            }
        }
    }

    private File copyTemplate(InputStream stream, File templateDir, String dest) throws IOException {
        File outFile = new File(templateDir, dest);

        if (outFile.exists()) {
            outFile = new File(templateDir, "Sample-" + dest);
        }

        copyTemplate(stream, outFile);

        return outFile;
    }

    private void copyTemplate(InputStream in, File outFile) throws IOException {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(outFile));
            copyStream(in, out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    out = null;
                }
            }
        }

    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        ResourceUtils.copyStream(in, out);
    }

    private void addImportQFXFile(JMenu menu) {
        AbstractAction action = new AbstractAction("Import QFX to MSMoney") {
            private static final long serialVersionUID = 1L;

            private JFileChooser fc = null;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (fc == null) {
                    initFileChooser();
                }
                if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fc.getSelectedFile();
                importQFXFile(file);
            }

            private void initFileChooser() {
                fc = new JFileChooser(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PRE_IMPORT_QFX_DIR, "."));
                FileFilter filter = new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "QFX";
                    }

                    @Override
                    public boolean accept(File file) {
                        if (file.isDirectory()) {
                            return true;
                        }
                        String name = file.getName();
                        return name.endsWith(".qfx");
                    }
                };
                this.fc.setFileFilter(filter);
            }
        };

        JMenuItem menuItem = new JMenuItem(action);
        menu.add(menuItem);
    }

    private void addShowStatementProgressMenuItem(JMenu menu) {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem("Show statement progress");
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();
                if (selected) {
                    showStatementProgress = Boolean.TRUE;
                } else {
                    showStatementProgress = Boolean.FALSE;
                }
                le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.putBoolean(PREF_SHOW_STATEMENT_PROGRESS, showStatementProgress);
                if (getDownloadView() != null) {
                    getDownloadView().setShowProgress(showStatementProgress);
                }
            }
        };
        menuItem.addActionListener(listener);

        if (getDownloadView() != null) {
            getDownloadView().setShowProgress(showStatementProgress);
        }
        menuItem.setSelected(showStatementProgress);
        menu.add(menuItem);
    }

    private void addAutoClickToolMenuItem(JMenu menu) {
        setImportDialogAutoClickService(new ImportDialogAutoClickService());

        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem("Import dialog auto-click");
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();
                if (selected) {
                    getImportDialogAutoClickService().setEnable(true);
                    le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.putBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.TRUE);
                } else {
                    getImportDialogAutoClickService().setEnable(false);
                    le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.putBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.FALSE);
                }
            }
        };
        menuItem.addActionListener(listener);
        Boolean autoClick = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.getBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.FALSE);
        menuItem.setSelected(autoClick);
        getImportDialogAutoClickService().setEnable(autoClick);
        menu.add(menuItem);
    }

    /**
     * Adds the help menu.
     *
     * @param menubar
     *            the menubar
     */
    private void addHelpMenu(JMenuBar menubar) {
        JMenu menu = null;
        JMenuItem menuItem = null;

        menu = new JMenu("Help");

        menuItem = new JMenuItem(new AbstractAction("Home page") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "http://code.google.com/p/hle-ofx-quotes/";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Documentation (Wiki)") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "http://code.google.com/p/hle-ofx-quotes/w/list";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Log a bug") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "http://code.google.com/p/hle-ofx-quotes/issues/list";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Discussion group") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "https://groups.google.com/forum/?fromgroups#!forum/hleofxquotes";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(new AboutAction(this, "About"));
        menu.add(menuItem);

        menubar.add(menu);
    }

    /**
     * Adds the file menu.
     *
     * @param menubar
     *            the menubar
     */
    private void addFileMenu(JMenuBar menubar) {
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("File");
        menubar.add(menu);

        JMenu newMenu = new JMenu("New");
        menu.add(newMenu);

        menuItem = new JMenuItem(new CreateNewFiAction(this, "Financial Institution"));
        newMenu.add(menuItem);

        JMenu profilesMenu = new JMenu("Open Quotes Profiles");
        menu.add(profilesMenu);
        addProfilesToMenu(profilesMenu);

        menu.addSeparator();
        menuItem = new JMenuItem(new ExitAction(this, "Exit"));
        menu.add(menuItem);
    }

    /**
     * Adds the profiles to menu.
     *
     * @param profilesMenu
     *            the profiles menu
     */
    private void addProfilesToMenu(JMenu profilesMenu) {
        File dir = new File("profiles");
        if (!dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            LOGGER.info("file=" + file);
            if (file.isDirectory()) {
                continue;
            }
            if (!file.isFile()) {
                continue;
            }

            addProfileToMenu(profilesMenu, file);
        }
    }

    /**
     * Adds the profile to menu.
     *
     * @param profilesMenu
     *            the profiles menu
     * @param file
     *            the file
     */
    private void addProfileToMenu(JMenu profilesMenu, File file) {
        Properties props = new Properties();
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            props.load(reader);
            String name = props.getProperty("name");
            if (CheckNullUtils.isEmpty(name)) {
                name = file.getName();
            }
            JMenuItem item = new JMenuItem(new ProfileSelectedAction(this, name, props));
            profilesMenu.add(item);
        } catch (IOException e) {
            LOGGER.warn(e);
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

    /**
     * Adds the edit menu.
     *
     * @param menubar
     *            the menubar
     */
    private void addEditMenu(JMenuBar menubar) {
        JMenu menu = new JMenu("Edit");
        menubar.add(menu);

        addQuotesMenu(menu);

        addStatementMenu(menu);

        addEditTemplateMenu(menu);
    }

    private void addEditTemplateMenu(JMenu parentMenu) {
        JMenu menu = new JMenu("Template");
        // menubar.add(menu);
        parentMenu.add(menu);

        addDecimalSeparatorMenuItem(menu);
    }

    private void addDecimalSeparatorMenuItem(JMenu menu) {
        AbstractAction action = new AbstractAction("Set Decimal Separator") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Set<String> keys = new TreeSet<String>();
                keys.add(TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
                keys.add(TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_COMMA);
                keys.add(TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_PERIOD);
                String[] possibilities = new String[keys.size()];
                int i = 0;
                for (String key : keys) {
                    possibilities[i++] = key;
                }
                // get curent value
                String currentValue = getTemplateDecimalSeparator();
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + currentValue + "\n" + "Choices:", "Set Template Decimal Separator",
                        JOptionPane.PLAIN_MESSAGE, icon, possibilities, currentValue);

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    setTemplateDecimalSeparator(value);
                } else {
                }
            }
        };
        JMenuItem menuItem = new JMenuItem(action);
        menu.add(menuItem);
    }

    protected void setTemplateDecimalSeparator(String value) {
        LOGGER.info("Setting Template Decimal Separator to " + value);
        le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(PREF_TEMPLATE_DECIMAL_SEPARATOR, value);
    }

    public String getTemplateDecimalSeparator() {
        String str = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_TEMPLATE_DECIMAL_SEPARATOR, TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
        return str;
    }

    private void addStatementMenu(JMenu parentMenu) {
        JMenu menu;

        menu = new JMenu("Statement");
        // menubar.add(menu);
        parentMenu.add(menu);

        addShowStatementProgressMenuItem(menu);
    }

    private void addQuotesMenu(JMenu parentMenu) {
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("Quotes");
        // menubar.add(menu);
        parentMenu.add(menu);

        // menu.addSeparator();
        menuItem = new JMenuItem(new EditCurrencyAction(this, "Currency"));
        menu.add(menuItem);

        menuItem = new JMenuItem(new EditOFXAccountIdAction(this, "OFX Account Id"));
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new EditWarnSuspiciousPriceAction(this, "Warn Suspicious Price"));
        menu.add(menuItem);

        // menu.addSeparator();
        menuItem = new JMenuItem(new EditRandomizeShareCountAction(this, "Randomize Share Count"));
        menu.add(menuItem);

        // incrementallyIncreasedShareCount
        menuItem = new JMenuItem(new AbstractAction("Incrementally Increased Share Count") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] possibilities = { "true", "false" };
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + getIncrementallyIncreasedShareCount() + "\n" + "Choices:",
                        "Set Incrementally Increased Share Count", JOptionPane.PLAIN_MESSAGE, icon, possibilities,
                        null);

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Incrementally Increased Share Count': " + value);
                    Boolean newValue = Boolean.valueOf(value);
                    if (newValue.compareTo(getIncrementallyIncreasedShareCount()) != 0) {
                        setIncrementallyIncreasedShareCount(newValue);
                        le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(PREF_INCREMENTALLY_INCREASED_SHARE_COUNT,
                                getIncrementallyIncreasedShareCount().toString());
                        // to clear the pricing table
                        QuoteSource quoteSource = null;
                        stockSymbolsStringReceived(quoteSource, null);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Force <INVTRANLIST>") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] possibilities = { "true", "false" };
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + forceGeneratingINVTRANLIST + "\n" + "Choices:", "Force generating <INVTRANLIST>",
                        JOptionPane.PLAIN_MESSAGE, icon, possibilities, null);

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Force <INVTRANLIST>': " + value);
                    Boolean newValue = Boolean.valueOf(value);
                    if (newValue.compareTo(forceGeneratingINVTRANLIST) != 0) {
                        forceGeneratingINVTRANLIST = newValue;
                        le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(PREF_FORCE_GENERATING_INVTRANLIST, forceGeneratingINVTRANLIST.toString());
                        // to clear the pricing table
                        QuoteSource quoteSource = null;
                        stockSymbolsStringReceived(quoteSource, null);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Date Offset") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String[] possibilities = null;
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + dateOffset + "\n" + "Number of days:", "Set number of date to offset",
                        JOptionPane.PLAIN_MESSAGE, icon, possibilities, dateOffset.toString());

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Date Offset': " + value);
                    try {
                        Integer newValue = Integer.valueOf(value);
                        if (newValue.compareTo(dateOffset) != 0) {
                            dateOffset = newValue;
                            le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(PREF_DATE_OFFSET, dateOffset.toString());
                            // to clear the pricing table
                            QuoteSource quoteSource = null;
                            stockSymbolsStringReceived(quoteSource, null);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(GUI.this, "Not a valid number - " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem(new AbstractAction("Minimum Fraction Digits") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String[] possibilities = null;
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + minimumFractionDigits + "\n" + "Minimum number of fraction digits:", "Set minimum number of fraction digits: 0.nnnn",
                        JOptionPane.PLAIN_MESSAGE, icon, possibilities, minimumFractionDigits.toString());

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Minimum Fraction Digits': " + value);
                    try {
                        Integer newValue = Integer.valueOf(value);
                        if (newValue.compareTo(minimumFractionDigits) != 0) {
                            minimumFractionDigits = newValue;
                            le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(le.com.tools.moneyutils.ofx.quotes.GUI.PREF_MINIMUM_FRACTION_DIGITS, minimumFractionDigits.toString());
                            // to clear the pricing table
                            QuoteSource quoteSource = null;
                            stockSymbolsStringReceived(quoteSource, null);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(GUI.this, "Not a valid number - " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem(new AbstractAction("Maximum Fraction Digits") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String[] possibilities = null;
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + maximumFractionDigits + "\n" + "Maximum number of fraction digits:", "Set maximum number of fraction digits: 0.nnnn",
                        JOptionPane.PLAIN_MESSAGE, icon, possibilities, maximumFractionDigits.toString());

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Maximum Fraction Digits': " + value);
                    try {
                        Integer newValue = Integer.valueOf(value);
                        if (newValue.compareTo(maximumFractionDigits) != 0) {
                            maximumFractionDigits = newValue;
                            le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(le.com.tools.moneyutils.ofx.quotes.GUI.PREF_MAXIMUM_FRACTION_DIGITS, maximumFractionDigits.toString());
                            // to clear the pricing table
                            QuoteSource quoteSource = null;
                            stockSymbolsStringReceived(quoteSource, null);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(GUI.this, "Not a valid number - " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);

    }

    /**
     * Creates the main view.
     *
     * @return the component
     */
    private Component createMainView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        Dimension preferredSize = new Dimension(600, 600);
        view.setPreferredSize(preferredSize);

        setMainTabbed(new JTabbedPane(SwingConstants.BOTTOM));

        getMainTabbed().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JTabbedPane p = (JTabbedPane) event.getSource();
                LOGGER.info("selectedIndex=" + p.getSelectedIndex());
            }
        });
        // TAB: #1
        getMainTabbed().addTab("Quotes", createQuotesView());

        // TAB: #2
        setDownloadView(new StatementPanel());
        getDownloadView().setShowProgress(showStatementProgress);
        getDownloadView().setFiDir(getFiDir());
        getDownloadView().refreshFiDir();
        getMainTabbed().addTab("Statements", getDownloadView());

        // TAB: #3
        backupView = new BackupPanel();
        backupView.setThreadPool(getThreadPool());
        getMainTabbed().addTab("Backup", backupView);

        view.add(getMainTabbed(), BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the quotes view.
     *
     * @return the component
     */
    private Component createQuotesView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        Component comp0 = createQuotesSourceTabView();
        Component comp1 = createResultView();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, comp0, comp1);
        splitPane.setResizeWeight(0.33);
        splitPane.setDividerLocation(0.33);

        view.add(splitPane, BorderLayout.CENTER);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("< createMainDataView");
        }

        return view;
    }

    /**
     * Creates the quotes source tab view.
     *
     * @return the component
     */
    private Component createQuotesSourceTabView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        view.add(p, BorderLayout.NORTH);

        JLabel label = new JLabel("Quote Sources");
        p.add(label);

        // final JTabbedPane tabbedPane = createQuoteSourceTabs();
        final JTabbedPane tabbedPane = createQuoteSourceTabsX();

        view.add(tabbedPane, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the quote source tabs X.
     *
     * @return the j tabbed pane
     */
    private JTabbedPane createQuoteSourceTabsX() {
        final JTabbedPane tabbedPane = new JTabbedPane();

        // JPopupMenu popup = new JPopupMenu();
        // popup.addPopupMenuListener(new PopupMenuListener() {
        // public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // int selected = tabbedPane.getSelectedIndex();
        // boolean enabled = tabbedPane.isEnabledAt(selected);
        // log.info("selected=" + selected + ", enabled=" + enabled);
        // }
        //
        // public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // }
        //
        // public void popupMenuCanceled(PopupMenuEvent e) {
        // }
        // });
        // popup.add(new AbstractAction("TODO") {
        // public void actionPerformed(ActionEvent e) {
        // }
        // });
        // tabbedPane.addMouseListener(new PopupListener(popup));

        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("> creating createYahooSourceView");
        // }
        // tabbedPane.addTab("Yahoo", createYahooSourceView());

        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("> creating createYahooApiSourceView");
        // }
        // tabbedPane.addTab("Yahoo Options", createYahooApiSourceView());
        //
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("> creating createFtDotComSourceView");
        // }
        // tabbedPane.addTab("ft.com", createFtDotComSourceView());
        //
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("> creating createYahooHistoricalSourceView");
        // }
        // tabbedPane.addTab("Yahoo Historical",
        // createYahooHistoricalSourceView());
        //
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("> creating createBloombergSourceView");
        // }
        // tabbedPane.addTab("Bloomberg", createBloombergSourceView());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> creating createYahooScreenScrapper2SourceView");
        }
        QuoteSourcePanel quoteSourcePanel = null;

        quoteSourcePanel = createYahooScreenScrapper2SourceView();
//        quoteSources.add(quoteSourcePanel.getQuoteSource());
        tabbedPane.addTab("Yahoo", quoteSourcePanel);

        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("> creating createTIAACREFQuoteSourceView");
        // }
        // tabbedPane.addTab("Scholarshare", createTIAACREFQuoteSourceView());

        quoteSourcePanel = createFtEquitiesSourceView();
//        quoteSources.add(quoteSourcePanel.getQuoteSource());
        tabbedPane.addTab("FT", quoteSourcePanel);

        // tabbedPane.addTab("FT Equities", createFtEquitiesSourceView());

        // createFtFundsSourceView
        // tabbedPane.addTab("FT Funds", createFtFundsSourceView());

        // createFtEtfsSourceView
        // tabbedPane.addTab("FT ETFs", createFtEtfsSourceView());

//        quoteSourcePanel = createMultiSourceView0();
//        quoteSourcePanel = createMultiSourceView();
//        quoteSources.add(quoteSourcePanel.getQuoteSource());
//        tabbedPane.addTab("Multi", quoteSourcePanel);

        int initialIndex = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.getInt(PREF_SELECTED_QUOTE_SOURCE, 0);
        LOGGER.info("RESTORE selectedQuoteSource, index=" + initialIndex);
        if (initialIndex >= tabbedPane.getTabCount()) {
            initialIndex = 0;
        }
        tabbedPane.setSelectedIndex(initialIndex);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JTabbedPane p = (JTabbedPane) event.getSource();
                quoteSourceTabSelected(p);
            }
        });
        return tabbedPane;
    }

    protected void saveSelectedQuoteSource(int index) {
        LOGGER.info("SAVE selectedQuoteSource, index=" + index);
        le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.putInt(PREF_SELECTED_QUOTE_SOURCE, index);
    }

    /**
     * Creates the yahoo source view.
     *
     * @return the component
     */
    private QuoteSourcePanel createYahooSourceView() {
        final YahooQuoteSourcePanel view = new YahooQuoteSourcePanel(this);
        this.yahooQuoteSourceView = view;
        return view;
    }

    /**
     * Creates the yahoo api source view.
     *
     * @return the component
     */
    private QuoteSourcePanel createYahooApiSourceView() {
        final YahooApiQuoteSourcePanel view = new YahooApiQuoteSourcePanel(this);
        this.yahooApiQuoteSourcePanel = view;
        return view;
    }

    /**
     * Creates the ft dot com source view.
     *
     * @return the component
     */
    private QuoteSourcePanel createFtDotComSourceView() {
        final FtCsvQuoteSourcePanel view = new FtCsvQuoteSourcePanel(this);
        this.ftDotComQuoteSourcePanel = view;
        return view;
    }

    /**
     * Creates the yahoo historical source view.
     *
     * @return the component
     */
    private QuoteSourcePanel createYahooHistoricalSourceView() {
        final QuoteSourcePanel view = new YahooHistSourcePanel(this);
        this.yahooHistoricalQuoteSourceView = view;
        return view;
    }

    /**
     * Creates the bloomberg source view.
     *
     * @return the component
     */
    private QuoteSourcePanel createBloombergSourceView() {
        final YahooApiQuoteSourcePanel view = new BloombergQuoteSourcePanel(this);
        this.bloombergQuoteSourcePanel = view;
        return view;
    }

    private QuoteSourcePanel createYahooScreenScrapper2SourceView() {
        final YahooSS2SourcePanel view = new YahooSS2SourcePanel(this);
//        this.yahooScreenScrapper2SourcePanel = view;
        return view;
    }

    private QuoteSourcePanel createFtEquitiesSourceView() {
        final FtEquitiesSourcePanel view = new FtEquitiesSourcePanel(this);
        this.ftEquitiesSourcePanel = view;
        return view;
    }

    private QuoteSourcePanel createFtFundsSourceView() {
        final FtFundsSourcePanel view = new FtFundsSourcePanel(this);
        this.ftFundsSourcePanel = view;
        return view;
    }

    private QuoteSourcePanel createFtEtfsSourceView() {
        final FtEtfsSourcePanel view = new FtEtfsSourcePanel(this);
        this.ftEtfsSourcePanel = view;
        return view;
    }

    private QuoteSourcePanel createMultiSourceView() {
        final MultiSourcePanel view = new MultiSourcePanel(this);
        // this.multiSourcePanel = view;
        return view;
    }
    
    /**
     * Creates the TIAACREF quote source view.
     *
     * @return the component
     */
    private QuoteSourcePanel createTIAACREFQuoteSourceView() {
        final TIAACREFQuoteSourcePanel view = new TIAACREFQuoteSourcePanel(this);
        this.tIAACREFQuoteSourcePanel = view;
        return view;
    }

    /**
     * Creates the result top view.
     *
     * @return the component
     */
    private Component createResultTopView() {
        JPanel view = new JPanel();
        view.setLayout(new BoxLayout(view, BoxLayout.PAGE_AXIS));
        view.add(createResultTopViewRow1());
        view.add(createResultTopViewRow2());
        return view;
    }

    /**
     * Creates the result top view row 2.
     *
     * @return the component
     */
    private Component createResultTopViewRow2() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
        accountIdLabel = new JLabel("OFX Account Id: " + getAccountId());
        view.add(accountIdLabel, BorderLayout.WEST);
        return view;
    }

    /**
     * Creates the result top view row 1.
     *
     * @return the component
     */
    private Component createResultTopViewRow1() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        defaulCurrencyLabel = new JLabel("Default currency: " + getDefaultCurrency());
        view.add(defaulCurrencyLabel, BorderLayout.WEST);

        String[] timeZoneIds = {
                /* "America/New_York", */
                /* "Europe/London", */
        };
        if ((timeZoneIds != null) && (timeZoneIds.length > 0)) {
            TimeZone zone = null;
            clockFormatters = new SimpleDateFormat[timeZoneIds.length];
            for (int i = 0; i < timeZoneIds.length; i++) {
                clockFormatters[i] = new SimpleDateFormat("EEEEEEEEE, dd-MMM-yy HH:mm:ss z");
                zone = TimeZone.getTimeZone(timeZoneIds[i]);
                clockFormatters[i].setTimeZone(zone);
            }
            clockLabel = new JLabel(getClockDisplayString());
            scheduleClockUpdate();
            view.add(clockLabel, BorderLayout.EAST);
        }

        return view;
    }

    /**
     * Schedule clock update.
     */
    private void scheduleClockUpdate() {
        Timer timer = new Timer();
        long delay = 1000L;
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                updateClockDisplay();
            }
        };
        long period = 1000L;
        timer.scheduleAtFixedRate(task, delay, period);
    }

    /**
     * Update clock display.
     */
    private void updateClockDisplay() {
        if (clockLabel == null) {
            return;
        }
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                String str = getClockDisplayString();
                clockLabel.setText(str);
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Creates the result view.
     *
     * @return the component
     */
    private Component createResultView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        view.add(createResultTopView(), BorderLayout.NORTH);

        setBottomTabs(new JTabbedPane());

        PriceTableViewOptions priceTableViewOptions = null;

        priceTableViewOptions = new PriceTableViewOptions();
        priceTableViewOptions.setConvertWhenExport(true);
        priceTableViewOptions.setCreateImport(true);
        priceTableViewOptions.setCreateMenu(true);
        getBottomTabs().add("Quote Source Prices", createPricesView(getPriceList(), priceTableViewOptions));

        priceTableViewOptions = new PriceTableViewOptions();
        priceTableViewOptions.setConvertWhenExport(false);
        priceTableViewOptions.setCreateImport(false);
        priceTableViewOptions.setCreateMenu(true);
        getBottomTabs().add("Converted Prices", createPricesView(getConvertedPriceList(), priceTableViewOptions));

        priceTableViewOptions = new PriceTableViewOptions();
        priceTableViewOptions.setConvertWhenExport(false);
        priceTableViewOptions.setCreateImport(false);
        priceTableViewOptions.setCreateMenu(false);
        boolean[] editable = { true, true, true, true, true };
        priceTableViewOptions.setEditable(editable);
        getBottomTabs().add("Not Found Prices", createPricesView(getNotFoundPriceList(), priceTableViewOptions));

        // getBottomTabs().add("Mapper", createMapperView());

        view.add(getBottomTabs(), BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem(new AbstractAction("Import *.csv file") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            private JFileChooser fc = null;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fc == null) {
                    initFileChooser();
                }
                if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fc.getSelectedFile();
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    char delimiter = StockPriceCsvUtils.CSV_DELIMITER_COMMA_CHAR;
                    List<AbstractStockPrice> stockPrices = StockPriceCsvUtils.toStockPrices(reader, delimiter);
                    LOGGER.info("From file=" + file + ", count=" + stockPrices.size());

                    QuoteSource quoteSource = new DefaultQuoteSource();
                    getQuoteSourceListener().stockPricesReceived(quoteSource, stockPrices);
                } catch (IOException e1) {
                    LOGGER.warn(e);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            LOGGER.warn(e);
                        } finally {
                            reader = null;
                        }
                    }
                }

            }

            private void initFileChooser() {
                String key = SaveOfxAction.PREF_SAVE_OFX_DIR;
                fc = new JFileChooser(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(key, "."));
                FileFilter filter = new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "yahoo.com CSV";
                    }

                    @Override
                    public boolean accept(File file) {
                        if (file.isDirectory()) {
                            return true;
                        }
                        String name = file.getName();
                        return name.endsWith(".csv");
                    }
                };
                this.fc.setFileFilter(filter);
            }
        }));
        MouseListener listener = new PopupListener(popupMenu) {

            @Override
            protected void maybeShowPopup(MouseEvent e) {
                if (getBottomTabs().getSelectedIndex() != 0) {
                    return;
                }
                super.maybeShowPopup(e);
            }
        };
        getBottomTabs().addMouseListener(listener);

        // if ((symbolMapper != null) && (getMapper() != null)) {
        // MapperTableUtils.updateMapperTable(symbolMapper, getMapper());
        // }

        return view;
    }

    /**
     * Creates the prices view.
     *
     * @return the component
     */
    private Component createPricesView(EventList<AbstractStockPrice> priceList,
            PriceTableViewOptions priceTableViewOptions) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createPricesView");
        }
        final JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTextField priceFilterEdit = new JTextField(10);
        // EventList<AbstractStockPrice> localPriceList = getPriceList();
        PriceTableView<AbstractStockPrice> priceTableView = new PriceTableView<AbstractStockPrice>(priceList,
                priceFilterEdit, AbstractStockPrice.class, priceTableViewOptions);
        view.add(priceTableView, BorderLayout.CENTER);

        JPanel commandView = new JPanel();
        commandView.setTransferHandler(new FileDropHandler() {
            @Override
            public void handleFile(File file) {
                importQFXFile(file);
            }
        });

        commandView.setLayout(new BoxLayout(commandView, BoxLayout.LINE_AXIS));
        commandView.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        if (priceTableViewOptions.isCreateImport()) {
            addPriceTableImport(commandView);
            view.add(commandView, BorderLayout.SOUTH);
        }

        final JPopupMenu popupMenu = priceTableView.getPopupMenu();
        if (priceTableViewOptions.isCreateMenu()) {
            addPriceTableViewMenu(this, priceList, priceTableViewOptions, view, popupMenu);
        }

        return view;
    }

    private static void addPriceTableViewMenu(GUI gui, EventList<AbstractStockPrice> priceList,
            PriceTableViewOptions priceTableViewOptions, final Component parent, JPopupMenu popupMenu) {
        // Component parent = view;

        AbstractAction action;
        JMenu menu = null;

        // OFX
        menu = new JMenu("OFX");
        popupMenu.add(menu);

        action = new ImportAction("Open as *.ofx", gui);
        menu.add(action);

        action = new SaveOfxAction("Save", gui);
        menu.add(action);

        // QIF
        menu = new JMenu("QIF");
        popupMenu.add(menu);

        action = new SaveAndOpenQIFAction("Open as *.qif", gui, priceList, priceTableViewOptions, parent);
        menu.add(action);
        
        action = new SaveQIFAction("Save", gui, priceList, priceTableViewOptions, parent);
        menu.add(action);

        // MD
        menu = new JMenu("MD");
        popupMenu.add(menu);
        action = new SaveMDCSVAction("Save CSV", gui, priceList, priceTableViewOptions, parent);
        menu.add(action);
    }

    private void addPriceTableImport(JPanel commandView) {
        AbstractAction action;
        action = new ImportAction("Import to MSMoney", this);
        importToMoneyButton = new JButton(action);
        importToMoneyButton.setEnabled(false);
        commandView.add(importToMoneyButton);
        commandView.add(Box.createHorizontalStrut(5));

        commandView.add(new JLabel("Last import on:"));
        commandView.add(Box.createHorizontalStrut(3));
        setLastKnownImportString(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(PREF_LAST_KNOWN_IMPORT_STRING, null));
        setLastKnownImport(new JLabel(getLastKnownImportString() == null ? "Not known" : getLastKnownImportString()));
        commandView.add(getLastKnownImport());

        // commandView.add(Box.createHorizontalStrut(5));

        /*
         * 
         * // view2.add(new JLabel("Filter:")); //
         * view2.add(Box.createHorizontalStrut(3)); //
         * view2.add(priceFilterEdit);
         */
        commandView.add(Box.createHorizontalGlue());
        action = new SaveOfxAction("Save OFX", this);
        saveOfxButton = new JButton(action);
        saveOfxButton.setEnabled(false);
        commandView.add(saveOfxButton);
    }

    /**
     * Creates the mapper view.
     *
     * @return the component
     */
    private Component createMapperView() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createMapperView");
        }
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTextField filterEdit = new JTextField(10);
        JScrollPane scrollPane = createScrolledMapperTable(filterEdit);
        view.add(scrollPane, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the scrolled mapper table.
     *
     * @param filterEdit
     *            the filter edit
     * @return the j scroll pane
     */
    private JScrollPane createScrolledMapperTable(JTextField filterEdit) {
        Comparator<? super SymbolMapperEntry> comparator = new Comparator<SymbolMapperEntry>() {
            @Override
            public int compare(SymbolMapperEntry b1, SymbolMapperEntry b2) {
                return b1.getMsMoneySymbol().compareTo(b2.getMsMoneySymbol());
            }
        };
        // JTextField filterEdit = new JTextField(10);
        TextFilterator<SymbolMapperEntry> filter = null;
        // String propertyNames[] = { "stockName", "stockSymbol", };
        // filter = new BeanTextFilterator(propertyNames);
        filter = new TextFilterator<SymbolMapperEntry>() {
            @Override
            public void getFilterStrings(List<String> list, SymbolMapperEntry bean) {
                list.add(bean.getMsMoneySymbol());
                list.add(bean.getQuotesSourceSymbol());
            }
        };
        boolean addStripe = true;
        JTable table = MapperTableUtils.createMapperTable(/* getMapper() */ null, comparator, filterEdit, filter,
                addStripe);
        // table.setFillsViewportHeight(true);
        JScrollPane scrolledPane = new JScrollPane(table);

        return scrolledPane;
    }

    /**
     * Show main frame.
     */
    protected void showMainFrame() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Gets the output files.
     *
     * @return the output files
     */
    public List<File> getOutputFiles() {
        return outputFiles;
    }

    /**
     * Sets the output files.
     *
     * @param outputFiles
     *            the new output files
     */
    public void setOutputFiles(List<File> outputFiles) {
        this.outputFiles = outputFiles;
        if (outputFiles != null) {
            Runnable doRun = new Runnable() {
                @Override
                public void run() {
                    if (saveOfxButton != null) {
                        saveOfxButton.setEnabled(true);
                    }
                    if (importToMoneyButton != null) {
                        importToMoneyButton.setEnabled(true);
                    }
                    if (updateExchangeRateButton != null) {
                        updateExchangeRateButton.setEnabled(true);
                    }
                }
            };
            SwingUtilities.invokeLater(doRun);
        }
    }

    /**
     * Gets the thread pool.
     *
     * @return the thread pool
     */
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * Gets the clock display string.
     *
     * @return the clock display string
     */
    private String getClockDisplayString() {
        Date date = new Date();
        long time = date.getTime();
        // to seconds
        time = time / 1000;
        int i = (int) (time % clockFormatters.length);
        SimpleDateFormat formatter = clockFormatters[i];
        // String timeZoneDisplayName = "[" +
        // formatter.getTimeZone().getDisplayName() + "]";
        String str = formatter.format(date);
        return str;
    }

    /**
     * Gets the current working directory.
     *
     * @return the current working directory
     */
    public static String getCurrentWorkingDirectory() {
        return new File(".").getAbsoluteFile().getAbsolutePath();
    }

    /**
     * Gets the yahoo quote server.
     *
     * @return the yahoo quote server
     */
    public String getYahooQuoteServer() {
        String yahooQuoteServer = null;
        YahooQuoteSourcePanel quoteSourceView = getYahooQuoteSourceView();
        if (quoteSourceView != null) {
            yahooQuoteServer = quoteSourceView.getQuoteServer();
        } else {
            yahooQuoteServer = YahooQuotesGetter.DEFAULT_HOST;
        }
        return yahooQuoteServer;
    }

    /**
     * Gets the yahoo quote source view.
     *
     * @return the yahoo quote source view
     */
    public YahooQuoteSourcePanel getYahooQuoteSourceView() {
        return yahooQuoteSourceView;
    }

    /**
     * Gets the prefs.
     *
     * @return the prefs
     */
    public static Preferences getPrefs() {
        return le.com.tools.moneyutils.ofx.quotes.GUI.PREFS;
    }

    /**
     * Gets the quote source listener.
     *
     * @return the quote source listener
     */
    public QuoteSourceListener getQuoteSourceListener() {
        return quoteSourceListener;
    }

    /**
     * Gets the date offset.
     *
     * @return the date offset
     */
    public Integer getDateOffset() {
        return dateOffset;
    }

    /**
     * Gets the force generating INVTRANLIST.
     *
     * @return the force generating INVTRANLIST
     */
    public Boolean getForceGeneratingINVTRANLIST() {
        return forceGeneratingINVTRANLIST;
    }

    /**
     * Gets the randomize share count.
     *
     * @return the randomize share count
     */
    public Boolean getRandomizeShareCount() {
        return randomizeShareCount;
    }

    /**
     * Gets the suspicious price.
     *
     * @return the suspicious price
     */
    public Integer getSuspiciousPrice() {
        return suspiciousPrice;
    }

    /**
     * Gets the incrementally increased share count.
     *
     * @return the incrementally increased share count
     */
    public Boolean getIncrementallyIncreasedShareCount() {
        return incrementallyIncreasedShareCount;
    }

    /**
     * Sets the incrementally increased share count.
     *
     * @param incrementallyIncreasedShareCount
     *            the new incrementally increased share count
     */
    public void setIncrementallyIncreasedShareCount(Boolean incrementallyIncreasedShareCount) {
        this.incrementallyIncreasedShareCount = incrementallyIncreasedShareCount;
    }

    /**
     * Select new currency.
     *
     * @param value
     *            the value
     */
    public void selectNewCurrency(String value) {
        LOGGER.info("Selected new currency: " + value);
        String newValue = value;
        if (newValue.compareTo(getDefaultCurrency()) != 0) {
            setDefaultCurrency(value);
            le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(PREF_DEFAULT_CURRENCY, getDefaultCurrency());
            defaulCurrencyLabel.setText("Default currency: " + getDefaultCurrency());
            // to clear the pricing table
            QuoteSource quoteSource = null;
            stockSymbolsStringReceived(quoteSource, null);
        }
    }

    /**
     * Select new account id.
     *
     * @param value
     *            the value
     */
    public void selectNewAccountId(String value) {
        LOGGER.info("Selected new 'OFX Account Id': " + value);
        String newValue = value;
        if (newValue.compareTo(getAccountId()) != 0) {
            setAccountId(newValue);
            le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(PREF_ACCOUNT_ID, getAccountId());
            accountIdLabel.setText("OFX Account Id: " + getAccountId());
            // to clear the pricing table
            QuoteSource quoteSource = null;
            stockSymbolsStringReceived(quoteSource, null);
        }
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        // VelocityUtils.initVelocity();

        // String implementationVendorId = "com.le.tools.moneyutils";
        // com.hungle.tools.moneyutils
        String implementationVendorId = "com.hungle.tools.moneyutils";
        String buildNumber = BuildNumber.findBuilderNumber(implementationVendorId);
        if (buildNumber == null) {
            LOGGER.warn("Cannot find buildNumber from Manifest.");
            LOGGER.warn("Using built-in buildNumber which is likely to be wrong!");
        } else {
            GUI.VERSION = buildNumber;
        }

        String title = "OFX - Update stock prices - " + GUI.VERSION;
        LOGGER.info(title);

        String cwd = "Current directory is " + getCurrentWorkingDirectory();
        LOGGER.info(cwd);

        final GUI mainFrame = new GUI(title);
        LOGGER.info("Using Yahoo quote server: " + mainFrame.getYahooQuoteServer());
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                mainFrame.showMainFrame();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("post mainFrame.showMainFrame()");
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Gets the fi dir.
     *
     * @return the fi dir
     */
    public File getFiDir() {
        return fiDir;
    }

    /**
     * Sets the fi dir.
     *
     * @param fiDir
     *            the new fi dir
     */
    public void setFiDir(File fiDir) {
        this.fiDir = fiDir;
    }

    public EventList<AbstractStockPrice> getPriceList() {
        return priceList;
    }

    public void setPriceList(EventList<AbstractStockPrice> priceList) {
        this.priceList = priceList;
    }

    // public EventList<AbstractStockPrice> getExchangeRates() {
    // return exchangeRates;
    // }
    //
    // public void setExchangeRates(EventList<AbstractStockPrice> exchangeRates)
    // {
    // this.exchangeRates = exchangeRates;
    // }
    //
    // public EventList<SymbolMapperEntry> getMapper() {
    // return mapper;
    // }
    //
    // public void setMapper(EventList<SymbolMapperEntry> mapper) {
    // this.mapper = mapper;
    // }

    public JTabbedPane getBottomTabs() {
        return bottomTabs;
    }

    public void setBottomTabs(JTabbedPane bottomTabs) {
        this.bottomTabs = bottomTabs;
    }

    public JTextPane getResultView() {
        return resultView;
    }

    public void setResultView(JTextPane resultView) {
        this.resultView = resultView;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public StatementPanel getDownloadView() {
        return downloadView;
    }

    public void setDownloadView(StatementPanel downloadView) {
        this.downloadView = downloadView;
    }

    public JTabbedPane getMainTabbed() {
        return mainTabbed;
    }

    public void setMainTabbed(JTabbedPane mainTabbed) {
        this.mainTabbed = mainTabbed;
    }

    public ImportDialogAutoClickService getImportDialogAutoClickService() {
        return importDialogAutoClickService;
    }

    public void setImportDialogAutoClickService(ImportDialogAutoClickService importDialogAutoClickService) {
        this.importDialogAutoClickService = importDialogAutoClickService;
    }

    public void setRandomizeShareCount(Boolean randomizeShareCount) {
        this.randomizeShareCount = randomizeShareCount;
    }

    public void setSuspiciousPrice(Integer suspiciousPrice) {
        this.suspiciousPrice = suspiciousPrice;
    }

    public String getLastKnownImportString() {
        return lastKnownImportString;
    }

    public void setLastKnownImportString(String lastKnownImportString) {
        this.lastKnownImportString = lastKnownImportString;
    }

    public JLabel getLastKnownImport() {
        return lastKnownImport;
    }

    public void setLastKnownImport(JLabel lastKnownImport) {
        this.lastKnownImport = lastKnownImport;
    }

    public SymbolMapper getSymbolMapper() {
        return symbolMapper;
    }

    private void setSymbolMapper(SymbolMapper symbolMapper) {
        this.symbolMapper = symbolMapper;
    }

    public FxTable getFxTable() {
        return fxTable;
    }

    private void setFxTable(FxTable fxTable) {
        this.fxTable = fxTable;
    }

    public EventList<AbstractStockPrice> getConvertedPriceList() {
        return convertedPriceList;
    }

    public void setConvertedPriceList(EventList<AbstractStockPrice> convertedPriceList) {
        this.convertedPriceList = convertedPriceList;
    }

    public EventList<AbstractStockPrice> getNotFoundPriceList() {
        return notFoundPriceList;
    }

    public void setNotFoundPriceList(EventList<AbstractStockPrice> notFoundPriceList) {
        this.notFoundPriceList = notFoundPriceList;
    }

    private void importQFXFile(File file) {
        final String errorTitle = "Cannot import";
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            LOGGER.warn("Cannot find suffix for fileName=" + fileName);
            String message = "Don't know how to import file=" + file;
            JOptionPane.showConfirmDialog(getContentPane(), message, errorTitle, JOptionPane.ERROR_MESSAGE);
            return;
        }

        String suffix = fileName.substring(index);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("suffix=" + suffix);
        }

        if (suffix == null) {
            LOGGER.warn("Cannot find suffix for fileName=" + fileName);
            String message = "Don't know how to import\r\nfile=" + file;
            JOptionPane.showMessageDialog(getContentPane(), message, errorTitle, JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            File ofxFile = null;
            if (suffix.compareToIgnoreCase(".ofx") == 0) {
                // OK
                ofxFile = file;
            } else if (suffix.compareToIgnoreCase(".qfx") == 0) {
                ofxFile = ImportUtils.renameToOfxFile(file);
                LOGGER.info("Importing file=" + file + " as ofxFile=" + ofxFile);
            } else {
                String message = "Don't know how to import\r\nfile=" + file;
                JOptionPane.showMessageDialog(getContentPane(), message, errorTitle, JOptionPane.ERROR_MESSAGE);
                return;
            }
            String prefKey = GUI.PRE_IMPORT_QFX_DIR;
            le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(prefKey, file.getAbsoluteFile().getParentFile().getAbsolutePath());

            final File finalOfxFile = ofxFile;
            Runnable command = new Runnable() {
                @Override
                public void run() {
                    try {
                        ImportUtils.doImport(getThreadPool(), finalOfxFile);
                    } catch (IOException e) {
                        Runnable doRun = new Runnable() {
                            @Override
                            public void run() {
                                String message = e.getMessage();
                                JOptionPane.showMessageDialog(getContentPane(), message, errorTitle,
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        };
                        SwingUtilities.invokeLater(doRun);
                    }
                }
            };
            getThreadPool().execute(command);
        } catch (IOException e) {
            LOGGER.error(e, e);
            String message = e.getMessage();
            JOptionPane.showMessageDialog(getContentPane(), message, errorTitle, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAllTables() {
        clearPriceTable();
        clearFxTable();
        clearMapperTable();
    }

    public static String getHomeDirectory() {
        String homeDirectory = null;

        homeDirectory = System.getProperty("user.home", ".");

        return homeDirectory;
    }

    public static String getTopDirectory() {
        String homeDir = getHomeDirectory();
        File topDir = new File(homeDir, ".hleofxquotes");
        if (!topDir.exists()) {
            topDir.mkdirs();
        }
        return topDir.getAbsoluteFile().getAbsolutePath();
    }

    private QuoteSource getQuoteSource() {
        return quoteSource;
    }

    private void setQuoteSource(QuoteSource quoteSource) {
        this.quoteSource = quoteSource;
    }

    private void quoteSourceTabSelected(JTabbedPane p) {
        setSelectedQuoteSourceIndex(p.getSelectedIndex());
        LOGGER.info("selectedQuoteSource=" + getSelectedQuoteSourceIndex());
        saveSelectedQuoteSource(getSelectedQuoteSourceIndex());
        Component c = p.getSelectedComponent();
        if ((c != null) && (c instanceof QuoteSourcePanel)) {
            QuoteSource qs = ((QuoteSourcePanel) c).getQuoteSource();
//            stockPricesLookupStarted(qs, new ArrayList<String>());
            clearAllTables();
            restorePreviousResult(qs);
        }
    }

    private int getSelectedQuoteSourceIndex() {
        return selectedQuoteSourceIndex;
    }

    private void setSelectedQuoteSourceIndex(int selectedQuoteSourceIndex) {
        this.selectedQuoteSourceIndex = selectedQuoteSourceIndex;
    }
    

    private void restorePreviousResult(final QuoteSource quoteSource) {
        QuotesResult quotesResult = quoteSource.getQuotesResult();
        LOGGER.info("quotesResult=" + quotesResult);

        if (quotesResult != null) {
            restorePreviousResult(quotesResult);
        }
    }

    private void restorePreviousResult(QuotesResult quotesResult) {
        LOGGER.info("> restorePreviousResult");
        
        EventList<AbstractStockPrice> sourceList = null;

        sourceList = quotesResult.getPriceList();
        LOGGER.info("quotesResult.getPriceList()=" + sourceList);
        if (sourceList != null) {
            getPriceList().addAll(sourceList);
        }

        sourceList = quotesResult.getConvertedPriceList();
        LOGGER.info("quotesResult.getConvertedPriceList()=" + sourceList);
        if (sourceList != null) {
            getConvertedPriceList().addAll(sourceList);
        }

        sourceList = quotesResult.getNotFoundPriceList();
        LOGGER.info("quotesResult.getNotFoundPriceList()=" + sourceList);
        if (sourceList != null) {
            getNotFoundPriceList().addAll(sourceList);
        }
    }

    public Integer getMinimumFractionDigits() {
        return minimumFractionDigits;
    }

    public void setMinimumFractionDigits(Integer decimalDigits) {
        this.minimumFractionDigits = decimalDigits;
    }

    public Integer getMaximumFractionDigits() {
        return maximumFractionDigits;
    }

    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }
}
