package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Weapon
 * Subtype: Character
 * Title: Gaderffii Stick
 */
public class Card1_315 extends AbstractCharacterWeapon {
    public Card1_315() {
        super(Side.DARK, 4, Title.Gaderffii_Stick, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Notorious 'gaffi' weapon favored by Tusken Raiders. Built from scavenged metal. Intimidates and evokes fear.");
        setGameText("Use 2 Force to deploy on any Tusken Raider. If a battle has just been initiated where present, target a character for free; draw two destiny. If total destiny > 5, target's weapons are 'knocked away' (may not be used this battle).");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Tusken_Raider);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Tusken_Raider;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();

        // Only include fire weapon action if firing due to another action (e.g. Sniper), since normal weapon firing is handled by getGameTextOptionalAfterTriggers() due to a battle just initiated.
        if (!Filters.sameCardId(self).accepts(game, sourceCard)) {
            // Build action using common utility
            FireWeaponAction action = getFireGaderffiStickAction(playerId, game, self, forFree, extraForceRequired, sourceCard, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
            if (action != null) {
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.wherePresent(self))) {

            // Build action using common utility
            final FireWeaponAction fireWeaponAction = getFireGaderffiStickAction(playerId, game, self, false, 0, self, false, Filters.none, null, Filters.any, false);
            if (fireWeaponAction != null) {

                // Build game text trigger action
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.skipInitialMessageAndAnimation();
                action.setText(fireWeaponAction.getText());
                action.setActionMsg(null);
                // Performing the weapon firing action
                action.appendTargeting(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                // Allow fire weapon action to be aborted, if this trigger action is allowed to be aborted
                                fireWeaponAction.setAllowAbort(action.isAllowAbort());
                                action.appendTargeting(
                                        new StackActionEffect(action, fireWeaponAction));
                                action.appendTargeting(
                                        new PassthruEffect(action) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                if (fireWeaponAction.isChoosingTargetsComplete() || fireWeaponAction.wasCarriedOut()) {
                                                    // If weapon fire action got passed choose targets,
                                                    // then make this trigger action passed choosing targets
                                                    action.appendEffect(
                                                            new PassthruEffect(action) {
                                                                @Override
                                                                protected void doPlayEffect(SwccgGame game) {
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    private FireWeaponAction getFireGaderffiStickAction(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.and(Filters.or(Filters.character, targetedAsCharacter), Filters.armedWith(Filters.weapon)), TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            return actionBuilder.buildFireWeaponGaderffiStickAction();
        }
        return null;
    }
}
