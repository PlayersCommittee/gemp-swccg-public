package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for characters that are both alien and Rebel.
 */
public abstract class AbstractAlienRebel extends AbstractCharacter {

    /**
     * Creates a blueprint for a character that is both an alien and a Rebel.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param ability the ability value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractAlienRebel(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title) {
        this(side, destiny, deployCost, power, ability, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is both an alien and a Rebel.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param ability the ability value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlienRebel(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, ability, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is both an alien and a Rebel.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param ability the ability value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractAlienRebel(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, ability, forfeit, title, uniqueness, expansionSet, rarity);
        addCardType(CardType.ALIEN);
        addCardType(CardType.REBEL);
        addIcons(Icon.ALIEN, Icon.REBEL);
    }
}
