package com.gempukku.swccgo.cards.set102.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfCardPlayedModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.RetargetHighestAbilityCharacterForSenseAlterIfNeededEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
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
 * Title: For Luck
 */
public class Card102_001 extends AbstractNormalEffect {
    public Card102_001() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "For Luck", Uniqueness.UNIQUE, ExpansionSet.JEDI_PACK, Rarity.PM);
        setLore("Before swinging across the treacherous abyss, Leia gave Luke a kiss for luck. Despite an incessant storm of laserblasts, they made it.");
        setGameText("Deploy on table. If Counter Assault is played, may use 1 Force to add one destiny to your total. If Sense or Alter just played, may use X Force to exclude X Dark Jedi from being the 'highest-ability character.'");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Counter_Assault)
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
                            new ModifyGameTextModifier(self, Filters.samePermanentCardId(interruptCard), ModifyGameTextType.COUNTER_ASSAULT__ADD_DESTINY_TO_TOTAL),
                            "Adds one destiny to total"));
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.or(Filters.Sense, Filters.Alter), Filters.Dark_Jedi)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final RespondablePlayingCardEffect playingCardEffect = (RespondablePlayingCardEffect) effect;
            final PhysicalCard cardPlayed = playingCardEffect.getCard();
            int numDarkJedi = Filters.countActive(game, self, Filters.Dark_Jedi);
            int forceAvailableToUse = GameConditions.forceAvailableToUse(game, playerId);
            final int maxForceToUse = Math.min(numDarkJedi, forceAvailableToUse);
            if (maxForceToUse > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Exclude Dark Jedi from being 'highest-ability character'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsOnTableEffect(action, playerId, "Target Dark Jedi to exclude from being 'highest-ability character'", 1, maxForceToUse, Filters.Dark_Jedi) {
                            @Override
                            protected void cardsTargeted(int targetGroupId, final Collection<PhysicalCard> darkJedi) {
                                action.addAnimationGroup(darkJedi);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, darkJedi.size()));
                                // Allow response(s)
                                action.allowResponses("Exclude " + GameUtils.getAppendedNames(darkJedi) + " from being 'highest-ability character'",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfCardPlayedModifierEffect(action, cardPlayed,
                                                                new ExcludedFromBeingTheHighestAbilityCharacterModifier(self, Filters.in(darkJedi), Filters.samePermanentCardId(cardPlayed)),
                                                                "Excludes " + GameUtils.getAppendedNames(darkJedi) + " from being 'highest-ability character'"));
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