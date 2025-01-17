package com.gempukku.swccgo.cards.set224.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsLandedToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Starship
 * Subtype: Starfighter
 * Title: Starspeeder 1000
 */
public class Card224_007 extends AbstractStarfighter {
    public Card224_007() {
        super(Side.DARK, 4, 2, 2, null, 3, 6, 4, "StarSpeeder 1000", Uniqueness.UNRESTRICTED, ExpansionSet.SET_24, Rarity.V);
        setGameText("May add 6 passengers. Has ship-docking capability. Permanent pilot provides no ability. May deploy to exterior sites. Once per game, during your move phase, may use 2 Force to relocate (even if landed).");
        addIcons(Icon.EPISODE_VII, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_24);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        addModelType(ModelType.TRANSPORT);
        setPassengerCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsLandedToLocationModifier(self, Filters.exterior_site));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.STARSPEEDER_1000__RELOCATE;

        Filter validLocationFilter = Filters.locationCanBeRelocatedTo(self, 2);

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canUseForceToRelocateCard(game, self, 2, validLocationFilter)
                && Filters.pilotedForTakeOff.accepts(game, self)
        ) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate");
            action.setActionMsg("Relocate");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose location to relocate to", validLocationFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard locationSelected) {
                            action.addAnimationGroup(self);
                            action.addAnimationGroup(locationSelected);
                            // Pay cost(s)
                            action.appendCost(
                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, self, locationSelected, 2));
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(locationSelected),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, self, locationSelected, false));
                                        }
                                    });
                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }
}
