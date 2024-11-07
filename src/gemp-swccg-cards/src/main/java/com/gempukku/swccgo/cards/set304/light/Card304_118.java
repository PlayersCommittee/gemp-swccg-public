package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Title: Number One Gangster
 */
public class Card304_118 extends AbstractNormalEffect {
    public Card304_118() {
        super(Side.LIGHT, 7, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Number_One_Gangster, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("No one knows why, but Sqygorn is Candon Coburn's number one gangster. Candon turns a blind eye to the horrible acts that Sqygorn commits.");
        setGameText("Deploy on table. Once per turn, if Sqygorn on table, may deploy a gangster from Reserve Deck; reshuffle. While present with opponent's [CSP] character, Sqygorn is defense value +2 and immune to attrition. If Sqygorn just lost, place Effect in Used Pile. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.NUMBER_ONE_GANGSTER__DOWNLOAD_GANGSTER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Sqygorn)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy gangster from Reserve Deck");
            action.setActionMsg("Deploy a gangster from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.gangster, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter sqygornPresentWithOpponentsCSP = Filters.and(Filters.Sqygorn, Filters.presentWith(self, Filters.and(Filters.opponents(self), Filters.CSP_character)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, sqygornPresentWithOpponentsCSP, 2));
        modifiers.add(new ImmuneToAttritionModifier(self, sqygornPresentWithOpponentsCSP));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.Sqygorn)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}