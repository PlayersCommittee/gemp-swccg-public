package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromUsedPileEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 25
 * Type: Vehicle
 * Subtype: Combat
 * Title: First Order AT-M6
 */
public class Card225_019 extends AbstractCombatVehicle {
    public Card225_019() {
        super(Side.DARK, 2,6, 7, 6, null, 1, 7, "First Order AT-M6", Uniqueness.UNRESTRICTED, ExpansionSet.SET_25, Rarity.V);
        setLore("Enclosed.");
        setGameText("May add 1 pilot and 6 passengers. Permanent pilot provides ability of 2. Once during your deploy phase, may deploy a First Order trooper here from Used Pile; reshuffle.");
        addModelType(ModelType.AT_M6);
        addIcons(Icon.SCOMP_LINK, Icon.EPISODE_VII, Icon.FIRST_ORDER, Icon.PILOT, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.FIRST_ORDER_AT_M6__DOWNLOAD_FIRST_ORDER_TROOPER;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromUsedPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Used Pile");
            action.setActionMsg("Deploy a [First Order] Trooper from Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromUsedPileEffect(action, Filters.and(Icon.FIRST_ORDER, Filters.trooper), Filters.locationAndCardsAtLocation(Filters.here(self)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
