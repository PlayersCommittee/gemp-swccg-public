package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNewRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * Subtype: New Republic
 * Title: Jaina Solo
 */
public class Card601_046 extends AbstractNewRepublic {
    public Card601_046() {
        super(Side.LIGHT, 3, 4, 4, 4, 6, "Jaina Solo", Uniqueness.UNIQUE);
        setLore("Female Rogue Squadron pilot. Padawan.");
        setGameText("Adds 3 to power of anything she pilots. X-wings deploy -1 (and are immune to attrition < 5) here. During your control phase, may take one non-unique X-Wing (or Rogue Squadron pilot) into hand from Reserve Deck; reshuffle. Adds one battle destiny with a Skywalker or another Solo. Immune to attrition < 4 (< 6 if with Leia).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.BLOCK_9, Icon.DEATH_STAR_II);
        addKeywords(Keyword.FEMALE, Keyword.ROGUE_SQUADRON, Keyword.PADAWAN);
        addPersona(Persona.JAINA);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.X_wing, -1, Filters.here(self)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.X_wing, Filters.here(self)), 5));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Skywalker, Filters.Solo)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(4, 6, new WithCondition(self, Filters.Leia))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__JAINA__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Reserve Deck");
                action.setActionMsg("Take a non-unique X-wing (or Rogue Squadron pilot) into hand from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Pay cost(s)
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.or(Filters.X_wing, Filters.Rogue_Squadron_pilot)), true));
                return Collections.singletonList(action);

        }
        return null;
    }
}