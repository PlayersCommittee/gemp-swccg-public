package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a Premiere Introductory Two-Player Game box.
 */
public class PremiereIntroTwoPlayerGameBox extends BasePackagedCardProduct {
    private List<String> _fixed = new ArrayList<String>();

    /**
     * Creates a Premiere Introductory Two-Player Game box.
     * @param library the blueprint library
     */
    public PremiereIntroTwoPlayerGameBox(SwccgCardBlueprintLibrary library) {
        super(library);

        // Dark side
        _fixed.add("1_170");   // Death Star Trooper
        _fixed.add("1_170");   // Death Star Trooper
        _fixed.add("1_181");   // Imperial Trooper Guard
        _fixed.add("1_181");   // Imperial Trooper Guard
        _fixed.add("1_182");   // Jawa
        _fixed.add("1_182");   // Jawa
        _fixed.add("1_186");   // LIN-V8M (Elleyein-Veeateemm)
        _fixed.add("1_186");   // LIN-V8M (Elleyein-Veeateemm)
        _fixed.add("1_194");   // Stormtrooper
        _fixed.add("1_194");   // Stormtrooper
        _fixed.add("1_194");   // Stormtrooper
        _fixed.add("1_194");   // Stormtrooper
        _fixed.add("1_194");   // Stormtrooper
        _fixed.add("1_194");   // Stormtrooper
        _fixed.add("1_194");   // Stormtrooper
        _fixed.add("1_196");   // Tusken Raider
        _fixed.add("1_196");   // Tusken Raider
        _fixed.add("1_196");   // Tusken Raider
        _fixed.add("1_196");   // Tusken Raider
        _fixed.add("1_196");   // Tusken Raider
        _fixed.add("101_5");   // Vader
        _fixed.add("101_5");   // Vader
        _fixed.add("101_5");   // Vader
        _fixed.add("1_201");   // Comlink
        _fixed.add("1_207");   // Stormtrooper Utility Belt
        _fixed.add("1_207");   // Stormtrooper Utility Belt
        _fixed.add("1_221");   // Ket Maliss
        _fixed.add("1_224");   // Macroscan
        _fixed.add("1_237");   // Collateral Damage
        _fixed.add("1_238");   // Counter Assault
        _fixed.add("1_238");   // Counter Assault
        _fixed.add("1_249");   // Imperial Barrier
        _fixed.add("1_251");   // Imperial Reinforcements
        _fixed.add("1_252");   // It's Worse
        _fixed.add("1_254");   // Kintan Strider
        _fixed.add("1_262");   // Ommni Box
        _fixed.add("1_266");   // Scanning Crew
        _fixed.add("1_268");   // Set For Stun
        _fixed.add("1_269");   // Takeel
        _fixed.add("1_275");   // Tusken Scavengers
        _fixed.add("101_6");   // Vader's Obsession
        _fixed.add("101_6");   // Vader's Obsession
        _fixed.add("101_6");   // Vader's Obsession
        _fixed.add("1_279");   // You Overestimate Their Chances
        _fixed.add("1_284");   // Death Star: Detention Block Corridor
        _fixed.add("1_285");   // Death Star: Docking Bay 327
        _fixed.add("1_285");   // Death Star: Docking Bay 327
        _fixed.add("101_4");   // Death Star: Docking Control Room 327
        _fixed.add("101_4");   // Death Star: Docking Control Room 327
        _fixed.add("1_291");   // Tatooine: Docking Bay 94
        _fixed.add("1_292");   // Tatooine: Jawa Camp
        _fixed.add("1_293");   // Tatooine: Jundland Wastes
        _fixed.add("1_295");   // Tatooine: Mos Eisley
        _fixed.add("1_312");   // Blaster Rifle
        _fixed.add("1_317");   // Imperial Blaster
        _fixed.add("1_317");   // Imperial Blaster
        _fixed.add("1_315");   // Gaderffii Stick
        _fixed.add("1_315");   // Timer Mine
        _fixed.add("1_315");   // Timer Mine
        _fixed.add("1_322");   // Timer Mine

        // Light side
        _fixed.add("1_6");     // CZ-3 (Seezee-Three)
        _fixed.add("1_12");    // Jawa
        _fixed.add("1_12");    // Jawa
        _fixed.add("1_18");    // LIN-V8K (Elleyein-Veeatekay)
        _fixed.add("1_18");    // LIN-V8K (Elleyein-Veeatekay)
        _fixed.add("101_2");   // Luke
        _fixed.add("101_2");   // Luke
        _fixed.add("101_2");   // Luke
        _fixed.add("1_26");    // Rebel Guard
        _fixed.add("1_26");    // Rebel Guard
        _fixed.add("1_28");    // Rebel Trooper
        _fixed.add("1_28");    // Rebel Trooper
        _fixed.add("1_28");    // Rebel Trooper
        _fixed.add("1_28");    // Rebel Trooper
        _fixed.add("1_28");    // Rebel Trooper
        _fixed.add("1_28");    // Rebel Trooper
        _fixed.add("1_28");    // Rebel Trooper
        _fixed.add("1_30");    // Shistavanen Wolfman
        _fixed.add("1_30");    // Shistavanen Wolfman
        _fixed.add("1_30");    // Shistavanen Wolfman
        _fixed.add("1_31");    // Talz
        _fixed.add("1_31");    // Talz
        _fixed.add("1_31");    // Talz
        _fixed.add("1_35");    // Electrobinoculars
        _fixed.add("1_40");    // Tatooine Utility Belt
        _fixed.add("1_40");    // Tatooine Utility Belt
        _fixed.add("1_64");    // Sai'torr Kal Fas
        _fixed.add("1_77");    // Don't Underestimate Our Chances
        _fixed.add("1_80");    // Friendly Fire
        _fixed.add("1_80");    // Friendly Fire
        _fixed.add("1_84");    // Han's Dice
        _fixed.add("1_91");    // I've Got A Bad Feeling About This
        _fixed.add("1_90");    // It Could Be Worse
        _fixed.add("1_90");    // It Could Be Worse
        _fixed.add("1_98");    // Narrow Escape
        _fixed.add("1_100");   // Old Ben
        _fixed.add("1_105");   // Rebel Barrier
        _fixed.add("1_106");   // Rebel Reinforcements
        _fixed.add("101_3");   // Run Luke, Run!
        _fixed.add("101_3");   // Run Luke, Run!
        _fixed.add("101_3");   // Run Luke, Run!
        _fixed.add("1_113");   // Surprise Assault
        _fixed.add("1_113");   // Surprise Assault
        _fixed.add("1_115");   // The Bith Shuffle
        _fixed.add("1_120");   // We're Doomed
        _fixed.add("1_124");   // Death Star: Docking Bay 327
        _fixed.add("101_1");   // Death Star: Level 6 Core Shaft Corridor
        _fixed.add("101_1");   // Death Star: Level 6 Core Shaft Corridor
        _fixed.add("1_291");   // Tatooine: Docking Bay 94
        _fixed.add("1_291");   // Tatooine: Docking Bay 94
        _fixed.add("1_130");   // Tatooine: Dune Sea
        _fixed.add("1_131");   // Tatooine: Jawa Camp
        _fixed.add("1_131");   // Tatooine: Jawa Camp
        _fixed.add("1_132");   // Tatooine: Lars' Moisture Farm
        _fixed.add("1_152");   // Blaster
        _fixed.add("1_152");   // Blaster
        _fixed.add("1_153");   // Blaster Rifle
        _fixed.add("1_162");   // Timer Mine
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
        return ProductName.PREMIERE_INTRODUCTORY_TWO_PLAYER_GAME;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.PREMIERE_INTRODUCTORY_TWO_PLAYER_GAME;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addFixedCard(result, _fixed, 120);
        addProducts(result, ProductName.PREMIERE_BOOSTER_PACK, 1);
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
