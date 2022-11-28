package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Dagobah: Swamp
 */
public class Card4_087 extends AbstractSite {
    public Card4_087() {
        super(Side.LIGHT, Title.Dagobah_Swamp, Title.Dagobah, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.U);
        setLocationDarkSideGameText("At end of your turn, your starships and vehicles here 'sink' to bottom of Lost Pile.");
        setLocationLightSideGameText("At end of your turn, your starships and vehicles here 'sink' to Used Pile.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.SWAMP);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(final String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter filter = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.or(Filters.starship, Filters.vehicle), Filters.here(self));

        // Check condition(s)
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerOnDarkSideOfLocation)) {
            final Collection<PhysicalCard> cardsToSink = Filters.filterActive(game, self, filter);
            if (!cardsToSink.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make starships and vehicles 'sink'");
                action.setActionMsg("Make starships and vehicles at "+ GameUtils.getCardLink(self) + " 'sink' to bottom of Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, cardsToSink, false, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(final String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter filter = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.starship, Filters.vehicle), Filters.here(self));

        // Check condition(s)
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerOnLightSideOfLocation)) {
            final Collection<PhysicalCard> cardsToSink = Filters.filterActive(game, self, filter);
            if (!cardsToSink.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make starships and vehicles 'sink'");
                action.setActionMsg("Make starships and vehicles at "+ GameUtils.getCardLink(self) + " 'sink' to Used Pile");
                // Perform result(s)
                action.appendEffect(
                        new PlaceCardsInUsedPileFromTableEffect(action, cardsToSink));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}