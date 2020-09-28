package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Greeata
 */
public class Card7_179 extends AbstractAlien {
    public Card7_179() {
        super(Side.DARK, 3, 1, 2, 1, 2, "Greeata", Uniqueness.UNIQUE);
        setLore("Rodian musician. In addition to her singing talents, she plays kloo horn. Befriended Sy Snootles on the luxury liner Kuari Princess.");
        setGameText("Other Rodians deploy -1 to same site. Once during each of your control phases, may use 1 Force to take any Rodian into hand from Reserve Deck; reshuffle. While at Audience Chamber, all your other musicians are deploy -1 and forfeit +3.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN);
        setSpecies(Species.RODIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourOtherMusiciansFilter = Filters.and(Filters.your(self), Filters.other(self), Filters.musician);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.other(self), Filters.Rodian), -1, Filters.sameSite(self)));
        modifiers.add(new DeployCostModifier(self, yourOtherMusiciansFilter, atAudienceChamber, -1));
        modifiers.add(new ForfeitModifier(self, yourOtherMusiciansFilter, atAudienceChamber, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GREEATA__UPLOAD_RODIAN;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Rodian into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1)
            );
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Rodian, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
