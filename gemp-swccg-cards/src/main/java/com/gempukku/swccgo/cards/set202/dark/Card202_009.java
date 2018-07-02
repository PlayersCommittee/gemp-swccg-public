package com.gempukku.swccgo.cards.set202.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RetargetWeaponEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Character
 * Subtype: Alien
 * Title: Daroe (V)
 */
public class Card202_009 extends AbstractAlien {
    public Card202_009() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Daroe", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Jawa who has inside connections with the Empire. Frequently speaks with his Imperial contact, whom he reports to regarding Rebel activity in the Outer Rim.");
        setGameText("[Pilot] 2. Information Broker. When deployed, may [download] a droid here or a Retraining Bolt. During battle, may retarget an opponent's weapon targeting your droid to another one of your characters present (even Daroe).");
        addIcons(Icon.TATOOINE, Icon.PILOT, Icon.VIRTUAL_SET_2);
        setSpecies(Species.JAWA);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Keyword.INFORMATION_BROKER));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DAROE__DOWNLOAD_DROID_OR_RETRAINING_BOLT;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a droid or Retraining Bolt from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.droid, Filters.Restraining_Bolt), Filters.here(self), Filters.Restraining_Bolt, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.droid, Filters.present(self)), Filters.and(Filters.opponents(self), Filters.weapon))
                && GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            final Collection<PhysicalCard> originalTargets = weaponFiringState.getTargets();
            Filter retargetFilter = Filters.none;
            for (PhysicalCard originalTarget : originalTargets) {
                retargetFilter = Filters.or(retargetFilter, Filters.weaponMayRetargetTo(originalTarget));
            }
            final PhysicalCard weapon = weaponFiringState.getPermanentWeaponFiring() != null ? weaponFiringState.getCardFiringWeapon() : weaponFiringState.getCardFiring();
            Filter droidFilter = Filters.and(Filters.your(self), Filters.character, Filters.present(self), retargetFilter);
            if (GameConditions.canTarget(game, self, droidFilter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Re-target weapon to another character");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose target to re-target from", Filters.in(originalTargets)) {
                            @Override
                            protected void cardSelected(final PhysicalCard originalTarget) {
                                Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.present(self), Filters.weaponMayRetargetTo(originalTarget));
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose character to re-target to", filter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                                action.addAnimationGroup(targetedCard);
                                                // Allow response(s)
                                                action.allowResponses("Re-target " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(targetedCard),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RetargetWeaponEffect(action, originalTarget, finalTarget));
                                                            }
                                                        }
                                                );
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
