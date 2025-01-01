package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.CrossOverAttemptTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Defensive Shield
 * Title: There Is Another Lap'lamiz
 */
public class Card304_143 extends AbstractDefensiveShield {
    public Card304_143() {
        super(Side.LIGHT, PlayCardZoneOption.ATTACHED, Title.There_Is_Another_Laplamiz, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("While Kamjin claims to be a benevolent Emperor he would burn the Empire to the ground to have his children back at his side. Especially his eldest son, Kai. A fate Kai has accepted if it helps others.");
        setGameText("Plays on Complications unless Kai or Locita has been deployed this game (even as a captive). Sibling Bait is lost. Opponent's Objective and [Great Hutt Expansion] Effects target Kai instead of Locita. Attempts to cross Kai over are -2. Opponent loses no Force to their Objective.");
        addIcons(Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Complications;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !GameConditions.hasDeployedAtLeastXCardsThisGame(game, playerId, 1, Filters.or(Filters.Locita, Filters.Kai));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        // We're The Bait is lost.
        Filter toBeLost = Filters.Sibling_Bait;

        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> lostCards = Filters.filterActive(game, self, toBeLost);

            if (!lostCards.isEmpty()) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Make Sibling Bait lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(lostCards) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, lostCards));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        String opponent = game.getOpponent(player);

        Filter greatHuttExpansionFilter = Filters.or(Filters.Hostile_Takeover, Filters.Usurped, Filters.Complications, Filters.Competitive_Advantage);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendModifierEffectsModifier(self, Filters.Locita, greatHuttExpansionFilter));
        modifiers.add(new ModifyGameTextModifier(self, greatHuttExpansionFilter, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA));
        modifiers.add(new CrossOverAttemptTotalModifier(self, Filters.Kai, -2));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.and(Filters.opponents(self), Filters.Objective), opponent));
        return modifiers;
    }

}
