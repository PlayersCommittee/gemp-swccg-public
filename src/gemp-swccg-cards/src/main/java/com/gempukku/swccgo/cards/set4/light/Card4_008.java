package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.conditions.HasMatchingPilotAboardCondition;
import com.gempukku.swccgo.cards.conditions.PilotedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayPilotWithAstromechModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Device
 * Title: Astromech Translator
 */
public class Card4_008 extends AbstractDevice {
    public Card4_008() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Astromech_Translator, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Standard technology found on hyperdrive-capable starfighters. Many manufacturers. Converts electronic impulses and high-density electronic languages into readable text.");
        setGameText("Deploy on any starfighter. While an astromech character is aboard: If not piloted, starfighter may move and may use power, maneuver and hyperspeed. OR if piloted, starfighter is immune to attrition < 3 (< 6 if matching pilot aboard).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.starfighter;
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.starfighter;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        PhysicalCard starfighter = self.getAttachedTo();
        Filter attachedStarfighter = Filters.hasAttached(self);
        Condition astromechCharacterAboard = new HasAboardCondition(starfighter, Filters.and(Filters.astromech_droid, Filters.character));
        Condition piloted = new PilotedCondition(starfighter);
        Condition matchingPilotAboard = new HasMatchingPilotAboardCondition(starfighter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayPilotWithAstromechModifier(self, attachedStarfighter, astromechCharacterAboard));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, attachedStarfighter, new AndCondition(astromechCharacterAboard, piloted), 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, attachedStarfighter, new AndCondition(astromechCharacterAboard, piloted, matchingPilotAboard), 6));
        return modifiers;
    }
}
