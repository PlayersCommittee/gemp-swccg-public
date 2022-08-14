package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Device
 * Title: Holoprojector
 */
public class Card6_051 extends AbstractCharacterDevice {
    public Card6_051() {
        super(Side.LIGHT, 4, "Holoprojector");
        setLore("Common feature on astromechs. Displays technical information. Sometimes used by princesses to send distress calls to old Jedi Knights, but not often.");
        setGameText("Deploy on any R-unit droid. During your control phase, may use 1 Force to search your Reserve Deck, take one hologram or dejarik card into hand and reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.DEVICE_THAT_DEPLOYS_ON_DROIDS, Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.R_unit);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.R_unit;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HOLOPROJECTOR__UPLOAD_HOLOGRAM_OR_DEJARIK;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take hologram or dejarik into hand from Reserve Deck");
            action.setActionMsg("Take a hologram or dejarik into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.hologram, Filters.dejarik), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}