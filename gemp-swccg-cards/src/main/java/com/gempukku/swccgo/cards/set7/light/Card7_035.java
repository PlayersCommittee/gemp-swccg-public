package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Princess Organa
 */
public class Card7_035 extends AbstractRebel {
    public Card7_035() {
        super(Side.LIGHT, 1, 3, 3, 3, 7, "Princess Organa", Uniqueness.UNIQUE);
        setLore("Adopted by Bail Organa. Former senator of Alderaan. Natural leader. Helped steal the Death Star plans. A key figure in the Rebel Alliance.");
        setGameText("While at a Rebel Base location, subtracts 1 from opponent's Force drains at related locations. Once during each of your deploy phases, may deploy one Leia Of Alderaan, Reflection or leader of ability < 3 to same location from Reserve Deck; reshuffle.");
        addPersona(Persona.LEIA);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.SENATOR, Keyword.LEADER);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.relatedLocation(self), new AtCondition(self, Filters.Rebel_Base_location),
                -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PRINCESS_ORGANA__DOWNLOAD_LEIA_OF_ALDERAAN_REFLECTION_OR_LEADER;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Arrays.asList(Title.Leia_Of_Alderaan, Title.Reflection))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Leia Of Alderaan, Reflection, or leader of ability < 3 from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Leia_Of_Alderaan, Filters.Reflection,
                            Filters.and(Filters.leader, Filters.abilityLessThan(3))), Filters.sameLocation(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
