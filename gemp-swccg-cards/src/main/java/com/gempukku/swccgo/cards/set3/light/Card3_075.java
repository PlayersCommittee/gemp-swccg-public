package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractArtilleryWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Weapon
 * Subtype: Artillery
 * Title: Golan Laser Battery
 */
public class Card3_075 extends AbstractArtilleryWeapon {
    public Card3_075() {
        super(Side.LIGHT, 4, 3, 3, "Golan Laser Battery");
        setLore("Modified Golan Arms DForfeit: 9 anti-infantry battery. Proton-shielded control cylinder capped by one or more rotating turret-mounted laser cannons.");
        setGameText("Deploy on any exterior planet site. Your warrior present may target a creature, character or vehicle at same or adjacent site using 2 Force. Draw destiny. Add 2 if targeting a creature or character. Target hit if destiny > defense value.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.LASER_CANNON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_planet_site;
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.warrior, Filters.present(self));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponByCharacterPresentWithHitAction(1);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, 2, Filters.or(Filters.character, Filters.creature)));
        return modifiers;
    }
}
