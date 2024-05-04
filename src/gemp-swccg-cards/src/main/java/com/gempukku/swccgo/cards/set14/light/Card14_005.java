package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AllCharactersOnSystemCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Alien
 * Title: Boss Nass
 */
public class Card14_005 extends AbstractAlien {
    public Card14_005() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, Title.Boss_Nass, Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("Ankura Gungan who is the leader of his people. Prone to nervous tics. Personally responsible for uniting the Ankura and Otolla races together.");
        setGameText("While at an underwater site and all of your characters on Naboo are Gungans: add one battle destiny in battles at Naboo sites and once during your deploy phase may deploy a Gungan or Steady, Steady from Reserve Deck; reshuffle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.GUNGAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new AtCondition(self, Filters.underwater_site),
                new DuringBattleAtCondition(Filters.Naboo_site), new AllCharactersOnSystemCondition(self, playerId, Title.Naboo, Filters.Gungan)),
                1, playerId, true));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BOSS_NASS__DOWNLOAD_GUNGAN_OR_STEADY_STEADY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.underwater_site)
                && GameConditions.isAllCharactersOnSystem(game, self, playerId, Title.Naboo, Filters.Gungan)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Gungan or Steady, Steady from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Gungan, Filters.Steady_Steady), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
