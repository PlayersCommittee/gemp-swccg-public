package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.cards.evaluators.InPlayDataAsFloatEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ParasiteTargetModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.EatenResult;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Creature
 * Title: Vine Snake
 */
public class Card4_113 extends AbstractCreature {
    public Card4_113() {
        super(Side.DARK, 3, 2, 0, 3, 0, "Vine Snake", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Found on various planets throughout the galaxy. Hides among hanging vines, dropping on unsuspecting travelers that pass beneath. Kills its victims through gradual constriction.");
        setGameText("Habitat: planet sites (except Hoth). Parasite: Non-droid Character. Each move phase, draw destiny; each time destiny > ability, add 1 to X (X starts at 0). Host is power -X (eaten if power = 0).");
        addModelType(ModelType.OPHIDIAN);
        addIcons(Icon.DAGOBAH, Icon.SELECTIVE_CREATURE);
        addKeyword(Keyword.PARASITE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.planet_site, Filters.not(Filters.Hoth_site));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter host = Filters.and(Filters.hasAttached(self), Filters.non_droid_character);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ParasiteTargetModifier(self, Filters.non_droid_character));
        modifiers.add(new CalculationVariableModifier(self, new InPlayDataAsFloatEvaluator(self)));
        modifiers.add(new PowerModifier(self, host, new NegativeEvaluator(new CalculateCardVariableEvaluator(self, Variable.X) {
            @Override
            protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return 0;
            }
        })));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && GameConditions.canDrawDestiny(game, playerId)) {
            final GameState gameState = game.getGameState();
            final PhysicalCard host = self.getAttachedTo();
            if (host != null && Filters.non_droid_character.accepts(game, host)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Draw destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return Collections.singletonList(host);
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                if (totalDestiny == null) {
                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                    return;
                                }
                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                float ability = game.getModifiersQuerying().getAbility(gameState, host);
                                gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                if (totalDestiny > ability) {
                                    gameState.sendMessage("Result: Succeeded");
                                    float valueOfX = 0;
                                    if (self.getWhileInPlayData() != null && self.getWhileInPlayData().getFloatValue() != null) {
                                        valueOfX = self.getWhileInPlayData().getFloatValue();
                                    }
                                    self.setWhileInPlayData(new WhileInPlayData(valueOfX + 1));
                                    action.appendEffect(
                                            new TriggeringResultEffect(action, new ResetOrModifyCardAttributeResult(playerId, host)));
                                } else {
                                    gameState.sendMessage("Result: Failed");
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            WhileInPlayData whileInPlayData = self.getWhileInPlayData();
            if (whileInPlayData == null || whileInPlayData.getPhysicalCard() == null) {
                final PhysicalCard host = self.getAttachedTo();
                if (host != null && Filters.non_droid_character.accepts(game, host)) {
                    GameState gameState = game.getGameState();
                    ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                    final float power = modifiersQuerying.getPower(gameState, host);
                    if (power == 0) {
                        final float forfeit = modifiersQuerying.getForfeit(gameState, host);
                        final boolean captive = host.isCaptive();
                        final PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(game.getGameState(), self);

                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setSingletonTrigger(true);
                        action.setText("Eat host");
                        action.setActionMsg("Make " + GameUtils.getCardLink(host) + " eaten");
                        // Perform result(s)
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        float valueOfX = 0;
                                        if (self.getWhileInPlayData() != null && self.getWhileInPlayData().getFloatValue() != null) {
                                            valueOfX = self.getWhileInPlayData().getFloatValue();
                                        }
                                        self.setWhileInPlayData(new WhileInPlayData(valueOfX, host));
                                        action.appendEffect(
                                                new TriggeringResultEffect(action,
                                                        new EatenResult(host, power, null, forfeit, captive, self, location)));
                                        action.appendEffect(
                                                new LoseCardFromTableEffect(action, host));
                                    }
                                }
                        );
                        actions.add(action);
                    }
                }
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check if reached end of each move phase and action was not performed yet.
        if (TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.MOVE)
                && GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)) {
            final GameState gameState = game.getGameState();
            final PhysicalCard host = self.getAttachedTo();
            if (host != null && Filters.non_droid_character.accepts(game, host)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Draw destiny");
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return Collections.singletonList(host);
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                if (totalDestiny == null) {
                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                    return;
                                }
                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                float ability = game.getModifiersQuerying().getAbility(gameState, host);
                                gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                if (totalDestiny > ability) {
                                    gameState.sendMessage("Result: Succeeded");
                                    float valueOfX = 0;
                                    if (self.getWhileInPlayData() != null && self.getWhileInPlayData().getFloatValue() != null) {
                                        valueOfX = self.getWhileInPlayData().getFloatValue();
                                    }
                                    self.setWhileInPlayData(new WhileInPlayData(valueOfX + 1));
                                    action.appendEffect(
                                            new TriggeringResultEffect(action, new ResetOrModifyCardAttributeResult(playerId, host)));
                                } else {
                                    gameState.sendMessage("Result: Failed");
                                }
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}
