package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPlayedThisGameCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Defensive Shield
 * Title: Failure At The Cave (V)
 */
public class Card213_013 extends AbstractDefensiveShield {
    public Card213_013() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Failure At The Cave", ExpansionSet.SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLore("'That place is strong with the dark side of the Force. A domain of evil it is. In you must go.'");
        setGameText("Plays on table. Stone Pile is canceled. While a Jedi Test on table, opponent may not reduce your Force drains. Unless opponent has deployed a battleground, Domain Of Evil may not modify destiny draws more than once per battle.");
        addIcons(Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.title(Title.Domain_Of_Evil),
                new NotCondition(new HasPlayedThisGameCondition(self, Filters.battleground, game.getOpponent(self.getOwner()))),
                ModifyGameTextType.DOMAIN_OF_EVIL__LIMIT_USES_PER_BATTLE));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, new OnTableCondition(self, Filters.Jedi_Test), game.getOpponent(self.getOwner()), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Stone_Pile)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Stone_Pile)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Stone_Pile), Title.Stone_Pile);
            actions.add(action);
        }

        return actions;
    }
}
