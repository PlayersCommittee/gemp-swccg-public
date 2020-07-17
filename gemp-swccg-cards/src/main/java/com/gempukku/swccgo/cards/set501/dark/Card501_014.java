package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Executor: Control Station (v)
 */
public class Card501_014 extends AbstractUniqueStarshipSite {
    public Card501_014() {
        super(Side.DARK, Title.Control_Station, Persona.EXECUTOR);
        setLocationDarkSideGameText("While your Imperial here, [Dagobah] Executor is deploy - 7 (except to Fondor).");
        setLocationLightSideGameText("Force drain -1 here. If you initiate battle here, add one battle destiny.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
        setTestingText("Executor: Control Station (v)");
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.icon(Icon.DAGOBAH), Filters.Executor), new HereCondition(self, Filters.Imperial), -7, Filters.not(Filters.Fondor)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
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