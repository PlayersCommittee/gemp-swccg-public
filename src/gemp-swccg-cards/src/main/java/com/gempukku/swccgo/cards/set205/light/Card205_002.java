package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 5
 * Type: Character
 * Subtype: Rebel
 * Title: Ensign Chad Hilse
 */
public class Card205_002 extends AbstractRebel {
    public Card205_002() {
        super(Side.LIGHT, 2, 2, 2, 1, 3, "Ensign Chad Hilse", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setLore("Corellian Corvette trooper Ensign Chad Hilse, an Alderaanian, typifies the loyal Rebel volunteers dedicated to defeating the Empire. Trained in starship and ground combat.");
        setGameText("During battle, if present at a site with a stormtrooper (or Rebel leader), may add one destiny to total power only. Once per turn, may subtract 1 from a destiny draw of an opponent's Interrupt or weapon targeting a non-Jedi Rebel here.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.TROOPER);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)
                && GameConditions.isPresentAt(game, self, Filters.site)
                && GameConditions.isPresentWith(game, self, Filters.or(Filters.stormtrooper, Filters.Rebel_leader))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny to total power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        Filter nonJediRebelHere = Filters.and(Filters.Rebel, Filters.not(Filters.Jedi), Filters.here(self));

        // Check condition(s)
        if ((TriggerConditions.isDestinyJustDrawnTargeting(game, effectResult, Filters.and(Filters.opponents(self), Filters.Interrupt), nonJediRebelHere)
                || TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.and(Filters.opponents(self), Filters.weapon), nonJediRebelHere))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 1 from destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            return Collections.singletonList(action);
        }
        return null;
    }
}