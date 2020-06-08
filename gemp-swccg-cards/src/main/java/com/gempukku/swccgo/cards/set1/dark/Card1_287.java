package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.evaluators.PerYwingEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Location
 * Subtype: Site
 * Title: Death Star: War Room
 */
public class Card1_287 extends AbstractSite {
    public Card1_287() {
        super(Side.DARK, "Death Star: War Room", Title.Death_Star);
        setLocationDarkSideGameText("If you control, with a leader here, all Rebel Troopers and Y-wings on table are forfeit -1.");
        setLocationLightSideGameText("If you initiate a battle here, add one battle destiny.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition controlsWithLeaderCondition = new ControlsWithCondition(playerOnDarkSideOfLocation, self, Filters.leader);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Rebel, Filters.trooper, Filters.onTable), controlsWithLeaderCondition, -1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Y_wing, Filters.onTable), controlsWithLeaderCondition, new PerYwingEvaluator(-1)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerOnLightSideOfLocation, self)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add one battle destiny");
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1, playerOnLightSideOfLocation));
            return Collections.singletonList(action);
        }
        return null;
    }
}