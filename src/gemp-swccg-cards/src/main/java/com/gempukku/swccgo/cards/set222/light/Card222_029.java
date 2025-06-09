package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Character
 * Subtype: Rebel
 * Title: Young Skywalker
 */
public class Card222_029 extends AbstractRebel {
    public Card222_029() {
        super(Side.LIGHT, 6, 8, 6, 6, 9, "Young Skywalker", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setLore("Scout.");
        setGameText("[Pilot] 2. Deploys -3 to Endor. Power +2 while armed with Luke's Lightsaber. " +
                "Once per game, may retrieve A Jedi's Focus or A Jedi's Fury into hand. " +
                "Immune to attrition < 5 (< 6 while armed with Luke's Lightsaber).");
        addPersona(Persona.LUKE);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_22);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToTargetModifier(self, -3, Filters.and(Filters.Endor_location, Filters.battleground)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition armedWithLukesLightsaber = new ArmedWithCondition(self, Filters.Lukes_Lightsaber);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, armedWithLukesLightsaber, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 6, armedWithLukesLightsaber)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.YOUNG_SKYWALKER__RETRIEVE_CARD_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a card into hand");
            action.setActionMsg("Retrieve A Jedi's Focus or A Jedi's Fury into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.or(Filters.title("A Jedi's Focus"), Filters.title("A Jedi's Fury"))));
            return Collections.singletonList(action);
        }
        return null;
    }
}
