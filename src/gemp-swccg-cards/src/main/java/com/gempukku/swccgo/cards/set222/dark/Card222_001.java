package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: A Fine Addition to my Collection
 */
public class Card222_001 extends AbstractLostInterrupt {
    public Card222_001() {
        super(Side.DARK, 5, "A Fine Addition to my Collection", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setLore("");
        setGameText("If Grievous just swung a lightsaber, add one battle destiny. " +
                "OR Cancel an attempt to target Grievous with a lightsaber. " +
                "OR Deploy any lightsaber from your Lost Pile on Grievous (he may use it until he is no longer carrying it).");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.A_FINE_ADDITION_TO_MY_COLLECTION__DEPLOY_SABER_FROM_LOST_PILE;

        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Grievous)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Deploy lightsaber from Lost Pile");
            action.appendTargeting(
                    new ChooseCardFromLostPileEffect(action, playerId, Filters.lightsaber) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            // Allow response(s)
                            action.allowResponses("Deploy a lightsaber on Grievous from Lost Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfGameModifierEffect(action, new MayDeployToTargetModifier(self, selectedCard, Filters.Grievous), "")
                                            );
                                            action.appendEffect(
                                                    new AddUntilEndOfGameModifierEffect(action, new MayUseWeaponModifier(self, Filters.Grievous, selectedCard), "Grievous may use " + GameUtils.getCardLink(selectedCard))
                                            );
                                            action.appendEffect(
                                                    new DeployCardToTargetFromLostPileEffect(action, selectedCard, Filters.Grievous, false, false)
                                            );
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.weaponJustFiredBy(game, effectResult, Filters.lightsaber, Filters.Grievous)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.Grievous, Filters.lightsaber)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel lightsaber targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }
}
