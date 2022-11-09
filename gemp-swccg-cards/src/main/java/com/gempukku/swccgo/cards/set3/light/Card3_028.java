package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireArtilleryWeaponWithoutWarriorPresentModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Device
 * Title: Artillery Remote
 */
public class Card3_028 extends AbstractCharacterDevice {
    public Card3_028() {
        super(Side.LIGHT, 4, "Artillery Remote", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.R2);
        setLore("Although artillery weapons have a manual firing mechanism, this optional device allows weapons operation from a remote location. Uses coded signals.");
        setGameText("Use 2 Force to deploy on any warrior. Your artillery weapons anywhere on same planet may fire without a warrior present. Also, once each turn during your control phase, one of your artillery weapons on same planet may be fired (at normal use of the Force).");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.warrior);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.warrior;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter weaponFilter = Filters.and(Filters.your(self), Filters.artillery_weapon, Filters.onSamePlanet(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireArtilleryWeaponWithoutWarriorPresentModifier(self, weaponFilter));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseDevice(game, self)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.artillery_weapon, Filters.onSamePlanet(self), Filters.canBeFiredAt(self, Filters.canBeTargetedBy(self), 0));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Fire an artillery weapon");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                action.appendUsage(
                        new UseDeviceEffect(action, self));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose artillery weapon to fire", weaponFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                action.setActionMsg("Fire " + GameUtils.getCardLink(weapon));
                                // Perform result(s)
                                action.appendEffect(
                                        new FireWeaponEffect(action, weapon, false, Filters.canBeTargetedBy(self)));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}