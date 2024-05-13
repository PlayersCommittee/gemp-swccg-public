package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Republic
 * Title: Anakin Skywalker, Jedi Knight
 */
public class Card216_021 extends AbstractRepublic {
    public Card216_021() {
        super(Side.LIGHT, 1, 8, 7, 6, 8, "Anakin Skywalker, Jedi Knight", Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("Jedi Council member.");
        setGameText("[Pilot] 3. While armed with a lightsaber, adds 2 to his defense value. During battle, may lose 2 Force to add one battle destiny. Immune to attrition < 6.");
        addPersona(Persona.ANAKIN);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_16, Icon.CLONE_ARMY);
        addKeyword(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DefenseValueModifier(self, new ArmedWithCondition(self, Filters.lightsaber), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canAddBattleDestinyDraws(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay Costs
            action.appendCost(
                    new LoseForceEffect(action, playerId, 2, true)
            );
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
