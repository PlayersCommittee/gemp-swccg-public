package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for characters that are Rebels.
 */
public abstract class AbstractRebel extends AbstractCharacter {

    /**
     * Creates a blueprint for a character that is a Rebel.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractRebel(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title) {
        this(side, destiny, deployCost, power, ability, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is a Rebel.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractRebel(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, ability, forfeit, title, uniqueness);
        addCardType(CardType.REBEL);
        addIcons(Icon.REBEL);
    }
}
