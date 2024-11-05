package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DrivingCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToDrivenBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Character
 * Subtype: Alien
 * Title: Mercenary Pilot
 */
public class Card112_013 extends AbstractAlien {
    public Card112_013() {
        super(Side.DARK, 2, 2, 2, 1, 3, "Mercenary Pilot", Uniqueness.UNRESTRICTED, ExpansionSet.JPSD, Rarity.PM);
        setLore("Smugglers. Candidates who resent authority often abandon Imperial academies to sell their piloting skills to criminals. Will work for any high paying crime syndicate.");
        setGameText("Adds 2 to power of anything he pilots or drives. When driving a transport vehicle, adds one battle destiny. When piloting at a cloud sector, once per turn adds one battle destiny during battle at a related exterior site.");
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.MERCENARY, Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new DrivingCondition(self, Filters.transport_vehicle), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.exterior_site, Filters.relatedSite(self)))
                && GameConditions.isOncePerTurn(game, self, gameTextSourceCardId)
                && GameConditions.isPilotingAt(game, self, Filters.cloud_sector)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1, self.getOwner()));
            return Collections.singletonList(action);
        }
        return null;
    }
}
