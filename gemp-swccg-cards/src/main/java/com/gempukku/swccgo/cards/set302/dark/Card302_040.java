package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Imperial
 * Title: Brotherhood Pilot
 */
public class Card302_040 extends AbstractImperial {
    public Card302_040() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Brotherhood Pilot", Uniqueness.RESTRICTED_2, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("The Brotherhood invests considerable time in training its pilots. Specifically in training with the latest TIE models to dominate enemy combatants.");
        setGameText("Adds 2 to power of anything she pilots. When piloting an TIE, draws one battle destiny if not able to otherwise. When a starship she pilots fires a cannon, once per turn, may draw two weapon destiny and choose one. ");
        addIcons(Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingTIE = new PilotingCondition(self, Filters.TIE);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingTIE, 1));
        
        return modifiers;
   }
   
    
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        
        PhysicalCard starshipPiloting = Filters.findFirstActive(game,self,Filters.and(Filters.hasPiloting(self),Filters.starship));
        
        if (starshipPiloting!=null
        		&& TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId, Filters.starship_cannon)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringWeaponFiringAtTarget(game, Filters.weaponBeingFiredBy(starshipPiloting), Filters.any)
                && GameConditions.canDrawDestinyAndChoose(game, 2)
                ) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw two and choose one");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DrawDestinyAndChooseInsteadEffect(action, 2, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
