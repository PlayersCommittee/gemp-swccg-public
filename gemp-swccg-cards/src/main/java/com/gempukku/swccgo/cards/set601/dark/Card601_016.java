package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Wookiee Slaving Camp
 */
public class Card601_016 extends AbstractSite {
    public Card601_016() {
        super(Side.DARK, "Kashyyyk: Wookiee Slaving Camp", Title.Kashyyyk);
        setLocationDarkSideGameText("Jabba's Sail Barge may deploy here.  If your Trandoshan present, Force Drain +1 here.");
        setLocationLightSideGameText("While a Trandoshan present and you have no Wookiees on Kashyyyk, you may not modify or cancel opponent's force drains here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Condition treatTrandoshanAsSlaver = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return modifiersQuerying.hasGameTextModification(gameState, self, ModifyGameTextType.LEGACY__YOUR_SITES__TREAT_TRANDOSHAN_AS_SLAVER);
            }
        };

        Condition trandoshanPresent = new OrCondition(new PresentAtCondition(Filters.species(Species.TRANDOSHAN), self),
                new AndCondition(treatTrandoshanAsSlaver, new PresentAtCondition(Filters.slaver, self)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new JabbasSailBargeMayDeployHereModifier(self, self));
        modifiers.add(new ForceDrainModifier(self, self, trandoshanPresent, 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Condition treatTrandoshanAsSlaver = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return modifiersQuerying.hasGameTextModification(gameState, self, ModifyGameTextType.LEGACY__YOUR_SITES__TREAT_TRANDOSHAN_AS_SLAVER);
            }
        };

        Condition trandoshanPresent = new OrCondition(new PresentAtCondition(Filters.species(Species.TRANDOSHAN), self),
                new AndCondition(treatTrandoshanAsSlaver, new PresentAtCondition(Filters.slaver, self)));

        //While a Trandoshan present and you have no Wookiees on Kashyyyk, you may not modify or cancel opponent's force drains here.
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, self,
                new AndCondition(trandoshanPresent, new NotCondition(new OnCondition(self, Filters.Wookiee, Title.Kashyyyk))),
                playerOnLightSideOfLocation, game.getOpponent(playerOnLightSideOfLocation)));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, self,
                new AndCondition(trandoshanPresent, new NotCondition(new OnCondition(self, Filters.Wookiee, Title.Kashyyyk))),
                playerOnLightSideOfLocation, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}