package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AllAbilityAtLocationProvidedByCondition;
import com.gempukku.swccgo.cards.conditions.DefendingBattleAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: Alien
 * Title: R'kik D'nec, Hero Of The Dune Sea (V)
 */
public class Card208_010 extends AbstractAlien {
    public Card208_010() {
        super(Side.LIGHT, 1, 3, 3, 1, 3, "R'kik D'nec, Hero Of The Dune Sea", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("A tribe of Tusken Raiders, a herd of angry banthas, a raging Krayt Dragon and R'kik. Minutes later, the Jawa emerged from the Dune Sea, a bantha tusk over his shoulder.");
        setGameText("Deploys only on Tatooine. Once per game, may [download] Jawa Ion Gun (for free) on R'kik. When defending a battle at Dune Sea or a desert, and all your ability here is provided by Jawas, opponent may not draw battle destiny here.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR, Icon.VIRTUAL_SET_8);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RKIK_DNEC_HERO_OF_THE_DUNE_SEA__DOWNLOAD_JAWA_ION_GUN;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Jawa Ion Gun from Reserve Deck");
            action.setActionMsg("Deploy a Jawa Ion Gun on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Jawa_Ion_Gun, Filters.sameCardId(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawBattleDestinyModifier(self, Filters.here(self), new AndCondition(new DefendingBattleAtCondition(self, Filters.or(Filters.Dune_Sea, Filters.desert)),
                new AllAbilityAtLocationProvidedByCondition(self, playerId, Filters.here(self), Filters.Jawa)), opponent));
        return modifiers;
    }
}
