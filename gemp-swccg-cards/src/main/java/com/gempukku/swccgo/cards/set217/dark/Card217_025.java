package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 17
 * Type: Character
 * Subtype: Republic
 * Title: Wat Tambor
 */
public class Card217_025 extends AbstractRepublic {
    public Card217_025() {
        super(Side.DARK, 2, 3, 3, 4, 5, "Wat Tambor", Uniqueness.UNIQUE);
        setLore("Skakoan leader. Trade Federation. Techno Union.");
        setGameText("Opponent may not target your Republic characters with weapons unless each of your [Presence] droids present with them are 'hit.' [Set 8] Where Are Those Droidekas?! ignores Wat Tambor. Once per game, may draw two cards from Reserve Deck.");
        addIcons(Icon.SEPARATIST, Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.SKAKOAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.Republic_character, Filters.presentWith(self, Filters.and(Filters.not(Filters.hit), Filters.and(Icon.PRESENCE, Filters.droid))))));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.VIRTUAL_SET_8, Filters.Where_Are_Those_Droidekas), ModifyGameTextType.WAT_TAMBOR__IGNORED_BY_WHERE_ARE_THOSE_DROIDEKAS));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WAT_TAMBOR__DRAW_CARDS;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw two cards from Reserve Deck");

            action.appendUsage(
                    new OncePerGameEffect(action));

            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));

            return Collections.singletonList(action);

        }
        return null;
    }
}
