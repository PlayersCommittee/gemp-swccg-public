package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
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
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Gray Squadron Y-wing Pilot
 */
public class Card9_015 extends AbstractRebel {
    public Card9_015() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Gray Squadron Y-wing Pilot", Uniqueness.RESTRICTED_3, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("Veteran pilots of the reliable Koensayr starfigher were assigned by Ackbar to a key role at Endor. Both the pilots and their venerable Y-wings were up to the task.");
        setGameText("Adds 2 to power of anything he pilots. When piloting a Y-wing, draws one battle destiny if not able to otherwise. When a starship he pilots fires an ion cannon, once per turn, may draw two weapon destiny and choose one.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.GRAY_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingYwing = new PilotingCondition(self, Filters.Y_wing);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingYwing, 1));
        
        return modifiers;
   }
   
    
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        
        PhysicalCard starshipPiloting = Filters.findFirstActive(game,self,Filters.and(Filters.hasPiloting(self),Filters.starship));
        
        if (starshipPiloting!=null
        		&& TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId, Filters.ion_cannon)
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
