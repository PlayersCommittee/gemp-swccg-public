package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentWhereAffectedCardIsAtEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 */
public class Card601_087 extends AbstractObjective {
    public Card601_087() {
        super(Side.DARK, 0, Title.Hunt_Down_And_Destroy_The_Jedi, ExpansionSet.LEGACY, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Coruscant system, Imperial City, and A Sith's Plans.  May deploy If The Trace Was Correct.\n" +
                "For remainder of game, you may not deploy [Episode I] Dark Jedi.  Whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table, opponent loses 2 Force.\n" +
                "While this side up, may take Rogue Shadow into hand from Reserve Deck; reshuffle.  Galen's immunity to attrition is +2 for each Jedi present.\n" +
                "Flip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site.");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Coruscant system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Imperial_City, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Imperial City to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.A_Siths_Plans), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose A Sith's Plans to deploy";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardsFromReserveDeckEffect(action, Filters.If_The_Trace_Was_Correct, 0, 1, true, false) {
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose If The Trace Was Correct to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HUNT_DOWN_V__UPLOAD_ROGUE_SHADOW;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Rogue Shadow into hand from Reserve Deck");
            action.setActionMsg("Take Rogue Shadow into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Rogue_Shadow), true));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.and(Icon.EPISODE_I, Filters.Dark_Jedi), self.getOwner()));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.Galen, new TrueCondition(), new MultiplyEvaluator(2, new PresentWhereAffectedCardIsAtEvaluator(self, Filters.Jedi))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.or(Filters.Galen, Filters.Vader), Filters.at(Filters.battleground_site)))
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.unique, Filters.character, Filters.abilityMoreThan(3), Filters.at(Filters.battleground_site)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }


        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table, opponent loses 2 Force.
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.or(Persona.GALENS_LIGHTSABER, Persona.VADERS_LIGHTSABER))) {
            PhysicalCard justHitCard = ((HitResult)effectResult).getCardHit();

            if (justHitCard != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.skipInitialMessageAndAnimation();
                action.setSingletonTrigger(true);
                action.setActionMsg(null);

                final int permCardIdSelf = self.getPermanentCardId();
                final int permCardIdHitCharacter = justHitCard.getPermanentCardId();
                action.appendEffect(new AddUntilEndOfTurnActionProxyEffect(action, new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        List<TriggerAction> actions1 = new LinkedList<TriggerAction>();

                        //might need to check for being restored to normal

                        PhysicalCard objective = game.findCardByPermanentId(permCardIdSelf);
                        PhysicalCard hitCharacter = game.findCardByPermanentId(permCardIdHitCharacter);

                        if (TriggerConditions.leavesTable(game, effectResult, Filters.samePermanentCardId(hitCharacter))) {
                            RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                            action1.setPerformingPlayer(objective.getOwner());
                            action1.setSingletonTrigger(true);
                            action1.setText("Lose 2 Force");
                            action1.setActionMsg("Lose 2 Force whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table");
                            action1.appendEffect(new LoseForceEffect(action1, game.getOpponent(objective.getOwner()), 2));
                            actions1.add(action1);
                        }
                        return actions1;
                    }
                }));

                actions.add(action);
            }
        }
        return actions;
    }
}
