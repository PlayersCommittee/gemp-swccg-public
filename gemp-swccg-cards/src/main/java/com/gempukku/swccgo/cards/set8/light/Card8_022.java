package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Mon Mothma
 */
public class Card8_022 extends AbstractRebel {
    public Card8_022() {
        super(Side.LIGHT, 1, 5, 2, 3, 8, "Mon Mothma", Uniqueness.UNIQUE);
        setLore("Native of Chandrila. Leader of the Alliance. A former member of the Imperial Senate where she was a formidable opponent of then-Senator Palpatine. Friend of Bail Organa.");
        setGameText("While at your war room, subtracts 1 from each opponent's battle destiny at all system locations and adds 2 to forfeit of all non-unique Rebels. Once during each of your deploy phases, may deploy one non-unique Rebel from Reserve Deck; reshuffle.");
        addPersona(Persona.MON_MOTHMA);
        addIcons(Icon.ENDOR);
        addKeywords(Keyword.FEMALE, Keyword.LEADER, Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atYourWarRoom = new AtCondition(self, Filters.and(Filters.your(self), Filters.war_room));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.system, atYourWarRoom, -1, game.getOpponent(self.getOwner()), true));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.non_unique, Filters.Rebel), atYourWarRoom, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MON_MOTHMA__DOWNLOAD_REBEL;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a non-unique Rebel from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.non_unique, Filters.Rebel), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
