package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Honor Of The Jedi
 */
public class Card9_033 extends AbstractNormalEffect {
    public Card9_033() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Honor_Of_The_Jedi, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("The final act of Luke's ascension to Jedi Knighthood was sending off his father, draped in the garb of Vader.");
        setGameText("Deploy on table. Unless opponent occupies three battlegrounds, each time you must lose Force (except from your card, battle damage or a Force drain at a battleground), reduce the loss by 2. (Immune to Alter while you occupy any battleground).");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && !TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.or(Filters.your(self), Filters.immuneToCardTitle(self.getTitle())))
                && !TriggerConditions.isAboutToLoseForceFromBattleDamage(game, effectResult, playerId)
                && !TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.battleground)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, gameTextSourceCardId)
                && !GameConditions.occupies(game, opponent, 3, Filters.battleground)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce Force loss by 2");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerForceLossEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ReduceForceLossEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OccupiesCondition(playerId, Filters.battleground), Title.Alter));
        return modifiers;
    }
}