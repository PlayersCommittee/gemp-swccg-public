package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByPermanentWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Rebel
 * Title: Ahsoka Tano
 */
public class Card211_059 extends AbstractRebel {
    public Card211_059() {
        super(Side.LIGHT, 1, 5, 5, 6, 7, "Ahsoka Tano", Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("Female Togruta.");
        setGameText("Subtracts 1 from opponent's battle destiny draws here. During any deploy phase, if a Padawan or a Sith character at an adjacent site, Ahsoka may move to that site (using landspeed) as a regular move. Immune to [Permanent Weapon] weapons and attrition < 5.");
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_11);
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.TOGRUTA);
        addPersona(Persona.AHSOKA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), -1, game.getDarkPlayer()));
        //it might be self, Filter.self
        modifiers.add(new MayNotBeTargetedByPermanentWeaponsModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    //new action: move
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_5;

        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY)
            && Filters.movableAsRegularMove(playerOnLightSideOfLocation, false, 0, false, Filters.and(Filters.adjacentSite(self), Filters.or(Filters.sameSiteAs(self, Filters.Sith), Filters.sameSiteAs(self, Filters.padawan)))).accepts(game, self)
            ) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Move Ahsoka to a Sith or Padawan");
            action.appendTargeting(
                new ChooseCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose site to move to", Filters.and(Filters.adjacentSite(self), Filters.or(Filters.sameSiteAs(self, Filters.Sith), Filters.sameSiteAs(self, Filters.padawan)))) {
                @Override
                protected void cardSelected(PhysicalCard targetSite) {
                    action.addAnimationGroup(self);
                    action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to an adjacent site where there is a Sith or Padawan");
                            // Perform result(s)
                    action.appendEffect(
                    new MoveCardAsRegularMoveEffect(action, playerOnLightSideOfLocation, self, false, false, Filters.sameTitle(targetSite)));
                }
        }
            );
            actions.add(action);
            }

        return actions;
    }
}