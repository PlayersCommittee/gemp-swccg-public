package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: General Dodonna (V)
 */
public class Card209_005 extends AbstractRebel {
    public Card209_005() {
        super(Side.LIGHT, 2, 3, 3, 2, 5, Title.General_Dodonna, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Skilled tactician and natural leader. Planned attack on Death Star after analyzing technical readouts provided by Princess Leia. Star Destroyer captain during Old Republic.");
        setGameText("Once per game, if at a war room, may [upload] a related battleground (or a Y-wing). While present at a war room, opponent's spies may not deploy here and your Epic Event destiny draws are +1.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GENERAL_DODONNA_V__UPLOAD_BG_OR_YWING;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.war_room)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            //final PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            Filter relatedBGFilter = Filters.and(Filters.battleground, Filters.relatedLocationEvenWhenNotInPlay(self));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take related battleground or Y-wing into hand from Reserve Deck");
            action.setActionMsg("Take related battleground or Y-wing into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(relatedBGFilter, Filters.Y_wing), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.spy), new PresentAtCondition(self, Filters.war_room), Filters.sameSite(self)));
        modifiers.add(new EpicEventDestinyDrawModifier(self, playerId, Filters.Epic_Event, new PresentAtCondition(self, Filters.war_room), 1));
        return modifiers;
    }

}