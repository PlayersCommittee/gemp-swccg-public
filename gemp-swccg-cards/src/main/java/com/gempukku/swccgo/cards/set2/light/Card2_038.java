package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Solomahal
 */
public class Card2_038 extends AbstractNormalEffect {
    public Card2_038() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Solomahal");
        setLore("A crafty purloiner from Tirac Munda with a heart of gold. Often hired to advise the wealthy on how to protect their own property. Well traveled and skilled in many languages.");
        setGameText("Deploy on your warrior to give that warrior scout skill. That warrior may move as a 'react.' OR Deploy on your scout. When that scout 'reacts,' it is power +2 for remainder of turn. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy on warrior"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy on scout"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_1) {
            return Filters.and(Filters.your(self), Filters.warrior);
        }
        else if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2) {
            return Filters.and(Filters.your(self), Filters.scout);
        }
        return Filters.none;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition playCardOptionId1 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Filter attachedTo = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, attachedTo, playCardOptionId1, Keyword.SCOUT));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move as 'react'", playCardOptionId1, self.getOwner(), attachedTo, Filters.any));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.reactedToLocation(game, effectResult, Filters.hasAttached(self), Filters.any)) {
            PhysicalCard attachedTo = self.getAttachedTo();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 2 to power");
            action.setActionMsg("Add 2 to " + GameUtils.getCardLink(attachedTo) + "'s power");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfTurnEffect(action, attachedTo, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}