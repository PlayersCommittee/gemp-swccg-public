package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractAlienRebel;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 25
 * Type: Character
 * Subtype: Alien/Rebel
 * Title: Lando With Blaster Rifle
 */

public class Card225_049 extends AbstractAlienRebel {
    public Card225_049() {
        super(Side.LIGHT, 1, 3, 3, 3, 5, "Lando With Blaster Rifle", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Works every time. Smuggler.");
        setGameText("[Pilot] 2. Permanent weapon is •Lando's Blaster Rifle (may target a character for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value). During battle, if your total battle destiny is odd, may retrieve 1 Force (2 if total is 11).");
        addPersona(Persona.LANDO);
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    // Define "Blaster Pistol" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon(Title.Landos_Blaster_Rifle, Uniqueness.UNIQUE) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {

                    // Build action using common utility
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE, true, 0);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER);
        return permanentWeapon;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForPlayer(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.hasLostPile(game, playerId)) {
            final BattleState battleState = game.getGameState().getBattleState();
            final float totalBattleDestiny = battleState.getTotalBattleDestiny(game, playerId);
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            if (totalBattleDestiny == 11) {
                action.setText("Retrieve 2 Force");
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 2));
                return Collections.singletonList(action);
            } else if (totalBattleDestiny % 2 == 1) {
                action.setText("Retrieve 1 Force");
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}


