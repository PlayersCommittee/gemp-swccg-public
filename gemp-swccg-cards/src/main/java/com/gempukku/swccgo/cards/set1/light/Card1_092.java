package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.PowerMultiplierModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Jedi Presence
 */
public class Card1_092 extends AbstractLostInterrupt {
    public Card1_092() {
        super(Side.LIGHT, 3, Title.Jedi_Presence, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Jedi Knights inspired allies on the battlefield through bravery and heroism. Obi-Wan's presence encouraged the Rebels and gave them a chance to escape the Death Star.");
        setGameText("If one of your Jedi is present during any battle, use 1 Force to cause all other Rebels there to battle at double power.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter jediFilter = Filters.and(Filters.your(self), Filters.Jedi, Filters.presentInBattle, Filters.inBattleWith(Filters.and(Filters.Rebel, Filters.canBeTargetedBy(self))));

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTarget(game, self, jediFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a Jedi", jediFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedJedi) {
                            action.addAnimationGroup(targetedJedi);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Double power of all other Rebels with " + GameUtils.getCardLink(targetedJedi),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalJedi = action.getPrimaryTargetCard(targetGroupId1);
                                            // Perform result(s)
                                            final Collection<PhysicalCard> rebels = Filters.filterActive(game, self, Filters.and(Filters.Rebel,
                                                    Filters.not(finalJedi), Filters.participatingInBattle, Filters.canBeTargetedBy(self)));
                                            if (!rebels.isEmpty()) {
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new PowerMultiplierModifier(self, Filters.in(rebels), 2),
                                                                "Doubles power of " + GameUtils.getAppendedNames(rebels)));
                                            }
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
}