package com.divarsmartsearch.app.presentation.screens.webview

/**
 * Ported directly from the old browser extension's content-list.js /
 * content-detail.js. Same passive-reading philosophy: only extracts
 * whatever is already rendered on the page the user is looking at
 * inside our own in-app browser tab — no extra network requests, no
 * simulated clicks on "show number" buttons.
 *
 * Re-injected periodically by DivarWebViewScreen (Divar's search page is
 * a single-page app, so a one-time injection at page load isn't enough
 * to catch content that appears later as the user scrolls/navigates).
 */
object JsExtractionScripts {

    /** Runs on any divar.ir page; picks list-page or detail-page logic based on the URL. */
    val EXTRACTION_SCRIPT = """
        (function () {
          try {
            var PERSIAN_DIGITS = '۰۱۲۳۴۵۶۷۸۹';
            function toAsciiDigits(text) {
              return text.replace(/[۰-۹]/g, function(d) { return String(PERSIAN_DIGITS.indexOf(d)); });
            }
            function parseNumber(text) {
              if (!text) return null;
              var normalized = toAsciiDigits(text).replace(/[,٬٫]/g, '');
              var match = normalized.match(/\d+(\.\d+)?/);
              return match ? parseFloat(match[0]) : null;
            }
            function extractToken(href) {
              var match = href.match(/\/v\/[^\/]+\/([\w-]+)/);
              return match ? match[1] : null;
            }

            function extractListPage() {
              var anchors = Array.prototype.slice.call(document.querySelectorAll('a[href*="/v/"]'));
              var seen = {};
              var listings = [];
              for (var i = 0; i < anchors.length; i++) {
                var href = anchors[i].getAttribute('href');
                if (!href) continue;
                var token = extractToken(href);
                if (!token || seen[token]) continue;
                seen[token] = true;

                var card = anchors[i];
                for (var d = 0; d < 4 && card.parentElement; d++) card = card.parentElement;

                var text = (card.innerText || '').trim();
                var lines = text.split('\n').map(function(l){return l.trim();}).filter(Boolean);
                if (lines.length === 0) continue;

                var title = lines[0];
                var priceLine = lines.filter(function(l){return l.indexOf('تومان') !== -1 || l.indexOf('توافقی') !== -1;})[0];
                var areaLine = lines.filter(function(l){return l.indexOf('متر') !== -1;})[0];

                listings.push({
                  divarToken: token,
                  url: new URL(href, location.origin).toString(),
                  title: title,
                  price: priceLine ? parseNumber(priceLine) : null,
                  area: areaLine ? parseNumber(areaLine) : null,
                  pricePerMeter: null,
                  neighborhood: null,
                  description: null,
                  contactPhone: null
                });
              }
              return listings;
            }

            function extractDetailPage() {
              var match = location.pathname.match(/\/v\/[^\/]+\/([\w-]+)/);
              if (!match) return [];
              var token = match[1];

              var telLink = document.querySelector('a[href^="tel:"]');
              var phone = telLink ? telLink.getAttribute('href').replace('tel:', '').trim() : null;

              var paragraphs = Array.prototype.slice.call(document.querySelectorAll('p'));
              var description = null;
              if (paragraphs.length > 0) {
                var longest = paragraphs.reduce(function(a, b) {
                  return (b.innerText || '').length > (a.innerText || '').length ? b : a;
                });
                var text = (longest.innerText || '').trim();
                description = text.length > 20 ? text : null;
              }

              var bodyLines = (document.body.innerText || '').split('\n');
              var priceLine = bodyLines.filter(function(l){return l.indexOf('تومان') !== -1;})[0];

              var h1 = document.querySelector('h1');
              var title = h1 ? h1.innerText.trim() : document.title;

              return [{
                divarToken: token,
                url: location.href,
                title: title,
                description: description,
                price: priceLine ? parseNumber(priceLine) : null,
                area: null,
                pricePerMeter: null,
                neighborhood: null,
                contactPhone: phone
              }];
            }

            var listings = /^\/v\//.test(location.pathname) ? extractDetailPage() : extractListPage();

            if (listings.length > 0 && window.AndroidBridge) {
              window.AndroidBridge.onListingsExtracted(JSON.stringify(listings));
            }
          } catch (e) {
            // Swallow errors silently — extraction is best-effort and must
            // never break the page the user is actually trying to browse.
          }
        })();
    """.trimIndent()
}
