package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.FiredWeaponsInBattleCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Defensive Shield
 * Title: There Is Another
 */
public class Card209_015 extends AbstractDefensiveShield {
    public Card209_015() {
        super(Side.LIGHT, PlayCardZoneOption.ATTACHED, "There Is Another");
        setLore("Princess Leia Organa. Alderaanian senator. Targeted by Vader for capture and interrogation. The Dark Lord of the Sith wanted her alive.");
        setGameText("Plays on Your Destiny unless Leia or Luke has been deployed this game (even as a captive). [Death Star II] Luke and We’re The Bait are lost. Opponent’s Objective and [Death Star II] Effects target Leia instead of Luke. Force loss from Take Your Father’s Place is -1.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Your_Destiny;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !GameConditions.hasDeployedAtLeastXCardsThisGame(game, playerId, 1, Filters.or(Filters.Luke, Filters.Leia));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        //final GameState gameState = game.getGameState();

        // Check condition(s)
        // reduce light side force loss from Take Your Father's Place by 1
        if (TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.Take_Your_Fathers_Place)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, gameTextSourceCardId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce Force loss by 1");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerForceLossEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ReduceForceLossEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        // reduce dark side force loss from Take Your Father's Place by 1
        else if (TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, opponent, Filters.Take_Your_Fathers_Place)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, gameTextSourceCardId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce Force loss by 1");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerForceLossEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ReduceForceLossEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        else if (self.getWhileInPlayData() == null) {
            GameTextActionId gameTextActionId = GameTextActionId.THERE_IS_ANOTHER__FOR_REMAINDER_OF_GAME_CHANGES;
            self.setWhileInPlayData(new WhileInPlayData());

            if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
                final int permCardId = self.getPermanentCardId();

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText(null);
                action.skipInitialMessageAndAnimation();

                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));

                action.appendEffect(
                        new AddUntilEndOfGameActionProxyEffect(action,
                                new AbstractActionProxy() {
                                    @Override
                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                        List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                        PhysicalCard self = game.findCardByPermanentId(permCardId);

                                        // Check condition(s)
                                        if (TriggerConditions.isTableChanged(game, effectResult)) {
                                            Collection<PhysicalCard> lostCards = Filters.filterActive(game, self, SpotOverride.INCLUDE_ALL, Filters.or(Filters.Were_The_Bait, Filters.and(Filters.Luke, Icon.DEATH_STAR_II)));

                                            if (!lostCards.isEmpty()) {
                                                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                action.setSingletonTrigger(true);
                                                action.setText("Make [Death Star II] Luke and We're The Bait lost");
                                                action.setActionMsg("Make " + GameUtils.getAppendedNames(lostCards) + " lost");

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new LoseCardsFromTableEffect(action, lostCards));
                                                actions.add(action);
                                            }
                                        }
                                        return actions;
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

/*
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter ds2Filter = Filters.or(Filters.Bring_Him_Before_Me, Filters.Take_Your_Fathers_Place, Filters.Your_Destiny, Filters.Insignificant_Rebellion);

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(ds2Filter, Filters.not(Filters.hasGameTextModification(ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE))))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make opponent's Objective and [DS2] Effects target Leia");
            action.setActionMsg("Make opponent's Objective and [DS2] Effects target Leia instead of Luke for remainder of game");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action, new ModifyGameTextModifier(self,
                            Filters.or(Filters.Bring_Him_Before_Me, Filters.Take_Your_Fathers_Place, Filters.Your_Destiny, Filters.Insignificant_Rebellion), ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE),
                            "Makes opponent's Objective and [DS2] Effects target Leia instead of Luke for remainder of game"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Filter ds2Filter = Filters.or(Filters.Bring_Him_Before_Me, Filters.Take_Your_Fathers_Place, Filters.Your_Destiny, Filters.Insignificant_Rebellion);

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(ds2Filter, Filters.not(Filters.hasGameTextModification(ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action, new ModifyGameTextModifier(self, ds2Filter, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE),
                            "Makes opponent's Objective and [DS2] Effects target Leia instead of Luke for remainder of game"));
            return action;
        }
        return null;
    }
*/
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();

        Filter ds2Filter = Filters.or(Filters.Bring_Him_Before_Me, Filters.Take_Your_Fathers_Place, Filters.Your_Destiny, Filters.Insignificant_Rebellion);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendModifierEffectsModifier(self, Filters.Luke, ds2Filter));
        modifiers.add(new ModifyGameTextModifier(self, ds2Filter, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE));
        return modifiers;
    }

}
