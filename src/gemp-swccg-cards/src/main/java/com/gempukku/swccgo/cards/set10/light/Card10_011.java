package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Device
 * Title: Mercenary Armor
 */
public class Card10_011 extends AbstractCharacterDevice {
    public Card10_011() {
        super(Side.LIGHT, 3, Title.Mercenary_Armor, Uniqueness.RESTRICTED_2, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Worn by hired guns throughout the galaxy. Often used by Rebels when infiltrating underworld organizations. Leia wore Boushh's armor when she infiltrated Black Sun.");
        setGameText("Use 2 Force (free on Leia) to deploy on a Rebel without armor. Character gains spy skill, power +3, armor = 4, and immunity to attrition < 3. Imperial Barrier is canceled. Blasters deploy for free and fire for free on this character.");
        addIcons(Icon.REFLECTIONS_II);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Leia));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Rebel, Filters.not(Filters.hasArmor));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.Rebel, Filters.not(Filters.hasArmor));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, hasAttached, Keyword.SPY));
        modifiers.add(new PowerModifier(self, hasAttached, 3));
        modifiers.add(new ResetArmorModifier(self, hasAttached, 4));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, hasAttached, 3));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.blaster, hasAttached));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.blaster, Filters.attachedTo(hasAttached))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Imperial_Barrier)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}