package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DeathStarPowerShutDownCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Central Core (Light)
 */
public class Card215_006 extends AbstractSite {
    public Card215_006() {
        super(Side.LIGHT, Title.Death_Star_Central_Core, Title.Death_Star);
        setLocationDarkSideGameText("If you occupy, opponent must first use 1 Force to move a character from here.");
        setLocationLightSideGameText("If A Power Loss 'shut down' this game, Force drain +1 here and Death Star Tractor Beam lost.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ForceDrainModifier(self, new DeathStarPowerShutDownCondition(), 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new MoveCostFromLocationModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.character), new OccupiesCondition(playerOnDarkSideOfLocation, self), 1, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(final String playerOnLightSideOfLocation, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isDeathStarPowerShutDown(game)
                && GameConditions.canSpot(game, self, Filters.Death_Star_Tractor_Beam)) {
            PhysicalCard deathStarTractorBeam = Filters.findFirstActive(game, self, Filters.Death_Star_Tractor_Beam);
            if (deathStarTractorBeam != null) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make " + GameUtils.getCardLink(deathStarTractorBeam) + " lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(deathStarTractorBeam) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, deathStarTractorBeam));
                actions.add(action);
            }
        }
        return actions;
    }
}
