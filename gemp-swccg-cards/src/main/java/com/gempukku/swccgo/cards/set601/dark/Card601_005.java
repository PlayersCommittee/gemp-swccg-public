package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 8
 * Type: Effect
 * Title: Den Of Thieves & Special Delivery
 */
public class Card601_005 extends AbstractNormalEffect {
    public Card601_005() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Den Of Thieves & Special Delivery", Uniqueness.UNIQUE);
        addComboCardTitles("Den Of Thieves", Title.Special_Delivery);
        setGameText("Deploy on table. Once per turn, if Skyhook Platform on table and opponent just forfeited a character present with your slaver, may 'enslave' that character (stack character face down under Skyhook Platform). May place an 'enslaved' character in owner's Lost Pile to activate up to 3 Force. Once per turn, while Indentured To The Empire on table, may cancel a Force drain by placing here from hand any non-unique slaver. Slavers may deploy from here as if from hand. May not be canceled. (Immune to Alter.)");
        addIcons(Icon.JABBAS_PALACE, Icon.BLOCK_8);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.stackedOn(self), Filters.slaver)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        String opponent = game.getOpponent(playerId);



        //TODO optional after trigger action: if opp just forfeited a character may enslave


        Filter nonuniqueSlaver = Filters.and(Filters.non_unique, Filters.slaver);

        //optional after trigger action: Once per turn, while Indentured To The Empire on table, may cancel a Force drain by placing here from hand any non-unique slaver.
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.title("Indentured To The Empire"))
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasInHand(game, playerId, nonuniqueSlaver)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, nonuniqueSlaver));
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            actions.add(action);
        }


        return actions;
    }


    //TODO top level action: May place an 'enslaved' character in owner's Lost Pile to activate up to 3 Force


}