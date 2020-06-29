package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Starship
 * Subtype: Capital
 * Title: Leia's Resistance Transport
 */
public class Card501_035 extends AbstractCapitalStarship {
    public Card501_035() {
        super(Side.LIGHT, 3, 3, 3, 4, null, 3, 5, "Leia's Resistance Transport", Uniqueness.UNIQUE);
        setGameText("May add 2 pilot and 5 passengers. Permanent pilot provides ability of 2. Deploys and moves like a starfighter. When deployed, may deploy a Resistance leader aboard for free from Reserve Deck; reshuffle.");
        setPilotCapacity(2);
        setPassengerCapacity(5);
        isDeploysAndMovesLikeStarfighter();
        addIcons(Icon.RESISTANCE, Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {
        });
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEIAS_RESISTANCE_TRANSPORT__DOWNLOAD_RESISTANCE_LEADER;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Resistance Leader from Reserve Deck");
            action.setActionMsg("Deploy a Resistance Leader aboard " + GameUtils.getCardLink(self) + " from Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.Resistance_leader, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}