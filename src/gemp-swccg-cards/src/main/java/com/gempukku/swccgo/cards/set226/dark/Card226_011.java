package com.gempukku.swccgo.cards.set226.dark;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Alien
 * Title: Salacious Crumb (V)
 */
public class Card226_011 extends AbstractAlien {
    public Card226_011() {
        super(Side.DARK, 3, 1, 1, 1, 3, Title.Salacious_Crumb, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Male Kowakian. Prankster. Humiliates others for Jabba's amusement. His life depends on making Jabba laugh at least once per day.");
        setGameText("When deployed, may reveal the top two cards of your Reserve Deck; take one into hand and place the other in Used Pile. Unless Crumb is 'hit', your leaders here may not be targeted by weapons. Undercover spies here are lost. ('AH-hahahaha!')");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_26);
        setSpecies(Species.KOWAKIAN);
        setVirtualSuffix(true);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reveal top two cards of Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new RevealTopCardsOfReserveDeckEffect(action, playerId, 2) {
                        @Override
                        protected void cardsRevealed(final List<PhysicalCard> cards) {
                            if (cards.size() == 2) {
                                action.appendEffect(
                                        new ChooseArbitraryCardsEffect(action, playerId, "Choose card to take into hand", cards, 1, 1) {
                                            @Override
                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                PhysicalCard cardToTakeIntoHand = selectedCards.iterator().next();
                                                if (cardToTakeIntoHand != null) {
                                                    action.appendEffect(
                                                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, cardToTakeIntoHand, false));
                                                    Collection<PhysicalCard> nonSelectedCards = Filters.filter(cards, game, Filters.not(cardToTakeIntoHand));
                                                    PhysicalCard cardToPlaceInUsedPile = nonSelectedCards.iterator().next();
                                                    if (cardToPlaceInUsedPile != null) {
                                                        action.appendEffect(
                                                                new PutCardFromReserveDeckOnTopOfCardPileEffect(action, cardToPlaceInUsedPile, Zone.USED_PILE, false));
                                                    }
                                                }
                                            }
                                        }
                                );
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition unlessCrumbHit = new UnlessCondition(new HitCondition(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.leader, Filters.here(self)), unlessCrumbHit));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        Filter undercoverSpies = Filters.and(Filters.here(self), Filters.undercover_spy);
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, TargetingReason.TO_BE_LOST, undercoverSpies)) {

            Collection<PhysicalCard> toBeLost = Filters.filterActive(game, self, SpotOverride.INCLUDE_UNDERCOVER, TargetingReason.TO_BE_LOST, undercoverSpies);
            if (!toBeLost.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make Undercover spies here lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(toBeLost) + " lost");

                // Send Easter Egg Message
                action.appendCost(
                    new SendMessageEffect(action, "Salacious Crumb: AH-hahahaha!"));
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, toBeLost));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}