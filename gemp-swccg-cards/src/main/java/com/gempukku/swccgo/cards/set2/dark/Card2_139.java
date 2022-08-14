package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Sniper
 */
public class Card2_139 extends AbstractLostInterrupt {
    public Card2_139() {
        super(Side.DARK, 3, Title.Sniper, Uniqueness.UNIQUE);
        setLore("Tusken Raiders often attack lone desert travelers at long range. Their cowardly nature leads them to rely on surprise attacks rather than direct engagement.");
        setGameText("During your control phase, fire one of your weapons. If URoRRuR'R'R firing, may add 2 to each weapon destiny draw. (A seeker may be targeted by a character weapon using defense value of 4.) Any 'hit' targets are immediately lost.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final int gameTextSourceCardId = self.getCardId();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.weapon_or_character_with_permanent_weapon, Filters.canBeFiredAt(self, Filters.canBeTargetedBy(self), 0, Filters.seeker));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Fire a weapon");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                // Allow response(s)
                                action.allowResponses("Fire " + GameUtils.getCardLink(weapon),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final PhysicalCard finalWeapon = action.getPrimaryTargetCard(targetGroupId);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new FireWeaponEffect(action, finalWeapon, false, Filters.seeker, 4, Filters.canBeTargetedBy(self)) {
                                                            @Override
                                                            protected List<ActionProxy> getWeaponFiringActionProxies(String playerId2, SwccgGame game, final PhysicalCard weapon) {
                                                                ActionProxy actionProxy = new AbstractActionProxy() {
                                                                    @Override
                                                                    public List<TriggerAction> getOptionalAfterTriggers(String playerId3, SwccgGame game, EffectResult effectResult) {
                                                                        List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                        // Check condition(s)
                                                                        if (playerId.equals(playerId3)
                                                                                && TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId, weapon, Filters.URoRRuRRR)) {

                                                                            final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                                                                            action1.setText("Add 2 to weapon destiny");
                                                                            // Perform result(s)
                                                                            action1.appendEffect(
                                                                                    new ModifyDestinyEffect(action1, 2));
                                                                            actions.add(action1);
                                                                        }
                                                                        return actions;
                                                                    }
                                                                };
                                                                return Collections.singletonList(actionProxy);
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