package com.gempukku.swccgo.cards.set224.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Effect
 * Title: All Wrapped Up (V)
 */
public class Card224_001 extends AbstractNormalEffect {
    public Card224_001() {
        super(Side.DARK, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.All_Wrapped_Up, Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("A capture cable is a quick and effective way for bounty hunters to suddenly snare their target.");
        setGameText("Deploy on table. May [download] [Jabba's Palace] Ord Mantell. Unless Court Of The Vile Gangster on table, [Dagobah] and [Cloud City] bounty hunters are forfeit +2. If opponent's character is about to be forfeited, your bounty hunter present may capture that character (character is first restored to normal). [Immune to Alter.]");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_24);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.or(Icon.DAGOBAH, Icon.CLOUD_CITY), Filters.bounty_hunter), new UnlessCondition(new OnTableCondition(self, Filters.Court_Of_The_Vile_Gangster)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.ALL_WRAPPED_UP__DOWNLOAD_ORD_MANTELL;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Ord_Mantell)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Ord Mantell from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.JABBAS_PALACE, Filters.Ord_Mantell_system), true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;
        Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.canBeTargetedBy(self, targetingReason),
                Filters.at(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.bounty_hunter))));

        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeited(game, effectResult, opponentsCharacterFilter)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Capture " + GameUtils.getFullName(cardToBeForfeited));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, cardToBeForfeited) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            result.getForfeitCardEffect().preventEffectOnCard(finalTarget);
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
