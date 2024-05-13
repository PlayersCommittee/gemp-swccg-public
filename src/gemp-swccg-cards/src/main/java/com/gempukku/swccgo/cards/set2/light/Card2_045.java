package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBattleModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Blast The Door, Kid!
 */
public class Card2_045 extends AbstractLostInterrupt {
    public Card2_045() {
        super(Side.LIGHT, 4, Title.Blast_The_Door_Kid, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("When Vader turned his attention to the escaping Rebels, Han immediately offered Luke some sage advice.");
        setGameText("If a battle was just initiated at an interior site, use 1 Force to exclude from that battle all characters of ability > 2 and all leaders (on both sides).");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.or(Filters.and(Filters.character, Filters.abilityMoreThan(2)), Filters.leader),
                Filters.participatingInBattle, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.interior_site)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final Collection<PhysicalCard> charactersToExclude = Filters.filterActive(game, self, filter);
            if (!charactersToExclude.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Exclude characters from battle");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                // Allow response(s)
                action.allowResponses("Exclude " + GameUtils.getAppendedNames(charactersToExclude) + " from battle",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ExcludeFromBattleEffect(action, charactersToExclude));
                                action.appendEffect(
                                        new AddUntilEndOfBattleModifierEffect(action,
                                                new ExcludedFromBattleModifier(self, filter), null));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}