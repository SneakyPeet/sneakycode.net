goog.provide('cljs.foo');
goog.require('cljs.core');
goog.require('cljs.core.constants');
cljs.foo.press = (function cljs$foo$press(e){
return alert("See It Works");
});
goog.exportSymbol('cljs.foo.press', cljs.foo.press);
