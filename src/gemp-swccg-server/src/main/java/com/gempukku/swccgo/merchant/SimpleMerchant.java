package com.gempukku.swccgo.merchant;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.CardCounts;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.db.MerchantDAO;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.packagedProduct.ProductPrice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines a merchant the defines the product prices based on simple rules.
 */
public class SimpleMerchant implements Merchant {
    public static final int FOIL_PRICE_MULTIPLIER = 4;
    public static final int FOIL_BASE_PRICE = 50;
    private final float _profitMargin = 0.10f;
    private Map<Integer, SetRarity> _rarities = new HashMap<Integer, SetRarity>();
    private SwccgCardBlueprintLibrary _library;
    private MerchantDAO _merchantDao;

    /**
     * Creates a simple merchant.
     * @param library the blueprint library
     * @param merchantDao the merchant database access object
     */
    public SimpleMerchant(SwccgCardBlueprintLibrary library, MerchantDAO merchantDao) {
        _library = library;
        _merchantDao = merchantDao;

        RarityReader rarityReader = new RarityReader();
        for (int i = 1; i < (1 + CardCounts.FULL_SETS_CARD_COUNTS.length); i++) {
            _rarities.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 101; i < (101 + CardCounts.PREMIUM_SETS_CARD_COUNTS.length); i++) {
            _rarities.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 200; i < (200 + CardCounts.VIRTUAL_SETS_CARD_COUNTS.length); i++) {
            _rarities.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 301; i < (301 + CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS.length); i++) {
            _rarities.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
    }

    /**
     * Gets the card sell price.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @return the price
     */
    @Override
    public Integer getCardSellPrice(String blueprintId, Date currentTime) {
        blueprintId = _library.getBaseBlueprintId(blueprintId);

        float normalPrice = getNormalPrice(blueprintId);

        return Math.max(2, (int) Math.floor(normalPrice));
    }

    /**
     * Gets the card buy price.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @return the price
     */
    @Override
    public Integer getCardBuyPrice(String blueprintId, Date currentTime) {
        boolean foil = blueprintId.endsWith("*");
        blueprintId = _library.getBaseBlueprintId(blueprintId);

        float normalPrice = getNormalPrice(blueprintId);
        int price = Math.max(1, (int) Math.floor(_profitMargin * normalPrice));
        if (foil) {
            price *= FOIL_PRICE_MULTIPLIER;
            price += FOIL_BASE_PRICE;
        }

        return price;
    }

    /**
     * Gets the normal price of the card.
     * @param blueprintId the card blueprint id
     * @return the price
     */
    private float getNormalPrice(String blueprintId) {
        int underscoreIndex = blueprintId.indexOf("_");
        if (underscoreIndex == -1) {
            throw new RuntimeException("Unknown blueprintId: " + blueprintId);
        }
        SetRarity rarity = _rarities.get(Integer.parseInt(blueprintId.substring(0, blueprintId.indexOf("_"))));
        if (rarity == null) {
            throw new RuntimeException("Unknown blueprintId: " + blueprintId);
        }

        final float BASE_PRICE = (float) ProductPrice.BASE_CARD_PRICE;

        Rarity cardRarity = rarity.getCardRarity(blueprintId);
        if (cardRarity == Rarity.UR) // Ultra Rare (UR)
            return BASE_PRICE * 10;
        if (cardRarity == Rarity.XR) // Exclusive Rare (XR)
            return BASE_PRICE * 3;
        if (cardRarity == Rarity.PM) // Premium (P)
            return BASE_PRICE * 2;
        if (cardRarity == Rarity.PV) // Preview (PV)
            return BASE_PRICE * 2;
        if (cardRarity == Rarity.R1) // Rare (R1)
            return BASE_PRICE * 2;
        if (cardRarity == Rarity.R)  // Rare (R)
            return (BASE_PRICE * 3) / 2;
        if (cardRarity == Rarity.R2) // Rare (R2)
            return BASE_PRICE;
        if (cardRarity == Rarity.F)  // Fixed (F)
            return BASE_PRICE / 2;
        if (cardRarity == Rarity.V)  // Virtual (V)
            return BASE_PRICE / 2;
        if (cardRarity == Rarity.U1) // Uncommon (U1)
            return BASE_PRICE / 2;
        if (cardRarity == Rarity.U)  // Uncommon (U)
            return BASE_PRICE / 3;
        if (cardRarity == Rarity.U2) // Uncommon (U2)
            return BASE_PRICE / 4;
        if (cardRarity == Rarity.C1) // Common (C1)
            return BASE_PRICE / 5;
        if (cardRarity == Rarity.C)  // Common (C)
            return BASE_PRICE / 8;
        if (cardRarity == Rarity.C2) // Common (C2)
            return BASE_PRICE / 10;
        if (cardRarity == Rarity.C3) // Common (C3)
            return BASE_PRICE / 15;
        throw new RuntimeException("Unknown rarity for priced card: " + cardRarity);
    }

    /**
     * Called when card was sold by merchant.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @param price the price
     */
    @Override
    public void cardSold(String blueprintId, Date currentTime, int price) {
        _merchantDao.addTransaction(blueprintId, price, currentTime, MerchantDAO.TransactionType.SELL);
    }

    /**
     * Called when card was bought by merchant.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @param price the price
     */
    @Override
    public void cardBought(String blueprintId, Date currentTime, int price) {
        boolean foil = blueprintId.endsWith("*");
        if (foil) {
            price = (price / FOIL_PRICE_MULTIPLIER)-FOIL_BASE_PRICE;
        }
        blueprintId = _library.getBaseBlueprintId(blueprintId);
        _merchantDao.addTransaction(blueprintId, (price / _profitMargin), currentTime, MerchantDAO.TransactionType.BUY);
    }

    /**
     * Called when cards were bought by merchant.
     * @param blueprintId the card blueprint id
     * @param currentTime the current time
     * @param price the price
     * @param quantity the number of cards
     */
    @Override
    public void cardsBought(String blueprintId, Date currentTime, int price, int quantity) {
        boolean foil = blueprintId.endsWith("*");
        if (foil) {
            price = (price / FOIL_PRICE_MULTIPLIER)-FOIL_BASE_PRICE;
        }
        blueprintId = _library.getBaseBlueprintId(blueprintId);
        _merchantDao.addTransaction(blueprintId, (price / _profitMargin)*quantity, currentTime, MerchantDAO.TransactionType.BUY);
    }
}
