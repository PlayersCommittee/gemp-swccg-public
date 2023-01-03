package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Graxol Kelvyyn
 */
public class Card12_006 extends AbstractAlien {
    public Card12_006() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Graxol Kelvyyn", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Gentle male Anx who enjoys the risk and reward of the Podraces. Has a bet against Watto that Skywalker will win the Boonta Eve event.");
        setGameText("While at Podrace Arena, at end of opponent's turn: you retrieve 1 Force if Anakin's Podracer is leading the Podrace, or you lose 1 Force if Anakin's Podracer is losing the Podrace.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        setSpecies(Species.ANX);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)
                && GameConditions.isAtLocation(game, self, Filters.Podrace_Arena)) {
            if (GameConditions.isLeadingPodrace(game, Filters.Anakins_Podracer)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Retrieve 1 Force");
                action.setActionMsg("Make " + playerId + " retrieve 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 1) {
                            @Override
                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                return Collections.singletonList(Filters.findFirstActive(game, null, Filters.Anakins_Podracer));
                            }
                        });
                return Collections.singletonList(action);
            }
            else if (GameConditions.isBehindInPodrace(game, Filters.Anakins_Podracer)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Lose 1 Force");
                action.setActionMsg("Make " + playerId + " lose 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, playerId, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
