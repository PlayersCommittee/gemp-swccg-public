package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Watto's Cube booster pack.
 */
public class WattosCubeDraftPack extends BasePackagedCardProduct {
    private final int DRAFT_PACK_CARD_COUNT = 9;
    private Random _random = new Random();
    private String _side;

    /**
     * Creates a Watto's Cube booster pack.
     * @param library the blueprint library
     * @param side Dark or Light
     */
    public WattosCubeDraftPack(SwccgCardBlueprintLibrary library, String side) {
        super(library);
        _side = side;
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return _side.equals("Dark") ? ProductName.CUBE_DRAFT_PACK_DARK : ProductName.CUBE_DRAFT_PACK_LIGHT;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return 0.0f;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        return openPackageWithExclusions(Collections.<String>emptyList());
    }

    public List<CardCollection.Item> openPackageWithExclusions(List<String> exclusions) {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomCardWithExclusions(result, DRAFT_PACK_CARD_COUNT, exclusions);
        return result;
    }

    private void addRandomCardWithExclusions(List<CardCollection.Item> result, int count,List<String> exclusions) {
        List<String> possibleCards = new ArrayList<String>();
        switch(_side) {
            case "Dark":
                possibleCards.addAll(darkDraftCards());
                break;
            case "Light":
                possibleCards.addAll(lightDraftCards());
                break;
        }


        //the exclusions should not include the cards that were received in the objective packs
        List<String> removeFromExclusionsForObjectivePacks = new LinkedList<String>();
        WattosCubeObjectivePack objectivePack = new WattosCubeObjectivePack(_library, _side);

        List<String> archetypeDefiningCards = objectivePack.getArchetypeDefiningCards(_side, Collections.<String>emptyList());

        for(String archetype: archetypeDefiningCards) {
            if(exclusions.contains(archetype)&&exclusions.containsAll(objectivePack.getRelatedCards(Collections.singletonList(archetype)))) {
                removeFromExclusionsForObjectivePacks.add(archetype);
                removeFromExclusionsForObjectivePacks.addAll(objectivePack.getRelatedCards(Collections.singletonList(archetype)));
            }
        }

        for(String s:removeFromExclusionsForObjectivePacks) {
            exclusions.remove(s);
        }

        for(String s:exclusions) {
            possibleCards.remove(s);
        }


        filterNonExistingCards(possibleCards);

        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }


    public List<String> darkDraftCards() {
        List<String> cards = new ArrayList<String>();
        cards.add("9_92");
        cards.add("10_32");
        cards.add("213_2");
        cards.add("11_52");
        cards.add("209_33");
        cards.add("6_95");
        cards.add("13_55");
        cards.add("206_9");
        cards.add("200_75");
        cards.add("7_169");
        cards.add("203_24");
        cards.add("12_99");
        cards.add("200_79");
        cards.add("201_24");
        cards.add("209_35");
        cards.add("10_37");
        cards.add("6_101");
        cards.add("204_42");
        cards.add("12_107");
        cards.add("205_13");
        cards.add("200_84");
        cards.add("201_25");
        cards.add("110_9");
        cards.add("211_3");
        cards.add("200_86");
        cards.add("200_86");
        cards.add("211_1");
        cards.add("203_28");
        cards.add("7_195");
        cards.add("10_45");
        cards.add("207_22");
        cards.add("211_5");
        cards.add("211_61");
        cards.add("207_24");
        cards.add("10_48");
        cards.add("205_16");
        cards.add("200_91");
        cards.add("201_27");
        cards.add("204_46");
        cards.add("200_76");
        cards.add("9_109");
        cards.add("205_12");
        cards.add("208_35");
        cards.add("209_39");
        cards.add("109_6");
        cards.add("200_71");
        cards.add("204_36");
        cards.add("10_41");
        cards.add("109_11");
        cards.add("14_83");
        cards.add("14_84");
        cards.add("12_114");
        cards.add("12_115");
        cards.add("200_88");
        cards.add("208_37");
        cards.add("204_38");
        cards.add("208_31");
        cards.add("204_41");
        cards.add("204_43");
        cards.add("209_37");
        cards.add("208_33");
        cards.add("211_4");
        cards.add("9_97");
        cards.add("200_73");
        cards.add("3_82");
        cards.add("9_98");
        cards.add("203_22");
        cards.add("9_99");
        cards.add("9_99");
        cards.add("9_103");
        cards.add("9_104");
        cards.add("9_105");
        cards.add("210_30");
        cards.add("5_96");
        cards.add("9_106");
        cards.add("203_25");
        cards.add("8_97");
        cards.add("9_107");
        cards.add("9_108");
        cards.add("200_77");
        cards.add("200_78");
        cards.add("108_6");
        cards.add("108_6");
        cards.add("208_30");
        cards.add("206_10");
        cards.add("207_20");
        cards.add("213_5");
        cards.add("213_6");
        cards.add("200_81");
        cards.add("10_40");
        cards.add("207_21");
        cards.add("200_82");
        cards.add("200_82");
        cards.add("9_110");
        cards.add("208_32");
        cards.add("8_101");
        cards.add("8_102");
        cards.add("8_104");
        cards.add("200_85");
        cards.add("8_106");
        cards.add("9_116");
        cards.add("212_2");
        cards.add("208_36");
        cards.add("2_98");
        cards.add("8_111");
        cards.add("211_7");
        cards.add("213_12");
        cards.add("204_44");
        cards.add("204_44");
        cards.add("210_46");
        cards.add("209_40");
        cards.add("200_92");
        cards.add("203_27");
        cards.add("210_40");
        cards.add("301_3");
        cards.add("301_3");
        cards.add("203_26");
        cards.add("208_34");
        cards.add("213_10");
        cards.add("207_23");
        cards.add("1_201");
        cards.add("203_29");
        cards.add("200_101");
        cards.add("14_94");
        cards.add("208_38");
        cards.add("200_103");
        cards.add("8_118");
        cards.add("1_211");
        cards.add("13_60");
        cards.add("200_105");
        cards.add("200_105");
        cards.add("1_214");
        cards.add("203_30");
        cards.add("201_29");
        cards.add("200_106");
        cards.add("212_1");
        cards.add("1_215");
        cards.add("7_229");
        cards.add("8_126");
        cards.add("8_126");
        cards.add("5_120");
        cards.add("200_108");
        cards.add("200_109");
        cards.add("201_31");
        cards.add("200_111");
        cards.add("9_129");
        cards.add("8_131");
        cards.add("7_240");
        cards.add("207_26");
        cards.add("200_112");
        cards.add("208_41");
        cards.add("7_244");
        cards.add("7_244");
        cards.add("12_143");
        cards.add("200_114");
        cards.add("201_33");
        cards.add("1_234");
        cards.add("200_144");
        cards.add("109_7");
        cards.add("200_116");
        cards.add("200_117");
        cards.add("4_139");
        cards.add("1_240");
        cards.add("1_241");
        cards.add("5_135");
        cards.add("1_243");
        cards.add("200_119");
        cards.add("200_119");
        cards.add("200_120");
        cards.add("2_132");
        cards.add("10_39");
        cards.add("200_121");
        cards.add("200_122");
        cards.add("12_148");
        cards.add("12_148");
        cards.add("1_249");
        cards.add("1_249");
        cards.add("9_137");
        cards.add("9_137");
        cards.add("209_48");
        cards.add("7_257");
        cards.add("7_257");
        cards.add("2_135");
        cards.add("205_20");
        cards.add("205_20");
        cards.add("5_149");
        cards.add("1_267");
        cards.add("1_267");
        cards.add("10_46");
        cards.add("12_158");
        cards.add("12_158");
        cards.add("200_123");
        cards.add("212_4");
        cards.add("200_124");
        cards.add("2_139");
        cards.add("208_45");
        cards.add("208_45");
        cards.add("2_140");
        cards.add("200_125");
        cards.add("201_36");
        cards.add("106_17");
        cards.add("3_138");
        cards.add("6_160");
        cards.add("12_163");
        cards.add("12_163");
        cards.add("12_163");
        cards.add("5_160");
        cards.add("5_163");
        cards.add("12_164");
        cards.add("13_57");
        cards.add("200_126");
        cards.add("213_23");
        cards.add("9_148");
        cards.add("204_51");
        cards.add("2_146");
        cards.add("210_39");
        cards.add("209_50");
        cards.add("6_168");
        cards.add("208_51");
        cards.add("208_52");
        cards.add("208_55");
        cards.add("6_171");
        cards.add("6_171");
        cards.add("9_152");
        cards.add("4_166");
        cards.add("202_13");
        cards.add("202_14");
        cards.add("200_129");
        cards.add("200_130");
        cards.add("200_131");
        cards.add("9_154");
        cards.add("200_132");
        cards.add("200_133");
        cards.add("109_10");
        cards.add("200_134");
        cards.add("9_156");
        cards.add("203_35");
        cards.add("204_54");
        cards.add("209_52");
        cards.add("209_52");
        cards.add("9_157");
        cards.add("1_302");
        cards.add("211_23");
        cards.add("210_34");
        cards.add("9_158");
        cards.add("204_55");
        cards.add("208_58");
        cards.add("12_182");
        cards.add("207_28");
        cards.add("9_160");
        cards.add("9_161");
        cards.add("200_136");
        cards.add("9_162");
        cards.add("9_162");
        cards.add("9_163");
        cards.add("9_164");
        cards.add("9_165");
        cards.add("201_40");
        cards.add("200_137");
        cards.add("3_153");
        cards.add("206_14");
        cards.add("200_138");
        cards.add("110_12");
        cards.add("14_120");
        cards.add("3_154");
        cards.add("200_139");
        cards.add("200_140");
        cards.add("210_38");
        cards.add("8_170");
        cards.add("8_172");
        cards.add("8_173");
        cards.add("202_15");
        cards.add("8_176");
        cards.add("8_177");
        cards.add("213_34");
        cards.add("213_34");
        cards.add("213_34");
        cards.add("211_25");
        cards.add("211_25");
        cards.add("211_25");
        cards.add("12_187");
        cards.add("12_187");
        cards.add("12_188");
        cards.add("12_188");
        cards.add("12_188");
        cards.add("7_323");
        cards.add("7_324");
        cards.add("7_324");
        cards.add("112_19");
        cards.add("6_179");

        return cards;
    }

    public List<String> lightDraftCards() {
        List<String> cards = new ArrayList<String>();
        cards.add("9_2");
        cards.add("9_4");
        cards.add("200_1");
        cards.add("106_1");
        cards.add("210_6");
        cards.add("7_8");
        cards.add("11_1");
        cards.add("204_4");
        cards.add("10_6");
        cards.add("7_14");
        cards.add("203_4");
        cards.add("14_10");
        cards.add("1_15");
        cards.add("7_25");
        cards.add("209_8");
        cards.add("201_1");
        cards.add("112_3");
        cards.add("200_17");
        cards.add("203_7");
        cards.add("200_19");
        cards.add("211_57");
        cards.add("201_3");
        cards.add("10_12");
        cards.add("12_20");
        cards.add("6_33");
        cards.add("213_39");
        cards.add("213_41");
        cards.add("204_11");
        cards.add("10_24");
        cards.add("7_44");
        cards.add("213_42");
        cards.add("213_43");
        cards.add("6_47");
        cards.add("204_1");
        cards.add("208_2");
        cards.add("206_5");
        cards.add("213_38");
        cards.add("201_6");
        cards.add("11_13");
        cards.add("207_3");
        cards.add("210_19");
        cards.add("201_2");
        cards.add("210_23");
        cards.add("14_27");
        cards.add("202_4");
        cards.add("203_1");
        cards.add("209_1");
        cards.add("211_59");
        cards.add("204_2");
        cards.add("207_1");
        cards.add("206_1");
        cards.add("206_3");
        cards.add("204_3");
        cards.add("200_4");
        cards.add("10_3");
        cards.add("207_2");
        cards.add("200_7");
        cards.add("200_8");
        cards.add("209_2");
        cards.add("205_1");
        cards.add("208_4");
        cards.add("10_5");
        cards.add("200_10");
        cards.add("203_5");
        cards.add("210_14");
        cards.add("209_4");
        cards.add("200_12");
        cards.add("9_13");
        cards.add("209_5");
        cards.add("108_1");
        cards.add("108_1");
        cards.add("1_13");
        cards.add("203_6");
        cards.add("207_6");
        cards.add("202_1");
        cards.add("108_2");
        cards.add("9_21");
        cards.add("200_20");
        cards.add("9_24");
        cards.add("200_21");
        cards.add("108_3");
        cards.add("203_9");
        cards.add("201_4");
        cards.add("108_4");
        cards.add("108_4");
        cards.add("10_18");
        cards.add("301_7");
        cards.add("211_56");
        cards.add("211_56");
        cards.add("207_9");
        cards.add("209_12");
        cards.add("209_13");
        cards.add("204_12");
        cards.add("203_12");
        cards.add("205_3");
        cards.add("202_3");
        cards.add("9_31");
        cards.add("208_13");
        cards.add("3_27");
        cards.add("301_1");
        cards.add("301_1");
        cards.add("200_2");
        cards.add("200_2");
        cards.add("208_3");
        cards.add("209_3");
        cards.add("203_2");
        cards.add("200_3");
        cards.add("200_6");
        cards.add("209_6");
        cards.add("203_10");
        cards.add("208_5");
        cards.add("204_6");
        cards.add("207_5");
        cards.add("204_7");
        cards.add("204_8");
        cards.add("204_9^");
        cards.add("209_10");
        cards.add("209_11");
        cards.add("208_11");
        cards.add("211_55");
        cards.add("1_35");
        cards.add("205_4");
        cards.add("200_34");
        cards.add("201_8");
        cards.add("8_34");
        cards.add("210_7");
        cards.add("3_32");
        cards.add("8_35");
        cards.add("7_55");
        cards.add("7_55");
        cards.add("13_11");
        cards.add("1_45");
        cards.add("1_48");
        cards.add("200_38");
        cards.add("7_60");
        cards.add("111_3");
        cards.add("200_39");
        cards.add("8_38");
        cards.add("200_41");
        cards.add("8_40");
        cards.add("8_40");
        cards.add("210_17");
        cards.add("1_55");
        cards.add("9_38");
        cards.add("207_11");
        cards.add("9_39");
        cards.add("211_52");
        cards.add("9_41");
        cards.add("200_46");
        cards.add("7_77");
        cards.add("14_37");
        cards.add("203_16");
        cards.add("200_47");
        cards.add("208_16");
        cards.add("12_52");
        cards.add("1_70");
        cards.add("12_53");
        cards.add("12_53");
        cards.add("1_71");
        cards.add("200_143");
        cards.add("200_49");
        cards.add("201_11");
        cards.add("6_61");
        cards.add("6_61");
        cards.add("200_50");
        cards.add("5_38");
        cards.add("4_48");
        cards.add("10_4");
        cards.add("5_45");
        cards.add("200_51");
        cards.add("209_19");
        cards.add("201_12");
        cards.add("201_12");
        cards.add("2_49");
        cards.add("2_50");
        cards.add("10_8");
        cards.add("200_53");
        cards.add("1_90");
        cards.add("106_5");
        cards.add("5_55");
        cards.add("200_54");
        cards.add("204_20");
        cards.add("1_97");
        cards.add("209_21");
        cards.add("210_24");
        cards.add("12_66");
        cards.add("12_66");
        cards.add("1_105");
        cards.add("1_105");
        cards.add("203_17");
        cards.add("203_17");
        cards.add("209_22");
        cards.add("211_2");
        cards.add("1_109");
        cards.add("1_109");
        cards.add("10_21");
        cards.add("7_102");
        cards.add("2_57");
        cards.add("10_23");
        cards.add("204_22");
        cards.add("6_77");
        cards.add("207_14");
        cards.add("1_119");
        cards.add("12_72");
        cards.add("6_79");
        cards.add("14_46");
        cards.add("14_46");
        cards.add("14_46");
        cards.add("12_76");
        cards.add("213_56");
        cards.add("8_68");
        cards.add("9_58");
        cards.add("204_26");
        cards.add("211_43");
        cards.add("211_42");
        cards.add("12_79");
        cards.add("14_49");
        cards.add("200_58");
        cards.add("7_123");
        cards.add("12_84");
        cards.add("11_42");
        cards.add("1_133");
        cards.add("1_138");
        cards.add("200_59");
        cards.add("200_59");
        cards.add("111_2");
        cards.add("202_7");
        cards.add("211_28");
        cards.add("210_5");
        cards.add("200_60");
        cards.add("200_61");
        cards.add("203_20");
        cards.add("1_140");
        cards.add("202_8");
        cards.add("207_17");
        cards.add("200_62");
        cards.add("201_18");
        cards.add("205_7");
        cards.add("9_74");
        cards.add("209_30");
        cards.add("204_33");
        cards.add("7_144");
        cards.add("7_144");
        cards.add("1_143");
        cards.add("200_63");
        cards.add("10_17");
        cards.add("208_27");
        cards.add("207_18");
        cards.add("209_31");
        cards.add("2_72");
        cards.add("200_66");
        cards.add("9_81");
        cards.add("9_82");
        cards.add("208_28");
        cards.add("205_8");
        cards.add("205_9");
        cards.add("206_7");
        cards.add("7_149");
        cards.add("201_19");
        cards.add("206_8");
        cards.add("205_10");
        cards.add("200_67");
        cards.add("203_21");
        cards.add("209_32");
        cards.add("209_32");
        cards.add("201_20");
        cards.add("102_4");
        cards.add("3_66");
        cards.add("3_66");
        cards.add("3_67");
        cards.add("3_68");
        cards.add("7_154");
        cards.add("210_26");
        cards.add("210_26");
        cards.add("9_87");
        cards.add("211_33");
        cards.add("211_33");
        cards.add("211_33");
        cards.add("7_161");
        cards.add("207_19");
        cards.add("12_94");
        cards.add("12_94");
        cards.add("12_94");
        cards.add("12_95");
        cards.add("12_95");
        cards.add("12_95");
        cards.add("1_159");
        cards.add("112_8");
        cards.add("7_162");
        cards.add("7_162");

        return cards;
    }
}
