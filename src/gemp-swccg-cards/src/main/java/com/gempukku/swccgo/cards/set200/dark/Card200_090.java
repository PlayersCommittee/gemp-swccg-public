package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotUseCardToTransportToOrFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Droid
 * Title: DRK-1 (Dark Eye Probe Droid)
 */
public class Card200_090 extends AbstractDroid {
    public Card200_090() {
        super(Side.DARK, 3, 1, 1, 2, "DRK-1 (Dark Eye Probe Droid)", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setManeuver(3);
        setGameText("Nabrun Leids may not transport to or from here. During your move phase, may use 2 Force to relocate a Dark Jedi (with any captives they are escorting), your Mara, or an Inquisitor from here to same site as a Dark Jedi or Jedi; place this droid in Used Pile.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_0);
        addModelTypes(ModelType.PROBE, ModelType.RECON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotUseCardToTransportToOrFromLocationModifier(self, Filters.Nabrun_Leids, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)) {
            final Filter sameSiteAsDarkJediOrJedi = Filters.sameSiteAs(self, Filters.or(Filters.Dark_Jedi, Filters.Jedi));
            Filter darkJediFilter = Filters.and(Filters.Dark_Jedi, Filters.here(self), Filters.canBeRelocatedToLocation(sameSiteAsDarkJediOrJedi, false, true, false, 2, false));
            Filter maraOrInquisitorFilter = Filters.and(Filters.or(Filters.and(Filters.your(playerId), Filters.Mara_Jade), Filters.inquisitor), Filters.here(self), Filters.canBeRelocatedToLocation(sameSiteAsDarkJediOrJedi, 2));
            Filter characterFilter = Filters.or(darkJediFilter, maraOrInquisitorFilter);
            if (GameConditions.canSpot(game, self, characterFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate character to another site");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard characterTargeted) {
                                Filter siteToRelocateTo = Filters.and(sameSiteAsDarkJediOrJedi, Filters.locationCanBeRelocatedTo(characterTargeted, false, true, false, 2, false));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterTargeted) + " to", siteToRelocateTo) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.addAnimationGroup(characterTargeted);
                                                action.addAnimationGroup(siteSelected);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, characterTargeted, siteSelected, 2));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(characterTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, characterTargeted, siteSelected));
                                                                action.appendEffect(
                                                                        new PlaceCardInUsedPileFromTableEffect(action, self));
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
