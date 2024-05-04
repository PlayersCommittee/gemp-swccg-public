package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RearmCharacterEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeviceMayNotBeRemovedUnlessDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Device
 * Title: Bionic Hand
 */
public class Card5_012 extends AbstractCharacterDevice {
    public Card5_012() {
        super(Side.LIGHT, 7, "Bionic Hand", Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Uses state-of-the-art digital processors. Although Luke had lost his hand, the Alliance could rebuild it. They had the technology. They could make it better, stronger, faster.");
        setGameText("Use 2 Force to deploy on a Disarmed character. Cancels the disarming card. Character is power +1 and adds 1 to weapon destiny when firing character weapons. Device lost if character Disarmed again (otherwise, may not be removed).");
        addIcons(Icon.CLOUD_CITY);
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
        return Filters.and(Filters.your(self), Filters.disarmed_character);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.character;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard character = self.getAttachedTo();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Re-arm " + GameUtils.getFullName(character));
            action.setActionMsg("Re-arm " + GameUtils.getCardLink(character));
            // Perform result(s)
            action.appendEffect(
                    new RearmCharacterEffect(action, character));
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.justDisarmed(game, effectResult, Filters.hasAttached(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttached, 1));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.character_weapon, hasAttached, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeviceMayNotBeRemovedUnlessDisarmedModifier(self));
        return modifiers;
    }
}