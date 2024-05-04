package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: 3B3-10
 */
public class Card14_069 extends AbstractDroid {
    public Card14_069() {
        super(Side.DARK, 2, 2, 3, 3, "3B3-10", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setArmor(3);
        setLore("Infantry battle droid who assumed command after his regiment's commander fell to Amidala's blaster. Re-routed Droid Control Ship order to himself in the heat of battle.");
        setGameText("While at Theed Palace Hallway, opponent's characters may not move from here. Once per game may take Halt! Into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.INFANTRY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character),
                new AtCondition(self, Filters.Theed_Palace_Hallway), Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId._3B310__UPLOAD_HALT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Halt! into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Halt, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
