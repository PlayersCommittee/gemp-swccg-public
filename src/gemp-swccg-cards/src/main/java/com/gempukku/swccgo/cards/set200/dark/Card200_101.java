package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: A Day Long Remembered (V)
 */
public class Card200_101 extends AbstractNormalEffect {
    public Card200_101() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Day Long Remembered", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("'It has seen the end of Kenobi and will soon see the end of the Rebellion.'");
        setGameText("Deploy on table. All immunity to attrition is -3. You may not fire [Permanent Weapon] weapons. If opponent just lost a Jedi or a battle, they lose 1 Force. During your draw phase, lose 1 Force or place this Effect in Used Pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
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
        if (TriggerConditions.justLost(game, effectResult, opponent, Filters.Jedi)
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

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Lose 1 Force or place in Used Pile");
            action.setActionMsg("Lose 1 Force or place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            effectsToChoose.add(new LoseForceEffect(action, playerId, 1, true));
            effectsToChoose.add(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, effectsToChoose));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)) {

            final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action1.setText("Lose 1 Force");
            // Update usage limit(s)
            action1.appendUsage(
                    new OncePerPhaseEffect(action1));
            // Perform result(s)
            action1.appendEffect(
                    new LoseForceEffect(action1, playerId, 1, true));
            actions.add(action1);

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