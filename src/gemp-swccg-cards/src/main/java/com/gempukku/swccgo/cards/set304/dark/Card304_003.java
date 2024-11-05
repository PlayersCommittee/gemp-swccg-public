package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToBattleForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotUseCardToTransportToOrFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Duk
 */
public class Card304_003 extends AbstractImperial {
    public Card304_003() {
        super(Side.DARK, 3, 2, 2, 3, 2, "Duk", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("The apprentice of Kamjin Lap'lamiz, Duk has been utilized as an assassin. People are noticely concerned with his reappearance and what Kamjin had him doing.");
        setGameText("May deploy for free if a battle has been initiated with Kamjin. Power is +2 in battle with Kamjin.");
        addIcons(Icon.WARRIOR, Icon.CSP);
		setSpecies(Species.TOGRUTA);
		addKeywords(Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        Condition kamjinBattleFree = new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self), Filters.Kamjin));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToBattleForFreeModifier(self, kamjinBattleFree));
		modifiers.add(new PowerModifier(self, new InBattleWithCondition(self, Filters.Kamjin), 2));
        return modifiers;
    }
	
	@Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.Kamjin)) {
            if (Filters.deployableToLocation(self, Filters.Kamjin, true, 0).accepts(game, self)) {

                PlayCardAction playCardAction = self.getBlueprint().getPlayCardAction(playerId, game, self, self, true, 0, null, null, null, null, null, false, 0, Filters.Kamjin, null);
                if (playCardAction != null) {
                    return Collections.singletonList(playCardAction);
                }
            }
        }
        return null;
    }
		
}
