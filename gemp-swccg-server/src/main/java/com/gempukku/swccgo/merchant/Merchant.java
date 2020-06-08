package com.gempukku.swccgo.merchant;

import java.util.Date;

/**
 * Defines the methods that a merchant must implement.
 */
public interface Merchant {

    /**
     * Gets the card sell price.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @return the price
     */
    Integer getCardSellPrice(String blueprintId, Date currentTime);

    /**
     * Gets the card buy price.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @return the price
     */
    Integer getCardBuyPrice(String blueprintId, Date currentTime);

    /**
     * Called when card was sold by merchant.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @param price the price
     */
    void cardSold(String blueprintId, Date currentTime, int price);

    /**
     * Called when card was bought by merchant.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @param price the price
     */
    void cardBought(String blueprintId, Date currentTime, int price);
}
