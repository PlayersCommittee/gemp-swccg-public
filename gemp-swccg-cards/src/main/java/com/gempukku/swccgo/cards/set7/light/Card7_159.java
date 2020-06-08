package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Weapon
 * Subtype: Starship
 * Title: Intruder Missile
 */
public class Card7_159 extends AbstractStarshipWeapon {
    public Card7_159() {
        super(Side.LIGHT, 7, "Intruder Missile");
        setLore("Prototype missile developed by Slayn & Korpil. Ionizing warhead disrupts onboard systems. Capable of destroying capital starships by overloading shield generators.");
        setGameText("Deploy on your B-wing, Z-95, YT-1300 Transport, or Falcon. May target a capital starship for free. Draw destiny. Add 3 if that capital starship was targeted by another weapon this turn. Target hit if total destiny > defense value. After firing, place Missile in Used Pile.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.MISSILE, Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.B_wing, Filters.Z_95, Filters.YT_1300_Transport, Filters.Falcon));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.B_wing, Filters.Z_95, Filters.YT_1300_Transport, Filters.Falcon);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.capital_starship, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE);
            action.appendAfterEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, 3, Filters.and(Filters.capital_starship, Filters.hasBeenTargetedByWeaponThisTurn(Filters.other(self)))));
        return modifiers;
    }
}