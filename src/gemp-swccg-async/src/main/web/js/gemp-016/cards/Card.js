class Card {
    blueprintId = null;
    bareBlueprint = null;
    foil = null;
    alternateImage = null;
    blueprintHorizontal = null
    horizontal = null;
    imageUrl = null;
    backSideImageUrl = null;
    testingText = null;
    backSideTestingText = null;

    zone = null;
    cardId = null;
    owner = null;
    attachedCards = null;
    locationIndex = null;
    inverted = false;
    upsideDown = false;
    sideways = false;
    onSide = false;
    inBattle = false;
    attackingInAttack = false;
    defendingInAttack = false;
    inDuelOrLightsaberCombat = false;
    incomplete = null;
    frozen = null;
    suspended = null;
    collapsed = null;
    
    static CardCache = {};
    static CardScale = 350 / 490;
    
    static StripBlueprintId(blueprintId) {
        var stripped = blueprintId;
        
        stripped = stripped.replace("*", "");
        stripped = stripped.replace("^", "");

        return stripped;
    }
    
    static GetFoil(bpid) {
       //At the very smallest, a card id can be 3 characters, i.e. 1_1
        //Thus we start searching at the 2nd character
        return bpid.includes("*", 2); 
    }
    
    static GetAlternateImage(bpid) {
        return bpid.includes("^", 2);
    }

    constructor (blueprintId, testingText, backSideTestingText, horizontal, zone, cardId, owner, locationIndex, upsideDown, onSide, frozen, suspended, collapsed) {
        this.blueprintId = blueprintId;
        this.bareBlueprint = Card.StripBlueprintId(this.blueprintId);
        
        this.foil = Card.GetFoil(blueprintId);
        this.alternateImage = Card.GetAlternateImage(blueprintId);
        
        if (this.alternateImage) {
            if (fixedImages[this.bareBlueprint + "ai"] != null)
                this.bareBlueprint = this.bareBlueprint + "ai";
        }


        this.testingText = null;
        if (testingText !== undefined) {
            this.testingText = testingText;
        }
        this.backSideTestingText = null;
        if (backSideTestingText !== undefined) {
            this.backSideTestingText = backSideTestingText;
        }

        this.zone = zone;
        this.cardId = cardId;
        this.owner = owner;
        if (locationIndex !== undefined) {
            this.locationIndex = parseInt(locationIndex);
        }
        this.inverted = false;
        if (upsideDown !== undefined) {
            this.upsideDown = upsideDown;
        }
        this.sideways = false;
        if (onSide !== undefined) {
            this.onSide = onSide;
        }
        this.frozen = false;
        if (frozen !== undefined) {
            this.frozen = frozen;
        }
        this.suspended = false;
        if (suspended !== undefined) {
            this.suspended = suspended;
        }
        this.collapsed = false;
        if (collapsed !== undefined) {
            this.collapsed = collapsed;
        }
        this.attachedCards = new Array();
        if (this.bareBlueprint == "rules") {
            this.imageUrl = "https://res.starwarsccg.org/cards/rules.png";
            return;
        }

        this.blueprintHorizontal = horizontal === true || horizontal === 'true' || this.isHorizontalVirtualAiImage()
        this.horizontal = this.blueprintHorizontal && !Card.isZoneNeverHorizontal(zone);
        
        if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2" && Card.CardCache[this.bareBlueprint] != null) {
            var cardFromCache = Card.CardCache[this.bareBlueprint];
            this.imageUrl = cardFromCache.imageUrl;
            this.backSideImageUrl = cardFromCache.backSideImageUrl;
            this.incomplete = cardFromCache.incomplete;
        } else {
            this.imageUrl = Card.getImageUrl(this.bareBlueprint);
            this.backSideImageUrl = Card.getBackSideUrl(this.bareBlueprint);
            this.incomplete = Card.isIncomplete(this.bareBlueprint);

            if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2") {
                Card.CardCache[this.bareBlueprint] = {
                    imageUrl:this.imageUrl,
                    backSideImageUrl:this.backSideImageUrl,
                    incomplete:this.incomplete
                };
            }
        }
    }

    flipOverCard() {
        this.bareBlueprint = Card.getBackSideBlueprintId(this.bareBlueprint);
        var tempText = this.testingText;
        this.testingText = this.backSideTestingText;
        this.backSideTestingText = tempText;

        if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2" && Card.CardCache[this.bareBlueprint] != null) {
            var cardFromCache = Card.CardCache[this.bareBlueprint];
            this.imageUrl = cardFromCache.imageUrl;
            this.backSideImageUrl = cardFromCache.backSideImageUrl;
            this.incomplete = cardFromCache.incomplete;
        } else {
            this.imageUrl = Card.getImageUrl(this.bareBlueprint);
            this.backSideImageUrl = Card.getBackSideUrl(this.bareBlueprint);
            this.incomplete = Card.isIncomplete(this.bareBlueprint);

            if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2") {
                Card.CardCache[this.bareBlueprint] = {
                    imageUrl:this.imageUrl,
                    backSideImageUrl:this.backSideImageUrl,
                    incomplete:this.incomplete
                };
            }
        }
        $(".card:cardId(" + this.cardId + ") > img").attr('src', this.imageUrl);
    }

    turnCardOver(tempBlueprintId) {
        this.bareBlueprint = tempBlueprintId;
        var tempText = this.testingText;
        this.testingText = this.backSideTestingText;
        this.backSideTestingText = tempText;

        if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2" && Card.CardCache[this.bareBlueprint] != null) {
            var cardFromCache = Card.CardCache[this.bareBlueprint];
            this.imageUrl = cardFromCache.imageUrl;
            this.backSideImageUrl = cardFromCache.backSideImageUrl;
            this.incomplete = cardFromCache.incomplete;
        } else {
            this.imageUrl = Card.getImageUrl(this.bareBlueprint);
            this.backSideImageUrl = Card.getBackSideUrl(this.bareBlueprint);
            this.incomplete = Card.isIncomplete(this.bareBlueprint);

            if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2") {
                Card.CardCache[this.bareBlueprint] = {
                    imageUrl:this.imageUrl,
                    backSideImageUrl:this.backSideImageUrl,
                    incomplete:this.incomplete
                };
            }
        }
        $(".card:cardId(" + this.cardId + ") > img").attr('src', this.imageUrl);
    }

    isFoil() {
        return this.foil;
    }

    isPack() {
        return packBlueprints[this.blueprintId] != null;
    }

    isHorizontalVirtualAiImage() {
        return this.bareBlueprint == "204_47ai"
                || this.bareBlueprint == "200_41ai"
                || this.bareBlueprint == "206_6ai";
    }

    static isZoneNeverHorizontal(zone) {
        if (zone == "RESERVE_DECK" || zone == "FORCE_PILE"
                || zone == "USED_PILE" || zone == "LOST_PILE"
                || zone == "TOP_OF_RESERVE_DECK" || zone == "TOP_OF_FORCE_PILE"
                || zone == "TOP_OF_USED_PILE" || zone == "TOP_OF_LOST_PILE"
                || zone == "STACKED" || zone == "STACKED_FACE_DOWN"
                || zone == "DESTINY_ANIMATION") {
            return true;
        }

        return false;
    }

    static isIncomplete(blueprintId) {
        var separator = blueprintId.indexOf("_");
        var setNo = parseInt(blueprintId.substr(0, separator));
        var cardNo = parseInt(blueprintId.substr(separator + 1));
        
        if (setNo >= 400 && setNo < 600) {
            return true;
        }

        return false;
    }

    static getImageUrl(blueprintId) {
        if (fixedImages[blueprintId] != null)
            return fixedImages[blueprintId];

        if (packBlueprints[blueprintId] != null)
            return packBlueprints[blueprintId];

        return null;
    }

    static getBackSideBlueprintId(blueprintId) {
        if (blueprintId.endsWith("_BACK")) {
            return blueprintId.substring(0, blueprintId.length - 5);
        }
        var backSideUrl = Card.getImageUrl(blueprintId.concat("_BACK"));
        if (backSideUrl != null) {
            return blueprintId.concat("_BACK");
        }
        var genericBackUrl = Card.getImageUrl(blueprintId);
        if (genericBackUrl != null) {
            if (Card.getImageUrl(blueprintId).includes("-Dark/"))
                    return "-1_2";
                else
                    return "-1_1";
        }
    }

    static getBackSideUrl(blueprintId) {
        return Card.getImageUrl(Card.getBackSideBlueprintId(blueprintId));
    }

    getHeightForWidth(width) {
        if (this.horizontal)
            return Math.floor(width * Card.CardScale);
        else
            return Math.floor(width / Card.CardScale);
    }

    getHeightForColumnWidth(columnWidth) {
        if (this.horizontal)
            return columnWidth;
        else
            return Math.floor(columnWidth / Card.CardScale);
    }

    getWidthForHeight(height) {
        if (this.horizontal)
            return Math.floor(height / Card.CardScale);
        else
            return Math.floor(height * Card.CardScale);
    }

    getWidthForMaxDimension(maxDimension) {
        if (this.horizontal)
            return maxDimension;
        else
            return Math.floor(maxDimension * Card.CardScale);
    }

    getHeightForMaxDimension(maxDimension) {
        if (this.horizontal)
            return Math.floor(maxDimension * Card.CardScale);
        else
            return maxDimension;
    }
    
    static CreateCardDiv(image, testingText, text, foil, tokens, noBorder, incomplete) {
        var cardDiv = $("<div class='card'><img src='" + image + "' width='100%' height='100%'>" + ((text != null) ? text : "") + "</div>");

        if (incomplete) {
            var incompleteDiv = $("<div class='incompleteOverlay'><img src='https://res.starwarsccg.org/gemp/incompleteCard.png' width='100%' height='100%'></div>");
            cardDiv.append(incompleteDiv);
        }

        if (foil) {
            var foilDiv = $("<div class='foilOverlay'><img src='https://res.starwarsccg.org/cards/holo.jpg' width='100%' height='100%'></div>");
            cardDiv.append(foilDiv);
        }

        var frozenDiv = $("<div class='frozenOverlay'><img src='https://res.starwarsccg.org/cards/carbonite.gif' width='100%' height='100%'></div>");
        cardDiv.append(frozenDiv);

        var suspendedDiv = $("<div class='suspendedOverlay'><img src='https://res.starwarsccg.org/gemp/gray.jpg' width='100%' height='100%'></div>");
        cardDiv.append(suspendedDiv);

        var collapsedDiv = $("<div class='collapsedOverlay'><img src='https://res.starwarsccg.org/gemp/collapsed.jpg' width='100%' height='100%'></div>");
        cardDiv.append(collapsedDiv);

        if (tokens === undefined || tokens) {
            var overlayDiv = $("<div class='tokenOverlay'></div>");
            cardDiv.append(overlayDiv);
        }

        if (testingText != null) {
            var testingTextDiv = $("<div class='testingTextOverlay'></div>");
            var firstPipe = testingText.indexOf('|');
            if (firstPipe !== -1) {
                testingTextDiv.html(testingText.substring(0, firstPipe));
            }
            else {
                testingTextDiv.html(testingText);
            }
            cardDiv.append(testingTextDiv);
        }

        var borderDiv = $("<div class='borderOverlay'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
        if (noBorder)
            borderDiv.addClass("noBorder");
        cardDiv.append(borderDiv);
        
        var cardPileCountDiv = $("<div class='cardPileCount'></div>");
        cardDiv.append(cardPileCountDiv);

        return cardDiv;
    }

    static CreateFullCardDiv(image, testingText, foil, horizontal, noBorder) {
        if (horizontal) {
            var cardDiv = $("<div style='position: relative;width:497px;height:357px;'></div>");
            cardDiv.append("<div class='fullcard' style='position:absolute'><img src='" + image + "' width='497' height='357'></div>");

            if (foil) {
                var foilDiv = $("<div class='foilOverlay' style='position:absolute;width:497px;height:357px'><img src='https://res.starwarsccg.org/cards/holo.jpg' width='100%' height='100%'></div>");
                cardDiv.append(foilDiv);
            }

            if (testingText != null) {
                var testingTextDiv = $("<div class='testingTextOverlay' style='position:absolute;left:25px;width:449px;top:18px;height:321px'></div>");
                testingTextDiv.html(testingText.replace(/\|/g, "<br/>"));
                cardDiv.append(testingTextDiv);
            }

            if (noBorder) {
                var borderDiv = $("<div class='borderOverlay,noBorder' style='position:absolute;width:497px;height:357px;border-width:0px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            } else {
                var borderDiv = $("<div class='borderOverlay' style='position:absolute;width:465px;height:325px;border-width:16px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            }

        } else {
            var cardDiv = $("<div style='position: relative;width:357px;height:497px;'></div>");
            cardDiv.append("<div class='fullcard' style='position:absolute'><img src='" + image + "' width='357' height='497'></div>");

            if (foil) {
                var foilDiv = $("<div class='foilOverlay' style='position:absolute;width:357px;height:497px'><img src='https://res.starwarsccg.org/cards/holo.jpg' width='100%' height='100%'></div>");
                cardDiv.append(foilDiv);
            }

            if (testingText != null) {
                var testingTextDiv = $("<div class='testingTextOverlay' style='position:absolute;left:36px;width:285px;top:50px;height:398px'></div>");
                testingTextDiv.html(testingText.replace(/\|/g, "<br/>"));
                cardDiv.append(testingTextDiv);
            }

            if (noBorder) {
                var borderDiv = $("<div class='borderOverlay,noBorder' style='position:absolute;width:357px;height:497px;border-width:0px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            } else {
                var borderDiv = $("<div class='borderOverlay' style='position:absolute;width:325px;height:465px;border-width:16px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            }
        }

        return cardDiv;
    }

    static CreateSimpleCardDiv(image, testingText, foil, incomplete, borderWidth) {
        var cardDiv = $("<div class='card'><img src='" + image + "' width='100%' height='100%'></div>");

        if (incomplete) {
            var incompleteDiv = $("<div class='incompleteOverlay' style='position:absolute;left:0px;top:0px;width:100%;height:100%'><img src='https://res.starwarsccg.org/gemp/incompleteCard.png' width='100%' height='100%'></div>");
            cardDiv.append(incompleteDiv);
        }

        if (foil) {
            var foilDiv = $("<div class='foilOverlay' style='position:absolute;left:0px;top:0px;width:100%;height:100%'><img src='https://res.starwarsccg.org/gemp/holo.jpg' width='100%' height='100%'></div>");
            cardDiv.append(foilDiv);
        }

        if (testingText != null) {
            var testingTextDiv = $("<div class='testingTextOverlay' style='position:absolute;left:5%;top:5%;width:90%;height:90%'></div>");
            testingTextDiv.html(testingText.replace(/\|/g, "<br/>"));
            cardDiv.append(testingTextDiv);
        }

        var borderDiv = $("<div class='borderOverlay' style='position:absolute;left:0px;top:0px;width:100%;height:100%;border-width:" + borderWidth + "px;box-sizing:border-box'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
        cardDiv.append(borderDiv);

        return cardDiv;
    }
}
