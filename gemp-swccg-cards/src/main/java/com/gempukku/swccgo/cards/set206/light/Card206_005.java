package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Character
 * Subtype: Droid
 * Title: K-2SO (Kay-Tueesso)
 */
public class Card206_005 extends AbstractDroid {
    public Card206_005() {
        super(Side.LIGHT, 3, 4, 4, 4, Title.K2SO, Uniqueness.UNIQUE, ExpansionSet.SET_6, Rarity.V);
        setArmor(4);
        setLore("Spy.");
        setGameText("[Pilot] 2. Draws one battle destiny if unable to otherwise. While with Cassian, attrition against opponent is +1 here. If just lost, place out of play; if lost from a site, opponent may not move from there for remainder of turn.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PRESENCE, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.SPY);
        addModelType(ModelType.SECURITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new AttritionModifier(self, Filters.here(self), new WithCondition(self, Filters.Cassian), 1, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {
            PhysicalCard lostFromLocation = ((LostFromTableResult) effectResult).getFromLocation();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromOffTableEffect(action, self));
            if (lostFromLocation != null && Filters.site.accepts(game, lostFromLocation)) {
                action.appendEffect(
                        new AddUntilEndOfTurnModifierEffect(action,
                                new MayNotMoveFromLocationModifier(self, Filters.opponents(self), Filters.sameLocationId(lostFromLocation)),
                                "Makes opponent not able to move from " + GameUtils.getCardLink(lostFromLocation)));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
