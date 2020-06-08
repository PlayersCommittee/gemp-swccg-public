package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for characters that are both alien and Resistance.
 */
public abstract class AbstractAlienResistance extends AbstractCharacter {

    /**
     * Creates a blueprint for a character that is both an alien and Resistance.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param ability the ability value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractAlienResistance(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title) {
        this(side, destiny, deployCost, power, ability, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is both an alien and Resistance.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param ability the ability value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlienResistance(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, ability, forfeit, title, uniqueness);
        addCardType(CardType.ALIEN);
        addCardType(CardType.RESISTANCE);
        addIcons(Icon.ALIEN, Icon.RESISTANCE);
    }
}
