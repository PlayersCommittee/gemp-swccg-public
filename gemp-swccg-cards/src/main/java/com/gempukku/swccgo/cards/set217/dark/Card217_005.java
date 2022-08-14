package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Alien
 * Title: Burg
 */
public class Card217_005 extends AbstractAlien {
    public Card217_005() {
        super(Side.DARK, 2, 3, 6, 1, 4, "Burg", Uniqueness.UNIQUE);
        setArmor(3);
        setLore("Devaronian mercenary.");
        setGameText("Once during battle, may use 1 Force to make Burg power +2 for remainder of turn. Once during battle, opponent may use 1 Force to make Burg power -2 for remainder of turn. At the end of each of your turns, use 1 Force or place Burg in Used Pile.");
        setSpecies(Species.DEVARONIAN);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canSpot(game, self, Filters.title("Burg"))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make Burg power +2");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new PowerModifier(self, Filters.title("Burg"), 2), "make Burg power +2 until end of turn"));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canSpot(game, self, Filters.title("Burg"))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make Burg power -2");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new PowerModifier(self, Filters.title("Burg"), -2), "make Burg power -2 until end of turn"));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // At the end of each of your turns, use 1 Force or place Burg in Used Pile.

        // Check condition(s)
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerId)) {
            boolean useForceIsOption = GameConditions.canUseForce(game, playerId, 1);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            if (useForceIsOption)
                action.setText("Use 1 Force or place Burg in Used Pile");
            else
                action.setText("Place Burg in Used Pile");

            // Perform result(s)
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            if (useForceIsOption) {
                effectsToChoose.add(new UseForceEffect(action, playerId, 1));
            }
            effectsToChoose.add(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, effectsToChoose));
            return Collections.singletonList(action);
        }
        return null;
    }
}
