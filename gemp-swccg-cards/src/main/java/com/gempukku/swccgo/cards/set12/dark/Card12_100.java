package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyPoliticsUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Coruscant Guard
 */
public class Card12_100 extends AbstractRepublic {
    public Card12_100() {
        super(Side.DARK, 3, 2, 2, 1, 4, "Coruscant Guard", Uniqueness.UNRESTRICTED, ExpansionSet.CORUSCANT, Rarity.C);
        setLore("Coruscant Guards are an elite force whose assignments include the protection of important political figures, as well as the policing of Coruscant's higher profile city districts.");
        setGameText("Once per turn, may use 1 Force to add 2 to politics of one of your senators present for remainder of turn. While present at Galactic Senate, all characters without politics here are forfeit = 0 (except Coruscant Guards).");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.CORUSCANT_GUARD);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.your(self), Filters.senator, Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add 2 to senator's politics");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose senator", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Add " + 2 + " to " + GameUtils.getCardLink(targetedCard) + "'s politics",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyPoliticsUntilEndOfTurnEffect(action, targetedCard, 2,
                                                            "Adds " + 2 + " to " + GameUtils.getCardLink(targetedCard) + "'s politics"));
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
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetForfeitModifier(self, Filters.and(Filters.character_without_politics, Filters.here(self),
                Filters.except(Filters.Coruscant_Guard)), new PresentAtCondition(self, Filters.Galactic_Senate), 0));
        return modifiers;
    }
}
