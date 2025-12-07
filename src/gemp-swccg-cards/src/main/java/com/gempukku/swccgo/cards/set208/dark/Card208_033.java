package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Dopheld Mitaka
 */
public class Card208_033 extends AbstractFirstOrder {
    public Card208_033() {
        super(Side.DARK, 2, 2, 2, 2, 5, "Lieutenant Dopheld Mitaka", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setGameText("[Pilot] 2. Deploys -2 to (and forfeit -2 at) same location as Kylo. During your deploy phase, may relocate Kylo from here to same site as a Resistance Agent.");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.sameLocationAs(self, Filters.Kylo)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, new WithCondition(self, Filters.Kylo), -2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)) {
            final Filter sameSiteAsResistanceAgent = Filters.sameSiteAs(self, Filters.Resistance_Agent);
            Filter kyloFilter = Filters.and(Filters.Kylo, Filters.here(self), Filters.canBeRelocatedToLocation(sameSiteAsResistanceAgent, true, 0));
            if (GameConditions.canSpot(game, self, kyloFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate Kylo to another site");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Kylo", kyloFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard kyloTargeted) {
                                Filter siteToRelocateKylo = Filters.and(sameSiteAsResistanceAgent, Filters.locationCanBeRelocatedTo(kyloTargeted, true, 0));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(kyloTargeted) + " to", siteToRelocateKylo) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.addAnimationGroup(kyloTargeted);
                                                action.addAnimationGroup(siteSelected);
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(kyloTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, kyloTargeted, siteSelected));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
