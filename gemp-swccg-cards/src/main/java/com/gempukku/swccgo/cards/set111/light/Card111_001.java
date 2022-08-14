package com.gempukku.swccgo.cards.set111.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardCombinationIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Third Anthology)
 * Type: Effect
 * Title: A New Secret Base
 */
public class Card111_001 extends AbstractNormalEffect {
    public Card111_001() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "A New Secret Base", Uniqueness.UNIQUE);
        setLore("Due to constant Imperial pursuit, Alliance engineers became adept at constructing new facilities quickly.");
        setGameText("Deploy on 1st marker. Yavin 4 locations do not count for Imperial Decree. S-foils and your 'insert' cards are canceled. Once during each of your turns, may take up to two sites (or one site and one Effect) with 'Echo' in title or one planet system with two [Light Side Force] icons into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.First_Marker;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Imperial_Decree, ModifyGameTextType.IMPERIAL_DECREE__DOES_NOT_COUNT_YAVIN_4_LOCATIONS));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if ((TriggerConditions.isPlayingCard(game, effect, Filters.S_foils)
                || TriggerConditions.isPlayingCardAsInsertCard(game, effect, Filters.your(self)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.S_foils)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.S_foils, Title.S_foils);
                actions.add(action);
            }
        }
        // Check condition(s)
        if (TriggerConditions.justRevealedInsertCard(game, effectResult, Filters.your(self))) {
            if (GameConditions.canCancelRevealedInsertCard(game, self, effectResult)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelRevealedInsertCardAction(action, effectResult);
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.A_NEW_SECRET_BASE__UPLOAD_ECHO_CARDS_OR_PLANET_SYSTEM;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final Filter siteWithEchoInTitle = Filters.and(Filters.site, Filters.titleContains("Echo"));
            final Filter effectWithEchoInTitle = Filters.and(Filters.Effect, Filters.titleContains("Echo"));
            final Filter planetSystemWithTwoLightIcons = Filters.and(Filters.planet_system, Filters.iconCount(Icon.LIGHT_FORCE, 2));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take cards into hand from Reserve Deck");
            action.setActionMsg("Take up to two sites (or one site and one Effect) with 'Echo' in title or one planet system with two [Light Side Force] icons into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardCombinationIntoHandFromReserveDeckEffect(action, playerId, true) {
                        @Override
                            public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                            return "Choose up to two sites (or one site and one Effect) with 'Echo' in title or one planet system with two [Light Side Force] icons";
                        }

                        @Override
                        public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                            Filter filter = Filters.none;
                            if (cardsSelected.isEmpty()) {
                                filter = Filters.or(siteWithEchoInTitle, effectWithEchoInTitle, planetSystemWithTwoLightIcons, filter);
                            }
                            else if (cardsSelected.size() == 1) {
                                if (!Filters.filterCount(cardsSelected, game, 1, siteWithEchoInTitle).isEmpty()) {
                                    filter = Filters.or(siteWithEchoInTitle, effectWithEchoInTitle, filter);
                                }
                                else if (!Filters.filterCount(cardsSelected, game, 1, effectWithEchoInTitle).isEmpty()) {
                                    filter = Filters.or(siteWithEchoInTitle, filter);
                                }
                            }
                            return filter;
                        }

                        @Override
                        public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                            if (cardsSelected.size() == 1) {
                                if (Filters.filter(cardsSelected, game, siteWithEchoInTitle).size() == 1) {
                                    return true;
                                }
                                if (Filters.filter(cardsSelected, game, effectWithEchoInTitle).size() == 1) {
                                    return true;
                                }
                                if (Filters.filter(cardsSelected, game, planetSystemWithTwoLightIcons).size() == 1) {
                                    return true;
                                }
                            }
                            else if (cardsSelected.size() == 2) {
                                if (Filters.filter(cardsSelected, game, siteWithEchoInTitle).size() == 2) {
                                    return true;
                                }
                                if (Filters.filter(cardsSelected, game, siteWithEchoInTitle).size() == 1
                                        && Filters.filter(cardsSelected, game, effectWithEchoInTitle).size() == 1) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}