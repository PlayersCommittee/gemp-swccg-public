package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: A Dark Time For The Rebellion
 */
public class Card3_116 extends AbstractLostInterrupt {
    public Card3_116() {
        super(Side.DARK, 4, "A Dark Time For The Rebellion", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.C1);
        setLore("Absolute control wielded by the Emperor enables the Imperial forces to dominate planetary systems before the Rebel Alliance can gain a foothold.");
        setGameText("If opponent just deployed a planet site, search through your Reserve Deck for the related system and immediately deploy it. Shuffle, cut and replace.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.A_DARK_TIME_FOR_THE_REBELLION__DOWNLOAD_RELATED_SYSTEM;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.planet_site)) {
            PhysicalCard cardPlayed = ((PlayCardResult) effectResult).getPlayedCard();
            final String systemName = cardPlayed.getPartOfSystem();
            if (systemName != null) {
                if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, systemName, true)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                    action.setText("Deploy " + systemName + " system from Reserve Deck");
                    // Allow response(s)
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.system, Filters.title(systemName)), true));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}