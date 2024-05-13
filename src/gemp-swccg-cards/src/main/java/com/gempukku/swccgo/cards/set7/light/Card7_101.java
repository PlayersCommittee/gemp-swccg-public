package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Rebel Ambush
 */
public class Card7_101 extends AbstractUsedInterrupt {
    public Card7_101() {
        super(Side.LIGHT, 5, Title.Rebel_Ambush, Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Entrenched Rebel troops defend their ground with surprising fierceness.");
        setGameText("If a battle was just initiated at a site, each of your troopers present is power +1 (+2 if at a Rebel Base site) and immune to attrition for remainder of turn.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.your(self), Filters.trooper, Filters.presentInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {
            final int amount = GameConditions.isDuringBattleAt(game, Filters.Rebel_Base_site) ? 2 : 1;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add power and immunity to troopers");
            // Allow response(s)
            action.allowResponses("Make troopers present power +" + amount + " and immune to attrition",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> troopers = Filters.filterActive(game, self, filter);
                            if (!troopers.isEmpty()) {

                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new PowerModifier(self, Filters.in(troopers), amount),
                                                "Makes " + GameUtils.getAppendedNames(troopers) + " power +" + amount));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new ImmuneToAttritionModifier(self, Filters.in(troopers)),
                                                "Makes " + GameUtils.getAppendedNames(troopers) + " immune to attrition"));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}