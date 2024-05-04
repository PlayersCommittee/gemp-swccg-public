package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Attack Pattern Delta
 */
public class Card3_040 extends AbstractLostInterrupt {
    public Card3_040() {
        super(Side.LIGHT, 3, "Attack Pattern Delta", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("Snowspeeder attack plan devised by Commander Skywalker and Rebel tactician Beryl Chifonage. Single-file formation protects the squadron as the leader draws fire.");
        setGameText("If a battle was just initiated at a site, identify your lead T-47 there. For remainder of this turn, all other T-47s at same site are power +1, are immune to attrition and cannot be targeted by weapons.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.your(self), Filters.T_47, Filters.piloted, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {
            final Collection<PhysicalCard> yourT47s = Filters.filterActive(game, self, filter);
            if (!yourT47s.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add power and immunity to T-47s");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose lead T-47", Filters.in(yourT47s)) {
                            @Override
                            protected void cardSelected(final PhysicalCard leadT47) {
                                action.addAnimationGroup(leadT47);

                                // Allow response(s)
                                action.allowResponses("Make T-47s other than " + GameUtils.getCardLink(leadT47) + " power +1, immune to attrition, and unable to be targeted by weapons",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final Collection<PhysicalCard> otherT47s = Filters.filter(yourT47s, game, Filters.not(leadT47));
                                                if (!otherT47s.isEmpty()) {

                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new AddUntilEndOfTurnModifierEffect(action,
                                                                    new PowerModifier(self, Filters.in(otherT47s), 1),
                                                                    "Makes " + GameUtils.getAppendedNames(otherT47s) + " power +1"));
                                                    action.appendEffect(
                                                            new AddUntilEndOfTurnModifierEffect(action,
                                                                    new ImmuneToAttritionModifier(self, Filters.in(otherT47s)),
                                                                    "Makes " + GameUtils.getAppendedNames(otherT47s) + " immune to attrition"));
                                                    action.appendEffect(
                                                            new AddUntilEndOfTurnModifierEffect(action,
                                                                    new MayNotBeTargetedByWeaponsModifier(self, Filters.in(otherT47s)),
                                                                    "Makes " + GameUtils.getAppendedNames(otherT47s) + " unable to be targeted by weapons"));

                                                }
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