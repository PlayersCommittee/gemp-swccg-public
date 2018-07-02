package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: General Crix Madine
 */
public class Card8_014 extends AbstractRebel {
    public Card8_014() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, Title.Madine, Uniqueness.UNIQUE);
        setLore("Military advisor to Mon Mothma. Leader of commando project. Corellian native. Defected to the Alliance shortly after the Battle of Yavin. Rescued by Rogue Squadron.");
        setGameText("Once during each of your deploy phases, may take one scout of ability < 3 into hand from Reserve Deck; reshuffle. While at your war room or aboard your capital starship, adds 1 to immunity to attrition of all your scouts who have immunity.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GENERAL_CRIX_MADINE__UPLOAD_SCOUT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a scout of ability < 3 into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.scout, Filters.abilityLessThan(3)), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.your(self), Filters.scout, Filters.hasAnyImmunityToAttrition),
                new OrCondition(new AtCondition(self, Filters.and(Filters.your(self), Filters.war_room)),
                        new AboardCondition(self, Filters.and(Filters.your(self), Filters.capital_starship))), 1));
        return modifiers;
    }
}
