package com.gempukku.swccgo.cards.set202.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Keeper Of The Peace
 */
public class Card202_004 extends AbstractJediMaster {
    public Card202_004() {
        super(Side.LIGHT, 1, 5, 3, 7, 7, "Yoda, Keeper Of The Peace", Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setLore("Yoda calls upon the Force often to try and foresee what destiny has in store.");
        setGameText("Deploys -1 to Coruscant. May lose 1 Force to cancel Force Lightning targeting your character present. Neither player may draw more than one battle destiny here (those destiny draws may not be modified or canceled). Immune to attrition.");
        addPersona(Persona.YODA);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_2);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Coruscant));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Force_Lightning, Filters.and(Filters.your(self), Filters.character, Filters.present(self)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition inBattle = new InBattleCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotModifyBattleDestinyModifier(self, inBattle));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, inBattle));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, inBattle, 1, playerId));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, inBattle, 1, opponent));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}
