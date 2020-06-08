package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInUsedPileFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Imperial
 * Title: Grand Admiral Thrawn
 */
public class Card10_040 extends AbstractImperial {
    public Card10_040() {
        super(Side.DARK, 1, 4, 2, 4, 7, Title.Thrawn, Uniqueness.UNIQUE);
        setLore("The last remaining Grand Admiral. Found legendary Katana fleet of missing dreadnaughts. Military genius. Master of unorthodox tactics. Passionate collector of art. Leader.");
        setGameText("Adds 3 to power of any starship he pilots. When piloting a star destroyer in battle, adds one battle destiny. While no other admiral and no star cruiser on table, your Admiral's Order just placed in Used Pile from table, may be taken into hand instead.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.THRAWN);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.starship));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PilotingCondition(self, Filters.Star_Destroyer),
                new InBattleCondition(self)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justPlacedInUsedPileFromTable(game, effectResult, Filters.and(Filters.your(self), Filters.Admirals_Order))
                && !GameConditions.canSpot(game, self, Filters.or(Filters.and(Filters.other(self), Filters.admiral), Filters.Star_Cruiser))) {
            PhysicalCard admiralsOrder = ((PutCardInUsedPileFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take " + GameUtils.getFullName(admiralsOrder) + " into hand");
            action.setActionMsg("Take " + GameUtils.getCardLink(admiralsOrder) + " into hand");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, admiralsOrder, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
