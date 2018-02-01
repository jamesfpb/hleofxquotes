package com.hungle.msmoney.core.stockprice;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.CheckNullUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class Price.
 */
public class Price extends Number implements Comparable<Price>, Cloneable {
    private static final Logger LOGGER = Logger.getLogger(Price.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The price. */
    private Double price;

    /** The currency. */
    private String currency;

    /** The price formatter. */
    private final NumberFormat priceFormatter;

    private String marketState;

    /**
     * Instantiates a new price.
     *
     * @param price
     *            the price
     */
    public Price(double price) {
        this.price = price;
        this.priceFormatter = createPriceFormatter();
    }

    private NumberFormat createPriceFormatter() {
        return createPriceFormatter(null);
    }

    public static final NumberFormat createPriceFormatter(Locale locale) {
        NumberFormat priceFormatter = null;
        if (locale == null) {
            priceFormatter = NumberFormat.getNumberInstance();
        } else {
            priceFormatter = NumberFormat.getNumberInstance(locale);
        }
        priceFormatter.setGroupingUsed(false);
        priceFormatter.setMinimumFractionDigits(4);
        priceFormatter.setMaximumFractionDigits(4);
        return priceFormatter;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Price clonePrice() {
        Price price = null;
        try {
            price = (Price) this.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.warn(e);
        }
        return price;
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price.
     *
     * @param price
     *            the new price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.priceFormatter.format(this.price));

        if (currency != null) {
            sb.append(" ");
            sb.append(currency);
        }

        // PREPRE, REGULAR, POST, CLOSED
        String marketState = getMarketState();
        if (marketState != null) {
            if (marketState.compareTo("REGULAR") == 0) {
                sb.append(" ");
                sb.append("(O)");
            } else {
                sb.append(" ");
                sb.append("(C)");
            }
        }

        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return price.doubleValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return price.floatValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return price.intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return price.longValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Price o) {
        return price.compareTo(o.getPrice());
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency
     *            the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the price formatter.
     *
     * @return the price formatter
     */
    public NumberFormat getPriceFormatter() {
        return priceFormatter;
    }

    public String getMarketState() {
        return marketState;
    }

    public void setMarketState(String marketState) {
        this.marketState = marketState;
    }

    public static String toPriceString(Price lastPrice, String language, String country) {
        NumberFormat priceFormatter = null;
        if (CheckNullUtils.isEmpty(language) || CheckNullUtils.isEmpty(country)) {
            priceFormatter = lastPrice.getPriceFormatter();
        } else {
            Locale locale = new Locale(language, country);
            priceFormatter = createPriceFormatter(locale);
        }
        return priceFormatter.format(lastPrice);
    }
}