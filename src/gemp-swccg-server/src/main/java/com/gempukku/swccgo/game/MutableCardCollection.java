package com.gempukku.swccgo.game;

import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;

import java.util.Map;

public interface MutableCardCollection extends CardCollection {
    public void addItem(String itemId, int count);

    public boolean removeItem(String itemId, int count);

    public void addCurrency(int currency);

    public boolean removeCurrency(int currency);

    public CardCollection openPack(String packId, String selection, PackagedProductStorage packBox);

    void setExtraInformation(Map<String, Object> extraInformation);
}
