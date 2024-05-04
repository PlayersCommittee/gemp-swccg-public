package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Yavin 4: Massassi War Room
 */
public class Card1_139 extends AbstractSite {
    public Card1_139() {
        super(Side.LIGHT, Title.Yavin_4_War_Room, Title.Yavin_4, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLocationDarkSideGameText("If you initiate a battle here, add one battle destiny.");
        setLocationLightSideGameText("If you control, with a leader here, all Stormtroopers and TIEs on table are forfeit -1.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerOnDarkSideOfLocation, self)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add one battle destiny");
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1, playerOnDarkSideOfLocation));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition controlWithLeader = new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.leader);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.stormtrooper, Filters.onTable), controlWithLeader, -1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.TIE, Filters.onTable), controlWithLeader, new PerTIEEvaluator(-1)));
        return modifiers;
    }
}