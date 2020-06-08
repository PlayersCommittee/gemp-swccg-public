package com.gempukku.swccgo.merchant;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.CardCounts;
import com.gempukku.swccgo.db.MerchantDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardItem;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.packagedProduct.ProductName;
import com.gempukku.swccgo.packagedProduct.ProductPrice;
import org.apache.commons.collections.map.LRUMap;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Provides the implementation of the merchant service for buying and selling cards.
 */
public class MerchantService {
    private Merchant _merchant;
    private long _priceGuaranteeExpire = 1000 * 60 * 5;
    private Map<String, PriceGuarantee> _priceGuarantees = Collections.synchronizedMap(new LRUMap(100));

    private ReadWriteLock _lock = new ReentrantReadWriteLock(true);
    private Set<CardItem> _merchantableItems = new HashSet<CardItem>();
    private Set<String> _merchantableStrings = new HashSet<String>();

    private Map<String, Integer> _fixedPriceItems = new HashMap<String, Integer>();

    private CollectionType _permanentCollection = CollectionType.MY_CARDS;
    private CollectionsManager _collectionsManager;

    /**
     * Creates the merchant service.
     * @param library the blueprint library
     * @param collectionsManager the collections manager
     * @param merchantDAO the merchant database access object
     */
    public MerchantService(SwccgCardBlueprintLibrary library, CollectionsManager collectionsManager, MerchantDAO merchantDAO) {
        _collectionsManager = collectionsManager;
        _merchant = new SimpleMerchant(library, merchantDAO);

        // Add Cards
        RarityReader rarityReader = new RarityReader();
        for (int i = 1; i < (1 + CardCounts.FULL_SETS_CARD_COUNTS.length); i++) {
            SetRarity rarity = rarityReader.getSetRarity(String.valueOf(i));
            for (String blueprintId : rarity.getAllCards()) {
                String baseBlueprintId = library.getBaseBlueprintId(blueprintId);
                _merchantableItems.add(new BasicCardItem(baseBlueprintId));
                _merchantableStrings.add(baseBlueprintId);
            }
        }
        for (int i = 101; i < (101 + CardCounts.PREMIUM_SETS_CARD_COUNTS.length); i++) {
            SetRarity rarity = rarityReader.getSetRarity(String.valueOf(i));
            for (String blueprintId : rarity.getAllCards()) {
                String baseBlueprintId = library.getBaseBlueprintId(blueprintId);
                _merchantableItems.add(new BasicCardItem(baseBlueprintId));
                _merchantableStrings.add(baseBlueprintId);
            }
        }
        for (int i = 200; i < (200 + CardCounts.VIRTUAL_SETS_CARD_COUNTS.length); i++) {
            SetRarity rarity = rarityReader.getSetRarity(String.valueOf(i));
            for (String blueprintId : rarity.getAllCards()) {
                String baseBlueprintId = library.getBaseBlueprintId(blueprintId);
                _merchantableItems.add(new BasicCardItem(baseBlueprintId));
                _merchantableStrings.add(baseBlueprintId);
            }
        }
        for (int i = 301; i < (301 + CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS.length); i++) {
            SetRarity rarity = rarityReader.getSetRarity(String.valueOf(i));
            for (String blueprintId : rarity.getAllCards()) {
                String baseBlueprintId = library.getBaseBlueprintId(blueprintId);
                _merchantableItems.add(new BasicCardItem(baseBlueprintId));
                _merchantableStrings.add(baseBlueprintId);
            }
        }

        // Add packs/decks/boxes
        addFixedItem(ProductName.PREMIERE_BOOSTER_PACK, ProductPrice.PREMIERE_BOOSTER_PACK);
        addFixedItem(ProductName.A_NEW_HOPE_BOOSTER_PACK, ProductPrice.A_NEW_HOPE_BOOSTER_PACK);
        addFixedItem(ProductName.HOTH_BOOSTER_PACK, ProductPrice.HOTH_BOOSTER_PACK);
        addFixedItem(ProductName.DAGOBAH_BOOSTER_PACK, ProductPrice.DAGOBAH_BOOSTER_PACK);
        addFixedItem(ProductName.CLOUD_CITY_BOOSTER_PACK, ProductPrice.CLOUD_CITY_BOOSTER_PACK);
        addFixedItem(ProductName.JABBAS_PALACE_BOOSTER_PACK, ProductPrice.JABBAS_PALACE_BOOSTER_PACK);
        addFixedItem(ProductName.SPECIAL_EDITION_BOOSTER_PACK, ProductPrice.SPECIAL_EDITION_BOOSTER_PACK);
        addFixedItem(ProductName.ENDOR_BOOSTER_PACK, ProductPrice.ENDOR_BOOSTER_PACK);
        addFixedItem(ProductName.DEATH_STAR_II_BOOSTER_PACK, ProductPrice.DEATH_STAR_II_BOOSTER_PACK);
        addFixedItem(ProductName.TATOOINE_BOOSTER_PACK, ProductPrice.TATOOINE_BOOSTER_PACK);
        addFixedItem(ProductName.CORUSCANT_BOOSTER_PACK, ProductPrice.CORUSCANT_BOOSTER_PACK);
        addFixedItem(ProductName.THEED_PALACE_BOOSTER_PACK, ProductPrice.THEED_PALACE_BOOSTER_PACK);
        addFixedItem(ProductName.REFLECTIONS_BOOSTER_PACK, ProductPrice.REFLECTIONS_BOOSTER_PACK);
        addFixedItem(ProductName.REFLECTIONS_II_BOOSTER_PACK, ProductPrice.REFLECTIONS_II_BOOSTER_PACK);
        addFixedItem(ProductName.REFLECTIONS_III_BOOSTER_PACK, ProductPrice.REFLECTIONS_III_BOOSTER_PACK);

        // Add booster boxes
        addFixedItem(ProductName.PREMIERE_BOOSTER_BOX, ProductPrice.PREMIERE_BOOSTER_BOX);
        addFixedItem(ProductName.A_NEW_HOPE_BOOSTER_BOX, ProductPrice.A_NEW_HOPE_BOOSTER_BOX);
        addFixedItem(ProductName.HOTH_BOOSTER_BOX, ProductPrice.HOTH_BOOSTER_BOX);
        addFixedItem(ProductName.DAGOBAH_BOOSTER_BOX, ProductPrice.DAGOBAH_BOOSTER_BOX);
        addFixedItem(ProductName.CLOUD_CITY_BOOSTER_BOX, ProductPrice.CLOUD_CITY_BOOSTER_BOX);
        addFixedItem(ProductName.JABBAS_PALACE_BOOSTER_BOX, ProductPrice.JABBAS_PALACE_BOOSTER_BOX);
        addFixedItem(ProductName.SPECIAL_EDITION_BOOSTER_BOX, ProductPrice.SPECIAL_EDITION_BOOSTER_BOX);
        addFixedItem(ProductName.ENDOR_BOOSTER_BOX, ProductPrice.ENDOR_BOOSTER_BOX);
        addFixedItem(ProductName.DEATH_STAR_II_BOOSTER_BOX, ProductPrice.DEATH_STAR_II_BOOSTER_BOX);
        addFixedItem(ProductName.TATOOINE_BOOSTER_BOX, ProductPrice.TATOOINE_BOOSTER_BOX);
        addFixedItem(ProductName.CORUSCANT_BOOSTER_BOX, ProductPrice.CORUSCANT_BOOSTER_BOX);
        addFixedItem(ProductName.THEED_PALACE_BOOSTER_BOX, ProductPrice.THEED_PALACE_BOOSTER_BOX);
        addFixedItem(ProductName.REFLECTIONS_BOOSTER_BOX, ProductPrice.REFLECTIONS_BOOSTER_BOX);
        addFixedItem(ProductName.REFLECTIONS_II_BOOSTER_BOX, ProductPrice.REFLECTIONS_II_BOOSTER_BOX);
        addFixedItem(ProductName.REFLECTIONS_III_BOOSTER_BOX, ProductPrice.REFLECTIONS_III_BOOSTER_BOX);

        // Add starter sets / starter decks / pre-constructed sets
        addFixedItem(ProductName.PREMIERE_STARTER_SET, ProductPrice.PREMIERE_STARTER_SET);
        addFixedItem(ProductName.SPECIAL_EDITION_DARK_STARTER_DECK, ProductPrice.SPECIAL_EDITION_STARTER_DECK);
        addFixedItem(ProductName.SPECIAL_EDITION_LIGHT_STARTER_DECK, ProductPrice.SPECIAL_EDITION_STARTER_DECK);
        addFixedItem(ProductName.DEATH_STAR_II_PRE_CONSTRUCTED_DARK_DECK, ProductPrice.DEATH_STAR_II_PRE_CONSTRUCTED_DECK);
        addFixedItem(ProductName.DEATH_STAR_II_PRE_CONSTRUCTED_LIGHT_DECK, ProductPrice.DEATH_STAR_II_PRE_CONSTRUCTED_DECK);

        // Add enhanced sets
        addFixedItem(ProductName.ENHANCED_PREMIERE_PACK_DARTH_VADER, ProductPrice.ENHANCED_PREMIERE_PACK);
        addFixedItem(ProductName.ENHANCED_PREMIERE_PACK_LUKE, ProductPrice.ENHANCED_PREMIERE_PACK);
        addFixedItem(ProductName.ENHANCED_PREMIERE_PACK_BOBA_FETT, ProductPrice.ENHANCED_PREMIERE_PACK);
        addFixedItem(ProductName.ENHANCED_PREMIERE_PACK_OBIWAN, ProductPrice.ENHANCED_PREMIERE_PACK);
        addFixedItem(ProductName.ENHANCED_PREMIERE_PACK_HAN, ProductPrice.ENHANCED_PREMIERE_PACK);
        addFixedItem(ProductName.ENHANCED_PREMIERE_PACK_LEIA, ProductPrice.ENHANCED_PREMIERE_PACK);
        addFixedItem(ProductName.ENHANCED_CLOUD_CITY_PACK_BOBA_FETT, ProductPrice.ENHANCED_CLOUD_CITY_PACK);
        addFixedItem(ProductName.ENHANCED_CLOUD_CITY_PACK_LANDO, ProductPrice.ENHANCED_CLOUD_CITY_PACK);
        addFixedItem(ProductName.ENHANCED_CLOUD_CITY_PACK_IG88, ProductPrice.ENHANCED_CLOUD_CITY_PACK);
        addFixedItem(ProductName.ENHANCED_CLOUD_CITY_PACK_CHEWIE, ProductPrice.ENHANCED_CLOUD_CITY_PACK);
        addFixedItem(ProductName.ENHANCED_JABBAS_PALACE_PACK_BOUSHH, ProductPrice.ENHANCED_JABBAS_PALACE_PACK);
        addFixedItem(ProductName.ENHANCED_JABBAS_PALACE_PACK_MARA_JADE, ProductPrice.ENHANCED_JABBAS_PALACE_PACK);
        addFixedItem(ProductName.ENHANCED_JABBAS_PALACE_PACK_MASTER_LUKE, ProductPrice.ENHANCED_JABBAS_PALACE_PACK);
        addFixedItem(ProductName.ENHANCED_JABBAS_PALACE_PACK_SEETHREEPIO, ProductPrice.ENHANCED_JABBAS_PALACE_PACK);

        // Add anthology boxes
        addFixedItem(ProductName.FIRST_ANTHOLOGY_BOX, ProductPrice.FIRST_ANTHOLOGY_BOX);
        addFixedItem(ProductName.SECOND_ANTHOLOGY_BOX, ProductPrice.SECOND_ANTHOLOGY_BOX);
        addFixedItem(ProductName.THIRD_ANTHOLOGY_BOX, ProductPrice.THIRD_ANTHOLOGY_BOX);

        // Add sealed decks
        addFixedItem(ProductName.OFFICIAL_TOURNAMENT_SEALED_DECK, ProductPrice.OFFICIAL_TOURNAMENT_SEALED_DECK);
        addFixedItem(ProductName.JABBAS_PALACE_SEALED_DECK, ProductPrice.JABBAS_PALACE_SEALED_DECK);

        // Add introductory 2-player games
        addFixedItem(ProductName.PREMIERE_INTRODUCTORY_TWO_PLAYER_GAME, ProductPrice.PREMIERE_INTRODUCTORY_TWO_PLAYER_GAME);
        addFixedItem(ProductName.EMPIRE_STRIKES_BACK_INTRODUCTORY_TWO_PLAYER_GAME, ProductPrice.EMPIRE_STRIKES_BACK_INTRODUCTORY_TWO_PLAYER_GAME);
    }

    /**
     * Adds a fixed-priced item.
     * @param blueprintId the blueprint id
     * @param price the price
     */
    private void addFixedItem(String blueprintId, int price) {
        _fixedPriceItems.put(blueprintId, price);
        _merchantableItems.add(new BasicCardItem(blueprintId));
        _merchantableStrings.add(blueprintId);
    }

    /**
     * Gets the sellable items.
     * @return the sellable items
     */
    public Set<CardItem> getSellableItems() {
        return Collections.unmodifiableSet(_merchantableItems);
    }

    /**
     * Gets the buy and sell prices available to the player for the specified cards or packaged products.
     * @param player the player
     * @param cardBlueprintIds the blueprint ids
     * @return the prices
     */
    public PriceGuarantee priceCards(Player player, Collection<CardItem> cardBlueprintIds) {
        Lock lock = _lock.readLock();
        lock.lock();
        try {
            Date currentTime = new Date();
            Map<String, Integer> buyPrices = new HashMap<String, Integer>();
            Map<String, Integer> sellPrices = new HashMap<String, Integer>();
            for (CardItem cardItem : cardBlueprintIds) {
                String blueprintId = cardItem.getBlueprintId();
                Integer fixedPrice = _fixedPriceItems.get(blueprintId);
                // Fixed price items are only sold by the merchant (not bought by the merchant)
                if (fixedPrice != null) {
                    sellPrices.put(blueprintId, fixedPrice);
                }
                else if (blueprintId.contains("_")) {
                    Integer buyPrice = _merchant.getCardBuyPrice(blueprintId, currentTime);
                    if (buyPrice != null) {
                        buyPrices.put(blueprintId, buyPrice);
                    }
                    if (_merchantableStrings.contains(blueprintId)) {
                        Integer sellPrice = _merchant.getCardSellPrice(blueprintId, currentTime);
                        if (sellPrice != null) {
                            sellPrices.put(blueprintId, sellPrice);
                        }
                    }
                }
            }
            PriceGuarantee priceGuarantee = new PriceGuarantee(sellPrices, buyPrices, currentTime);
            _priceGuarantees.put(player.getName(), priceGuarantee);
            return priceGuarantee;
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Buys a card product from the player.
     * @param player the player
     * @param blueprintId the blueprint id
     * @param price the price
     * @throws MerchantException
     */
    public void merchantBuysCard(Player player, String blueprintId, int price) throws MerchantException {
        Date currentTime = new Date();
        Lock lock = _lock.writeLock();
        lock.lock();
        try {
            PriceGuarantee guarantee = _priceGuarantees.get(player.getName());
            if (guarantee == null || guarantee.getDate().getTime() + _priceGuaranteeExpire < currentTime.getTime())
                throw new MerchantException("Price guarantee has expired");
            Integer guaranteedPrice = guarantee.getBuyPrices().get(blueprintId);
            if (guaranteedPrice == null || price != guaranteedPrice)
                throw new MerchantException("Guaranteed price does not match the user asked price");

            boolean success = _collectionsManager.sellCardInPlayerCollection(player, _permanentCollection, blueprintId, price);
            if (!success)
                throw new MerchantException("Unable to remove the sold card from your collection");

            _merchant.cardBought(blueprintId, currentTime, price);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Sells a card product to the player.
     * @param player the player
     * @param blueprintId the blueprint id
     * @param price the price
     * @throws MerchantException
     */
    public void merchantSellsCard(Player player, String blueprintId, int price) throws MerchantException {
        Date currentTime = new Date();
        Lock lock = _lock.writeLock();
        lock.lock();
        try {
            PriceGuarantee guarantee = _priceGuarantees.get(player.getName());
            if (guarantee == null || guarantee.getDate().getTime() + _priceGuaranteeExpire < currentTime.getTime())
                throw new MerchantException("Price guarantee has expired");
            Integer guaranteedPrice = guarantee.getSellPrices().get(blueprintId);
            if (guaranteedPrice == null || price != guaranteedPrice)
                throw new MerchantException("Guaranteed price does not match the user asked price");

            boolean success = _collectionsManager.buyCardToPlayerCollection(player, _permanentCollection, blueprintId, price);
            if (!success)
                throw new MerchantException("Unable to remove required currency from your collection");

            _merchant.cardSold(blueprintId, currentTime, price);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Gives the player a foil card in exchange for four copies of the card (plus a fee).
     * @param player the player
     * @param blueprintId the blueprint id
     * @throws MerchantException
     */
    public void tradeForFoil(Player player, String blueprintId) throws MerchantException {
        if (!blueprintId.contains("_") || blueprintId.endsWith("*"))
            throw new MerchantException("Unable to trade in this type of item");
        Lock lock = _lock.writeLock();
        lock.lock();
        try {
            boolean success = _collectionsManager.tradeCards(player, _permanentCollection, blueprintId, 4, blueprintId + "*", 1, 1500);
            if (!success)
                throw new MerchantException("Unable to remove the required cards or currency from your collection");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * A private class that represents a basic card item.
     */
    private static class BasicCardItem implements CardItem {
        private String _blueprintId;

        private BasicCardItem(String blueprintId) {
            _blueprintId = blueprintId;
        }

        /**
         * Gets the blueprint id.
         * @return the blueprint id
         */
        @Override
        public String getBlueprintId() {
            return _blueprintId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BasicCardItem that = (BasicCardItem) o;

            if (_blueprintId != null ? !_blueprintId.equals(that._blueprintId) : that._blueprintId != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return _blueprintId != null ? _blueprintId.hashCode() : 0;
        }
    }

    /**
     * Represents the buy and sell prices provided to the player for each item available from the merchant.
     */
    public static class PriceGuarantee {
        private Map<String, Integer> _sellPrices;
        private Map<String, Integer> _buyPrices;
        private Date _date;

        private PriceGuarantee(Map<String, Integer> sellPrices, Map<String, Integer> buyPrices, Date date) {
            _sellPrices = sellPrices;
            _buyPrices = buyPrices;
            _date = date;
        }

        public Date getDate() {
            return _date;
        }

        public Map<String, Integer> getBuyPrices() {
            return _buyPrices;
        }

        public Map<String, Integer> getSellPrices() {
            return _sellPrices;
        }
    }
}
