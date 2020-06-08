package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExchangeCardInHandWithTopCardOfLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Brangus Glee
 */
public class Card7_169 extends AbstractAlien {
    public Card7_169() {
        super(Side.DARK, 3, 3, 1, 4, 3, Title.Brangus_Glee, Uniqueness.UNIQUE);
        setLore("Elder of a dextrous race of renowned travelers. Gambler. Frequents casinos, bars and spaceports. Originally from distant planet Dor Nameth.");
        setGameText("Power +2 at Cantina. Adds 2 to power of anything he pilots. Once per turn, may exchange a docking bay from hand with top card of Lost Pile. Your docking bay transit is free when moving to or from same site. Immune to attrition < 3.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Cantina), 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, Filters.sameSite(self), playerId));
        modifiers.add(new DockingBayTransitToForFreeModifier(self, Filters.sameSite(self), playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasInHand(game, playerId, Filters.docking_bay)
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Exchange docking bay with top card of Lost Pile");
            action.setActionMsg("Exchange a docking bay in hand with top card of Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithTopCardOfLostPileEffect(action, playerId, Filters.docking_bay));
            return Collections.singletonList(action);
        }
        return null;
    }
}
