package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Set: Set 24
 * Type: Character
 * Subtype: Alien
 * Title: Jaxxon T. Tumperakki
 */
public class Card224_017 extends AbstractAlien {
    public Card224_017() {
        super(Side.LIGHT, 3, 3, 3, 3, 5, Title.Jaxxon_T_Tumperakki, Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Lepi gambler, smuggler and thief. Star-hopper mercenary.");
        setGameText("Deploys -1 to Cloud City. " +
                "Any blaster may deploy on Jaxxon. " +
                "Once per game, may deploy a blaster on Jaxxon from Lost Pile (or Jaxxon may steal one from opponent's Lost Pile). " +
                "While at opponent's battleground, Imperial Enforcement is suspended.");
        addKeywords(Keyword.GAMBLER, Keyword.SMUGGLER, Keyword.THIEF, Keyword.MERCENARY);
        setSpecies(Species.LEPI);
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_24);
        addIcon(Icon.WARRIOR, 2);
        addPersona(Persona.JAXXON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Cloud_City_location));
        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();

        modifiers.add(new MayDeployToTargetModifier(self, Filters.blaster, self));
        modifiers.add(new MayUseWeaponModifier(self, Filters.blaster));
        modifiers.add(new SuspendsCardModifier(self, Filters.Imperial_Enforcement,
                new AtCondition(self, Filters.and(Filters.opponents(self), Filters.battleground))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.JAXXON__DEPLOY_BLASTER_FROM_LOST_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY))
                && ((GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId))
                || (GameConditions.canSearchOpponentsLostPile(game, playerId, self, gameTextActionId)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Search Lost Pile for blaster");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.LOST_PILE) {
                        @Override
                        protected void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Search " + cardPileOwner + "'s Lost Pile for a blaster");
                            // Your Lost Pile
                            if (Objects.equals(cardPileOwner, playerId)) {
                                action.appendEffect(
                                        new DeployCardToTargetFromLostPileEffect(action, Filters.blaster, Filters.sameCardId(self), false));
                            // Opponent's Lost Pile
                            } else {
                                action.appendEffect(
                                        new StealCardAndAttachFromLostPileEffect(action, playerId, self, Filters.blaster));
                            }
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
