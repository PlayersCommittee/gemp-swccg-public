package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for characters that are droids.
 */
public abstract class AbstractDroid extends AbstractCharacter {

    /**
     * Creates a blueprint for a character that is a droid.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractDroid(Side side, float destiny, float deployCost, float power, int forfeit, String title) {
        this(side, destiny, deployCost, power, (float) forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is a droid.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractDroid(Side side, Float destiny, float deployCost, float power, Float forfeit, String title) {
        this(side, destiny, deployCost, power, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is a droid.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractDroid(Side side, Float destiny, float deployCost, float power, float forfeit, String title) {
        this(side, destiny, deployCost, power, (Float) forfeit, title, null);
    }

    /**
     * Creates a blueprint for a character that is a droid.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractDroid(Side side, double destiny, float deployCost, float power, float forfeit, String title, Uniqueness uniqueness) {
        this(side, (float) destiny, deployCost, power, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is a droid.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractDroid(Side side, float destiny, float deployCost, float power, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is a droid.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractDroid(Side side, float destiny, float deployCost, float power, Float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is a droid.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractDroid(Side side, Float destiny, float deployCost, float power, Float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a character that is a droid.
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
    protected AbstractDroid(Side side, Float destiny, float deployCost, float power, Float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, 0, forfeit, title, uniqueness, expansionSet, rarity);
        addCardType(CardType.DROID);
        addIcon(Icon.DROID);
    }
}
