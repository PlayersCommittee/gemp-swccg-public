package com.gempukku.swccgo.cards.set226.light;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Rebel
 * Title: Obi-Wan Kenobi, Jedi In Exile
 */
public class Card226_024 extends AbstractRebel {
    public Card226_024() {
        super(Side.LIGHT, 1, 8, 6, 6, 8, "Obi-Wan Kenobi, Jedi In Exile", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Jedi survivor.");
        setGameText("Adds one battle destiny with Maul, Sidious, or Vader. Opponent's characters here are power and forfeit -1. Once per game, may [upload] (or retrieve into hand) Glancing Blow or Help Me Obi-Wan Kenobi. Immune to You Are Beaten and attrition < 6.");
        addKeyword(Keyword.JEDI_SURVIVOR);
        addPersona(Persona.OBIWAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_26);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Maul, Filters.Sidious, Filters.Vader)), 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.here(self)), -1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.here(self)), -1));
        modifiers.add(new ImmuneToTitleModifier(self, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OBIWAN_KENOBI_JEDI_IN_EXILE__UPLOAD_OR_RETRIEVE_CARD; //Shared once-per-game action
        Filter GlancingBlowOrHelpMeObiWanKenobi = Filters.or(Filters.Glancing_Blow, Filters.Help_Me_ObiWan_Kenobi);

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, GlancingBlowOrHelpMeObiWanKenobi, true));
            actions.add(action);
        }

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId, true)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve card into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, GlancingBlowOrHelpMeObiWanKenobi));
            actions.add(action);
        }

        return actions;
    }
    
}
