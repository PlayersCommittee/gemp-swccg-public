package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Weapon
 * Subtype: Starship
 * Title: Proton Bombs
 */
public class Card4_179 extends AbstractStarshipWeapon {
    public Card4_179() {
        super(Side.DARK, 1, Title.Proton_Bombs, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("Proton-scattering energy warheads. Especially effective against ray- and energy-shielded targets. Ineffective against particle deflector.");
        setGameText("Deploy on a bomber: When present during a Force drain, may target a related interior site. Draw destiny. Site 'collapsed' if destiny > 4. OR May fire in a Bombing Run battle. Draw destiny. All characters, starships and vehicles with that destiny number at same site are lost.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.bomber);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.bomber;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();

        if (Filters.makingBombingRun.accepts(game, self.getAttachedTo())) {
            FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                    .firesWithoutTargeting().finishBuildPrep();
            if (actionBuilder != null) {

                // Build action using common utility
                FireWeaponAction action = actionBuilder.buildFireWeaponProtonBombsAsCarpetBombingAction();
                actions.add(action);
            }
        }

        // Only include "orbital bombardment" if firing due to another action (e.g. Sniper), since normal "orbital bombardment" is handled by getGameTextOptionalAfterTriggers() due to a Force drain.
        if (!Filters.sameCardId(sourceCard).accepts(game, self)) {
            // Build action using common utility
            FireWeaponAction action = getFireAsOrbitalBombardmentAction(playerId, game, self, forFree, extraForceRequired, sourceCard, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
            if (action != null) {
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))) {

            // Build action using common utility
            final FireWeaponAction fireWeaponAction = getFireAsOrbitalBombardmentAction(playerId, game, self, false, 0, self, false, Filters.none, null, Filters.any, false);
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
                        });
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    private FireWeaponAction getFireAsOrbitalBombardmentAction(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        Filter relatedSiteFilter = Filters.and(fireAtTargetFilter, Filters.interior_site, Filters.relatedSite(self), Filters.not(Filters.shielded_location));

        // Check condition(s)
        if (GameConditions.isPresentAt(game, self, Filters.or(Filters.system, Filters.cloud_sector))
                && GameConditions.canUseWeapon(game, self.getAttachedTo(), self)
                && GameConditions.canTarget(game, self, relatedSiteFilter)) {

            FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, relatedSiteFilter, ignorePerAttackOrBattleLimit)
                    .targetRelatedInteriorSite(TargetingReason.TO_BE_COLLAPSED).finishBuildPrep();
            if (actionBuilder != null) {

                // Build action using common utility
                return actionBuilder.buildFireWeaponProtonBombsAsOrbitalBombardmentAction();
            }
        }
        return null;
    }
}
