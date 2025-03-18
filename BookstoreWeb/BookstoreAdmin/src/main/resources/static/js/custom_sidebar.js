(function () {
  "use strict";

  // Navbar left
  // -------------------------------------------------

  // Variables
  var nb = $(".dash-navbar-left"),
    nbBtnToggle = $(".nb-btn-toggle"),
    nbBtnCollapse = $(".nb-btn-collapse"),
    contentWrap = $(".content-wrap"),
    contentWrapEffect = contentWrap.data("effect"),
    windowHeight = $(window).height() - 61,
    windowWidth = $(window).width() < 767;

  // Functions
  function cwShowOverflow() {
    if (windowWidth) {
      contentWrap.css({
        height: windowHeight,
        overflow: "hidden",
      });
      $("html, body").css("overflow", "hidden");
    }
  }

  function cwHideOverflow() {
    if (windowWidth) {
      contentWrap.css({
        height: "auto",
        overflow: "auto",
      });
      $("html, body").css("overflow", "auto");
    }
  }

  function nbShow() {
    nb.addClass("nb-show").removeClass("nb-hide");
    contentWrap.addClass(contentWrapEffect);
    cwShowOverflow();
    nbBtnToggle.find("span").removeClass("fa-bars").addClass("fa-arrow-left");
  }

  function nbHide() {
    nb.removeClass("nb-show").addClass("nb-hide");
    contentWrap.removeClass(contentWrapEffect);
    cwHideOverflow();
    nbBtnToggle.find("span").removeClass("fa-arrow-left").addClass("fa-bars");
  }

  // Toggle the edge navbar left
  nb.addClass("nb-hide");
  nbBtnToggle.click(function () {
    if (nb.hasClass("nb-hide")) {
      nbShow();
    } else {
      nbHide();
    }
  });

  // Collapse the dash navbar left subnav
  nbBtnCollapse.click(function (e) {
    e.preventDefault();
    if (nb.hasClass("nb-collapsed")) {
      nb.removeClass("nb-collapsed");
      contentWrap.removeClass("nb-collapsed");
      $(this)
        .find(".nb-link-icon")
        .removeClass("fa-arrow-right")
        .addClass("fa-arrow-left");
    } else {
      nb.addClass("nb-collapsed");
      contentWrap.addClass("nb-collapsed");
      $(this)
        .find(".nb-link-icon")
        .removeClass("fa-arrow-left")
        .addClass("fa-arrow-right");
    }
  });

  // Close left navbar when top navbar opens
  $(".navbar-toggle").click(function () {
    if ($(this).hasClass("collapsed")) {
      nbHide();
    }
  });

  // Close top navbar when left navbar opens
  nbBtnToggle.click(function () {
    $(".navbar-toggle").addClass("collapsed");
    $(".navbar-collapse").removeClass("in");
  });

  // Code credit: https://tr.im/CZzf4
  function isMobile() {
    try {
      document.createEvent("TouchEvent");
      return true;
    } catch (e) {
      return false;
    }
  }

  // Swipe the navbar
  if (isMobile() == true) {
    $(window).swipe({
      swipeRight: function () {
        nbShow();
        $(".navbar-collapse").removeClass("in");
      },
      swipeLeft: function () {
        nbHide();
      },
      threshold: 75,
    });
  }

  // Collapse navbar on content click
  $(".content-wrap").click(function () {
    nbHide();
  });

  // Auto collapse other opens subnavs
  /*$(".nb-nav > li > a").click( function() {
          $( document ).find( 'ul .in' ).collapse( 'hide' );
        });*/
})();
