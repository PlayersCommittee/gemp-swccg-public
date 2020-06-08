package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Character
 * Subtype: Droid
 * Title: IG-88 With Riot Gun
 */
public class Card109_011 extends AbstractDroid {
    public Card109_011() {
        super(Side.DARK, 1, 5, 4, 3, "IG-88 With Riot Gun", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Bounty hunter droid equipped with proprietary stealth technology. Archived several of its enhancement subroutines in favor of sophisticated tracking and capture programming.");
        setGameText("May initiate battle. Permanent weapon is riot gun (may target a character for free; draw destiny; target captured if destiny +1 > defense value). Adds one battle destiny if alone or with your other bounty hunter. Immune to Restraining Bolt and purchase.");
        addPersona(Persona.IG88);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addKeywords(Keyword.BOUNTY_HUNTER);
        addModelType(ModelType.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(new AloneCondition(self),
                new WithCondition(self, Filters.and(Filters.your(self), Filters.other(self), Filters.bounty_hunter))), 1));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        modifiers.add(new MayNotBePurchasedModifier(self));
        return modifiers;
    }

    // Define "Riot Gun" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return new AbstractPermanentWeapon("riot gun") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_CAPTURED).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponCaptureAction(1, 1, Statistic.DEFENSE_VALUE);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
    }
}
