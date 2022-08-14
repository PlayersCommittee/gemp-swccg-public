package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AllAbilityAtLocationProvidedByCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 9
 * Type: Character
 * Subtype: Rebel
 * Title: General Airen Cracken
 */
public class Card601_224 extends AbstractRebel {
    public Card601_224() {
        super(Side.LIGHT, 2, 3, 3, 2, 4, "General Airen Cracken", Uniqueness.UNIQUE);
        setLore("Leader, scout, and information broker.");
        setGameText("[Pilot] 2. Opponent may not add destiny draws to power or attrition here. While all your ability here is provided by scouts and/or spies, adds one battle destiny and once during your turn may peek at top card of opponent's Force Pile.");
        addPersona(Persona.CRACKEN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_9);
        addKeywords(Keyword.GENERAL, Keyword.LEADER, Keyword.SCOUT, Keyword.INFORMATION_BROKER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition whileInBattle = new InBattleCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotAddDestinyDrawsToPowerModifier(self, whileInBattle, opponent));
        modifiers.add(new MayNotAddDestinyDrawsToAttritionModifier(self, whileInBattle, opponent));
        modifiers.add(new AddsBattleDestinyModifier(self, new AllAbilityAtLocationProvidedByCondition(self, playerId, Filters.here(self), Filters.or(Filters.scout, Filters.spy)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasForcePile(game, opponent)
                && GameConditions.isAllAbilityAtLocationProvidedBy(game, self, playerId, Filters.here(self), Filters.or(Filters.scout, Filters.spy))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top of opponent's Force Pile");
            action.setActionMsg("Peek at top card of opponent's Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardOfForcePileEffect(action, playerId, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }
}
