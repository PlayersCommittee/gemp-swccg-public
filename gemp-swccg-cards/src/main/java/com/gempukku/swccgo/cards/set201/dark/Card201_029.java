package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Effect
 * Title: Endor Shield (V)
 */
public class Card201_029 extends AbstractNormalEffect {
    public Card201_029() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Endor_Shield, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Planetary scale shield projected from surface of Endor moon. Protected second Death Star during construction. Only another superlaser could penetrate it while operational.");
        setGameText("Deploy on table. While your [Endor] objective on table (and opponent's [Endor] objective not on table) opponent generates no Force at Endor system. Twice per game, may [upload] an Imperial admiral. [Immune to Alter]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.Endor_system, new AndCondition(new OnTableCondition(self, Filters.and(Filters.your(self), Icon.ENDOR, Filters.Objective)),
                new NotCondition(new OnTableCondition(self, Filters.and(Filters.opponents(self), Icon.ENDOR, Filters.Objective)))), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ENDOR_SHIELD__UPLOAD_IMPERIAL_ADMIRAL;

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take an Imperial admiral into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Imperial, Filters.admiral), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}