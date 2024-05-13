package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.FiredWeaponsInBattleCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Alien
 * Title: Wuher (V)
 */
public class Card223_002 extends AbstractAlien {
    public Card223_002() {
        super(Side.DARK, 3, 2, 2, 1, 3, Title.Wuher, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setVirtualSuffix(true);
        setLore("Gruff, surly, no-blasters-allowed bartender. Hates droids. 'We don't serve their kind here.' Wants to concoct the perfect drink for Jabba so he can work as his personal bartender.");
        setGameText("While at Cantina, each player may only fire one weapon during battle here and, while Jabba on table, " +
                "opponent generates no Force here. Once per game, may [upload] (or retrieve) Restraining Bolt.");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_23);
        addPersona(Persona.WUHER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new ArrayList<>();
        AtCondition atCantinaCondition = new AtCondition(self, Filters.Cantina);
        modifiers.add(new MayNotBeFiredModifier(self, Filters.and(Filters.your(playerId), Filters.at(Filters.Cantina)),
                new AndCondition(atCantinaCondition, new FiredWeaponsInBattleCondition(playerId, 1, Filters.any))));
        modifiers.add(new MayNotBeFiredModifier(self, Filters.and(Filters.your(opponent), Filters.at(Filters.Cantina)),
                new AndCondition(atCantinaCondition, new FiredWeaponsInBattleCondition(opponent, 1, Filters.any))));
        modifiers.add(new GenerateNoForceModifier(self, Filters.Cantina,
                new AndCondition(atCantinaCondition, new OnTableCondition(self, Filters.Jabba)), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.WUHER__UPLOAD_OR_RETRIEVE_RESTRAINING_BOLT;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Upload Restraining Bolt");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Restraining_Bolt, true)
            );

            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve Restraining Bolt");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.Restraining_Bolt)
            );

            actions.add(action);
        }
        return actions;
    }
}
