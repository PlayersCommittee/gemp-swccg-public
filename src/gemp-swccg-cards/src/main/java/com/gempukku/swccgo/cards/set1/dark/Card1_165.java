package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Chief Bast
 */
public class Card1_165 extends AbstractImperial {
    public Card1_165() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Chief_Bast, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Aide to Grand Moff Tarkin. Rarely underestimates enemies. Learned cunning and patience hunting big game as a youth.");
        setGameText("Adds 2 to power of anything he pilots. Power +1 if at same site as Tarkin. If a battle was just initiated at a system where Bast is present with your non-droid character aboard a starship, may 'evacuate' (relocate) both to a related site.");
        addIcons(Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.Tarkin), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter relatedSitesFilter = Filters.and(Filters.relatedSite(self), Filters.locationCanBeRelocatedTo(self, 0));
        Filter otherCharacterFilter = Filters.and(Filters.your(self), Filters.non_droid_character, Filters.presentWith(self), Filters.canBeRelocatedToLocation(relatedSitesFilter, 0));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSystem(self))
                && GameConditions.isAboardAnyStarship(game, self)
                && GameConditions.canTarget(game, self, otherCharacterFilter)
                && GameConditions.canUseForceToRelocateCard(game, self, 0, relatedSitesFilter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("'Evacuate' to related site");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose site to 'evacuate' to", relatedSitesFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard siteToRelocateTo) {
                            Filter otherCharacterFilter2 = Filters.and(Filters.your(self), Filters.non_droid_character, Filters.presentWith(self), Filters.canBeRelocatedToLocation(siteToRelocateTo, 0));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose character to 'evacuate' with", otherCharacterFilter2) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId, final PhysicalCard characterTargeted) {
                                            final List<PhysicalCard> cardsToRelocate = Arrays.asList(self, characterTargeted);
                                            action.addAnimationGroup(cardsToRelocate);
                                            action.addAnimationGroup(siteToRelocateTo);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, cardsToRelocate, siteToRelocateTo, 0));
                                            // Allow response(s)
                                            action.allowResponses("'Evacuate' " + GameUtils.getCardLink(self) + " and " + GameUtils.getCardLink(characterTargeted) + " to " + GameUtils.getCardLink(siteToRelocateTo),
                                                    new UnrespondableEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new RelocateBetweenLocationsEffect(action, cardsToRelocate, siteToRelocateTo));
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
