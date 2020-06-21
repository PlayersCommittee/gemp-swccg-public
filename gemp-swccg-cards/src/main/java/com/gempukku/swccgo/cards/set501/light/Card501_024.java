package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TakeFirstBattleWeaponsSegmentActionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Tobias Beckett
 */
public class Card501_024 extends AbstractAlien {
    public Card501_024() {
        super(Side.LIGHT, 0, 3, 4, 3, 6, "Tobias Beckett", Uniqueness.UNIQUE);
        setLore("Smuggler, musician, thief, and information broker. Glee Anselmian.");
        setGameText("Adds a [DS] icon at same battleground site. Once per game, may 'smuggle' a blaster (deploy on Beckett from Lost pile, even as a 'react'). While armed with a blaster, defense value +2 and if a battle was just initiated, may take first weapons phase action.");
        addPersona(Persona.BECKETT);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.MUSICIAN, Keyword.THIEF, Keyword.INFORMATION_BROKER);
        setSpecies(Species.GLEE_ANSELMIAN);
        setTestingText("Tobias Beckett");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IconModifier(self, Filters.and(Filters.battleground_site, Filters.here(self)), Icon.DARK_FORCE));
        modifiers.add(new DefenseValueModifier(self, new ArmedWithCondition(self, Filters.blaster), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        //Once per game, may 'smuggle' a blaster (deploy on Beckett from Lost pile, even as a 'react')
        GameTextActionId gameTextActionId = GameTextActionId.TOBIAS_BECKETT__SMUGGLE_BLASTER;
        if(GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.hasLostPile(game, playerId)){
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("'Smuggle' a blaster");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.blaster, Filters.sameCardId(self), false, true, false)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isArmedWith(game, self, Filters.blaster)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take first weapons phase action");
            action.setActionMsg("Take first weapons phase action");
            // Perform result(s)
            action.appendEffect(
                    new TakeFirstBattleWeaponsSegmentActionEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
