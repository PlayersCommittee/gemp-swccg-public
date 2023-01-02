package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeOneCardIntoHandFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ScumAndVillainyMayDeployAttachedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RetrieveForceResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Objective
 * Title: Wookiee Slaving Operations / Indentured To The Empire
 */
public class Card601_029_BACK extends AbstractObjective {
    public Card601_029_BACK() {
        super(Side.DARK, 7, Title.Indentured_To_The_Empire, ExpansionSet.LEGACY, Rarity.V);
        setGameText("While this side up, whenever you 'enslave' a character, opponent must choose to use 2 Force or lose 1 Force. Once per turn, during battle may add or subtract up to X from your just drawn destiny, where X = the number of Kashyyyk locations you control with a slaver. Once per turn, if you just retrieved Force during battle, may take a slaver, starship, or vehicle retrieved into hand.\n" +
                "Flip this card if opponent controls two Kashyyyk battlegrounds.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_8);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        //your Trandoshans are slavers
        modifiers.add(new KeywordModifier(self, Filters.species(Species.TRANDOSHAN), Keyword.SLAVER));
        //Scum And Villainy may deploy on Slaving Camp Headquarters and
        modifiers.add(new ScumAndVillainyMayDeployAttachedModifier(self, Filters.Slaving_Camp_Headquarters));
        //may not be canceled while you occupy that site.
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Scum_And_Villainy, new OccupiesCondition(self.getOwner(), Filters.Slaving_Camp_Headquarters)));
        //While you have < 13 cards in hand, your non-unique slavers are immune to Grimtaash.
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.slaver),
                new CardsInHandFewerThanCondition(self.getOwner(), 13), Title.Grimtaash));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        //Flip this card if your slavers control two Kashyyyk battlegrounds and opponent controls no Kashyyyk locations.

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, opponent, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Kashyyyk_location, Filters.battleground))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        if (TriggerConditions.characterEnslavedBy(game, effectResult, playerId)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Opponent must use 2 Force or lose 1 Force");
            action.setActionMsg("Opponent must use 2 Force or lose 1 Force");
            if (GameConditions.canUseForce(game, opponent, 2)) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new MultipleChoiceAwaitingDecision("Use 2 Force or lose 1 Force?", new String[]{"Use 2 Force", "Lose 1 Force"}) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        if (index == 0) {
                                            game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                            action.appendEffect(
                                                    new UseForceEffect(action, opponent, 2));
                                        } else if (index == 1) {
                                            game.getGameState().sendMessage(opponent + " chooses to lose 1 Force");
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 1));
                                        }
                                    }
                                }
                        )
                );
            } else {
                action.appendEffect(new LoseForceEffect(action, opponent, 1));
            }
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        //during battle add or subtract from your just drawn destiny

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)) {
            //X = the number of Kashyyyk locations you control with a slaver.
            int amount = Filters.countActive(game, self, Filters.and(Filters.Kashyyyk_location, Filters.controlsWith(playerId, self, Filters.slaver)));

            //add
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add up to " + amount + " to destiny");
            action.appendUsage(new OncePerTurnEffect(action));
            if (amount > 0) {
                action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                        new IntegerAwaitingDecision("Add how much to destiny?", 1, amount, amount) {
                            @Override
                            public void decisionMade(final int result) {
                                action.appendEffect(new ModifyDestinyEffect(action, result));
                            }
                        }
                ));
            } else {
                action.appendEffect(new ModifyDestinyEffect(action, 0));
            }
            actions.add(action);

            //subtract
            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action2.setText("Subtract up to " + amount + " from destiny");
            action2.appendUsage(new OncePerTurnEffect(action2));
            if (amount > 0) {
                action2.appendEffect(new PlayoutDecisionEffect(action2, playerId,
                        new IntegerAwaitingDecision("Subtract how much from destiny?", 1, amount, amount) {
                            @Override
                            public void decisionMade(final int result) {
                                action2.appendEffect(new ModifyDestinyEffect(action2, -result));
                            }
                        }
                ));
            } else {
                action2.appendEffect(new ModifyDestinyEffect(action2, 0));
            }
            actions.add(action2);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;
        //during battle if you just retrieved a slaver, starship, or vehicle
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.justRetrievedForce(game, effectResult, playerId)) {
            PhysicalCard retrievedCard = ((RetrieveForceResult)effectResult).getMostRecentCardRetrieved();
            if (Filters.or(Filters.slaver, Filters.starship, Filters.vehicle).accepts(game, retrievedCard)) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Take retrieved card into hand");
                action.appendUsage(new OncePerTurnEffect(action));
                action.appendEffect(new TakeOneCardIntoHandFromOffTableEffect(action, playerId, retrievedCard, "Take "+ GameUtils.getCardLink(retrievedCard)+" into hand") {
                    protected void afterCardTakenIntoHand() {}
                });
                actions.add(action);
            }
        }
        return actions;
    }
}