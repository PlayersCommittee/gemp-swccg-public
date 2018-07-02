package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for capital starships.
 */
public abstract class AbstractCapitalStarship extends AbstractStarship {

    /**
     * Creates a blueprint for a capital starship.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param hyperspeed the hyperspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractCapitalStarship(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, float hyperspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a capital starship.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param hyperspeed the hyperspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractCapitalStarship(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, Float hyperspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a capital starship.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param hyperspeed the hyperspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractCapitalStarship(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.CAPITAL);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
    }

    /**
     * Creates a blueprint for a capital starship.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param hyperspeed the hyperspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractCapitalStarship(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.CAPITAL);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
    }

    /**
     * Creates a blueprint for a capital starship.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param hyperspeed the hyperspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractCapitalStarship(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, Float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.CAPITAL);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
    }
}
