package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Virtual Alternate Image booster pack.
 */
public class VirtualAlternateImageBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private int _series;
    /**
     * Creates a Virtual Alternate Image booster pack.
     * @param library the blueprint library
     */
    public VirtualAlternateImageBoosterPack(SwccgCardBlueprintLibrary library, int series) {
        super(library);
        _series = series;
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        switch(_series) {
            case 1:
                return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_1;
            case 2:
                return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_2;
            case 3:
                return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_3;
            case 4:
                return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_4;
            case 5:
                return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_5;
            case 6:
                return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_6;
            case 7:
                return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_7;
        }

        return ProductName.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK_SERIES_1;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.VIRTUAL_ALTERNATE_IMAGE_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCard(result, 4);
        return result;
    }

    /**
     * Adds random cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        switch(_series) {
            case 1:
                possibleCards.add("200_1^"); //Aayla Secura
                possibleCards.add("203_22^"); //Agent Kallus
                possibleCards.add("200_2^"); //Anakin Skywalker, Padawan Learner
                possibleCards.add("202_7^"); //Azure Angel
                possibleCards.add("204_3^"); //Captain Hera Syndulla
                possibleCards.add("200_3^"); //Captain Rex, 501st Legion
                possibleCards.add("200_57^"); //Coruscant: Night Club
                possibleCards.add("200_80^"); //Droideka
                possibleCards.add("200_77^"); //DS-61-5
                possibleCards.add("203_27^"); //General Grievous
                possibleCards.add("201_18^"); //Green Leader In Green Squadron 1
                possibleCards.add("204_51^"); //Jakku (DS)
                possibleCards.add("201_25^"); //Jango Fett
                possibleCards.add("209_49^"); //Jedha: Jedha City
                possibleCards.add("203_6^"); //Kanan Jarrus
                possibleCards.add("200_58^"); //Nar Shaddaa
                possibleCards.add("204_9^"); //Rey
                possibleCards.add("209_26^"); //Scarif: Landing Pad Nine (Docking Bay)
                possibleCards.add("201_40^"); //Slave I, Symbol Of Fear
                possibleCards.add("203_14^"); //Stolen Data Tapes
                possibleCards.add("203_21^"); //Wild Karrde
                possibleCards.add("204_26^"); //Jakku (LS)
                break;
            case 2:
                possibleCards.add("301_1^"); //Ahsoka Tano With Lightsabers
                possibleCards.add("301_3^"); //Asajj Ventress With Lightsabers
                possibleCards.add("204_38^"); //Captain Phasma
                possibleCards.add("207_2^"); //Chirrut Imwe
                possibleCards.add("200_6^"); //Commander Cody
                possibleCards.add("204_54^"); //Finalizer
                possibleCards.add("207_18^"); //Profundity
                possibleCards.add("207_23^"); //Savage Opress
                possibleCards.add("208_51^"); //Starkiller Base
                possibleCards.add("209_6^"); //General Kenobi
                possibleCards.add("210_1^"); //Ahch-To: Saddle
                possibleCards.add("209_50^"); //Mustafar: Vader's Castle
                possibleCards.add("203_2^"); //CT-5555 (Fives)
                possibleCards.add("209_35^"); //Dr. Chelli Lona Aphra
                possibleCards.add("210_19^"); //Kit Fisto
                possibleCards.add("209_37^"); //Kylo Ren With Lightsaber
                possibleCards.add("209_10^"); //Rey With Lightsaber
                possibleCards.add("206_7^"); //Rogue One
                possibleCards.add("209_39^"); //Supreme Leader Snoke
                break;
            case 3:
                possibleCards.add("200_71^"); //4-LOM With Concussion Rifle (V)
                possibleCards.add("212_5^"); //Admiral Trench
                possibleCards.add("204_36^"); //B2 Battle Droid
                possibleCards.add("200_133^"); //Conquest (V)
                possibleCards.add("200_76^"); //Count Dooku
                possibleCards.add("203_26^"); //Darth Maul, Lone Hunter
                possibleCards.add("200_142^"); //Dooku's Lightsaber
                possibleCards.add("211_28^"); //BB-8 In Black Squadron 1
                possibleCards.add("204_4^"); //Chewie With Bowcaster
                possibleCards.add("204_27^"); //Jakku: Niima Outpost Shipyard
                possibleCards.add("204_8^"); //Poe Dameron
                possibleCards.add("209_25^"); //Scarif: Data Vault
                possibleCards.add("204_11^"); //Solo
                possibleCards.add("201_19^"); //Tantive IV (V)
                possibleCards.add("204_35^"); //The Falcon, Junkyard Garbage
                break;
            case 4:
                possibleCards.add("213_17^"); //A Lawless Time
                possibleCards.add("215_2^"); //A Power Loss
                possibleCards.add("208_30^"); //Darth Vader, Emperor's Enforcer
                possibleCards.add("207_20^"); //Director Orson Krennic
                possibleCards.add("204_6^"); //Finn
                possibleCards.add("204_41^"); //General Hux
                possibleCards.add("207_5^"); //General Leia Organa
                possibleCards.add("208_49^"); //Hoth: Ice Plains (V)
                possibleCards.add("213_20^"); //I've Been Searching For You For Some Time
                possibleCards.add("204_29^"); //Jakku: Rey's Encampment
                possibleCards.add("204_31^"); //Jakku: Tuanul Village (LS)
                possibleCards.add("206_4^"); //Jyn Erso
                possibleCards.add("213_58^"); //Leia's Resistance Transport
                possibleCards.add("213_10^"); //Maul (Hologram)
                possibleCards.add("213_39^"); //Qi'ra (Hologram)
                possibleCards.add("201_6^"); //R2-D2 (V)
                possibleCards.add("210_46^"); //The Grand Inquisitor
                possibleCards.add("209_40^"); //Vanee
                break;
            case 5:
                possibleCards.add("217_27^"); //Ajan Kloss: Training Course (Borderless)
                possibleCards.add("216_21^"); //Anakin Skywalker, Jedi Knight (Hologram AI)
                possibleCards.add("210_3^"); //Anakin's Lightsaber (V)
                possibleCards.add("203_31^"); //Coruscant (V) (DS)
                possibleCards.add("216_25^"); //Coruscant: Jedi Temple Meditation Room
                possibleCards.add("216_4^"); //Coruscant: The Works (Borderless)
                possibleCards.add("216_26^"); //Dagobah: Yoda’s Hut (V)
                possibleCards.add("202_9^"); //Daroe (V)
                possibleCards.add("213_3^"); //Darth Tyranus (Hologram AI)
                possibleCards.add("216_6^"); //Darth Vader, Betrayer Of The Jedi
                possibleCards.add("216_7^"); //Death Star (V)
                possibleCards.add("205_13^"); //Hondo Ohnaka
                possibleCards.add("200_108^"); //Imperial Decree (V)
                possibleCards.add("204_7^"); //Lor San Tekka
                possibleCards.add("201_21^"); //Mace Windu's Lightsaber
                possibleCards.add("210_23^"); //Plo Koon (V)
                possibleCards.add("208_17^"); //Restore Freedom To The Galaxy
                possibleCards.add("216_43^"); //Tatooine: Obi-Wan’s Hut (V) (Borderless)
                break;
            case 6:
                possibleCards.add("204_1^"); //BB-8 (Border Breaker)
                possibleCards.add("204_47^"); //Bow To The First Order
                possibleCards.add("219_2^"); //Chimaera (V)
                possibleCards.add("219_31^"); //Coruscant: Jedi Temple (Borderless)
                possibleCards.add("213_23^"); //Dathmoir: Maul's Chambers
                possibleCards.add("217_34^"); //Endor: Anakin's Funeral Pyre (Borderless)
                possibleCards.add("209_4^"); //Galen Erso
                possibleCards.add("205_17^"); //I Am Your Father (V)
                possibleCards.add("200_41^"); //I Must Be Allowed To Speak (V)
                possibleCards.add("211_23^"); //Invisible Hand (Border Breaker)
                possibleCards.add("202_5^"); //Like My Father Before Me
                possibleCards.add("200_20^"); //Luke Skywalker (V)
                possibleCards.add("210_20^"); //Luke Skywalker, The Last Jedi
                possibleCards.add("208_8^"); //Luke Skywalker, The Rebellion's Hope
                possibleCards.add("205_6^"); //Tatooine: Lars' Moisture Farm (V)
                possibleCards.add("218_31^"); //Tydirum (V) (Border Breaker)
                possibleCards.add("217_52^"); //Your Thoughts Dwell On Your Mother
                possibleCards.add("204_46^"); //Zam Wesell (Semi Border Break)
                break;
            case 7:
                possibleCards.add("224_9^"); //Balanced Attack & Darklighter Spin (Animated AI)    
                possibleCards.add("200_109^"); //Coarse And Rough And Irritating (AI)
                possibleCards.add("214_3^"); //Darksaber (AI)
                possibleCards.add("214_18^"); //Din Djarin (AI)
                possibleCards.add("200_39^"); //I Don't Like Sand (AI)
                possibleCards.add("210_17^"); //Jedi Business (AI)
                possibleCards.add("211_33^"); //Jedi Lightsaber (V) (AI)
                possibleCards.add("208_35^"); //Lord Sidious (Hologram AI)
                possibleCards.add("200_85^"); //Maarek Stele, The Emperor's Reach (AI)
                possibleCards.add("218_4^"); //Master Windu (Hologram AI)
                possibleCards.add("214_8^"); //Palpatine, Emperor Returned (AI)
                possibleCards.add("214_21^"); //Plo Koon's Jedi Starfighter (AI)
                possibleCards.add("200_88^"); //Probot (AI)
                possibleCards.add("214_22^"); //Rey, All Of The Jedi (AI)
                possibleCards.add("214_9^"); //Steadfast (AI)
                possibleCards.add("219_27^"); //Vader's Lightsaber (V) (AI)
                break;
        }

        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }
}
