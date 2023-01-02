package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataEqualsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NumberOfBattleDestinyDrawsMayNotBeLimitedForEitherPlayerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Objective
 * Title: Zero Hour / Liberation of Lothal
 */
public class Card219_048_BACK extends AbstractObjective {
    public Card219_048_BACK() {
        super(Side.LIGHT, 7, "Liberation of Lothal", ExpansionSet.SET_19, Rarity.V);
        setGameText("While this side up, if you Force drained at a battleground this turn, your other Force drains at battlegrounds are +1. " +
                "Once per turn during battle, may add or subtract X from a just drawn battle destiny (or opponent's weapon destiny), " +
                "where X = number of battlegrounds you occupy with Phoenix Squadron characters. " +
                "At Lothal system, the number of battle destiny draws may not be limited for either player." +
                "Flip this card if opponent controls more Lothal locations than you.");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, Filters.battleground, new InPlayDataEqualsCondition(self, true), 1, self.getOwner()));
        modifiers.add(new NumberOfBattleDestinyDrawsMayNotBeLimitedForEitherPlayerModifier(self, Filters.Lothal_system));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ZERO_HOUR__DEPLOY_LOCATION;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Lothal site from Reserve Deck");
            action.setActionMsg("Deploy a Lothal site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Lothal_site, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        String opponent = game.getOpponent(playerId);
        //during battle add or subtract from a just drawn battle destiny (or opponent's weapon destiny)

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattle(game)
                && (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, opponent))) {
            //X = the number of battlegrounds occupied by Phoenix Squadron character
            int amount = Filters.countTopLocationsOnTable(game,
                    Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.Phoenix_Squadron_character)));

            //add
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add " + amount + " to destiny");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new ModifyDestinyEffect(action, amount));
            actions.add(action);


            //subtract
            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action2.setText("Subtract " + amount + " from destiny");
            action2.appendUsage(new OncePerTurnEffect(action2));
            action2.appendEffect(new ModifyDestinyEffect(action2, -amount));
            actions.add(action2);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Track if you have force drained this turn
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerId, Filters.battleground)) {
            self.setWhileInPlayData(new WhileInPlayData(true));
        }

        // Reset at the end of each turn
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (Filters.countTopLocationsOnTable(game, Filters.and(Filters.Lothal_location, Filters.controls(opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE))) >
                Filters.countTopLocationsOnTable(game, Filters.and(Filters.Lothal_location, Filters.controls(playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}