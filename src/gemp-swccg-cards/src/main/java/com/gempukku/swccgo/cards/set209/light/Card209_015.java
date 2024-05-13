package com.gempukku.swccgo.cards.set209.light;

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
 * Title: There Is Another
 */
public class Card209_015 extends AbstractDefensiveShield {
    public Card209_015() {
        super(Side.LIGHT, PlayCardZoneOption.ATTACHED, Title.There_Is_Another, ExpansionSet.SET_9, Rarity.V);
        setLore("Princess Leia Organa. Alderaanian senator. Targeted by Vader for capture and interrogation. The Dark Lord of the Sith wanted her alive.");
        setGameText("Plays on Your Destiny unless Leia or Luke has been deployed this game (even as a captive). We're The Bait is lost. Opponent's Objective and [Death Star II] Effects target Leia instead of Luke. Attempts to cross Leia over are -2. Opponent loses no Force to their Objective.");
        addIcons(Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Your_Destiny;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !GameConditions.hasDeployedAtLeastXCardsThisGame(game, playerId, 1, Filters.or(Filters.Luke, Filters.Leia));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        // We're The Bait is lost.
        Filter toBeLost = Filters.Were_The_Bait;

        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> lostCards = Filters.filterActive(game, self, toBeLost);

            if (!lostCards.isEmpty()) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Make We're The Bait lost");
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

        Filter ds2Filter = Filters.or(Filters.Bring_Him_Before_Me, Filters.Take_Your_Fathers_Place, Filters.Your_Destiny, Filters.Insignificant_Rebellion);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendModifierEffectsModifier(self, Filters.Luke, ds2Filter));
        modifiers.add(new ModifyGameTextModifier(self, ds2Filter, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE));
        modifiers.add(new CrossOverAttemptTotalModifier(self, Filters.Leia, -2));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.and(Filters.opponents(self), Filters.Objective), opponent));
        return modifiers;
    }

}
