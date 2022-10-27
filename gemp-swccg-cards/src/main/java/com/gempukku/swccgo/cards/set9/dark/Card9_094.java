package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceAtLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Admiral's Order
 * Title: Fighters Coming In
 */
public class Card9_094 extends AbstractAdmiralsOrder {
    public Card9_094() {
        super(Side.DARK, "Fighters Coming In", ExpansionSet.DEATH_STAR_II, Rarity.R);
        setGameText("Unique (•) starfighters without permanent pilots are immune to attrition < 4 (or add 2 to immunity if starfighter already has immunity). Starships without pilot characters aboard are power -2. At docking bays related to systems you occupy, your Force drains are +1. Once per turn, your starfighter just lost from a system may be relocated to a related docking bay.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter uniqueStarfightersWithoutPermanentPilots = Filters.and(Filters.unique, Filters.starfighter, Filters.not(Filters.hasPermanentPilot));
        Filter alreadyHasImmunity = Filters.alreadyHasImmunityToAttrition(self);
        Filter starshipWithoutPilotCharacterAboard = Filters.and(Filters.starship, Filters.not(Filters.hasAboard(self, Filters.and(Filters.pilot, Filters.character))));
        Filter dockingBaysRelatedToSystemsYouOccupy = Filters.and(Filters.docking_bay, Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(uniqueStarfightersWithoutPermanentPilots, Filters.not(alreadyHasImmunity)), 4));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(uniqueStarfightersWithoutPermanentPilots, alreadyHasImmunity), 2));
        modifiers.add(new PowerModifier(self, starshipWithoutPilotCharacterAboard, -2));
        modifiers.add(new ForceDrainModifier(self, dockingBaysRelatedToSystemsYouOccupy, 1, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.starfighter), Filters.relatedSystemTo(self, Filters.docking_bay))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            PhysicalCard location = ((LostFromTableResult) effectResult).getFromLocation();
            Filter dockingBayFilter = Filters.and(Filters.docking_bay, Filters.relatedSite(location), Filters.locationCanBePlacedAt(justLostCard));
            if (GameConditions.canSpotLocation(game, dockingBayFilter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Relocate " + GameUtils.getFullName(justLostCard) + " to related docking bay");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose docking bay to relocate " + GameUtils.getCardLink(justLostCard), dockingBayFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                action.addAnimationGroup(selectedCard);
                                // Allow response(s)
                                action.allowResponses("Relocate just lost " + GameUtils.getCardLink(justLostCard) + " to " + GameUtils.getCardLink(selectedCard),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlaceAtLocationFromLostPileEffect(action, playerId, justLostCard, selectedCard, false, true));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
