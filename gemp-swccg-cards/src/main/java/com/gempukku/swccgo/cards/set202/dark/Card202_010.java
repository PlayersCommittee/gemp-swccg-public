package com.gempukku.swccgo.cards.set202.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyDrawForActionSourceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Character
 * Subtype: Imperial
 * Title: General Nevar
 */
public class Card202_010 extends AbstractImperial {
    public Card202_010() {
        super(Side.DARK, 2, 3, 3, 2, 4, "General Nevar", Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setLore("ISB leader.");
        setGameText("[Pilot] 2, 3: Blizzard 2. Deploys -1 to Hoth. When deployed, may peek at top card of any Reserve Deck. When a spy here is targeted by Trample, adds 1 to your destiny draw.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_2);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        setMatchingVehicleFilter(Filters.Blizzard_2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Blizzard_2)));
        modifiers.add(new DestinyDrawForActionSourceModifier(self, Filters.and(Filters.Trample, Filters.cardBeingPlayedTargeting(self, Filters.and(Filters.spy, Filters.here(self)))), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Hoth));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Peek at top card of Reserve Deck");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(final SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Peek at top card of " + cardPileOwner + "'s Reserve Deck");
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, cardPileOwner));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
