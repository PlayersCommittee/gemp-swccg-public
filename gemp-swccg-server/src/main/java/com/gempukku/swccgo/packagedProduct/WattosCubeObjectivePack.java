package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Watto's Cube objective pack.
 */
public class WattosCubeObjectivePack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private String _side;

    /**
     * Creates a Watto's Cube booster pack.
     * @param library the blueprint library
     * @param side Dark or Light
     */
    public WattosCubeObjectivePack(SwccgCardBlueprintLibrary library, String side) {
        super(library);
        _side = side;
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return _side.equals("Dark") ? ProductName.CUBE_OBJECTIVE_PACK_DARK : ProductName.CUBE_OBJECTIVE_PACK_LIGHT;
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
        System.out.println("debug WattosCubeObjectivePack: not using exclusions");
        return openPackageWithExclusions(Collections.<String>emptyList());
    }

    public List<CardCollection.Item> openPackageWithExclusions(List<String> exclusions) {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomObjectiveAndRelatedCardsWithExclusions(result, 1, exclusions);
        return result;
    }

    private void addRandomObjectiveAndRelatedCardsWithExclusions(List<CardCollection.Item> result, int count,List<String> exclusions) {
        List<String> possibleCards = new ArrayList<String>();
        switch(_side) {
            case "Dark":
                possibleCards.addAll(addObjectives("Dark", exclusions));
                break;
            case "Light":
                possibleCards.addAll(addObjectives("Light", exclusions));
                break;
        }
        filterNonExistingCards(possibleCards);
        if(exclusions.size()>0) {
            System.out.println("debug: WattosCubeObjectivePack trying to exclude things");
        } else {
            System.out.println("debug: WattosCubeObjectivePack no exclusions");
        }
        for(String s:exclusions) {
            possibleCards.remove(s);
        }

        Collections.shuffle(possibleCards, _random);
        List<String> objectives = possibleCards.subList(0, Math.min(possibleCards.size(), count));
        addCards(result, objectives, false);

        List<String> relatedCards =  getRelatedCards(objectives);
        filterNonExistingCards(relatedCards);
        addCards(result, relatedCards, false);
    }

    public List<String> addObjectives(String side, List<String> exclusions) {
        List<String> possibleCards = new ArrayList<String>();
        switch(side) {
            case "Dark":
                possibleCards.add("213_32"); //Shadow Collective
                possibleCards.add("3_143"); //Hoth CR (V)
                possibleCards.add("111_6"); //SYCFA
                possibleCards.add("7_296"); //CCT
                possibleCards.add("110_6"); //Court
                possibleCards.add("8_167"); //EOps
                possibleCards.add("210_42"); //ROps (V)
                possibleCards.add("301_4"); //Twin Suns
                possibleCards.add("7_297"); //Hunt Down
                possibleCards.add("12_180"); //Watto
                possibleCards.add("201_39"); //Entanglements
                possibleCards.add("7_275"); //Coruscant CR (V)
                possibleCards.add("9_151"); //BHBM
                possibleCards.add("208_57"); //Map
                possibleCards.add("213_31"); //Hunt Down (V)
                break;
            case "Light":
                possibleCards.add("9_61"); //TIGIH
                possibleCards.add("3_55"); //EBO (Hoth CP (V))
                possibleCards.add("208_26"); //YOps
                possibleCards.add("109_4"); //QMC
                possibleCards.add("110_4"); //Profit
                possibleCards.add("208_25"); //HITCO
                possibleCards.add("209_29"); //No Idea
                possibleCards.add("301_2"); //CITC
                possibleCards.add("204_32"); //OA
                possibleCards.add("10_26"); //WYS
                possibleCards.add("203_19"); //Diplo
                possibleCards.add("1_138"); //TRM
                possibleCards.add("112_1"); //AITC
                possibleCards.add("7_136"); //HB
                possibleCards.add("8_78"); //RST
                break;
            default:
                break;
        }


        return possibleCards;
    }

    public List<String> getRelatedCards(List<String> objectiveBlueprintIds) {
        List<String> cards = new ArrayList<String>();
        for(String id: objectiveBlueprintIds) {
            switch(id) {
                //Dark
                case "213_32": //Shadow Collective
                    cards.add("213_4");
                    cards.add("213_10");
                    cards.add("211_10");
                    cards.add("213_17");
                    cards.add("212_4");
                    cards.add("213_23");
                    cards.add("213_25");
                    cards.add("213_26");
                    cards.add("213_27");
                    break;
                case "3_143": //Hoth CR (V)
                    cards.add("200_81");
                    cards.add("13_82");
                    cards.add("11_77");
                    cards.add("200_117");
                    cards.add("104_7");
                    cards.add("3_144");
                    cards.add("3_147");
                    cards.add("208_49");
                    cards.add("210_38");
                    break;
                case "111_6": //SYCFA
                    cards.add("209_36");
                    cards.add("209_41");
                    cards.add("208_44");
                    cards.add("2_143");
                    cards.add("1_285");
                    cards.add("208_48");
                    cards.add("208_50");
                    cards.add("209_49");
                    cards.add("2_161");
                    break;
                case "7_296": //CCT
                    cards.add("200_83");
                    cards.add("10_42");
                    cards.add("211_8");
                    cards.add("201_28");
                    cards.add("109_7");
                    cards.add("5_166");
                    cards.add("200_126");
                    cards.add("6_162");
                    cards.add("6_164");
                    break;
                case "110_6": //Court
                    cards.add("200_84");
                    cards.add("6_149");
                    cards.add("6_149");
                    cards.add("9_139");
                    cards.add("7_276");
                    cards.add("6_162");
                    cards.add("6_164");
                    cards.add("6_168");
                    cards.add("6_170");
                    break;
                case "8_167": //EOps
                    cards.add("207_25");
                    cards.add("8_127");
                    cards.add("201_35");
                    cards.add("9_139");
                    cards.add("8_157");
                    cards.add("8_160");
                    cards.add("8_164");
                    cards.add("8_166");
                    cards.add("7_283");
                    break;
                case "210_42": //ROps (V)
                    cards.add("209_40");
                    cards.add("210_47");
                    cards.add("9_139");
                    cards.add("106_12");
                    cards.add("2_148");
                    cards.add("7_288");
                    cards.add("7_289");
                    cards.add("7_290");
                    cards.add("7_291");
                    break;
                case "301_4": //Twin Suns
                    cards.add("9_109");
                    cards.add("7_244");
                    cards.add("201_34");
                    cards.add("9_136");
                    cards.add("1_248");
                    cards.add("1_289");
                    cards.add("112_20");
                    cards.add("1_294");
                    cards.add("1_295");
                    break;
                case "7_297": //Hunt Down
                    cards.add("9_113");
                    cards.add("205_17");
                    cards.add("4_135");
                    cards.add("9_139");
                    cards.add("7_270");
                    cards.add("4_161");
                    cards.add("4_163");
                    cards.add("209_50");
                    cards.add("1_324");
                    break;
                case "12_180": //Watto
                    cards.add("11_65");
                    cards.add("11_65");
                    cards.add("11_65");
                    cards.add("9_139");
                    cards.add("11_90");
                    cards.add("12_164");
                    cards.add("8_156");
                    cards.add("208_56");
                    cards.add("12_178");
                    break;
                case "201_39": //Entanglements
                    cards.add("8_126");
                    cards.add("201_30");
                    cards.add("9_139");
                    cards.add("7_282");
                    cards.add("203_33");
                    cards.add("112_20");
                    cards.add("1_291");
                    cards.add("11_93");
                    cards.add("1_301");
                    break;
                case"7_275": //Coruscant CR (V)
                    cards.add("212_3");
                    cards.add("200_93");
                    cards.add("13_52");
                    cards.add("13_61");
                    cards.add("13_66");
                    cards.add("213_13");
                    cards.add("200_95");
                    cards.add("200_97");
                    cards.add("200_98");
                    cards.add("13_81");
                    cards.add("13_84");
                    cards.add("13_86");
                    cards.add("13_90");
                    cards.add("200_99");
                    cards.add("13_95");
                    cards.add("200_100");
                    cards.add("12_129");
                    cards.add("109_9");
                    cards.add("200_110");
                    cards.add("200_117");
                    cards.add("12_152");
                    cards.add("203_32");
                    cards.add("209_51");
                    cards.add("203_34");
                    break;
                case "9_151": //BHBM
                    cards.add("9_109");
                    cards.add("208_30");
                    cards.add("9_123");
                    cards.add("9_127");
                    cards.add("9_134");
                    cards.add("212_4");
                    cards.add("9_147");
                    cards.add("8_164");
                    cards.add("209_50");
                    break;
                case "208_57": //Map
                    cards.add("204_43");
                    cards.add("204_47");
                    cards.add("208_40");
                    cards.add("208_46");
                    cards.add("204_52");
                    cards.add("204_53");
                    cards.add("208_51");
                    cards.add("208_53");
                    cards.add("204_58");
                    break;
                case "213_31": //Hunt Down (V)
                    cards.add("108_6");
                    cards.add("210_31");
                    cards.add("213_16");
                    cards.add("213_18");
                    cards.add("212_4");
                    cards.add("7_273");
                    cards.add("213_29");
                    cards.add("213_28");
                    cards.add("209_50");
                    break;

                //Light
                case "9_61": //TIGIH
                    cards.add("200_21");
                    cards.add("9_34");
                    cards.add("12_44");
                    cards.add("202_5");
                    cards.add("9_51");
                    cards.add("8_71");
                    cards.add("8_76");
                    cards.add("9_57");
                    cards.add("9_90");
                    break;
                case "3_55": //EBO (Hoth CP (V))
                    cards.add("208_6");
                    cards.add("111_1");
                    cards.add("3_34");
                    cards.add("200_50");
                    cards.add("3_57");
                    cards.add("3_58");
                    cards.add("3_59");
                    cards.add("210_15");
                    cards.add("3_62");
                    break;
                case "208_26": //YOps
                    cards.add("211_58");
                    cards.add("5_24");
                    cards.add("9_39");
                    cards.add("208_17");
                    cards.add("9_51");
                    cards.add("204_26");
                    cards.add("9_59");
                    cards.add("211_32");
                    cards.add("208_24");
                    break;
                case "109_4": //QMC
                    cards.add("200_15");
                    cards.add("211_31");
                    cards.add("11_20");
                    cards.add("9_51");
                    cards.add("5_76");
                    cards.add("5_80");
                    cards.add("7_114");
                    cards.add("5_83");
                    cards.add("200_64");
                    break;
                case "110_4": //Profit
                    cards.add("200_14");
                    cards.add("200_45");
                    cards.add("201_9");
                    cards.add("211_30");
                    cards.add("6_81");
                    cards.add("14_49");
                    cards.add("12_84");
                    cards.add("7_131");
                    cards.add("200_70");
                    break;
                case "208_25": //HITCO
                    cards.add("213_44");
                    cards.add("9_34");
                    cards.add("208_14");
                    cards.add("213_48");
                    cards.add("213_49");
                    cards.add("9_51");
                    cards.add("12_76");
                    cards.add("4_89");
                    cards.add("208_23");
                    break;
                case "209_29": //No Idea
                    cards.add("211_56");
                    cards.add("211_29");
                    cards.add("209_18");
                    cards.add("9_51");
                    cards.add("209_23");
                    cards.add("209_24");
                    cards.add("209_25");
                    cards.add("209_26");
                    cards.add("208_24");
                    break;
                case "301_2": //CITC
                    cards.add("6_33");
                    cards.add("9_51");
                    cards.add("5_62");
                    cards.add("5_62");
                    cards.add("5_76");
                    cards.add("5_80");
                    cards.add("5_83");
                    cards.add("5_84");
                    cards.add("7_115");
                    break;
                case "204_32": //OA
                    cards.add("204_9");
                    cards.add("209_16");
                    cards.add("9_51");
                    cards.add("204_26");
                    cards.add("204_27");
                    cards.add("204_29");
                    cards.add("204_30");
                    cards.add("204_31");
                    cards.add("204_35");
                    break;
                case "10_26": //WYS
                    cards.add("213_37");
                    cards.add("213_46");
                    cards.add("9_39");
                    cards.add("9_51");
                    cards.add("213_55");
                    cards.add("213_57");
                    cards.add("12_84");
                    cards.add("1_128");
                    cards.add("1_129");
                    break;
                case "203_19": //Diplo
                    cards.add("203_11");
                    cards.add("204_10");
                    cards.add("203_14");
                    cards.add("9_51");
                    cards.add("1_121");
                    cards.add("8_67");
                    cards.add("12_84");
                    cards.add("1_130");
                    cards.add("201_19");
                    break;
                case "1_138": //TRM
                    cards.add("200_16");
                    cards.add("108_3");
                    cards.add("13_3");
                    cards.add("200_25");
                    cards.add("13_4");
                    cards.add("213_45");
                    cards.add("13_15");
                    cards.add("200_26");
                    cards.add("200_27");
                    cards.add("13_35");
                    cards.add("200_28");
                    cards.add("200_29");
                    cards.add("209_15");
                    cards.add("13_44");
                    cards.add("13_47");
                    cards.add("200_32");
                    cards.add("200_35");
                    cards.add("200_51");
                    cards.add("201_13");
                    cards.add("12_69");
                    cards.add("12_69");
                    cards.add("12_76");
                    cards.add("12_79");
                    cards.add("14_49");
                    break;
                case "112_1": //AITC
                    cards.add("14_10");
                    cards.add("14_12");
                    cards.add("9_41");
                    cards.add("208_16");
                    cards.add("9_51");
                    cards.add("6_81");
                    cards.add("12_84");
                    cards.add("112_9");
                    cards.add("205_6");
                    break;
                case "7_136": //HB
                    cards.add("201_8");
                    cards.add("9_51");
                    cards.add("201_16");
                    cards.add("201_16");
                    cards.add("8_68");
                    cards.add("211_43");
                    cards.add("200_58");
                    cards.add("7_123");
                    cards.add("12_84");
                    break;
                case "8_78": //RST
                    cards.add("203_15");
                    cards.add("8_43");
                    cards.add("9_51");
                    cards.add("8_63");
                    cards.add("204_24");
                    cards.add("8_69");
                    cards.add("204_25");
                    cards.add("8_77");
                    cards.add("8_90");
                    break;
                default:
                    break;
            }
        }
        return cards;
    }
}
