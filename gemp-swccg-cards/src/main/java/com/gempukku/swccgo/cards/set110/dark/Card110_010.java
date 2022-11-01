package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Mara Jade, The Emperor's Hand
 */
public class Card110_010 extends AbstractAlienImperial {
    public Card110_010() {
        super(Side.DARK, 1, 5, 4, 5, 7, "Mara Jade, The Emperor's Hand", Uniqueness.UNIQUE, ExpansionSet.ENHANCED_JABBAS_PALACE, Rarity.PM);
        setLore("Spy. Ordered to kill Luke Skywalker. Assumed the identity of a dancer named 'Arica' in order to sneak into Jabba's Palace.");
        setGameText("Adds 2 to power of anything she pilots. Power +2 if Emperor on table. May be targeted by Vader's Obsession and Epic Duel instead of Vader. Once per game, may deploy a weapon on her from Reserve Deck; reshuffle. Immune to attrition < 4.");
        addPersona(Persona.MARA_JADE);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new OnTableCondition(self, Filters.Emperor), 2));
        modifiers.add(new MayBeTargetedByModifier(self, Title.Vaders_Obsession));
        modifiers.add(new MayBeTargetedByModifier(self, Title.Epic_Duel));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MARA_JADE_THE_EMPERORS_HAND__DOWNLOAD_WEAPON;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a weapon on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.weapon, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
