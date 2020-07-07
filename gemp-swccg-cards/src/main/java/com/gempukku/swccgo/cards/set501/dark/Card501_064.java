package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: First Order / Dark Jedi Master
 * Title: Sheev Palpatine
 */
public class Card501_064 extends AbstractDarkJediMasterFirstOrder {
    public Card501_064() {
        super(Side.DARK, 4, 4, 2, 7, 9, "Sheev Palpatine", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("Never deploys to a location with a [LS] icon. [FO] Star Destroyers deploy -2. While on Exegol, your total power is +2 at same [E7] location as Rey, Kylo, Pryde or Snoke. Once per turn, you may draw top card of your Reserve Deck. Immune to attrition.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_13);
        addPersona(Persona.EMPEROR);
        addKeywords(Keyword.LEADER);
        setTestingText("Sheev Palpatine");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.icon(Icon.LIGHT_FORCE)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.icon(Icon.FIRST_ORDER), Filters.Star_Destroyer), -2));
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.icon(Icon.EPISODE_VII), Filters.sameLocationAs(self, Filters.or(Filters.Kylo, Filters.Rey, Filters.Pryde, Filters.Snoke))), new OnCondition(self, Title.Exegol), 2, self.getOwner()));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
