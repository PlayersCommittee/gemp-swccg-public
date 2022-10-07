package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for characters that are aliens.
 */
public abstract class AbstractAlien extends AbstractCharacter {

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractAlien(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title) {
        this(side, destiny, deployCost, power, ability, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractAlien(Side side, float destiny, Float deployCost, float power, float ability, float forfeit, String title) {
        this(side, destiny, deployCost, power, ability, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlien(Side side, double destiny, float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness) {
        this(side, (float) destiny, deployCost, power, ability, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlien(Side side, double destiny, Float deployCost, Float power, float ability, Float forfeit, String title, Uniqueness uniqueness) {
        this(side, (float) destiny, deployCost, power, ability, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlien(Side side, float destiny, float deployCost, float power, float ability, double forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, ability, (float) forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlien(Side side, float destiny, Float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, ability, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlien(Side side, float destiny, float deployCost, Float power, float ability, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, ability, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAlien(Side side, float destiny, float deployCost, float power, float ability, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, ability, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is an alien.
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
    protected AbstractAlien(Side side, Float destiny, Float deployCost, Float power, float ability, Float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, ability, forfeit, title, uniqueness, expansionSet, rarity);
        addCardType(CardType.ALIEN);
        addIcons(Icon.ALIEN);
    }
}
