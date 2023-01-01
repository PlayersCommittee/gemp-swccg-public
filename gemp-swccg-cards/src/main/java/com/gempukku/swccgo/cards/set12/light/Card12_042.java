package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceAndStackFaceDownEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Credits Will Do Fine
 */
public class Card12_042 extends AbstractNormalEffect {
    public Card12_042() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Credits_Will_Do_Fine, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.C);
        setLore("'No, they won't!'");
        setGameText("Use 2 Force to deploy on table. If you just initiated a Force drain (or won a battle) at Watto's Junkyard, opponent loses 1 Force (cannot be reduced) and stacks lost card here face down. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.Wattos_Junkyard)
                || TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.Wattos_Junkyard)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force and stack here");
            action.setActionMsg("Make opponent lose 1 Force and stack lost card face down on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceAndStackFaceDownEffect(action, opponent, 1, self) {
                        @Override
                        public boolean isShownIfLostFromHand() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}