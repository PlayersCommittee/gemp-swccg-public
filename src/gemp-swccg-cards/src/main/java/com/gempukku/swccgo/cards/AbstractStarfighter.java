package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for starfighters.
 */
public abstract class AbstractStarfighter extends AbstractStarship {

    /**
     * Creates a blueprint for a starfigher.
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
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractStarfighter(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float hyperspeed, float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.STARFIGHTER);
    }

    /**
     * Creates a blueprint for a starfigher.
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
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractStarfighter(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, float hyperspeed, float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.STARFIGHTER);
    }

    /**
     * Creates a blueprint for a starfigher.
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
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractStarfighter(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, Float hyperspeed, float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.STARFIGHTER);
    }
}
