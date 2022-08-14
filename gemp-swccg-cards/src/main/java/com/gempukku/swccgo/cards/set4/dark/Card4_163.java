package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ApplyAbilityToDrawBattleDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Executor: Meditation Chamber
 */
public class Card4_163 extends AbstractUniqueStarshipSite {
    public Card4_163() {
        super(Side.DARK, Title.Meditation_Chamber, Persona.EXECUTOR);
        setLocationDarkSideGameText("If Vader alone here, once per turn, may apply his ability to draw battle destiny at any system.");
        setLocationLightSideGameText("If you occupy, opponent's Meditation Chamber game text is canceled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeyword(Keyword.EXECUTOR_SITE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.Vader, Filters.alone, Filters.here(self));

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.system)
                && GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId)) {
            PhysicalCard vader = Filters.findFirstActive(game, self, filter);
            if (vader != null) {
                final float abilityToApply = game.getModifiersQuerying().getAbilityForBattleDestiny(game.getGameState(), vader);
                if (abilityToApply > 0) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
                    action.setText("Apply Vader's ability for battle destiny");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new ApplyAbilityToDrawBattleDestinyEffect(action, playerOnDarkSideOfLocation, vader));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Meditation_Chamber,
                new OccupiesCondition(playerOnLightSideOfLocation, self), game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}