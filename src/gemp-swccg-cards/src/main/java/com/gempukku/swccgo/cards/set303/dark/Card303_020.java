package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Shadow Academy
 * Type: Character
 * Subtype: Alien
 * Title: Seraine Ténamao, Emissary
 */
public class Card303_020 extends AbstractAlien {
    public Card303_020() {
        super(Side.DARK, 1, 6, 5, 6, 7, "Seraine Ténamao, Emissary", Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.R);
        setLore("A long standing member of the Brotherhood, she recently became Emissary. In her new role she seeks out new members to join the Brotherhood's ranks.");
        setGameText("Adds 3 to any ship she pilots. Once per game, during your move phase, may relocate Seraine to the location of a character ability < 4. Immune attrition < 4.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.DARK_COUNCILOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SERAINE__RELOCATE_TO_CHARACTER_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            PhysicalCard characterLocation = Filters.findFirstFromTopLocationsOnTable(game, Filters.sameLocationAs(self, Filters.and(Filters.character, Filters.abilityLessThan(4))));
            if (characterLocation != null) {
                if (Filters.locationCanBeRelocatedTo(self, 0).accepts(game, characterLocation)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Relocate to location of character with ability less than 4.");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerGameEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new RelocateBetweenLocationsEffect(action, self, characterLocation));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
