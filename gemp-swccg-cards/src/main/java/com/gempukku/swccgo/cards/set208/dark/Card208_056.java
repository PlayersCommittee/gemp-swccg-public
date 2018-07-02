package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.ImmuneToCardTitleWithOwnerModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeploySitesBetweenSitesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Mos Espa (V)
 */
public class Card208_056 extends AbstractSite {
    public Card208_056() {
        super(Side.DARK, Title.Mos_Espa, Title.Tatooine);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Your characters here and at Watto's Junkyard are immune to opponent's Sandwhirl.");
        setLocationLightSideGameText("For remainder of game, sites may not deploy between this site and Watto's Junkyard.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToCardTitleWithOwnerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.character, Filters.or(Filters.here(self), Filters.at(Filters.Wattos_Junkyard))), Title.Sandwhirl, game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (!GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {

            self.setForRemainderOfGameData(self.getCardId(), new ForRemainderOfGameData());
            // Add modifier here without creating an action
            game.getModifiersEnvironment().addUntilEndOfGameModifier(
                    new MayNotDeploySitesBetweenSitesModifier(self, Filters.sameLocationId(self), Filters.Wattos_Junkyard));
        }
        return null;
    }
}