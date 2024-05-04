package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Mindful Of The Future (V)
 */
public class Card204_021 extends AbstractUsedOrLostInterrupt {
    public Card204_021() {
        super(Side.LIGHT, 5, "Mindful Of The Future", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("'But not at the expense of the moment.'");
        setGameText("USED: If Maul just swung a lightsaber during battle, that lightsaber may not target again this battle. LOST: If [Episode I] Obi-wan or non-[Permanent Weapon] Qui-Gon is in battle with a Dark Jedi, add one battle destiny. OR Cancel Maul Strikes or The Phantom Menace.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.weaponJustFiredBy(game, effectResult, Filters.lightsaber, Filters.Maul)) {
            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;
            final PhysicalCard lightsaber = weaponFiredResult.getWeaponCardFired() != null ? weaponFiredResult.getWeaponCardFired() : weaponFiredResult.getPermanentWeaponFired().getPhysicalCard(game);
            if (GameConditions.canTarget(game, self, lightsaber)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Prevent " + GameUtils.getCardLink(lightsaber) + " from targeting again");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose lightsaber", lightsaber) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Prevent " + GameUtils.getCardLink(targetedCard) + " from targeting again this battle",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new MayNotBeFiredModifier(self, Filters.or(targetedCard, Filters.permanentWeaponOf(targetedCard))),
                                                                "Prevents " + GameUtils.getCardLink(targetedCard) + " from targeting again this battle"));
                                            }
                                        }
                                );
                            }
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.and(Filters.ObiWan, Icon.EPISODE_I), Filters.and(Filters.QuiGon, Filters.not(Icon.PERMANENT_WEAPON))))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Dark_Jedi)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add add battle destiny");
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
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Maul_Strikes)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Maul_Strikes, Title.Maul_Strikes);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.The_Phantom_Menace)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.The_Phantom_Menace, Title.The_Phantom_Menace);
            actions.add(action);
        }
        
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Maul_Strikes, Filters.The_Phantom_Menace))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}