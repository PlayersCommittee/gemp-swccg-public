package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Staging Areas
 */
public class Card9_040 extends AbstractNormalEffect {
    public Card9_040() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Staging_Areas, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("The Alliance carefully chooses docking bays from which to launch limited offensives.");
        setGameText("Deploy on table. Your Force generation is +1 at each docking bay you occupy (or +2 if you control). You non-unique Star Cruisers are deploy -1 and may deploy to same battleground system as any rebel starship. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourNonuniqueStarCruisers = Filters.and(Filters.your(self), Filters.non_unique, Filters.Star_Cruiser);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.docking_bay, Filters.occupies(playerId), Filters.not(Filters.controls(playerId))), 1, playerId));
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.docking_bay, Filters.controls(playerId)), 2, playerId));
        modifiers.add(new DeployCostModifier(self, yourNonuniqueStarCruisers, -1));
        modifiers.add(new MayDeployToTargetModifier(self, yourNonuniqueStarCruisers, Filters.and(Filters.battleground_system, Filters.sameLocationAs(self, Filters.Rebel_starship))));
        return modifiers;
    }
}