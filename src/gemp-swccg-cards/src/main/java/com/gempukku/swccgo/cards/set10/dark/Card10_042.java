package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.CapturedOnlyCondition;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceAtLocationFromHandUsedPileOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Rebel
 * Title: Jabba's Prize
 */
public class Card10_042 extends AbstractRebel {
    public Card10_042() {
        super(Side.DARK, 0, 0, 0, 0, 0, Title.Jabbas_Prize, Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Han Solo was frozen in carbonite to test the process Vader planned to use on Luke. When Boba Fett delivered Han to Jabba, the vile gangster called him his 'favorite decoration'.");
        setGameText("Jabba's Prize is a Dark Side card. Deploys only at start of game if Carbon Chamber Testing is on table to Security Tower, frozen and imprisoned, (instead of a Rebel from opponent's Reserve Deck). If You Can Either Profit By This... is on table, opponent does not deploy Han at start of game (relocate Jabba's Prize to Audience Chamber, flip Carbon Chamber Testing, and you may not move or transfer Jabba's Prize). May not be placed in Reserve Deck. Jabba's Prize is a persona of Han only while on table. If Jabba's Prize leaves table, place it out of play. May not be targeted by We're The Bait or Someone Who Loves You. While Jabba's Prize is at Audience Chamber, Jabba is power +3, defense value +3, and adds 3 to his immunity to attrition. If Jabba's Prize was just released, opponent may replace it with any Han from hand, Used Pile, or Reserve Deck; reshuffle (if not replaced, place Jabba's Prize out of play).");
        addPersona(Persona.HAN);
        setCharacterPersonaOnlyWhileOnTable(true);
        addIcons(Icon.REFLECTIONS_II);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (GameConditions.isEitherPlayersPhase(game, Phase.PLAY_STARTING_CARDS)
                && !GameConditions.cardHasWhileInPlayDataSet(self)) {
            PhysicalCard profit = Filters.findFirstActive(game, self, Filters.You_Can_Either_Profit_By_This);
            if (profit != null) {
                PhysicalCard audienceChamber = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.Audience_Chamber, Filters.locationCanBeRelocatedTo(self, true, 0)));
                if (audienceChamber != null) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Relocate to Audience Chamber");
                    action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(audienceChamber));
                    // Perform result(s)
                    action.appendEffect(
                            new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
                    action.appendEffect(
                            new AddUntilEndOfGameModifierEffect(action, new ModifyGameTextModifier(self, profit, ModifyGameTextType.YOU_CAN_EITHER_PROFIT_BY_THIS__DO_NOT_DEPLOY_HAN_AT_START_OF_GAME), null));
                    action.appendEffect(
                            new RelocateBetweenLocationsEffect(action, self, audienceChamber));
                    action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    PhysicalCard carbonChamberTesting = Filters.findFirstActive(game, self, Filters.Carbon_Chamber_Testing);
                                    if (carbonChamberTesting != null
                                            && GameConditions.canBeFlipped(game, carbonChamberTesting)) {
                                        action.appendEffect(
                                                new FlipCardEffect(action, carbonChamberTesting));
                                    }
                                    action.appendEffect(
                                            new AddUntilEndOfGameModifierEffect(action, new MayNotMoveModifier(self), null));
                                    action.appendEffect(
                                            new AddUntilEndOfGameModifierEffect(action, new MayNotBeTransferredModifier(self), null));
                                }
                            });
                    return Collections.singletonList(action);
                }
            }
        }

        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                            }
                        }
                    });
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.released(game, effectResult, self)) {
            final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play allow opponent to replace it with any Han");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            if (location != null) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new YesNoDecision("Do you want to replace " + GameUtils.getCardLink(self) + " with any Han from hand, Used Pile, or Reserve Deck?") {
                                    @Override
                                    protected void yes() {
                                        game.getGameState().sendMessage(opponent + " chooses to replace " + GameUtils.getCardLink(self) + " with any Han from hand, Used Pile, or Reserve Deck");
                                        action.appendEffect(
                                                new PlaceAtLocationFromHandUsedPileOrReserveDeckEffect(action, opponent, Filters.and(Filters.Han, Filters.character), Filters.sameLocation(location), true));
                                    }
                                    @Override
                                    protected void no() {
                                        game.getGameState().sendMessage(opponent + " chooses to not replace " + GameUtils.getCardLink(self) + " with any Han from hand, Used Pile, or Reserve Deck");
                                    }
                                }
                        ));
            }
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition isCaptive = new CapturedOnlyCondition(self);
        Condition atAudienceChamber = new AndCondition(isCaptive, new AtCondition(self, Filters.Audience_Chamber));
        Filter jabba = Filters.Jabba;

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByModifier(self, isCaptive, Filters.or(Filters.Were_The_Bait, Filters.Someone_Who_Loves_You)));
        modifiers.add(new PowerModifier(self, jabba, atAudienceChamber, 3));
        modifiers.add(new DefenseValueModifier(self, jabba, atAudienceChamber, 3));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, jabba, atAudienceChamber, 3));
        return modifiers;
    }
}
