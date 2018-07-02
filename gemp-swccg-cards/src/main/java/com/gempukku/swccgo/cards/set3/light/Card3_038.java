package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Tauntaun Bones
 */
public class Card3_038 extends AbstractNormalEffect {
    public Card3_038() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Tauntaun Bones", Uniqueness.UNIQUE);
        setLore("The bones of a tauntaun represent both death and life: death for the prey, life for the predator. A symbol of the ebb and flow of the Force through all things.");
        setGameText("Use 1 Force to deploy on table. Each opponent's destiny draw, you do the following: (0) Put top used card in hand. (1) lose 1 Force. (2) activate 1 Force. (3) lose this Effect. (4) retrieve top lost card. (5) deploy a character for free. (6+) nothing.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {
            if (GameConditions.isDestinyValueEqualTo(game, 0)) {
                // Put top used card in hand.
                if (GameConditions.hasUsedPile(game, playerId)) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    // Allow response(s)
                    action.allowResponses("Make " + playerId + " draw top card of Used Pile into hand",
                            new RespondableEffect(action) {
                                @Override
                                protected void performActionResults(final Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new DrawCardIntoHandFromUsedPileEffect(action, playerId));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
            else if (GameConditions.isDestinyValueEqualTo(game, 1)) {
                // Lose 1 Force

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Allow response(s)
                action.allowResponses("Make " + playerId + " lose 1 Force",
                        new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(final Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new LoseForceEffect(action, playerId, 1));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
            else if (GameConditions.isDestinyValueEqualTo(game, 2)) {
                // Activate 1 Force
                if (GameConditions.canActivateForce(game, playerId)) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    // Allow response(s)
                    action.allowResponses("Make " + playerId + " activate 1 Force",
                            new RespondableEffect(action) {
                                @Override
                                protected void performActionResults(final Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new ActivateForceEffect(action, playerId, 1));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
            else if (GameConditions.isDestinyValueEqualTo(game, 3)) {
                // Lose this Effect

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Allow response(s)
                action.allowResponses("Make " + GameUtils.getCardLink(self) + " lost",
                        new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(final Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, self));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
            else if (GameConditions.isDestinyValueEqualTo(game, 4)) {
                // Retrieve top lost card

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Allow response(s)
                action.allowResponses("Make " + playerId + " retrieve 1 Force",
                        new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(final Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, 1));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
            else if (GameConditions.isDestinyValueEqualTo(game, 5)) {
                // Deploy a character for free
                if (GameConditions.hasHand(game, playerId)) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    // Allow response(s)
                    action.allowResponses("Make " + playerId + " deploy a character for free",
                            new RespondableEffect(action) {
                                @Override
                                protected void performActionResults(final Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new DeployCardFromHandEffect(action, playerId, Filters.character, true));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}