package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;

/**
 * The abstract class providing the common implementation for squadrons.
 */
public abstract class AbstractSquadron extends AbstractStarship {
    private Integer _replacementCountForSquadron;
    private Filter _replacementFilterForSquadron;

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float hyperspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, Float deployCost, float power, Float armor, float maneuver, float hyperspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, Float deployCost, float power, Float armor, float maneuver, Float hyperspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, Float hyperspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, Float deployCost, float power, Float armor, float maneuver, Float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.SQUADRON);
    }

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, Float deployCost, float power, Float armor, float maneuver, float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.SQUADRON);
    }

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.SQUADRON);
    }

    /**
     * Creates a blueprint for a squadrons.
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
    protected AbstractSquadron(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, Float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.SQUADRON);
    }

    @Override
    public final Integer getReplacementCountForSquadron() {
        return _replacementCountForSquadron;
    }

    @Override
    public final Filter getReplacementFilterForSquadron() {
        return _replacementFilterForSquadron;
    }

    /**
     * Sets the filter and number of starfighters present at a location that are replaced by the squadron.
     * @param count the number of starfighters
     * @param filter the filter
     */
    protected final void setReplacementForSquadron(int count, Filter filter) {
        _replacementCountForSquadron = count;
        _replacementFilterForSquadron = filter;
    }
}
