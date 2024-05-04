package com.gempukku.swccgo.cards.set219.dark;

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
import com.gempukku.swccgo.logic.effects.LoseForceAndStackFaceUpEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Effect
 * Title: Thrawn's Art Collection
 */
public class Card219_020 extends AbstractNormalEffect {
    public Card219_020() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Thrawns_Art_Collection, Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setGameText("Use 4 Force to deploy on table. If you just initiated a Force drain (or won a battle) at same battleground as Thrawn, " +
                "opponent loses 1 Force (cannot be reduced) and stacks it here face up. (These stacked cards are artwork cards.) [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_19);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.and(Filters.battleground, Filters.sameLocationAs(self, Filters.Thrawn)))
                || TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.and(Filters.battleground, Filters.sameLocationAs(self, Filters.Thrawn)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force and stack here");
            action.setActionMsg("Make opponent lose 1 Force and stack lost card face up on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceAndStackFaceUpEffect(action, opponent, 1, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
