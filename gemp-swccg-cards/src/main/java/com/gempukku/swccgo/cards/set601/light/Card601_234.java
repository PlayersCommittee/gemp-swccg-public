package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Title: Down With The Emperor! (V)
 */
public class Card601_234 extends AbstractNormalEffect {
    public Card601_234() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Down With The Emperor!", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("News of Imperial defeat inspires the citizens of the galaxy. A major loss seriously undermines the Empire's ability to function.");
        setGameText("Deploy on table. All immunity to attrition is -3. You may not fire [Permanent Weapon] weapons. If opponent just lost a Dark Jedi or a battle, they lose 1 Force. During your draw phase, use 3 Force or place this Effect in Used Pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.any, -3));
        modifiers.add(new MayNotBeFiredModifier(self, Filters.and(Filters.your(self), Icon.PERMANENT_WEAPON, Filters.weapon)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, opponent, Filters.Dark_Jedi)
                || TriggerConditions.lostBattle(game, effectResult, opponent)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        // Check if reached end of draw phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.DRAW)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)) {
            if (GameConditions.canUseForce(game, playerId, 3)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Use 3 Force or place in Used Pile");
                action.setActionMsg("Use 3 Force or place " + GameUtils.getCardLink(self) + " in Used Pile");
                // Perform result(s)
                List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
                effectsToChoose.add(new UseForceEffect(action, playerId, 3));
                effectsToChoose.add(new PlaceCardInUsedPileFromTableEffect(action, self));
                action.appendEffect(
                        new ChooseEffectEffect(action, playerId, effectsToChoose));
                actions.add(action);
            }
            else {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place in Used Pile");
                action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
                action.appendEffect(
                        new PlaceCardInUsedPileFromTableEffect(action, self));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)) {
            if (GameConditions.canUseForce(game, playerId, 3)) {

                final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action1.setText("Use 3 Force");
                // Update usage limit(s)
                action1.appendUsage(
                        new OncePerPhaseEffect(action1));
                // Perform result(s)
                action1.appendEffect(
                        new UseForceEffect(action1, playerId, 3));
                actions.add(action1);
            }

            final TopLevelGameTextAction action2 = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action2.setText("Place in Used Pile");
            action2.setActionMsg("Place " + GameUtils.getCardLink(self) + " Used Pile");
            // Update usage limit(s)
            action2.appendUsage(
                    new OncePerPhaseEffect(action2));
            // Perform result(s)
            action2.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action2, self));
            actions.add(action2);

        }
        return actions;
    }
}