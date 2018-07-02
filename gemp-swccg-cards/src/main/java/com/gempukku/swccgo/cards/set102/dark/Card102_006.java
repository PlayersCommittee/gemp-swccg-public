package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBeingTheHighestAbilityCharacterModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jedi Pack)
 * Type: Effect
 * Title: Dark Forces
 */
public class Card102_006 extends AbstractNormalEffect {
    public Card102_006() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Dark_Forces, Uniqueness.UNIQUE);
        setLore("'The ability to destroy a planet is insignificant next to the power of the Force.'");
        setGameText("Deploy on your side of table. When Surprise Assault is played, may use 1 Force to add one destiny to your total. Also, when Sense or Alter is played, may use X Force to exclude X Jedi from being the 'highest-ability character.'");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Surprise_Assault)
                && GameConditions.canUseForce(game, playerId, 1)) {
            PhysicalCard interruptCard = ((RespondablePlayingCardEffect) effect).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add one destiny to total");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfCardPlayedModifierEffect(action, interruptCard,
                            new ModifyGameTextModifier(self, Filters.samePermanentCardId(interruptCard), ModifyGameTextType.SURPRISE_ASSAULT__ADD_DESTINY_TO_TOTAL),
                            "Adds one destiny to total"));
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.or(Filters.Sense, Filters.Alter), Filters.Jedi)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final RespondablePlayingCardEffect playingCardEffect = (RespondablePlayingCardEffect) effect;
            final PhysicalCard cardPlayed = playingCardEffect.getCard();
            int numJedi = Filters.countActive(game, self, Filters.Jedi);
            int forceAvailableToUse = GameConditions.forceAvailableToUse(game, playerId);
            final int maxForceToUse = Math.min(numJedi, forceAvailableToUse);
            if (maxForceToUse > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Exclude Jedi from being 'highest-ability character'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsOnTableEffect(action, playerId, "Target Jedi to exclude from being 'highest-ability character'", 1, maxForceToUse, Filters.Jedi) {
                            @Override
                            protected void cardsTargeted(int targetGroupId, final Collection<PhysicalCard> jedi) {
                                action.addAnimationGroup(jedi);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, jedi.size()));
                                // Allow response(s)
                                action.allowResponses("Exclude " + GameUtils.getAppendedNames(jedi) + " from being 'highest-ability character'",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfCardPlayedModifierEffect(action, cardPlayed,
                                                                new ExcludedFromBeingTheHighestAbilityCharacterModifier(self, Filters.in(jedi), Filters.samePermanentCardId(cardPlayed)),
                                                                "Excludes " + GameUtils.getAppendedNames(jedi) + " from being 'highest-ability character'"));
                                                action.appendEffect(
                                                        new RetargetHighestAbilityCharacterForSenseAlterIfNeededEffect(action, playingCardEffect));
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}