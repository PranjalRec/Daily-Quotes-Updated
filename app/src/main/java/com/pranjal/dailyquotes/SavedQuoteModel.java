package com.pranjal.dailyquotes;

public class SavedQuoteModel {

    String quote, quoteKey;

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getQuoteKey() {
        return quoteKey;
    }

    public void setQuoteKey(String quoteKey) {
        this.quoteKey = quoteKey;
    }

    public SavedQuoteModel(String quote, String quoteKey) {
        this.quote = quote;
        this.quoteKey = quoteKey;
    }
}
