package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromLocationToWeatherVane;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Chasm Walkway
 */
public class Card5_167 extends AbstractSite {
    public Card5_167() {
        super(Side.DARK, "Cloud City: Chasm Walkway", Title.Bespin);
        setLocationDarkSideGameText("If Weather Vane on table, characters 'hit' here are instead immediately relocated there.");
        setLocationLightSideGameText("If Weather Vane on table, characters 'hit' here are instead immediately relocated there.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeHit(game, effectResult, Filters.and(Filters.character, Filters.here(self)))
                && GameConditions.canSpot(game, self, Filters.Weather_Vane)) {
            final PhysicalCard card = ((AboutToBeHitResult) effectResult).getCardToBeHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Relocate " + GameUtils.getFullName(card) + " to Weather Vane");
            action.setActionMsg("Relocate " + GameUtils.getCardLink(card) + " to Weather Vane");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(card);
                            action.appendEffect(
                                    new RelocateFromLocationToWeatherVane(action, card));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeHit(game, effectResult, Filters.and(Filters.character, Filters.here(self)))
                && GameConditions.canSpot(game, self, Filters.Weather_Vane)) {
            PhysicalCard card = ((AboutToBeHitResult) effectResult).getCardToBeHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Relocate " + GameUtils.getFullName(card) + " to Weather Vane");
            action.setActionMsg("Relocate " + GameUtils.getCardLink(card) + " to Weather Vane");
            // Perform result(s)
            action.appendEffect(
                    new RelocateFromLocationToWeatherVane(action, card));
            return Collections.singletonList(action);
        }
        return null;
    }
}