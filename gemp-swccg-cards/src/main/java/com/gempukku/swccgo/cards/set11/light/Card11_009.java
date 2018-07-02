package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.*;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Republic
 * Title: Padme Naberrie (AI)
 */
public class Card11_009 extends AbstractRepublic {
    public Card11_009() {
        super(Side.LIGHT, 3, 2, 3, 3, 5, Title.Padme, Uniqueness.UNIQUE);
        setAlternateImageSuffix(true);
        setLore("Queen Amidala posed as one of her own handmaidens for added safety as well as to keep an eye on her Jedi protectors. Was to be protected by the Jedi at all times.");
        setGameText("Twice per game may deploy Qui-Gon or Obi-Wan here from Reserve Deck; reshuffle. While present with your Jedi at a site, opponent may not target Amidala with weapons.");
        addPersona(Persona.AMIDALA);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.HANDMAIDEN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PADME_NABERRIE__DOWNLOAD_QUIGON_OR_OBIWAN;

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, new HashSet<Persona>(Arrays.asList(Persona.QUIGON, Persona.OBIWAN)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Qui-Gon or Obi-Wan from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.QuiGon, Filters.ObiWan), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new AndCondition(new PresentWithCondition(self,
                Filters.and(Filters.your(self), Filters.Jedi)), new AtCondition(self, Filters.site))));
        return modifiers;
    }
}
