package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: Major Partagaz
 */
public class Card221_026 extends AbstractImperial {
    public Card221_026() {
        super(Side.DARK, 3, 3, 2, 4, 5, "Major Partagaz", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("ISB leader.");
        setGameText("When deployed, may shuffle any player's Reserve Deck, Lost Pile, or Used Pile. ISB agent leaders at same and related sites (or at all locations while Partagaz present at a Coruscant site) are power and defense value +1.");
        addIcons(Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentAtCoruscantSiteCondition = new PresentAtCondition(self, Filters.Coruscant_site);
        Filter ISBagentLeaders = Filters.and(Filters.ISB_agent, Filters.leader);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(ISBagentLeaders, Filters.at(Filters.sameOrRelatedLocation(self))), new NotCondition(presentAtCoruscantSiteCondition), 1));
        modifiers.add(new PowerModifier(self, Filters.and(ISBagentLeaders, Filters.at(Filters.location)), presentAtCoruscantSiteCondition, 1));
        modifiers.add(new DefenseValueModifier(self, Filters.and(ISBagentLeaders, Filters.at(Filters.sameOrRelatedLocation(self))), new NotCondition(presentAtCoruscantSiteCondition), 1));
        modifiers.add(new DefenseValueModifier(self, Filters.and(ISBagentLeaders, Filters.at(Filters.location)), presentAtCoruscantSiteCondition, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent)
                || GameConditions.hasLostPile(game, playerId) || GameConditions.hasLostPile(game, opponent)
                || GameConditions.hasUsedPile(game, playerId) || GameConditions.hasUsedPile(game, opponent))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Shuffle deck or pile");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Filters.or(Zone.RESERVE_DECK, Zone.LOST_PILE, Zone.USED_PILE)) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            action.appendEffect(
                                    new ShufflePileEffect(action, cardPileOwner, cardPile));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
