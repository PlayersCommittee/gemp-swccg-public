package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.StackedOnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Character
 * Subtype: Rebel
 * Title: Master Kenobi
 */
public class Card601_054 extends AbstractRebel {
    public Card601_054() {
        super(Side.LIGHT, 1, 5, 5, 6, 9, "Master Kenobi", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("A user of the Force can alter the environment to affect the minds of others. 'The Force can have a strong influence on the weak-minded.'");
        setGameText("While 'communing': you may not deploy [Maintenance] cards or [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, may lose 1 Force (or use 2 Force) to place one just forfeited in Used Pile; once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle.");
        addIcons(Icon.WARRIOR, Icon.LEGACY_BLOCK_7);
        addPersona(Persona.OBIWAN);
        setAsLegacy(true);
    }

    public List<Modifier> getWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        Condition communing = new StackedOnCondition(self, Filters.Communing);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Icon.MAINTENANCE, Filters.and(Icon.EPISODE_I, Filters.Jedi)), communing, self.getOwner()));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.and(Filters.Rebel, Filters.abilityLessThan(5)), communing, 1));
        return modifiers;
    }

    public List<TopLevelGameTextAction> getGameTextTopLevelWhileStackedActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MASTER_KENOBI__DEPLOY_SITE;

        if (game.getModifiersQuerying().isCommuning(game.getGameState(), self)){
            if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);

                action.setText("Deploy location from Reserve Deck");
                action.setActionMsg("Deploy Tatooine battleground or Obi-Wan's Hut from Reserve Deck");
                action.appendUsage(new OncePerTurnEffect(action));
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.and(Filters.Tatooine_location, Filters.battleground), Filters.title("Tatooine: Obi-Wan's Hut")), true));

                return Collections.singletonList(action);
            }
        }
        return null;
    }

    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenStacked(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (game.getModifiersQuerying().isCommuning(game.getGameState(), self)
                && TriggerConditions.justForfeited(game, effectResult, playerId, Filters.and(Filters.Rebel, Filters.abilityLessThan(5)))
                && GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)) {
            final PhysicalCard forfeitedRebel = ((LostFromTableResult) effectResult).getCard();
            if (forfeitedRebel != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);

                action.appendUsage(new OncePerTurnEffect(action));

                if (!GameConditions.canUseForce(game, playerId, 2)) {
                    action.appendCost(new LoseForceEffect(action, playerId, 1, true));
                } else {

                    action.appendCost(new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("Lose 1 Force or use 2 Force?", new String[]{"Lose 1 Force", "Use 2 Force"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        game.getGameState().sendMessage(playerId + " chooses to lose 1 Force");
                                        action.appendCost(
                                                new LoseForceEffect(action, playerId, 1, true));
                                    } else if (index == 1) {
                                        game.getGameState().sendMessage(playerId + " chooses to use 2 Force");
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, 2));
                                    }
                                }
                            }
                    ));
                }

                action.allowResponses(new RespondableEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, forfeitedRebel, true));
                    }
                });

                return Collections.singletonList(action);
            }
        }

        return null;
    }
}