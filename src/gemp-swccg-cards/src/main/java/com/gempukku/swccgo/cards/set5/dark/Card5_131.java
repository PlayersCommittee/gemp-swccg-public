package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Atmospheric Assault
 */
public class Card5_131 extends AbstractLostInterrupt {
    public Card5_131() {
        super(Side.DARK, 5, "Atmospheric Assault", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("The TIEs patrolling Cloud City took advantage of Bespin's dense cloud cover.");
        setGameText("If you just initiated a battle at a Cloud City sector, add one battle destiny. OR During your deploy phase, deploy one TIE Assault Squadron for free (no replacement is necessary).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.Bespin_Cloud_City)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, Filters.and(Filters.TIE_Assault_Squadron, Filters.deployable(self, false, null, true, 0, null, DeploymentOption.allowSquadronWithoutDeployCost(), null, null)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy a TIE Assault Squadron for free");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromHandEffect(action, playerId, Filters.TIE_Assault_Squadron, true, DeploymentOption.allowSquadronWithoutDeployCost()));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}