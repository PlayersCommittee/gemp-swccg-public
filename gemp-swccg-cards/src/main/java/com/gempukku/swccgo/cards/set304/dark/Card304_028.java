package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Master Kamjin 'Maverick' Lap'lamiz
 */
public class Card304_028 extends AbstractDarkJediMasterImperial {
    public Card304_028() {
        super(Side.DARK, 6, 6, 7, 7, 9, "Master Kamjin 'Maverick' Lap'lamiz", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("After years of study, Kamjin has been promoted to Master. Currently the leader of the Scholae Palatinae Empire. Despite his pleasant appearnace, Kamjin is the embodiment of the Dark Side of the Force.");
        setGameText("Deploys -3 at Seraph or Raganth locations. Adds +3 to anything he pilots. May be targeted by Force Lightning. Once per turn, you may take Force Lightning into hand from Reserve Deck; reshuffle. Immune to attrition.");
        addPersona(Persona.KAMJIN);
        addIcons(Icon.CSP, Icon.WARRIOR, Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.CSP_EMPEROR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -3, Filters.or(Filters.Deploys_on_Seraph, Filters.Deploys_on_Ragnath)));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));        
		modifiers.add(new MayBeTargetedByModifier(self, Title.Force_Lightning));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KAMJIN__UPLOAD_FORCE_LIGHTNING;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Force Lightning into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Force_Lightning, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
