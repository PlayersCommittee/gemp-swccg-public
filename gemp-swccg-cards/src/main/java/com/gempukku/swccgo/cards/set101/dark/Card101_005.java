package com.gempukku.swccgo.cards.set101.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.BonusAbilitiesEnabledCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Premium (Premiere Introductory Two Player Game)
 * Type: Character
 * Subtype: Imperial
 * Title: Vader
 */
public class Card101_005 extends AbstractImperial {
    public Card101_005() {
        super(Side.DARK, 1, 7, 4, 6, 5, Title.Vader, Uniqueness.UNIQUE, ExpansionSet.PREMIERE_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("Sought to extinguish all Jedi. Former student of Obi-Wan Kenobi. Seduced by the dark side of the Force.");
        setGameText("Must deploy on Death Star, but may move elsewhere. May not be deployed if two or more of opponent's unique (•) characters on table. If in a losing battle, draw destiny. If destiny > 4, 'choke' (lose) one Imperial present (your choice).");
        addPersona(Persona.VADER);
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (game.useBonusAbilities())
            return Filters.any;
        return Filters.Deploys_on_Death_Star;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, new AndCondition(new NotCondition(new BonusAbilitiesEnabledCondition()), new OnTableCondition(self, 2, Filters.and(Filters.opponents(self), Filters.unique, Filters.character)))));
        modifiers.add(new PowerModifier(self, new BonusAbilitiesEnabledCondition(), 2));
        modifiers.add(new ForfeitModifier(self, new BonusAbilitiesEnabledCondition(), 2));
        modifiers.add(new DestinyModifier(self, self, new BonusAbilitiesEnabledCondition(), 2));
        modifiers.add(new DeployCostModifier(self, self, new BonusAbilitiesEnabledCondition(), -2));
        modifiers.add(new IconModifier(self, new BonusAbilitiesEnabledCondition(), Icon.PILOT));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new BonusAbilitiesEnabledCondition(), 5));
        modifiers.add(new PowerModifier(self, Filters.hasPiloting(self), new BonusAbilitiesEnabledCondition(), 4));
        modifiers.add(new AddsBattleDestinyModifier(self, new BonusAbilitiesEnabledCondition(), 1));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, new BonusAbilitiesEnabledCondition()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && game.useBonusAbilities()) {
            TargetingReason targetingReason = TargetingReason.TO_BE_CHOKED;
            Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.present(self));
            if (GameConditions.canTarget(game, self, targetingReason, filter)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("'Choke' a character");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target character", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses(playerId + " targets to 'choke' " + GameUtils.getCardLink(targetedCard),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard cardToChoke = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId, 1, DestinyType.CHOKE_DESTINY) {
                                                            @Override
                                                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                return Collections.singletonList(cardToChoke);
                                                            }
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                    return;
                                                                }
                                                                float ability = game.getModifiersQuerying().getAbility(game.getGameState(), cardToChoke);
                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                if (totalDestiny > ability) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new LoseCardFromTableEffect(action, cardToChoke));
                                                                    if (Filters.Amidala.accepts(game, cardToChoke)) {
                                                                        gameState.sendMessage("She was alive! I felt it!");
                                                                    }
                                                                } else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
        && !game.useBonusAbilities()) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 1, DestinyType.CHOKE_DESTINY) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny != null) {
                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            }

                            if (totalDestiny != null && totalDestiny <= 4) {
                                gameState.sendMessage("Result: Failed");
                            }
                            else {
                                if (totalDestiny == null)
                                    gameState.sendMessage("Result: Successful due to failed destiny draw");
                                else
                                    gameState.sendMessage("Result: Successful");

                                Filter targetFilter = Filters.and(Filters.Imperial, Filters.present(self));
                                Set<TargetingReason> targetingReasons = new HashSet<>(Arrays.asList(TargetingReason.TO_BE_CHOKED, TargetingReason.TO_BE_LOST));
                                if (GameConditions.canTarget(game, self, targetingReasons, targetFilter)) {
                                    action.appendEffect(
                                            new ChooseCardToLoseFromTableEffect(action, playerId, TargetingReason.TO_BE_CHOKED, targetFilter));
                                }
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
