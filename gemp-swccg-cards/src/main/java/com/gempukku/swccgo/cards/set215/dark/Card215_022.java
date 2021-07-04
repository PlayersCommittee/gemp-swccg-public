package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttachModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 15
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Piett (V)
 */
public class Card215_022 extends AbstractImperial {
    public Card215_022() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Admiral Piett", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Veteran of the Imperial military machine. Leader of the Imperial fleet at Endor. Skilled at political maneuvering and appeasing his powerful superiors.");
        setGameText("[Pilot] 3: Executor. Deploys -1 for each of your starship sites on table. Landing Claw may not attach here. Once per game, if piloting Executor, may [upload] Emperor's Orders.");
        addPersona(Persona.PIETT);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Executor);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostModifier(self, new NegativeEvaluator(new OnTableEvaluator(self, Filters.and(Filters.your(self.getOwner()), Filters.starship_site)))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.Executor));
        modifiers.add(new MayNotAttachModifier(self, Filters.and(Filters.Landing_Claw, Filters.here(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ADMIRAL_PIETT__UPLOAD_EMPERORS_ORDERS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isPiloting(game, self, Filters.Executor)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Emperor's Orders into hand from Reserve Deck");
            action.setActionMsg("Take Emperor's Orders into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Emperors_Orders), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
