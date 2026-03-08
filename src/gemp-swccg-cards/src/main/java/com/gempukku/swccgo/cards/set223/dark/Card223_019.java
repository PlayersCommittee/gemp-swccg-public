package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Effect
 * Subtype: Normal
 * Title: Ominous Rumors (V)
 */
public class Card223_019 extends AbstractNormalEffect {
    public Card223_019() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Ominous_Rumors, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setVirtualSuffix(true);
        setLore("Rumors of a new 'technological terror' filled the galaxy with dread.");
        setGameText("Deploy on Bunker; may choose: Activate 1 Force for each opponent's non-battleground location on table, or take Endor Shield into hand from Force Pile; reshuffle. Fenson, Kensaric, and Perimeter Patrol's game text is canceled. Fallen Portal may not target Endor sites. [Immune to Alter.]");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_23);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Bunker;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OMINOUS_RUMORS__UPLOAD_CARD_OR_ACTIVATE;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canActivateForce(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            
            final int opponentsNonBattlegroundLocations = Filters.countTopLocationsOnTable(game, Filters.and(Filters.opponents(playerId), Filters.non_battleground_location));
            
            action.setText("Activate Force");
            action.setActionMsg("Activate 1 Force for each opponent's non-battleground location on table");
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, opponentsNonBattlegroundLocations));
            actions.add(action);
        }
        
        // Intentionally keeping the same gameTextActionId for this action so that it is shared between both possible responses; player will only be able to use one of them

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Endor Shield from Force Pile");
            action.setActionMsg("Take Endor Shield into hand from Force Pile");
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, Filters.Endor_Shield, true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.or(Filters.Fenson, Filters.Kensaric, Filters.Perimeter_Patrol)));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.Endor_site, Filters.Fallen_Portal));        
        return modifiers;
    }
}
