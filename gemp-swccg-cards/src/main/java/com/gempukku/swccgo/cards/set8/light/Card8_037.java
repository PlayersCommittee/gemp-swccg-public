package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Count Me In
 */
public class Card8_037 extends AbstractNormalEffect {
    public Card8_037() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Count_Me_In, Uniqueness.UNIQUE);
        setLore("Leia couldn't let Han out of her sight.");
        setGameText("Deploy on Leia if That's One on table. I Know is a Used Interrupt. Once per battle, when Leia is battling with Han and Chewie and you are about to draw a card for battle or weapon destiny, you may instead us Leia's ability number.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Leia;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Leia;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Thats_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
         modifiers.add(new UsedInterruptModifier(self, Filters.I_Know));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if ((TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                || TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId))
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Han)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Chewie)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Leia)) {

            PhysicalCard leia = Filters.findFirstActive(game, self, Filters.Leia);
            final float abilityNumber = leia.getBlueprint().getAbility();
            DestinyType destinyType = game.getGameState().getTopDrawDestinyState().getDrawDestinyEffect().getDestinyType();

            // Perform result(s)
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Substitute destiny");
            action.setActionMsg("Substitute " + GameUtils.getCardLink(leia) + "'s ability number of " + GuiUtils.formatAsString(abilityNumber) + " for " + destinyType.getHumanReadable());
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendEffect(
                    new SubstituteDestinyEffect(action, abilityNumber)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}