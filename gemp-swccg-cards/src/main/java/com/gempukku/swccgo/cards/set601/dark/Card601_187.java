package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.DuringDuelWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 5
 * Type: Character
 * Subtype: Droid
 * Title: Sith Probe Droid (V)
 */
public class Card601_187 extends AbstractDroid {
    public Card601_187() {
        super(Side.DARK, 3, 1, 1, 2, Title.Sith_Probe_Droid);
        setVirtualSuffix(true);
        setManeuver(3);
        setLore("Patrol droids utilized by the Sith. Each droid has several multispectral imaging devices and a communications package. Used by Maul to track down Amidala.");
        setGameText("When drawn for destiny during a battle or duel involving a Dark Jedi, destiny +2. During your move phase, may use 2 Force to relocate a Dark Jedi (with any captives they are escorting) to same site; place this droid in Used Pile.");
        addIcons(Icon.TATOOINE, Icon.LEGACY_BLOCK_5, Icon.EPISODE_I);
        addModelTypes(ModelType.PROBE, ModelType.RECON);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, new OrCondition(new DuringBattleWithParticipantCondition(Filters.Dark_Jedi),
                new DuringDuelWithParticipantCondition(Filters.Dark_Jedi)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.isAtLocation(game, self, Filters.site)) {
            final PhysicalCard sameSite = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            Filter darkJediFilter = Filters.and(Filters.Dark_Jedi, Filters.canBeRelocatedToLocation(sameSite, false, true, false, 2, false));
            if (GameConditions.canTarget(game, self, darkJediFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Relocate Dark Jedi to same site");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi to relocate", darkJediFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.addAnimationGroup(sameSite);
                                // Pay cost(s)
                                action.appendCost(
                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, targetedCard, sameSite, 2));
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(sameSite),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, targetedCard, sameSite));
                                                action.appendEffect(
                                                        new PlaceCardInUsedPileFromTableEffect(action, self));
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
