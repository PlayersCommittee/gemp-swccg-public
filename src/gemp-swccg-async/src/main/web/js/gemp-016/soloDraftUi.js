var GempSwccgSoloDraftUI = Class.extend({
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
                    for (var i = 0; i < availablePicks.length; i++) {
                        var availablePick = availablePicks[i];
                        var id = availablePick.getAttribute("id");
                        var url = availablePick.getAttribute("url");
                        var blueprintId = availablePick.getAttribute("blueprintId");

                        if (blueprintId != null) {
                            var card = new Card(blueprintId, null, null, false, "picks", "deck", "player");
                            var cardDiv = Card.CreateCardDiv(card.imageUrl, null, null, card.isFoil(), false, false, card.incomplete);
                            cardDiv.data("card", card);
                            cardDiv.data("choiceId", id);
                            that.picksDiv.append(cardDiv);
                        } else {
                            var card = new Card("rules", null, null, false, "picks", "deck", "player");
                            var cardDiv = Card.CreateCardDiv(url, null, null, false, false, true, false);
                            cardDiv.data("card", card);
                            cardDiv.data("choiceId", id);
                            that.picksDiv.append(cardDiv);
                        }
                    }
                    that.picksCardGroup.layoutCards();
                    // Fade in cards with staggered animation
                    that.picksDiv.find(".card").each(function(index) {
                        $(this).css({opacity: 0}).delay(index * 50).animate({opacity: 1}, 400, "easeOutQuad");
                    });
                    if (availablePicks.length > 0) {
                        that.messageDiv.text("Make a pick (stage " + stage + " / " + stages + ")");
                    }
                    else {
                        that.messageDiv.text("Draft is finished");
                        that.showCompletionScreen();
                    }
                }
            });

        this.comm.getCollection(this.leagueType, "sort:cardType,side,name", 0, 1000,
            function (xml) {
                var root = xml.documentElement;
                if (root.tagName == "collection") {
                    var cards = root.getElementsByTagName("card");
                    for (var i=0; i<cards.length; i++) {
                        var card = cards[i];
                        var count = card.getAttribute("count");
                        var blueprintId = card.getAttribute("blueprintId");
                        for (var no = 0; no < count; no++) {
                            var card = new Card(blueprintId, null, null, false, "drafted", "deck", "player");
                            var cardDiv = Card.CreateCardDiv(card.imageUrl, null, null, card.isFoil(), false, false, card.incomplete);
                            cardDiv.data("card", card);
                            that.draftedDiv.append(cardDiv);
                        }
                    }
                    that.draftedCardGroup.layoutCards();
                }
            });
    },

    clickCardFunction:function (event) {
        var that = this;

        var tar = $(event.target);
        if (tar.length == 1 && tar[0].tagName == "A")
            return true;

        if (!this.successfulDrag && this.infoDialog.dialog("isOpen")) {
            this.infoDialog.dialog("close");
            event.stopPropagation();
            return false;
        }

        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which == 1) {
                if (!this.successfulDrag) {
                    if (event.shiftKey) {
                        this.displayCardInfo(selectedCardElem.data("card"));
                    } else {
                        if (selectedCardElem.data("card").zone == "picks") {
                            var choiceId = selectedCardElem.data("choiceId");
                            that.comm.makeDraftPick(that.leagueType, choiceId, function (xml) {
                                var root = xml.documentElement;
                                if (root.tagName == "pickResult") {
                                    var pickedCards = root.getElementsByTagName("pickedCard");
                                    for (var i = 0; i < pickedCards.length; i++) {
                                        var pickedCard = pickedCards[i];
                                        var blueprintId = pickedCard.getAttribute("blueprintId");
                                        var count = pickedCard.getAttribute("count");
                                        for (var no = 0; no < count; no++) {
                                            var card = new Card(blueprintId, null, null, false, "drafted", "deck", "player");
                                            var cardDiv = Card.CreateCardDiv(card.imageUrl, null, null, card.isFoil(), false, false, card.incomplete);
                                            cardDiv.data("card", card);
                                            that.draftedDiv.append(cardDiv);
                                        }
                                    }
                                    that.draftedCardGroup.layoutCards();

                                    var availablePicks = root.getElementsByTagName("availablePick");
                                    var draftState = root.getElementsByTagName("state")[0];
                                    var stage = draftState.getAttribute("stage");
                                    var stages = draftState.getAttribute("stages");
                                    for (var i = 0; i < availablePicks.length; i++) {
                                        var availablePick = availablePicks[i];
                                        var id = availablePick.getAttribute("id");
                                        var url = availablePick.getAttribute("url");
                                        var blueprintId = availablePick.getAttribute("blueprintId");

                                        if (blueprintId != null) {
                                            var card = new Card(blueprintId, null, null, false, "picks", "deck", "player");
                                            var cardDiv = Card.CreateCardDiv(card.imageUrl, null, null, card.isFoil(), false, false, card.incomplete);
                                            cardDiv.data("card", card);
                                            cardDiv.data("choiceId", id);
                                            that.picksDiv.append(cardDiv);
                                        } else {
                                            var card = new Card("rules", null, null, false, "picks", "deck", "player");
                                            var cardDiv = Card.CreateCardDiv(url, null, null, false, false, true, false);
                                            cardDiv.data("card", card);
                                            cardDiv.data("choiceId", id);
                                            that.picksDiv.append(cardDiv);
                                        }
                                    }
                                    that.picksCardGroup.layoutCards();
                                    // Fade in cards with staggered animation
                                    that.picksDiv.find(".card").each(function(index) {
                                        $(this).css({opacity: 0}).delay(index * 50).animate({opacity: 1}, 400, "easeOutQuad");
                                    });
                                    if (availablePicks.length > 0) {
                                        that.messageDiv.text("Make a pick (stage " + stage + " / " + stages + ")");
                                    }
                                    else {
                                        that.messageDiv.text("Draft is finished");
                                        that.showCompletionScreen();
                                    }
                                }
                            });
                            $(".card", that.picksDiv).remove();
                        }
                    }

                    event.stopPropagation();
                }
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

         var horSpace = 30;
         var vertSpace = 45;

         if (card.horizontal) {
             // 500x360
             this.infoDialog.dialog({width:Math.min(500 + horSpace, windowWidth), height:Math.min(380 + vertSpace, windowHeight)});
         } else {
             // 360x500
             this.infoDialog.dialog({width:Math.min(360 + horSpace, windowWidth), height:Math.min(520 + vertSpace, windowHeight)});
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
