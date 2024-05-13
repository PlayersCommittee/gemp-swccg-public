package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;


public class SetTargetedCardEffect extends AbstractSuccessfulEffect implements TargetingEffect {
    private TargetId _targetId;
    private PhysicalCard _cardTargeting;
    private PhysicalCard _targetedCard;
    private Integer _targetGroupId;
    private Filter _validTargetFilter;

    public SetTargetedCardEffect(Action action, PhysicalCard sourceCard, TargetId targetId, Integer targetGroupId, PhysicalCard target, Filter validTargetFilter) {
        super(action);
        _targetId = targetId;
        _cardTargeting = sourceCard;
        _targetedCard = target;
        _targetGroupId = targetGroupId;
        _validTargetFilter = validTargetFilter;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        _cardTargeting.setTargetedCard(_targetId, _targetGroupId, _targetedCard, _validTargetFilter);
    }
}
