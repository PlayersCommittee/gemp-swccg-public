package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.*;

/**
 * Data that is stored in while in play data.
 */
public class WhileInPlayData implements Snapshotable<WhileInPlayData> {
    private boolean _booleanValue;
    private Float _floatValue;
    private String _textValue;
    private Set<String> _textValues = new HashSet<String>();
    private PhysicalCard _physicalCard;
    private List<PhysicalCard> _physicalCards = new ArrayList<PhysicalCard>();
    private Persona _persona;
    private Evaluator _evaluator;
    private Map<String, Boolean> _stringBooleanMap = new HashMap<String, Boolean>();
    private Species _speciesValue;

    @Override
    public void generateSnapshot(WhileInPlayData selfSnapshot, SnapshotData snapshotData) {
        WhileInPlayData snapshot = selfSnapshot;

        // Set each field
        snapshot._booleanValue = _booleanValue;
        snapshot._floatValue = _floatValue;
        snapshot._textValue = _textValue;
        snapshot._textValues.addAll(_textValues);
        snapshot._physicalCard = snapshotData.getDataForSnapshot(_physicalCard);
        for (PhysicalCard card : _physicalCards) {
            snapshot._physicalCards.add(snapshotData.getDataForSnapshot(card));
        }
        snapshot._persona = _persona;
        snapshot._evaluator = _evaluator;
        snapshot._stringBooleanMap.putAll(_stringBooleanMap);
        snapshot._speciesValue = _speciesValue;
    }

    /**
     * Creates data that is stored in while in play data.
     */
    public WhileInPlayData() {
    }

    /**
     * Creates data that is stored in while in play data.
     * @param booleanValue the boolean value of the data
     */
    public WhileInPlayData(boolean booleanValue) {
        _booleanValue = booleanValue;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param floatValue the float value of the data
     */
    public WhileInPlayData(Float floatValue) {
        _floatValue = floatValue;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param textValue a text value
     */
    public WhileInPlayData(String textValue) {
        _textValue = textValue;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param species a Species value
     */
    public WhileInPlayData(Species species) {
        _speciesValue = species;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param textValues list of text values
     */
    public WhileInPlayData(Set<String> textValues) {
        _textValues.addAll(textValues);
    }

    /**
     * Creates data that is stored in while in play data.
     * @param physicalCard a physical card
     */
    public WhileInPlayData(PhysicalCard physicalCard) {
        _physicalCard = physicalCard;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param evaluator an evaluator
     */
    public WhileInPlayData(Evaluator evaluator) {
        _evaluator = evaluator;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param booleanValue the boolean value of the data
     * @param physicalCard a physical card
     */
    public WhileInPlayData(boolean booleanValue, PhysicalCard physicalCard) {
        _booleanValue = booleanValue;
        _physicalCard = physicalCard;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param floatValue the float value of the data
     * @param physicalCard a physical card
     */
    public WhileInPlayData(Float floatValue, PhysicalCard physicalCard) {
        _floatValue = floatValue;
        _physicalCard = physicalCard;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param physicalCard a physical card
     * @param evaluator an evaluator
     */
    public WhileInPlayData(PhysicalCard physicalCard, Evaluator evaluator) {
        _physicalCard = physicalCard;
        _evaluator = evaluator;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param booleanValue the boolean value of the data
     * @param persona a persona
     */
    public WhileInPlayData(boolean booleanValue, Persona persona) {
        _booleanValue = booleanValue;
        _persona = persona;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param physicalCards a physical card list
     */
    public WhileInPlayData(List<PhysicalCard> physicalCards) {
        _physicalCards.addAll(physicalCards);
    }

    /**
     * Creates data that is stored in while in play data.
     * @param stringBooleanMap a String/Boolean map
     */
    public WhileInPlayData(Map<String, Boolean> stringBooleanMap) {
        _stringBooleanMap.putAll(stringBooleanMap);
    }

    /**
     * Gets the boolean value of the data.
     * @return true or false
     */
    public boolean getBooleanValue() {
        return _booleanValue;
    }

    /**
     * Gets the float value of the data.
     * @return the float value
     */
    public Float getFloatValue() {
        return _floatValue;
    }

    /**
     * Gets the text value of the data.
     * @return the text value
     */
    public String getTextValue() {
        return _textValue;
    }

    /**
     * Gets the species data
     * @return  the Species stored in data
     */
    public Species getSpeciesValue() { return _speciesValue; }

    /**
     * Gets the text values of the data.
     * @return the text values
     */
    public Set<String> getTextValues() {
        return _textValues;
    }

    /**
     * Gets the physical card data.
     * @return the physical card stored in data
     */
    public PhysicalCard getPhysicalCard() {
        return _physicalCard;
    }

    /**
     * Gets the physical cards data.
     * @return the physical cards data
     */
    public List<PhysicalCard> getPhysicalCards() {
        return _physicalCards;
    }

    /**
     * Gets the evaluator data.
     * @return the evaluator stored in data
     */
    public Evaluator getEvaluator() {
        return _evaluator;
    }

    /**
     * Gets the persona data.
     * @return the persona stored in data
     */
    public Persona getPersona() {
        return _persona;
    }

    /**
     * Gets the String/Boolean map.
     * @return the String/Boolean map
     */
    public Map<String, Boolean> getStringBooleanMap() {
        return _stringBooleanMap;
    }

}
