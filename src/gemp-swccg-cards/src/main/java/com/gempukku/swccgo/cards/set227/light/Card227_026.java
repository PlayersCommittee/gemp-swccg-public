package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractAlienRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Alien/Rebel
 * Title: The Mandalorian & Grogu
 */
public class Card227_026 extends AbstractAlienRebel {
    public Card227_026() {
        super(Side.LIGHT, 1, 6, 6, 4, 7, "The Mandalorian & Grogu", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("Din Djarin. Foundling child. Mandalorians.");
        setGameText("[Pilot] 3. While piloting or at a site, draws two battle destiny if unable to otherwise. Once per game, may [upload] (or retrieve) Beskar Deflection. Immune to None Shall Pass.");
        setArmor(5);
        addPersonas(Persona.DIN, Persona.GROGU);
        setSpecies(Species.MANDALORIAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whilePilotingOrAtSite = new OrCondition(new PilotingCondition(self), new AtCondition(self, Filters.site));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToTitleModifier(self, Title.None_Shall_Pass));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingOrAtSite, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.THE_MANDALORIAN_AND_GROGU__UPLOAD_OR_RETRIEVE_BESKAR_DEFLECTION;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            // Check condition(s)
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take Beskar Deflection into hand from Reserve Deck");
                action.appendUsage(
                    new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Beskar_Deflection, true));
                actions.add(action);
            }
            if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId, true)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Retrieve Beskar Deflection");
                action.appendUsage(
                    new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.Beskar_Deflection));
                actions.add(action);
            }
        }
        return actions;
    }
}
