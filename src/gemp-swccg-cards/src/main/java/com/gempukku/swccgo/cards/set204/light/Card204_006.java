package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Resistance
 * Title: Finn
 */
public class Card204_006 extends AbstractResistance {
    public Card204_006() {
        super(Side.LIGHT, 1, 3, 4, 3, 6, Title.Finn, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLore("Gunner. Stormtrooper.");
        setGameText("Adds 3 to power of any starship or enclosed vehicle he is aboard as a passenger. May deploy as a 'react' to same location as your pilot (free if Poe or Rey). Once per game, may [upload] Blaster Proficiency. Immune to attrition < 4.");
        addIcons(Icon.EPISODE_VII, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.GUNNER, Keyword.STORMTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.or(Filters.starship, Filters.enclosed_vehicle), Filters.hasPassenger(self)), 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter sameLocationAsPoeOrRey = Filters.sameLocationAs(self, Filters.and(Filters.your(self), Icon.PILOT, Filters.or(Filters.Poe, Filters.Rey)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.and(Filters.sameLocationAs(self, Filters.and(Filters.your(self), Icon.PILOT)), Filters.not(sameLocationAsPoeOrRey))));
        modifiers.add(new MayDeployAsReactToLocationForFreeModifier(self, sameLocationAsPoeOrRey));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.FINN__UPLOAD_BLASTER_PROFICIENCY;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Blaster Proficiency into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Blaster_Proficiency, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
