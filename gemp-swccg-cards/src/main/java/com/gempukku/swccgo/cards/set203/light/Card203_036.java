package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Republic
 * Title: Padme Naberrie (AI) (V)
 */
public class Card203_036 extends AbstractRepublic {
    public Card203_036() {
        super(Side.LIGHT, 3, 4, 3, 4, 7, Title.Padme, Uniqueness.UNIQUE);
        setAlternateImageSuffix(true);
        setVirtualSuffix(true);
        setLore("Queen Amidala posed as one of her own handmaidens for added safety as well as to keep an eye on her Jedi protectors. Was to be protected by the Jedi at all times.");
        setGameText("Cancels Vader's game text here. If with a Skywalker, may add one destiny to attrition. Immune to attrition < 3.");
        addPersona(Persona.AMIDALA);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.FEMALE, Keyword.HANDMAIDEN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.Vader, Filters.here(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleWith(game, self, Filters.Skywalker)
                && GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add one destiny to attrition");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToAttritionEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
