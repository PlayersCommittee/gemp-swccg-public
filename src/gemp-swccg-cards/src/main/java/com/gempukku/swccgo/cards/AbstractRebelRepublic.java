package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for characters that are Rebels and Republic.
 */
public abstract class AbstractRebelRepublic extends AbstractCharacter {

    /**
     * Creates a blueprint for a character that is a Rebel and Republic.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractRebelRepublic(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, ability, forfeit, title, uniqueness, expansionSet, rarity);
        addCardType(CardType.REBEL);
        addCardType(CardType.REPUBLIC);
        addIcons(Icon.REBEL, Icon.REPUBLIC);
    }
}
