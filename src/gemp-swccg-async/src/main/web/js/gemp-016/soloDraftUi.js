var GempSwccgSoloDraftUI = Class.extend({
    // Constants for card info dialog sizing
    DIALOG_HORIZONTAL_SPACE: 30,
    DIALOG_VERTICAL_SPACE: 45,
    CARD_HORIZONTAL_WIDTH: 500,
    CARD_HORIZONTAL_HEIGHT: 380,
    CARD_VERTICAL_WIDTH: 360,
    CARD_VERTICAL_HEIGHT: 520,

    communication:null,

    topDiv:null,
    bottomDiv:null,

    messageDiv:null,
    picksDiv:null,
    draftedDiv:null,
    completionScreen:null,

    picksCardGroup:null,
    draftedCardGroup:null,

    leagueType:null,
    autoZoom:null,
    currentDraftingSide:null,

    rightClickListenerAdded:false,

    init:function (url) {
        var that = this;

        this.comm = new GempSwccgCommunication(url,
                                function (xhr, ajaxOptions, thrownError) {
                                });

        this.leagueType = getUrlParam("leagueType");

        this.topDiv = $("#topDiv");
        this.bottomDiv = $("#bottomDiv");

        this.messageDiv = $("#messageDiv");
        this.picksDiv = $("#picksDiv");
        this.draftedDiv = $("#draftedDiv");
        this.completionScreen = $("#completionScreen");

        this.picksCardGroup = new NormalCardGroup(this.picksDiv, function (card) {
            return true;
        });
        this.picksCardGroup.maxCardHeight = 200;

        this.draftedCardGroup = new NormalCardGroup(this.draftedDiv, function (card) {
            return true;
        });
        this.draftedCardGroup.maxCardHeight = 200;

        this.autoZoom = new AutoZoom("autoZoomInSoloDraft");

        this.selectionFunc = this.addCardToDeckAndLayout;

        $("body").click(
                function (event) {
                    return that.clickCardFunction(event);
                });

        if (!this.rightClickListenerAdded) {
            $("body")[0].addEventListener("contextmenu",
                function (event) {
                    if(!that.clickCardFunction(event))
                    {
                        event.preventDefault();
                        return false;
                    }
                    return true;
                });
            this.rightClickListenerAdded = true;
        }

        $('body').unbind('mouseover');
        $("body").mouseover(
                function (event) {
                    return that.autoZoom.handleMouseOver(event.originalEvent,
                        that.dragCardData != null, that.infoDialog.dialog("isOpen"));
                });

        $('body').unbind('mouseout');
        $("body").mouseout(
                function (event) {
                    return that.autoZoom.handleMouseOut(event.originalEvent);
                });

        $("body").mousedown(
                function (event) {
                    that.autoZoom.handleMouseDown(event.originalEvent);
                    return that.dragStartCardFunction(event);
                });
        $("body").mouseup(
                function (event) {
                    return that.dragStopCardFunction(event);
                });

        $('body').unbind('keydown');
        $("body").keydown(
                function (event) {
                    return that.autoZoom.handleKeyDown(event.originalEvent);
                });

        $('body').unbind('keyup');
        $("body").keyup(
                function (event) {
                    return that.autoZoom.handleKeyUp(event.originalEvent);
                });

        var width = $(window).width();
        var height = $(window).height();

        this.infoDialog = $("<div></div>")
                .dialog({
            autoOpen:false,
            closeOnEscape:true,
            resizable:false,
            title:"Card information"
        });

        var swipeOptions = {
            threshold:20,
            swipeUp:function (event) {
                that.infoDialog.prop({ scrollTop:that.infoDialog.prop("scrollHeight") });
                return false;
            },
            swipeDown:function (event) {
                that.infoDialog.prop({ scrollTop:0 });
                return false;
            }
        };
        this.infoDialog.swipe(swipeOptions);

        this.getDraftState();
    },

    normalizeSide:function (side) {
        return side ? side.toLowerCase() : null;
    },

    addDraftedCardsToDisplay:function (blueprintId, horizontal, side, count) {
        var that = this;
        var sideClass = side ? this.normalizeSide(side) + "-side" : "";

        Array.from({length: count}).forEach(function() {
            var card = new Card(blueprintId, null, null, horizontal, "drafted", "deck", "player");
            var cardDiv = Card.CreateCardDiv(card.imageUrl, null, null, card.isFoil(), false, false, card.incomplete);
            cardDiv.data("card", card);
            if (sideClass) {
                cardDiv.addClass(sideClass);
            }
            that.draftedDiv.append(cardDiv);
        });
    },

    createPickCardFromXmlElement:function (availablePick) {
        var id = availablePick.getAttribute("id");
        var url = availablePick.getAttribute("url");
        var blueprintId = availablePick.getAttribute("blueprintId");
        var horizontal = availablePick.getAttribute("horizontal");
        var side = this.normalizeSide(availablePick.getAttribute("side"));

        var card, cardDiv;
        if (blueprintId != null) {
            card = new Card(blueprintId, null, null, horizontal, "picks", "deck", "player");
            cardDiv = Card.CreateCardDiv(card.imageUrl, null, null, card.isFoil(), false, false, card.incomplete);
            if (side) {
                cardDiv.addClass(side + "-side");
            }
        } else {
            card = new Card("rules", null, null, false, "picks", "deck", "player");
            cardDiv = Card.CreateCardDiv(url, null, null, false, false, true, false);
        }

        cardDiv.data("card", card);
        cardDiv.data("choiceId", id);

        return cardDiv;
    },

    animateCardFadeIn:function (containerDiv) {
        containerDiv.find(".card").each(function(index) {
            $(this).css({opacity: 0}).delay(index * 50).animate({opacity: 1}, 400, "easeOutQuad");
        });
    },

    calculateAndDisplayRemainingPicks:function (stage, stages, currentSide) {
        var totalStages = parseInt(stages);
        var currentStage = parseInt(stage);
        var halfStages = Math.floor(totalStages / 2);

        var lsRemaining, dsRemaining;
        if (currentSide === "light") {
            lsRemaining = halfStages - currentStage;
            dsRemaining = halfStages;
        } else {
            lsRemaining = 0;
            dsRemaining = totalStages - currentStage;
        }

        this.messageDiv.text("Picks Remaining  LS: " + lsRemaining + "  DS: " + dsRemaining);

        return {lsRemaining: lsRemaining, dsRemaining: dsRemaining};
    },

    loadDraftedCardsForCurrentSide:function (callback) {
        var that = this;
        this.comm.getCollection(this.leagueType, "sort:cardType,side,name", 0, 1000,
            function (xml) {
                var root = xml.documentElement;
                if (root.tagName == "collection") {
                    var cards = root.getElementsByTagName("card");
                    Array.from(cards).forEach(function(card) {
                        var count = parseInt(card.getAttribute("count"));
                        var blueprintId = card.getAttribute("blueprintId");
                        var horizontal = card.getAttribute("horizontal");
                        var side = that.normalizeSide(card.getAttribute("side"));

                        if (side && side === that.currentDraftingSide) {
                            that.addDraftedCardsToDisplay(blueprintId, horizontal, side, count);
                        }
                    });
                    that.draftedCardGroup.layoutCards();
                    if (callback) callback();
                }
            });
    },

    renderAvailablePicks:function (availablePicks) {
        var that = this;

        Array.from(availablePicks).forEach(function(availablePick) {
            var cardDiv = that.createPickCardFromXmlElement(availablePick);
            that.picksDiv.append(cardDiv);
        });

        this.picksCardGroup.layoutCards();
        this.animateCardFadeIn(this.picksDiv);
    },

    getDraftState:function () {
        var that = this;
        this.comm.getDraft(this.leagueType,
            function (xml) {
                var root = xml.documentElement;
                if (root.tagName == "availablePicks") {
                    var availablePicks = root.getElementsByTagName("availablePick");
                    var draftState = root.getElementsByTagName("state")[0];
                    var stage = draftState.getAttribute("stage");
                    var stages = draftState.getAttribute("stages");

                    // Render available picks with animation
                    that.renderAvailablePicks(availablePicks);

                    if (availablePicks.length > 0) {
                        var currentSide = availablePicks[0].getAttribute("side");
                        that.currentDraftingSide = currentSide;
                        that.calculateAndDisplayRemainingPicks(stage, stages, currentSide);
                    }
                    else {
                        that.messageDiv.text("Draft is finished");
                        that.showCompletionScreen();
                    }

                    // Load drafted cards for current side
                    that.loadDraftedCardsForCurrentSide();
                }
            });
    },

    addPickedCardsToDrafted:function (pickedCards) {
        var that = this;
        Array.from(pickedCards).forEach(function(pickedCard) {
            var blueprintId = pickedCard.getAttribute("blueprintId");
            var count = parseInt(pickedCard.getAttribute("count"));
            var horizontal = pickedCard.getAttribute("horizontal");
            that.addDraftedCardsToDisplay(blueprintId, horizontal, null, count);
        });
        this.draftedCardGroup.layoutCards();
    },

    processPickResult:function (xml) {
        var root = xml.documentElement;
        if (root.tagName == "pickResult") {
            // Add picked cards to drafted area
            var pickedCards = root.getElementsByTagName("pickedCard");
            this.addPickedCardsToDrafted(pickedCards);

            // Clear current picks and render new ones
            $(".card", this.picksDiv).remove();

            var availablePicks = root.getElementsByTagName("availablePick");
            var draftState = root.getElementsByTagName("state")[0];
            var stage = draftState.getAttribute("stage");
            var stages = draftState.getAttribute("stages");

            // Render new available picks
            this.renderAvailablePicks(availablePicks);

            if (availablePicks.length > 0) {
                var currentSide = availablePicks[0].getAttribute("side");
                var sideChanged = this.currentDraftingSide !== currentSide;
                this.currentDraftingSide = currentSide;

                this.calculateAndDisplayRemainingPicks(stage, stages, currentSide);

                // If side changed, reload drafted cards for the new side
                if (sideChanged) {
                    $(".card", this.draftedDiv).remove();
                    this.loadDraftedCardsForCurrentSide();
                }
            }
            else {
                this.messageDiv.text("Draft is finished");
                this.showCompletionScreen();
            }
        }
    },

    handlePickSelection:function (selectedCardElem) {
        var that = this;
        var choiceId = selectedCardElem.data("choiceId");
        this.comm.makeDraftPick(this.leagueType, choiceId, function (xml) {
            that.processPickResult(xml);
        });
    },

    clickCardFunction:function (event) {
        var tar = $(event.target);

        // Allow anchor tag clicks to proceed normally
        if (tar.length == 1 && tar[0].tagName == "A")
            return true;

        if (!this.successfulDrag && this.infoDialog.dialog("isOpen")) {
            this.infoDialog.dialog("close");
            event.stopPropagation();
            return false;
        }

        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which >= 1 && !this.successfulDrag) {
                // Shift-click or right-click shows card info
                if (event.shiftKey || event.which > 1) {
                    this.displayCardInfo(selectedCardElem.data("card"));
                    return false;
                } else {
                    if (selectedCardElem.data("card").zone == "picks") {
                        this.handlePickSelection(selectedCardElem);
                    }
                }
                event.stopPropagation();
            }
            return false;
        }
        return true;
    },

    dragCardData:null,
    dragStartX:null,
    dragStartY:null,
    successfulDrag:null,

    dragStartCardFunction:function (event) {
        this.successfulDrag = false;
        var tar = $(event.target);
        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which == 1) {
                this.dragCardData = selectedCardElem.data("card");
                this.dragStartX = event.clientX;
                this.dragStartY = event.clientY;
                return false;
            }
        }
        return true;
    },

    dragStopCardFunction:function (event) {
        if (this.dragCardData != null) {
            if (this.dragStartY - event.clientY >= 20) {
                this.displayCardInfo(this.dragCardData);
                this.successfulDrag = true;
            }
            this.dragCardData = null;
            this.dragStartX = null;
            this.dragStartY = null;
            return false;
        }
        return true;
    },

     displayCardInfo:function (card) {
         var that = this;
         this.infoDialog.html("");
         this.infoDialog.html("<div style='scroll: auto'></div>");
         var floatCardDiv = $("<div style='float: left;'></div>");
         var cardDiv = Card.CreateFullCardDiv(card.imageUrl, null, card.foil, card.horizontal, card.isPack());

         // Check if card div needs to be inverted
         this.infoDialog.cardImageRotation = 0;
         this.infoDialog.cardImageFlipped = false;
         $(cardDiv).click(
                function(event) {
                    // Check if need to show other card image if the image has two sides
                    if (card.backSideImageUrl != null) {
                        that.infoDialog.cardImageFlipped = !that.infoDialog.cardImageFlipped;
                        if (that.infoDialog.cardImageFlipped) {
                            $(cardDiv).find("div.fullcard img").attr('src', card.backSideImageUrl);
                        }
                        else {
                            $(cardDiv).find("div.fullcard img").attr('src', card.imageUrl);
                        }
                    }
                    // Otherwise rotate the image
                    else {
                        that.infoDialog.cardImageRotation = (that.infoDialog.cardImageRotation + 180) % 360;
                        $(cardDiv).rotate(that.infoDialog.cardImageRotation);
                    }
                    event.stopPropagation();
                });
         floatCardDiv.append(cardDiv);

         this.infoDialog.append(floatCardDiv);

         var windowWidth = $(window).width();
         var windowHeight = $(window).height();

         if (card.horizontal) {
             this.infoDialog.dialog({
                 width: Math.min(this.CARD_HORIZONTAL_WIDTH + this.DIALOG_HORIZONTAL_SPACE, windowWidth),
                 height: Math.min(this.CARD_HORIZONTAL_HEIGHT + this.DIALOG_VERTICAL_SPACE, windowHeight)
             });
         } else {
             this.infoDialog.dialog({
                 width: Math.min(this.CARD_VERTICAL_WIDTH + this.DIALOG_HORIZONTAL_SPACE, windowWidth),
                 height: Math.min(this.CARD_VERTICAL_HEIGHT + this.DIALOG_VERTICAL_SPACE, windowHeight)
             });
         }
         this.infoDialog.dialog("open");
     },

    layoutUI:function (layoutDivs) {
        if (layoutDivs) {
            var messageHeight = 40;
            var padding = 5;

            var topWidth = this.topDiv.width();
            var topHeight = this.topDiv.height();

            var bottomWidth = this.bottomDiv.width();
            var bottomHeight = this.bottomDiv.height();

            this.picksDiv.css({position:"absolute", left:padding, top:messageHeight+padding, width:topWidth-padding*2, height:topHeight-messageHeight-padding*2});
            this.picksCardGroup.setBounds(0, 0, topWidth-padding*2, topHeight-messageHeight-padding*2);

            this.draftedDiv.css({position:"absolute", left:padding, top:padding, width:bottomWidth-padding*2, height:bottomHeight-padding*2});
            this.draftedCardGroup.setBounds(0, 0, bottomWidth-padding*2, bottomHeight-padding*2);
        } else {
            this.picksCardGroup.layoutCards();
            this.draftedCardGroup.layoutCards();
        }
    },

    showCompletionScreen:function () {
        this.completionScreen.fadeIn(500);
    },

    processError:function (xhr, ajaxOptions, thrownError) {
        if (thrownError != "abort")
            alert("There was a problem during communication with server");
    }
});
