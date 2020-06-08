package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Character
 * Subtype: Droid
 * Title: C1-10P (Chopper)
 */
public class Card208_002 extends AbstractDroid {
    public Card208_002() {
        super(Side.LIGHT, 3, 2, 2, 3, "C1-10P (Chopper)", Uniqueness.UNIQUE);
        setLore("Information broker and spy.");
        setGameText("While aboard a starship, it is power and defense value +2 and immune to Lateral Damage. May 'fly' (landspeed = 2). Permanent weapon is Electroshock Prod (may target a character of ability < 4; target may not use its game text for remainder of turn).");
        addIcons(Icon.NAV_COMPUTER, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.INFORMATION_BROKER, Keyword.SPY);
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starshipFilter = Filters.and(Filters.starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, starshipFilter, 2));
        modifiers.add(new DefenseValueModifier(self, starshipFilter, 2));
        modifiers.add(new ImmuneToTitleModifier(self, starshipFilter, Title.Lateral_Damage));
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 2));
        return modifiers;
    }

    // Define "Electroshock Prod" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return new AbstractPermanentWeapon("Electroshock Prod") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.and(Filters.or(Filters.character, targetedAsCharacter), Filters.abilityLessThan(4)), TargetingReason.OTHER).noWeaponDestinyNeeded().finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponCancelGameTextAction(true);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
    }
}
