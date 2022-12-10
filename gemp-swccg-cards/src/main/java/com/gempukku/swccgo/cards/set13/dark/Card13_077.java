package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Location
 * Subtype: Site
 * Title: Naboo: Theed Palace Generator Core
 */
public class Card13_077 extends AbstractSite {
    public Card13_077() {
        super(Side.DARK, Title.Theed_Palace_Generator_Core, Title.Naboo, Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLocationDarkSideGameText("Any characters of ability < 5 'hit' here (and all cards on them) are placed in owner's Used Pile.");
        setLocationLightSideGameText("Any characters of ability < 5 'hit' here (and all cards on them) are placed in owner's Used Pile.");
        addIcon(Icon.DARK_FORCE, 3);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.THEED_PALACE_SITE);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeHit(game, effectResult, Filters.and(Filters.character, Filters.abilityLessThan(5), Filters.here(self)))) {
            final PhysicalCard card = ((AboutToBeHitResult) effectResult).getCardToBeHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(card) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(card) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(card);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, card, false, Zone.USED_PILE));
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
        if (TriggerConditions.isAboutToBeHit(game, effectResult, Filters.and(Filters.character, Filters.abilityLessThan(5), Filters.here(self)))) {
            final PhysicalCard card = ((AboutToBeHitResult) effectResult).getCardToBeHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(card) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(card) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(card);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, card, false, Zone.USED_PILE));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}