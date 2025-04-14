package com.gempukku.swccgo.cards.set225.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * Set: Set 25
 * Type: Effect
 * Title: Stranded
 */
public class Card225_006 extends AbstractNormalEffect {
    public Card225_006() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Stranded", Uniqueness.UNRESTRICTED, ExpansionSet.SET_25, Rarity.V);
        setLore("Imperial troopers use tactics to strand and cut off fugitives. Only daring and unpredictable actions gave Luke and Leia a chance to escape.");
        setGameText("Deploy on a character. Character may not be targeted by Clash Of Sabers and, if character is Luke or Leia, may not move except during opponent's move phase. Opponent may not cancel battle destiny draws here. Nabrun Leids is canceled. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_25);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.character;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Nabrun_Leids)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        final String bothPlayers = null; //null represents "both players" for the MayNotCancelBattleDestinyModifier
        
        Filter characterFilter = Filters.hasAttached(self);
        Filter hereFilter = Filters.here(self);
        Condition onLukeOrLeia = new AttachedCondition(self, Filters.or(Filters.Luke, Filters.Leia));
        Condition unlessOpponentsMovePhase = new UnlessCondition(new PhaseCondition(Phase.MOVE, opponent));

        modifiers.add(new MayNotBeTargetedByModifier(self, characterFilter, Filters.Clash_Of_Sabers));
        modifiers.add(new MayNotMoveModifier(self, characterFilter, new AndCondition(onLukeOrLeia, unlessOpponentsMovePhase)));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, hereFilter, bothPlayers, opponent));

        return modifiers;
    }
}
