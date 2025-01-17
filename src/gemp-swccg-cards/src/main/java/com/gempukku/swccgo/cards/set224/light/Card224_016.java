package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 24
 * Type: Character
 * Subtype: Rebel
 * Title: Han Solo (V)
 */

public class Card224_016 extends AbstractRebel {
    public Card224_016() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, Title.Han_Solo, Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Smuggler, gambler and 'freelance law-bender.' Crafty Corellian pirate. Rebel hero. Owns Millennium Falcon. Co-pilot Chewbacca promised him 'life-debt.' Has bounty on head.");
        setGameText("[Pilot] 3. Draws one battle destiny if unable to otherwise. Adds one battle destiny with Chewie. While piloting Falcon, adds 2 to maneuver and, once during battle, may re-circulate.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER, Keyword.PIRATE);
        addPersona(Persona.HAN);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Falcon);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.Chewie), 1));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Falcon, Filters.hasPiloting(self)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isPiloting(game, self, Filters.Falcon)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.hasUsedPile(game, playerId)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);

            action.setText("Re-circulate");
            action.setActionMsg("Re-circulate");
            action.appendUsage(
                new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                new RecirculateEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
