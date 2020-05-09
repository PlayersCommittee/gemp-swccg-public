package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: J'Quille
 */
public class Card6_110 extends AbstractAlien {
    public Card6_110() {
        super(Side.DARK, 2, 5, 4, 1, 3, Title.Jquille, Uniqueness.UNIQUE);
        setLore("Whiphid spy in league with Lady Valarian. Yearns for battle. Enjoys hearing the screams of his victims. Plotting to kill Jabba.");
        setGameText("Power +2 on Hoth. At the start of a battle, may cause one opponent's character of ability < 3 present to be excluded from the battle. While at Audience Chamber, all your other Whipids are forfeit +2.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.WHIPHID);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OnCondition(self, Title.Hoth), 2));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Whiphid),
                new AtCondition(self, Filters.Audience_Chamber), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(3), Filters.present(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Exclude character from battle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Exclude " + GameUtils.getCardLink(targetedCard) + " from battle",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ExcludeFromBattleEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
