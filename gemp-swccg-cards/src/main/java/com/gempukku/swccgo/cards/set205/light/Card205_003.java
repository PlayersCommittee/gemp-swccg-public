package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Rebel
 * Title: Tycho Celchu (V)
 */
public class Card205_003 extends AbstractRebel {
    public Card205_003() {
        super(Side.LIGHT, 2, 3, 2, 2, 4, "Tycho Celchu", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Spy fighting for the memory of his homeworld of Alderaan. Rogue Squadron pilot. Volunteered to fly an A-wing at the Battle of Endor. Former TIE fighter pilot.");
        setGameText("[Pilot] 3. May reveal from hand to steal an unpiloted TIE from opponent's Lost Pile and deploy both simultaneously. Any snub fighter he pilots is maneuver +1 and immune to attrition < 4.");
        addPersona(Persona.TYCHO);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.SPY, Keyword.ROGUE_SQUADRON);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter snubFighterPiloted = Filters.and(Filters.snub_fighter, Filters.hasPiloting(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, snubFighterPiloted, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, snubFighterPiloted, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.TYCHO_CELCHU__STEAL_TIE_FROM_LOST_PILE;

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canStealCardsFromLostPile(game, playerId, self, gameTextActionId)
                && Filters.isUniquenessOnTableNotReached.accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to steal an unpiloted TIE");
            action.setActionMsg("Steal an unpiloted TIE from opponent's Lost Pile and deploy both simultaneously");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromLostPileSimultaneouslyWithCardEffect(action, self, opponent, Filters.and(Filters.unpiloted, Filters.tieCountNoMoreThan(1)), false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
