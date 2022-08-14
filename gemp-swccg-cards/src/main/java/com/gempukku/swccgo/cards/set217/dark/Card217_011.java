package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringAttackWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsDuringAttackModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Alien
 * Title: Grummgar
 */
public class Card217_011 extends AbstractAlien {
    public Card217_011() {
        super(Side.DARK, 2, 4, 6, 2, 5, "Grummgar", Uniqueness.UNIQUE);
        setLore("Dowutin mercenary.");
        setGameText("During battle with an information broker (or during an attack), adds one destiny to total power. Once per game, may [download] a creature (or a blaster or rifle without 'lost' in game text) here.");
        setSpecies(Species.DOWUTIN);
        addIcons(Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new InBattleWithCondition(self, Filters.information_broker), 1, self.getOwner()));
        modifiers.add(new NumDestinyDrawsDuringAttackModifier(self, new DuringAttackWithParticipantCondition(self), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GRUMMGAR__DEPLOY_CARD_HERE;

        //Once per game, may deploy a creature (or a blaster or rifle without 'lost' in game text) here from Reserve Deck; reshuffle.
        Filter filter = Filters.or(Filters.creature, Filters.and(Filters.or(Filters.blaster, Filters.rifle), Filters.not(Filters.gameTextContains("lost"))));

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card here from Reserve Deck");
            action.setActionMsg("Deploy a creature (or a blaster or rifle without 'lost' in game text) here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, filter, Filters.here(self), true));
            return Collections.singletonList(action);
        }


        return null;
    }
}
