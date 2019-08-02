package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 9
 * Type: Character
 * Subtype: Alien
 * Title: Yoxgit (v)
 */

public class Card209_014 extends AbstractAlien {
    public Card209_014() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Yoxgit", Uniqueness.UNIQUE);
        setLore("Male Ugnaught. Left Cloud City after the Empire took control. Works for Hermi Odle, helping to supply him with various weaponry. Hopes to someday return to Cloud City.");
        setGameText("Deploys free to (and power +2 at) a Cloud City, Jabba's Palace, or Maz's Castle location. Once per turn, if you just drew an alien (or [Independent] starship) for destiny, may take that card into hand to cancel and redraw that destiny.");
        addIcons(Icon.VIRTUAL_SET_9, Icon.JABBAS_PALACE);
        setSpecies(Species.UGNAUGHT);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter CC_JP_OR_MP_Location = Filters.or(Filters.Cloud_City_location, Filters.Jabbas_Palace_site, Filters.Mazs_Castle_Location);
        modifiers.add(new DeploysFreeToLocationModifier(self, CC_JP_OR_MP_Location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter CC_JP_OR_MP_Location = Filters.or(Filters.Cloud_City_location, Filters.Jabbas_Palace_site, Filters.Mazs_Castle_Location);
        modifiers.add(new PowerModifier(self, new AtCondition(self, CC_JP_OR_MP_Location), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.or(Filters.alien, Filters.and(Icon.INDEPENDENT, Filters.starship)))
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take destiny card into hand and redraw");
            action.setActionMsg("Take just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", into hand");
            // Update usage limit(s)
            action.appendUsage(new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(new TakeDestinyCardIntoHandEffect(action));
            action.appendEffect(new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
