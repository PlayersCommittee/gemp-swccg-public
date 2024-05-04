package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Rebel
 * Title: Princess Leia
 */
public class Card5_007 extends AbstractRebel {
    public Card5_007() {
        super(Side.LIGHT, 1, 4, 3, 4, 7, "Princess Leia", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Prominent leader in the struggling Alliance. Former member of the Imperial Senate. Beginning to discover her true heritage. Likes scoundrels.");
        setGameText("May only deploy on Hoth or Cloud City. Adds 1 to power of anything she pilots. May deploy (on Hoth or Cloud City) or move as a 'react' to same site as Han or Luke. Immune to attrition < 3.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.FEMALE, Keyword.SENATOR);
        addPersona(Persona.LEIA);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Hoth, Filters.Deploys_on_Cloud_City);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.sameSiteAs(self, Filters.or(Filters.Han, Filters.Luke))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.sameSiteAs(self, Filters.or(Filters.Han, Filters.Luke))));
        return modifiers;
    }
}
