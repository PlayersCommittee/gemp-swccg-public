package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeExcludedFromBattleResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Alien
 * Title: Snoova
 */
public class Card10_048 extends AbstractAlien {
    public Card10_048() {
        super(Side.DARK, 1, 5, 6, 2, 4, "Snoova", Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Perhaps the only true Wookiee bounty hunter in the galaxy. Even high ranking members of the Alliance fear him. Favors a vibro-ax for his personal weapon.");
        setGameText("Deploys -3 to same site as any smuggler or bounty. During your deploy phase, a Vibro-Ax may deploy for free on Snoova from Reserve Deck; reshuffle. When Snoova excludes a target with a Vibro-Ax, he may capture target instead.");
        addIcons(Icon.REFLECTIONS_II, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.WOOKIEE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -3, Filters.sameSiteAs(self, Filters.or(Filters.smuggler, Filters.any_bounty))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SNOOVA__DOWNLOAD_VIBRO_AX;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Vibro-Ax from Reserve Deck");
            action.setActionMsg("Deploy a Vibro-Ax on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Vibro_Ax, Filters.sameCardId(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeExcludedFromBattleByWeaponFiredBy(game, effectResult, Filters.character, Filters.Vibro_Ax, self)) {
            final AboutToBeExcludedFromBattleResult aboutToBeExcludedFromBattleResult = (AboutToBeExcludedFromBattleResult) effectResult;
            final PhysicalCard cardToBeExcluded = aboutToBeExcludedFromBattleResult.getCardToBeExcluded();
             if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, targetingReason, cardToBeExcluded)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Capture " + GameUtils.getFullName(cardToBeExcluded));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, targetingReason, cardToBeExcluded) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Capture " + GameUtils.getCardLink(cardTargeted),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard cardToCapture = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PreventEffectOnCardEffect(action, aboutToBeExcludedFromBattleResult.getPreventableCardEffect(), cardToBeExcluded, null));
                                                action.appendEffect(
                                                        new CaptureCharacterOnTableEffect(action, cardToCapture));
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
