package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Figrin D'an & The Modal Nodes
 */
public class Card216_027 extends AbstractAlien {
    public Card216_027() {
        super(Side.LIGHT, 3, 4, 4, 2, 6, "Figrin D'an & The Modal Nodes", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Figrin_Dan, "The Modal Nodes");
        setLore("Bith musicians. Figrin D'an, Doikk Na'ts, Ickabel G'ont, Nalan Cheel, and Tedn Dahai.");
        setGameText("Power +2 at an antechamber, bar, cantina, or night club. Draws one battle destiny if unable to otherwise. During your control phase, if you have alien characters of five different species on table, may retrieve 1 Force. Once per game, may re-circulate.");
        addIcons(Icon.VIRTUAL_SET_16);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.BITH);
        addPersonas(Persona.FIGRIN_DAN, Persona.DOIKK_NATS, Persona.ICKABEL_GONT, Persona.NALAN_CHEEL, Persona.TEDN_DAHAI);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 4;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter venueFilter = Filters.or(Filters.titleContains("antechamber"), Filters.titleContains("bar"), Filters.titleContains("cantina"), Filters.titleContains("night club"));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, venueFilter), 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // During your control phase, if you have alien characters of five different species on table, may retrieve 1 Force.
        if (GameConditions.hasLostPile(game, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.countSpeciesOnTable(game, playerId) >= 5) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));

            actions.add(action);

        }


        // Once per game, may re-circulate.
        gameTextActionId = GameTextActionId.FIGRIN_DAN_AND_THE_MODAL_NODES__RECIRCULATE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasUsedPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Re-circulate");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RecirculateEffect(action, playerId));
            actions.add(action);
        }

        return actions;
    }
}
