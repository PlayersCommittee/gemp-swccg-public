package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Rebel
 * Title: Han Solo, Optimistic General
 */
public class Card215_011 extends AbstractRebel {
    public Card215_011() {
        super(Side.LIGHT, 1, 4, 4, 3, 6, "Han Solo, Optimistic General", Uniqueness.UNIQUE);
        setLore("Leader. Scout.");
        setGameText("May be targeted instead of a Resistance character by I Want That Map. Adds 3 to power of anything he pilots. [Endor] scouts are destiny +1. Cancels Kylo's game text here. During battle with Chewie or [Endor] Leia, may add one destiny to total power.");
        addPersona(Persona.HAN);
        addIcons(Icon.WARRIOR, Icon.PILOT, Icon.ENDOR, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.LEADER, Keyword.SCOUT, Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayBeRevealedAsResistanceAgentModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.Kylo, Filters.here(self))));
        modifiers.add(new DestinyModifier(self, Filters.and(Icon.ENDOR, Filters.scout), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Chewie, Filters.and(Icon.ENDOR, Filters.Leia)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny to total power");
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1, playerId));

            return Collections.singletonList(action);
        }

        return null;
    }
}