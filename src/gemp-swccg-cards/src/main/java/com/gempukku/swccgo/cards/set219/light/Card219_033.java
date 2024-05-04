package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Rebel
 * Title: Ezra, Hero Of Phoenix Squadron
 */
public class Card219_033 extends AbstractRebel {
    public Card219_033() {
        super(Side.LIGHT, 2, 5, 4, 5, 6, "Ezra, Hero Of Phoenix Squadron", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLore("Commander. Leader. Padawan");
        setGameText("Other Phoenix Squadron characters here are forfeit and defense value +2. " +
                    "Once per game, may retrieve a Phoenix Squadron character into hand. " +
                    "[Set 13] Maul may not modify destiny draws. Immune to attrition < 4.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_19);
        addPersona(Persona.EZRA);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.PADAWAN, Keyword.PHOENIX_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.other(self), Filters.here(self), Filters.Phoenix_Squadron_character), 2));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.other(self), Filters.here(self), Filters.Phoenix_Squadron_character), 2));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.VIRTUAL_SET_13, Filters.Maul), ModifyGameTextType.MAUL__MAY_NOT_MODIFIY_DESTINIES));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.EZRA_HERO_OF_PHOENIX_SQUADRON__RETRIEVE_PHOENIX_SQUADRON_CHARACTER_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a character into hand");
            action.setActionMsg("Retrieve a Phoenix Squadron character into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.Phoenix_Squadron_character));
            return Collections.singletonList(action);
        }
        return null;
    }
}
