package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for Effects.
 */
public abstract class AbstractEffect extends AbstractDeployable {

    /**
     * Creates a blueprint for an Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     */
    protected AbstractEffect(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, String title) {
        this(side, destiny, playCardZoneOption, title, null);
    }

    /**
     * Creates a blueprint for an Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractEffect(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness) {
        super(side, destiny, playCardZoneOption, null, title, uniqueness);
        setCardCategory(CardCategory.EFFECT);
        addCardType(CardType.EFFECT);
        addIcon(Icon.EFFECT);
    }

    /**
     * Gets the valid filter for targets to relocate the Effect when the specified Effect is relocated.
     * @param playerId the player to relocate the Effect
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public final Filter getValidRelocateEffectTargetFilter(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Filter cards that this card is not prohibited from being at
        Filter filter = Filters.and(Filters.not(Filters.or(Filters.holosite, Filters.hasAttached(self), Filters.attachedToWithRecursiveChecking(self))),
                Filters.notProhibitedFromTarget(self), Filters.notProhibitedFromCarrying(self), Filters.canBeTargetedBy(self));

        // Filter cards that a this type of card can be placed (based on game rules for that card type/subtype, etc.)
        filter = Filters.and(filter, getValidDeployTargetFilterForCardType(playerId, game, self, false, false, null, null),
                getValidDeployTargetFilterByCheckingGameText(game, self, new PlayCardOption(self.getPlayCardOptionId(), PlayCardZoneOption.ATTACHED, null), null));

        return filter;
    }
}
