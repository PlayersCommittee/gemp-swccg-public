package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.InfectCharacterEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCarryModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Effect
 * Title: Crystal Infection
 */
public class Card302_039 extends AbstractNormalEffect {
    public Card302_039() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Crystal_Infection, Uniqueness.UNRESTRICTED, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("The Children of Mortis's use of crystals took deadly hold of");
        setGameText("If opponent has a character, creature, or creature vehicle present at same site as Crystal Mist, deploy on that opponent's character, creature, or creature vehicle during any control phase. Target loses all weapons, is power -2 and may no longer carry weapons. Immune to Alter.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringEitherPlayersPhase(game, Phase.CONTROL);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.creature, Filters.creature_vehicle), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_INFECTED),
                Filters.presentAt(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Crystal_Mist))));
    }

    @Override
    public Filter getValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.character, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_INFECTED));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard character = self.getAttachedTo();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            // Perform result(s)
            action.setText("Infect " + GameUtils.getFullName(character));
            action.setActionMsg("Infect " + GameUtils.getCardLink(character));
            // Perform result(s)
            action.appendEffect(
                    new InfectCharacterEffect(action, character, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttached, -2));
        modifiers.add(new MayNotCarryModifier(self, hasAttached, Filters.weapon));
        return modifiers;
    }
}