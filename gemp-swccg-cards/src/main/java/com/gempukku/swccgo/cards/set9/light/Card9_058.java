package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: Site
 * Title: Home One: War Room
 */
public class Card9_058 extends AbstractUniqueStarshipSite {
    public Card9_058() {
        super(Side.LIGHT, "Home One: War Room", Persona.HOME_ONE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLocationDarkSideGameText("Force drain -1 here. When you initiate a battle here, add one battle destiny.");
        setLocationLightSideGameText("While you control with a Rebel here, Home One is deploy -5 and power +3.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DEATH_STAR_II, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnDarkSideOfLocation));
        return modifiers;
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
        Condition controlWithRebel = new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.Rebel);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.Home_One, controlWithRebel, -5));
        modifiers.add(new PowerModifier(self, Filters.Home_One, controlWithRebel, 3));
        return modifiers;
    }
}