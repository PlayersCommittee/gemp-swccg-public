package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Rebel
 * Title: Major Bren Derlin (V)
 */
public class Card203_008 extends AbstractRebel {
    public Card203_008() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, Title.Derlin, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Hero of Nentan. Supervised construction of Echo Base on Hoth. Head of base security. At the Mos Eisley Cantina, everyone knows his name.");
        setGameText("Scout. Deploys free to (and power +2 at) Cantina, Endor, or Hoth. Opponent may not 'react' to or from here. Once per game, may retrieve an Explosive Charge into hand.");
        addIcons(Icon.HOTH, Icon.WARRIOR, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.or(Filters.Cantina, Filters.Deploys_at_Endor, Filters.Deploys_at_Hoth)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition atCantinaEndorOrHoth = new OrCondition(new AtCondition(self, Filters.Cantina), new AtCondition(self, Title.Endor), new AtCondition(self, Title.Hoth));
        Filter here = Filters.here(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atCantinaEndorOrHoth, 2));
        modifiers.add(new MayNotReactToLocationModifier(self, here, opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, here, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MAJOR_BREN_DERLIN__RETRIEVE_EXPLOSIVE_CHARGE_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve an Explosive Charge into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.Explosive_Charge));
            return Collections.singletonList(action);
        }
        return null;
    }
}
