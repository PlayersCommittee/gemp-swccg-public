package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines an Empire Strikes Back Introductory Two-Player Game box.
 */
public class EmpireStrikesBackIntroTwoPlayerGameBox extends BasePackagedCardProduct {
    private List<String> _fixed = new ArrayList<String>();

    /**
     * Creates an Empire Strikes Back starter set.
     * @param library the blueprint library
     */
    public EmpireStrikesBackIntroTwoPlayerGameBox(SwccgCardBlueprintLibrary library) {
        super(library);

        // Dark side
        _fixed.add("3_83");   // AT-AT Driver
        _fixed.add("3_83");   // AT-AT Driver
        _fixed.add("3_83");   // AT-AT Driver
        _fixed.add("3_86");   // FX-10 (Effex-ten)
        _fixed.add("2_91");   // Imperial Commander
        _fixed.add("2_91");   // Imperial Commander
        _fixed.add("1_181");  // Imperial Trooper Guard
        _fixed.add("1_181");  // Imperial Trooper Guard
        _fixed.add("1_181");  // Imperial Trooper Guard
        _fixed.add("2_92");   // Imperial Squad Leader
        _fixed.add("2_92");   // Imperial Squad Leader
        _fixed.add("1_186");  // LIN-V8M (Elleyein-Veeateemm)
        _fixed.add("1_186");  // LIN-V8M (Elleyein-Veeateemm)
        _fixed.add("2_95");   // Lt. Pol Treidum
        _fixed.add("2_98");   // Officer Evax
        _fixed.add("3_91");   // Snowtrooper
        _fixed.add("3_91");   // Snowtrooper
        _fixed.add("3_91");   // Snowtrooper
        _fixed.add("3_91");   // Snowtrooper
        _fixed.add("3_91");   // Snowtrooper
        _fixed.add("3_91");   // Snowtrooper
        _fixed.add("3_92");   // Snowtrooper Officer
        _fixed.add("3_92");   // Snowtrooper Officer
        _fixed.add("104_6");  // Veers
        _fixed.add("104_6");  // Veers
        _fixed.add("104_6");  // Veers
        _fixed.add("1_205");  // Restraining Bolt
        _fixed.add("1_221");  // Ket Maliss
        _fixed.add("3_117");  // Cold Feet
        _fixed.add("3_117");  // Cold Feet
        _fixed.add("3_117");  // Cold Feet
        _fixed.add("1_238");  // Counter Assault
        _fixed.add("1_238");  // Counter Assault
        _fixed.add("3_126");  // He Hasn't Come Back Yet
        _fixed.add("3_126");  // He Hasn't Come Back Yet
        _fixed.add("3_126");  // He Hasn't Come Back Yet
        _fixed.add("3_127");  // I'd Just As Soon Kiss A Wookiee
        _fixed.add("2_140");  // Stunning Leader
        _fixed.add("2_140");  // Stunning Leader
        _fixed.add("104_7");  // Walker Garrison
        _fixed.add("104_7");  // Walker Garrison
        _fixed.add("104_7");  // Walker Garrison
        _fixed.add("3_144");  // Hoth: Defensive Perimeter (3rd Marker)
        _fixed.add("3_147");  // Hoth: Echo Docking Bay
        _fixed.add("3_148");  // Hoth: Ice Plains (5th Marker)
        _fixed.add("3_148");  // Hoth: Ice Plains (5th Marker)
        _fixed.add("104_4");  // Hoth: Mountains (6th Marker)
        _fixed.add("104_4");  // Hoth: Mountains (6th Marker)
        _fixed.add("3_149");  // Hoth: North Ridge (4th Marker)
        _fixed.add("104_5");  // Imperial Walker
        _fixed.add("104_5");  // Imperial Walker
        _fixed.add("104_5");  // Imperial Walker
        _fixed.add("1_312");  // Blaster Rifle
        _fixed.add("1_312");  // Blaster Rifle
        _fixed.add("1_317");  // Imperial Blaster
        _fixed.add("1_317");  // Imperial Blaster
        _fixed.add("3_160");  // Infantry Mine
        _fixed.add("3_160");  // Infantry Mine
        _fixed.add("1_322");   // Timer Mine
        _fixed.add("1_322");   // Timer Mine

        // Light side
        _fixed.add("104_1");   // Chewie
        _fixed.add("104_1");   // Chewie
        _fixed.add("104_1");   // Chewie
        _fixed.add("2_4");     // Commander Evram Lajaie
        _fixed.add("2_6");     // Corellian
        _fixed.add("3_6");     // Echo Base Trooper
        _fixed.add("3_6");     // Echo Base Trooper
        _fixed.add("3_6");     // Echo Base Trooper
        _fixed.add("3_6");     // Echo Base Trooper
        _fixed.add("3_6");     // Echo Base Trooper
        _fixed.add("3_6");     // Echo Base Trooper
        _fixed.add("3_7");     // Echo Base Trooper Officer
        _fixed.add("3_7");     // Echo Base Trooper Officer
        _fixed.add("3_9");     // FX-7 (Effex-Seven)
        _fixed.add("102_3");   // Leia
        _fixed.add("1_18");    // LIN-V8K (Elleyein-Veeatekay)
        _fixed.add("1_18");    // LIN-V8K (Elleyein-Veeatekay)
        _fixed.add("2_17");    // Rebel Commander
        _fixed.add("2_17");    // Rebel Commander
        _fixed.add("1_26");    // Rebel Guard
        _fixed.add("3_16");    // Rebel Scout
        _fixed.add("3_16");    // Rebel Scout
        _fixed.add("3_21");    // Tauntaun Handler
        _fixed.add("3_21");    // Tauntaun Handler
        _fixed.add("3_30");    // Hoth Survival Gear
        _fixed.add("3_30");    // Hoth Survival Gear
        _fixed.add("1_38");    // Restraining Bolt
        _fixed.add("1_64");    // Sai'torr Kal Fas
        _fixed.add("3_42");    // Fall Back!
        _fixed.add("3_42");    // Fall Back!
        _fixed.add("2_50");    // Houjix
        _fixed.add("2_50");    // Houjix
        _fixed.add("3_44");    // It Can Wait
        _fixed.add("1_90");    // It Could Be Worse
        _fixed.add("104_2");   // Lone Rogue
        _fixed.add("104_2");   // Lone Rogue
        _fixed.add("104_2");   // Lone Rogue
        _fixed.add("3_46");    // Nice Of You Guys To Drop By
        _fixed.add("1_113");   // Surprise Assault
        _fixed.add("1_113");   // Surprise Assault
        _fixed.add("3_56");    // Hoth: Defensive Perimeter (3rd Marker)
        _fixed.add("3_56");    // Hoth: Defensive Perimeter (3rd Marker)
        _fixed.add("3_58");    // Hoth: Echo Corridor
        _fixed.add("3_59");    // Hoth: Echo Docking Bay
        _fixed.add("3_59");    // Hoth: Echo Docking Bay
        _fixed.add("3_60");    // Hoth: Echo Med Lab
        _fixed.add("3_63");    // Hoth: Snow Trench (2nd Marker)
        _fixed.add("104_3");   // Rebel Snowspeeder
        _fixed.add("104_3");   // Rebel Snowspeeder
        _fixed.add("104_3");   // Rebel Snowspeeder
        _fixed.add("104_3");   // Rebel Snowspeeder
        _fixed.add("104_3");   // Rebel Snowspeeder
        _fixed.add("3_70");    // Tauntaun
        _fixed.add("3_70");    // Tauntaun
        _fixed.add("1_152");   // Blaster
        _fixed.add("1_152");   // Blaster
        _fixed.add("3_76");    // Infantry Mine
        _fixed.add("3_76");    // Infantry Mine
        _fixed.add("1_162");   // Timer Mine
        _fixed.add("1_162");   // Timer Mine

        filterNonExistingCards(_fixed);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.EMPIRE_STRIKES_BACK_INTRODUCTORY_TWO_PLAYER_GAME;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.EMPIRE_STRIKES_BACK_INTRODUCTORY_TWO_PLAYER_GAME;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addFixedCard(result, _fixed, 120);
        addProducts(result, ProductName.HOTH_BOOSTER_PACK, 1);
        return result;
    }

    /**
     * Adds fixed cards to the list.
     * @param result the list of cards in the pack
     * @param fromCards the list of cards to add from
     * @param count the number cards to add
     */
    private void addFixedCard(List<CardCollection.Item> result, List<String> fromCards, int count) {
        addCards(result, fromCards.subList(0, Math.min(fromCards.size(), count)), false);
    }
}
