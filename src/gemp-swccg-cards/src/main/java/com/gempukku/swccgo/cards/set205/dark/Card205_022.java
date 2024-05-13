package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 5
 * Type: Weapon
 * Subtype: Character
 * Title: Boba Fett's Blaster Rifle (V)
 */
public class Card205_022 extends AbstractCharacterWeapon {
    public Card205_022() {
        super(Side.DARK, 1, Title.Boba_Fetts_Blaster_Rifle, Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sawed off BlasTech EE-3 blaster rifle. Although its barrel is a few centimeters under the legal limit, no one has lived to file an official complaint.");
        setGameText("Deploy on your non-[Maintenance] bounty hunter. If on Boba Fett, once per game, during your move phase, may use 2 Force to relocate him (with any captive he is escorting) to a [Jabba's Palace] site. May target a character or vehicle for free. Draw destiny. Target hit if destiny + 2 > defense value.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.BLASTER_RIFLE);
        setMatchingCharacterFilter(Filters.Boba_Fett);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.bounty_hunter, Filters.not(Icon.MAINTENANCE));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.bounty_hunter, Filters.not(Icon.MAINTENANCE));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BOBA_FETTS_BLASTER_RIFLE__RELOCATE_BOBA_FETT;

        // Check condition(s)
        final PhysicalCard attachedTo = self.getAttachedTo();
        if (GameConditions.isAttachedTo(game, self, Filters.Boba_Fett)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canUseWeapon(game, attachedTo, self)) {
            final Filter jpSite = Filters.and(Filters.site, Icon.JABBAS_PALACE, Filters.locationCanBeRelocatedTo(attachedTo, false, true, false, 2, false));
            if (GameConditions.canSpot(game, self, jpSite)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate " + GameUtils.getFullName(attachedTo));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                action.appendUsage(
                        new UseWeaponEffect(action, self));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(attachedTo) + " to", jpSite) {
                            @Override
                            protected void cardSelected(final PhysicalCard siteSelected) {
                                action.addAnimationGroup(attachedTo);
                                action.addAnimationGroup(siteSelected);
                                // Pay cost(s)
                                action.appendCost(
                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, attachedTo, siteSelected, 2));
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(attachedTo) + " to " + GameUtils.getCardLink(siteSelected),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, attachedTo, siteSelected));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
