package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleInitiatedResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Gran
 */
public class Card6_017 extends AbstractAlien {
    public Card6_017() {
        super(Side.LIGHT, 2, 2, 1, 1, 3, "Gran", Uniqueness.RESTRICTED_3);
        setLore("From an ancient civilization. Pacifists. Those who commit acts of violence are banished from their home planet, Kinyen. Organizes peaceful protests against the Empire.");
        setGameText("May retrieve 1 Force whenever opponent initiates battle at same or adjacent site (if at same site, retrieved Force may be taken into hand).");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.GRAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.sameOrAdjacentSite(self))) {
            final boolean atSameSite = Filters.sameSite(self).accepts(game, ((BattleInitiatedResult) effectResult).getLocation());

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1) {
                        @Override
                        public boolean mayBeTakenIntoHand() {
                            return atSameSite;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
