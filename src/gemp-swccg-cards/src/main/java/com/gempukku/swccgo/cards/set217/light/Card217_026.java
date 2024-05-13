package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 17
 * Type: Character
 * Subtype: Republic
 * Title: Admiral Kilian
 */
public class Card217_026 extends AbstractRepublic {
    public Card217_026() {
        super(Side.LIGHT, 2, 3, 2, 3, 5, "Admiral Kilian", Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setLore("Corellian leader.");
        setGameText("[Pilot] 2. Unless with Boba Fett, your total power is +2 here. While piloting a capital starship, it is immune to attrition < 4 and, once per game, if it is about to be lost, may relocate your characters aboard to a related exterior site.");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_17);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), new NotCondition(new WithCondition(self, Filters.Boba_Fett)), 2, self.getOwner()));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.capital_starship, Filters.hasPiloting(self)), 4));
        return modifiers;
    }

    @Override
    public List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.ADMIRAL_KILIAN__EVACUATE_CHARACTERS;

        final Filter aboardFilter = Filters.and(Filters.your(self), Filters.character, Filters.aboard(Filters.hasPiloting(self)));
        final Filter destinationFilter = Filters.and(Filters.relatedSite(self), Filters.exterior_site);

        // Check condition(s)
        if ((TriggerConditions.isAboutToBeLost(game, effectResult, Filters.and(Filters.capital_starship, Filters.hasPiloting(self)))
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.and(Filters.capital_starship, Filters.hasPiloting(self))))
                && GameConditions.canSpot(game, self, destinationFilter)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            Collection<PhysicalCard> possibleSites = new HashSet<>();
            for (PhysicalCard card : Filters.filterActive(game, self, aboardFilter)) {
                possibleSites.addAll(Filters.filterTopLocationsOnTable(game, Filters.and(destinationFilter, Filters.locationCanBeRelocatedTo(card, 0))));
            }

            if (!possibleSites.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Relocate characters");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose site where characters will be relocated", Filters.in(possibleSites)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard destination = action.getPrimaryTargetCard(targetGroupId);
                                Collection<PhysicalCard> toRelocate = Filters.filterActive(game, self, Filters.and(aboardFilter, Filters.canBeRelocatedToLocation(destination, 0)));
                                action.appendEffect(new RelocateBetweenLocationsEffect(action, toRelocate, destination));
                            }
                        });
                    }
                });

                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
