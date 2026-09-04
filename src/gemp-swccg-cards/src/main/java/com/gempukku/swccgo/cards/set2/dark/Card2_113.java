package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Device
 * Title: Laser Gate
 */
public class Card2_113 extends AbstractDevice {
    public Card2_113() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Laser_Gate, Uniqueness.RESTRICTED_2, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Security corridors are guarded by a grid of laser emplacements which can be activated upon demand to seal off sensitive areas from intrusion.");
        setGameText("Deploy between any two interior mobile sites. To pass, a character must have (power + ability) > 4 or use a Lift Tube (all other vehicles are blocked). Laser Gate defense value = 3; may be targeted (as if a character) by a character weapon from either site.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        return Filters.location;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // Attach to an interior mobile site that has an adjacent interior mobile site.
        return Filters.and(Filters.interior_mobile_site, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpot(game, self, Filters.and(Filters.adjacentSite(physicalCard), Filters.interior_mobile_site));
            }
        });
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.interior_mobile_site;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.interior_mobile_site;
    }

    @Override
    protected List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(final Action action, String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        final Filter targetFilter = Filters.and(Filters.interior_mobile_site, Filters.adjacentSite(target), Filters.not(target));
        TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose adjacent interior mobile site", targetFilter) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard chosen) {
                action.addAnimationGroup(chosen);
                self.setTargetedCard(TargetId.EFFECT_TARGET_1, targetGroupId, chosen, targetFilter);
            }
        };
        return Collections.singletonList(targetingEffect);
    }

    /**
     * Characters with (power + ability) > 4, Lift Tubes, and cards aboard Lift Tubes may pass.
     */
    private static Filter cardsThatMayPass() {
        final Filter powerPlusAbilityGreaterThan4 = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.character.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                float power = modifiersQuerying.getPower(gameState, physicalCard);
                float ability = modifiersQuerying.getAbility(gameState, physicalCard);
                return (power + ability) > 4;
            }
        };
        return Filters.or(
                powerPlusAbilityGreaterThan4,
                Filters.Lift_Tube,
                Filters.aboard(Filters.Lift_Tube)
        );
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter siteA = Filters.hasAttached(self);
        Filter siteB = Filters.targetedByCardOnTableAsTargetId(self, TargetId.EFFECT_TARGET_1);
        Filter blockedFromPassing = Filters.not(cardsThatMayPass());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        // Block movement both directions between the two bounding sites for cards that cannot pass.
        modifiers.add(new MayNotMoveFromLocationToLocationModifier(self, blockedFromPassing, siteA, siteB));
        modifiers.add(new MayNotMoveFromLocationToLocationModifier(self, blockedFromPassing, siteB, siteA));
        modifiers.add(new DefinedByGameTextDefenseValueModifier(self, 3));
        // Character weapons may target this device (as if a character). Full "from either site" /
        // during-battle non-participant targeting needs shared between-sites engine support.
        modifiers.add(new MayBeTargetedByWeaponsModifier(self, Filters.character_weapon));
        return modifiers;
    }
}
