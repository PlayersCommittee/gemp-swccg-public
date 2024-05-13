package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Dagobah: Cave
 */
public class Card4_158 extends AbstractSite {
    public Card4_158() {
        super(Side.DARK, Title.Dagobah_Cave, Title.Dagobah, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLocationDarkSideGameText("If opponent has presence here, your Force Icons here are canceled.");
        setLocationLightSideGameText("Lose 1 Force during each of your control phases if Yoda on table but not on Dagobah.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.UNDERGROUND, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelForceIconsModifier(self, Filters.here(self), new OccupiesCondition(game.getOpponent(playerOnDarkSideOfLocation), self), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Yoda, Filters.not(Filters.on(Title.Dagobah))))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Lose 1 Force");
            action.setActionMsg("Make " + playerOnLightSideOfLocation + " lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, playerOnLightSideOfLocation, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerOnLightSideOfLocation)
                && GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Yoda, Filters.not(Filters.on(Title.Dagobah))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose 1 Force");
            action.setActionMsg("Make " + playerOnLightSideOfLocation + " lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, playerOnLightSideOfLocation, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}